This page is about async chunk loading.

# Introduction

When a [chunk](chunk.md) is loaded on an [async thread](../threads.md), this is called an *async chunk load*.

The only async threads on which chunks can possibly be loaded are the [stained glass threads](../threads.md#stained-glass-threads).
This page will focus solely on how one can load chunks using the `getBlockState` calls in the stained glass thread code.

Async chunk loading is important, because an async chunk load can trigger an [async terrain population](population.md#glass-threads-causing-async-updates), which then causes async block updates,
which can be used for [threadstone exploits](../async-line.md#applications).

The best method for async chunk loading in survival is the [Void synchronized method](#void-synchronized-method-for-regular-async-load).

## Terminology

Loading a chunk on a stained glass thread is quite difficult, because the `getBlockState` calls of the thread all happen below the stained glass block that started the thread,
so these calls all happen in a chunk that was already loaded when the stained glass block got placed or broken.

The chunk in which we start the stained glass threads will be called the *glass chunk*.

There are two ways one can asyncly load a chunk using the `getBlockState` calls of the stained glass threads:
1. Unload the glass chunk, while the async threads are running in it. This case is called a *regular async load*.
2. Load the glass chunk even though it is already loaded. This case is called a *chunk swap*.

To do a chunk swap one needs to exploit [race conditions in the chunk hashmap](chunk-hashmap.md#race-conditions).

There are two kinds of chunk swaps, depending on the race condition one uses to get the chunk swap:
1. If one uses a [`get`+`remove` race condition](chunk-hashmap.md#get-remove), the chunk swap is called an *Unload Chunk Swap*.
2. If one uses a [`get`+`rehash` race condition](chunk-hashmap.md#get-rehash), the chunk swap is called a *Rehash Chunk Swap*.

There are two kinds of rehash chunk swaps: 
1. If the `rehash` downsizes the chunk hashmap, it is called a *Downsize Rehash Chunk Swap*.
2. If the `rehash` upsizes the chunk hashmap, it is called an *Upsize Rehash Chunk Swap*.

# Common Techniques

Before we discuss specific methods for async chunk loading, we discuss various techniques that appear in multiple specific setups.

## Slowing down Stained Glass Threads
For almost all async chunk loading methods it is important to slow down the stained glass threads relative to the main thread as much as possible, so that the main thread has time either unload the glass chunk,
or perform an operation that allows a chunk swap in the glass chunk.

The following methods can be used for slowing down stained glass threads:
1. [Cluster chunks](chunk/chunk-hashmap.md#cluster-chunks) can slow down the `getBlockState` call in the stained glass thread code.
2. Beacon blocks below the stained glass can force the stained glass thread code to call the `((ServerWorld)world).submit` function, which slows down the thread.

Beacons are particularly noteworthy, because they lead to a `synchronized` block.

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
If it finds a beacon, it call the `((ServerWorld)world).submit` function, which
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

## Racing to the Unload Phase
All methods except for the upsize rehash chunk swap, require the main thread to unload a chunk as quickly as possible.




# Specific Methods

## Upsize Rehash Chunk Swap

Rehash chunk swaps can be done both when upsizing or when downsizing the chunk hashmap.
We will here only discuss the case of upsizing the chunk hashmap.

As explained in [the chunk hashmap article on rehash chunk swaps](chunk-hashmap.md#get-rehash) upsize rehash chunk swaps only work in specific locations:
- If one uses no cluster chunks, then rehash chunk swaps work in chunks whose hash value is exactly 2^n after upsizing, where n is the number of bits of the chunk hashmap before upsizing.
- If one uses [cluster chunks](chunk-hashmap.md#cluster-chunks), then the rehash can be done in any chunk whose hash value is near the end of the chunk hashmap, but whose index has been forced to the beginning of the chunk hashmap by the cluster.

We will now assume that the chunk we want to asyncly load is in such a suitable location, and call this chunk the *glass chunk*.

To do an upsize rehash chunk swap, the main thread needs to upsize the chunk hashmap, while stained glass threads are running in the glass chunk.
This means the main thread needs to do the following things:
1. Load chunks until the chunk hashmap is 3/4 filled, so that an additional chunk load can upsize the chunk hashmap.
2. Place or break stained glass in the glass chunks, to start the stained glass thread.
3. Load an additional chunk.
4. Upsize the chunk hashmap.

The main difficulty is to keep the async threads alive, while the main thread is loading the additional chunk.



## Classical Multiplayer Unload Chunk Swap

## ElRich Singleplayer Unload Chunk Swap

## Void Synchronized Method for Regular Async Load

The main difficulty with performing an async regular load is that the `getBlockState` calls of the async thread can cancel chunk unloading.
The last tick phase before the [unload phase](../tick-phases.md#chunk-unloading) in which one can schedule chunk unloading is the [player phase](../tick-phases.md#player-phase).
So to perform an async regular load, one needs to first schedule the chunk in which the async thread is running to be unloaded, and this scheduling happens in the player phase or even earlier. And then the main thread needs to get through the whole player phase, and the whole mob spawning phase and into the unload phase, all while the async thread does not do a single `getBlockState` call.
If the async thread does do a single `getBlockState` call in this time, then the scheduled chunk unloading will be cancelled, and the chunk will not get unloaded in the unload phase.


# Miscellaneous

# Preventing crashes during async chunk load

When a chunk is loaded on an async thread, then the async thread will load in all the entities and tile entities that exist in that chunk.
If the main thread starts processing entities or tile entities while the async thread is loading in entities or tile entities, then this can crash the game in a `ConcurrentModificationException`.

For this reason it is customary to create a lag spike on the main thread in the [block event phase](../tick-phases.md#block-event-phase) right after one has performed an async chunk load, to prevent the main thread from processing entities or tile entities too early.
One can create a lag spike during the block event phase, by activating a piston, and letting the piston activate an [update multiplier chain](../update-multiplier.md#lag-spikes).

# Using Chunk Swaps for Falling Block Swaps








