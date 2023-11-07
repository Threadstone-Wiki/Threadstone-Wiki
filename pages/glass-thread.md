# Stained Glass Threads

## Table of Contents

- [Introduction](#introduction)
- [Slowing down stained glass threads](#slowing-down-stained-glass-threads)

# Introduction

Every time you place or break a stained glass block in minecraft, the game starts a new [thread](threads.md#stained-glass-thread), called the *stained glass thread*.

Every stained glass thread executes the following code:
```
public static void updateBeam(World world, BlockPos pos) {
		HttpUtil.DOWNLOAD_THREAD_FACTORY.submit(new Runnable() {
			@Override
			public void run() {
				WorldChunk c_6849228 = world.getChunk(pos);

				for (int i = pos.getY() - 1; i >= 0; --i) {
					final BlockPos c_3674802 = new BlockPos(pos.getX(), i, pos.getZ());
					if (!c_6849228.hasSkyAccess(c_3674802)) {
						break;
					}

					BlockState c_2441996 = world.getBlockState(c_3674802);
					if (c_2441996.getBlock() == Blocks.BEACON) {
						((ServerWorld)world).submit(new Runnable() {
							@Override
							public void run() {
								BlockEntity c_3622326 = world.getBlockEntity(c_3674802);
								if (c_3622326 instanceof BeaconBlockEntity) {
									((BeaconBlockEntity)c_3622326).update();
									world.addBlockEvent(c_3674802, Blocks.BEACON, 1, 0);
								}
							}
						});
					}
				}
			}
		});
	}
```

The thread checks for beacon blocks below the stained glass, and whenever it finds a beacon, it schedules the beacon to change its beacon beam color on the main thread in the next [player phase](tick-phases.md#player-phase).

The `getBlockState` call in this piece of code can under unusual circumstances cause [async chunk loading](async-chunk-loading.md), which can trigger an [async terrain population](population.md#glass-threads-causing-async-updates),
which can trigger async block updates, which enables many powerful [threadstone exploits](async-line.md#applications).

# Slowing down stained glass threads
If one wants to perform [async chunk loading](async-chunk-loading.md), it is often important to slow down stained glass threads, relative to the main thread.

The following methods can be used for slowing down stained glass threads:
1. [Cluster chunks](chunk/chunk-hashmap.md#cluster-chunks) can slow down the `getBlockState` call in the stained glass thread code.
2. Beacon blocks below the stained glass can force the stained glass thread code to call the `((ServerWorld)world).submit` function, which slows down the thread.

Beacons are particularly noteworthy, because they lead to a `synchronized` block.
The `((ServerWorld)world).submit` function
calls the following code in the `MinecraftServer` class
```
public <V> ListenableFuture<V> submit(Callable<V> event) {
		Validate.notNull(event);
		if (!this.isOnSameThread() && !this.hasStopped()) {
			ListenableFutureTask<V> listenableFutureTask = ListenableFutureTask.create(event);
			synchronized (this.pendingEvents) {
				this.pendingEvents.add(listenableFutureTask);
				return listenableFutureTask;
			}
		} else {
			try {
				return Futures.immediateFuture(event.call());
			} catch (Exception var6) {
				return Futures.immediateFailedCheckedFuture(var6);
			}
		}
	}
```
and this contains a `synchronized (this.pendingEvents)` block.

This means that if multiple stained glass threads find beacon blocks at the same time, and try to submit their beacon beam color change task at the same time, then one of the threads has to wait for the other to finish the `synchronized` block.

This leads to the following additional method for slowing down stained glass threads:

3. If one has beacons below the stained glass blocks, then starting additional stained glass threads will slow down already existing stained glass threads more than it slows down the main thread.

Note that starting multiple stained glass threads is only worth it if one uses beacons. If one does not use beacons, then starting many many stained glass threads will still slow down the stained glass threads,
because they take execution time away from each other. But without beacons, having many stained glass threads does not slow them down relative to the main thread, because the stained glass threads will take away just as much execution time from the main thread as they take away from each other,
so that the relative speed of main thread and stained glass threads remains the same.

The `synchronized` block that the beacons lead to have another use.

The `MinecraftServer` class has another `synchronized (this.pendingEvents)` block at the beginning of the `tickWorlds` method
```
	synchronized (this.pendingEvents) {
		while (!this.pendingEvents.isEmpty()) {
			Utils.run(this.pendingEvents.poll(), LOGGER);
		}
	}
```
This is the [player phase of the main thread](tick-phases.md#player-phases).

This leads to another way of slowing down stained glass threads:

4. If a stained glass thread finds a beacon, while the main thread is in the player phase, then the stained glass thread will wait until the main thread has finished the player phase.




