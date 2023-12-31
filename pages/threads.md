# Threads ☆

## Table of Contents

- [Introduction](#introduction)
- [Main Thread](#main-thread)
- [Client Threads](#client-threads)
- [Stained Glass Threads](#stained-glass-threads)
- [Chunk Saving Thread](#chunk-saving-thread)
- [Server Watchdog](#server-watchdog)
- [Timer Hack Thread](#timer-hack-thread)
- [Miscellaneous](#miscellaneous)


# Introduction

Multithreading is a general computer science concept, that Wikipedia is more qualified to explain than me:

- [Multithreading](https://en.wikipedia.org/wiki/Multithreading_(computer_architecture))
- [Race Conditions](https://en.wikipedia.org/wiki/Race_condition)

A video crash course to multithreading is in Earthcomputer's video [\[1.12\] Why Minecraft Isn't Multithreaded (lol), at 10:20](https://www.youtube.com/watch?v=BQnejuEjMJs&t=620s).

In minecraft the following threads exist.

# Main Thread
The *main thread* is a thread that repeatedly executes [game ticks](tick-phases.md),
and under lag-free circumstances waits 0.05 seconds between game ticks.

All threads which are not the main thread are called *async threads*.

# Client Threads
There is a *client*, consisting of several *client threads*, responsible for rendering the game and playing sounds.
So everything you see and hear in minecraft is done by the client.
In each client there are client threads with the following names:
- Client Thread
- Client Shutdown Thread
- Chunk Batcher
- Sound Library Loader
- Texture Downloader
- Downloader
- LanServerDetector
- Server connector
- Realms-connect-task

In singleplayer there is a single client. In multiplayer there is one client for every player on the server.
In singleplayer the client is part of the same JVM as all the other threads.
In multiplayer the client is part of a separate JVM and might even be on a different computer.

All threads which are not part of a client are called *server threads*.

# Stained Glass Threads
Every time you place or break a stained glass block in minecraft, the game starts a new thread, called the *stained glass thread*.

Every stained glass thread executes the following code:
```
public static void updateBeam(World world, BlockPos pos) {
		HttpUtil.DOWNLOAD_THREAD_FACTORY.submit(new Runnable() {
			@Override
			public void run() {
				WorldChunk chunk = world.getChunk(pos);

				for (int i = pos.getY() - 1; i >= 0; --i) {
					final BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
					if (!chunk.hasSkyAccess(blockPos)) {
						break;
					}

					BlockState blockState = world.getBlockState(blockPos);
					if (blockState.getBlock() == Blocks.BEACON) {
						((ServerWorld)world).submit(new Runnable() {
							@Override
							public void run() {
								BlockEntity tileEntity = world.getBlockEntity(blockPos);
								if (tileEntity instanceof BeaconBlockEntity) {
									((BeaconBlockEntity)tileEntity).update();
									world.addBlockEvent(blockPos, Blocks.BEACON, 1, 0);
								}
							}
						});
					}
				}
			}
		});
	}
}
```
The thread checks for beacon blocks below the stained glass, and whenever it finds a beacon, it schedules the beacon to change its beacon beam color on the main thread in the next [player phase](tick-phases.md#player-phase).

The `getBlockState` call in this piece of code can under unusual circumstances cause [async chunk loading](chunk/async-chunk-loading.md), which can trigger an [async terrain population](chunk/population.md#glass-threads-causing-async-updates),
which can trigger async block updates, which enables many powerful [threadstone exploits](async-line.md#applications).

In Spigot Paper the patch that removes this code is called the ["Shame on you mojang"-patch](https://steamwar.de/devlabs/Mirrors/Paper/src/commit/4fbed1adab87251e5e11d507919f96b410e6faad/Spigot-Server-Patches/0199-Shame-on-you-Mojang.patch).

# Chunk Saving Thread
Whenever an autosave occurs, all loaded chunks get saved to disk, and whenever chunk is unloaded, it gets saved to disk. These chunk saving operations happen on a separate thread called the *chunk saving thread*.

If a chunk gets unloaded, and gets reloaded before the chunk saving thread finished saving it, then the chunk will not be loaded from disk, but it will be returned from a cache in exactly the state it was before unloading.
This can cause [savestates to fail](chunk/savestates.md#quick-reloads-break-savestates). This failure is especially likely, since it takes a long time for the chunk saving thread to (fail to) save a savestated chunk because savestated chunks contains so much data.

# Server Watchdog 
The *Server Watchdog* is thread that shuts down the server, if a tick takes longer than the `max-tick-time` specified in the `server.properties`

# Timer Hack Thread
There is a *timer hack thread* which gets started at the beginning of the game, and which then just sleeps forever doing nothing.
On Windows having such a completely inactive thread in the background improves the accuracy of `Thread.sleep` calls.
For details see [this stackoverflow answer](https://stackoverflow.com/questions/824110/accurate-sleep-for-java-on-windows/824472#824472).

# Miscellaneous
There are server threads with the following names:
- Server console handler
- User Authenticator
- LanServerPinger
