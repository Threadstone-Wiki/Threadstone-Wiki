This page is about async chunk loading.

# Introduction

When a [chunk](chunk.md) is loaded on an [async thread](../threads.md), this is called an *async chunk load*.

The only async threads on which chunks can possibly be loaded are the [stained glass threads](../threads.md#stained-glass-threads).
This page will focus solely on how one can load chunks using the `getBlockState` calls in the stained glass thread code.

Async chunk loading is important, because an async chunk load can trigger an [async terrain population](population.md#glass-threads-causing-async-updates), which then causes async block updates,
which can be used for [threadstone exploits](../async-line.md#applications).

Loading a chunk on a stained glass thread is quite difficult, because the `getBlockState` calls of the thread all happen below the stained glass block that started the thread,
so these calls all happen in a chunk that was already loaded when the stained glass block got placed or broken.
To load a chunk with these `getBlockState` calls, one either needs to unload the chunk containing the stained glass block, while the async thread is running,
or load the chunk, even though it is already loaded.

If the chunk gets loaded even though it is already loaded, then this is called a *chunk swap*.

If the chunk gets unloaded while the async thread is running, then the resulting async chunk load this is called a *regular load*.

The best method for async chunk loading in survival is the [Void synchronized method](#void-synchronized-method).

# Chunk swap

In each dimension the loaded chunks are stored in a [`Long2ObjectOpenHashmap`](https://github.com/karussell/fastutil/blob/master/src/it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap.java).
The `Long2ObjectOpenhashmap` is a data structure that does not support asynchronous operations. If multiple threads access the `Long2ObjectOpenhashmap` at the same time, it can fail to work as intended.
In particular it is possible to load chunks that are already loaded. For this there are two different methods.

## Rehash chunk swap
A detailed explanation of rehash chunk swaps is in [cool mann's homework](https://docs.google.com/document/d/1rTKfmVLAtmvBMWW1QSgnetSG8Fuit5CaUvV77T9SgXk/edit)


## Unload chunk swap

# Regular load

The main difficulty with performing an async regular load is that the `getBlockState` calls of the async thread can cancel chunk unloading.
The last tick phase before the [unload phase](../tick-phases.md#chunk-unloading) in which one can schedule chunk unloading is the [player phase](../tick-phases.md#player-phase).
So to perform an async regular load, one needs to first schedule the chunk in which the async thread is running to be unloaded, and this scheduling happens in the player phase or even earlier. And then the main thread needs to get through the whole player phase, and the whole mob spawning phase and into the unload phase, all while the async thread does not do a single `getBlockState` call.
If the async thread does do a single `getBlockState` call in this time, then the scheduled chunk unloading will be cancelled, and the chunk will not get unloaded in the unload phase.

## Void synchronized method

# Preventing crashes during async chunk load

When a chunk is loaded on an async thread, then the async thread will load in all the entities and tile entities that exist in that chunk.
If the main thread starts processing entities or tile entities while the async thread is loading in entities or tile entities, then this can crash the game in a `ConcurrentModificationException`.

For this reason it is customary to create a lag spike on the main thread in the [block event phase](../tick-phases.md#block-event-phase) right after one has performed an async chunk load, to prevent the main thread from processing entities or tile entities too early.
One can create a lag spike during the block event phase, by activating a piston, and letting the piston activate an [update multiplier chain](../update-multiplier.md#lag-spikes).

