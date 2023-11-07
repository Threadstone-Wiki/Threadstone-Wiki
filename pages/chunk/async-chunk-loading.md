This page is about async chunk loading.

# Introduction

When a [chunk](chunk.md) is loaded on an [async thread](../threads.md), this is called an *async chunk load*.

The only async threads on which chunks can possibly be loaded are the [stained glass threads](../threads.md#stained-glass-threads).
This page will focus solely on how one can load chunks using the `getBlockState` calls in the stained glass thread code.

Async chunk loading is important, because an async chunk load can trigger an [async terrain population](population.md#glass-threads-causing-async-updates), which then causes async block updates,
which can be used for [threadstone exploits](../async-line.md#applications).

The best method for async chunk loading in survival is the [Void synchronized method](#void-synchronized-method).

# Methods
Loading a chunk on a stained glass thread is quite difficult, because the `getBlockState` calls of the thread all happen below the stained glass block that started the thread,
so these calls all happen in a chunk that was already loaded when the stained glass block got placed or broken.

To load a chunk with these `getBlockState` calls, one either needs to unload the chunk containing the stained glass block, while the async thread is running,
or load the chunk, even though it is already loaded.

If the chunk gets loaded even though it is already loaded, then this is called a *chunk swap*.

If the chunk gets unloaded while the async thread is running, then the resulting async chunk load this is called a *regular load*.

The chunk in which we start the stained glass threads will be called the *glass chunk*.

## Slowing down glass threads

Most async chunk load methods need to slow down the stained glass threads, so that they survive long enough, until the main thread either unloads the glass chunk or does an operation that causes a chunk swap in the glass chunk.

There are exactly two ways to slow down stained glass threads:
- Use [cluster chunks](chunk-hashmap.md#cluster-chunks) to slow down chunk accesses in the glass chunk.
- Have beacons below the stained glass.




## Chunk swap
To load an already loaded chunk, one needs to exploit [race conditions in the chunk hashmap](chunk-hashmap.md#race-conditions).


### Rehash chunk swap
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





### Unload chunk swap

## Regular load

The main difficulty with performing an async regular load is that the `getBlockState` calls of the async thread can cancel chunk unloading.
The last tick phase before the [unload phase](../tick-phases.md#chunk-unloading) in which one can schedule chunk unloading is the [player phase](../tick-phases.md#player-phase).
So to perform an async regular load, one needs to first schedule the chunk in which the async thread is running to be unloaded, and this scheduling happens in the player phase or even earlier. And then the main thread needs to get through the whole player phase, and the whole mob spawning phase and into the unload phase, all while the async thread does not do a single `getBlockState` call.
If the async thread does do a single `getBlockState` call in this time, then the scheduled chunk unloading will be cancelled, and the chunk will not get unloaded in the unload phase.

### Void synchronized method



# Preventing crashes during async chunk load

When a chunk is loaded on an async thread, then the async thread will load in all the entities and tile entities that exist in that chunk.
If the main thread starts processing entities or tile entities while the async thread is loading in entities or tile entities, then this can crash the game in a `ConcurrentModificationException`.

For this reason it is customary to create a lag spike on the main thread in the [block event phase](../tick-phases.md#block-event-phase) right after one has performed an async chunk load, to prevent the main thread from processing entities or tile entities too early.
One can create a lag spike during the block event phase, by activating a piston, and letting the piston activate an [update multiplier chain](../update-multiplier.md#lag-spikes).

