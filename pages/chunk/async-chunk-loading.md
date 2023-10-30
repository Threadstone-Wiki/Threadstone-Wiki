This page is about async chunk loading.

# Introduction

When a [chunk](chunk.md) is loaded on an [async thread](../threads.md), this is called an *async chunk load*.

Async chunk loading is important, because an async chunk load can trigger an [async terrain population](population.md#glass-threads-causing-async-updates), which then causes async block updates,
which can be used for [threadstone exploits](../async-line.md#applications).

The only async threads on which chunks can possibly be loaded are the [stained glass threads](../threads.md#stained-glass-threads).

Loading a chunk on a stained glass thread is quite difficult, because the `getBlockState` calls of the thread all happen below the stained glass block that started the thread,
so these calls all happen in a chunk that was already loaded when the stained glass block got placed or broken.

To load a chunk with the `getBlockState` calls of a stained glass thread, one either needs to unload the chunk containing the stained glass block, while the async thread is running,
or load the chunk, even though it is already loaded.

If the chunk gets loaded even though it is already loaded, then this is called a *chunk swap*.

If the chunk gets unloaded while the async thread is running, then the resulting async chunk load this is called a *regular load*.

Unloading the chunk while async threads are running in it is difficult, because if one schedules a chunk to be unloaded in the next [unload phase](../tick-phases.md#chunk-unloading), but the async thread does a `getBlockState` call before the chunk is actually unloaded, then the scheduled unloading gets cancelled, and the chunk does not get unloaded in the next unload phase. The last tick phase before the unload phase in which chunk unloading could possibly be scheduled is the [player phase](../tick-phases.md#player-phase).
So to unload a chunk in which a stained glass thread is running, the main thread needs to get from the player phase, through the mob spawning phase, to the unload phase, all while the async thread does not do a single `getBlockState` call.

# Chunk swap

In each dimension the loaded chunks are stored in a [Long2ObjectOpenHashmap](https://github.com/karussell/fastutil/blob/master/src/it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap.java).
The Long2ObjectOpenhashmap does not support asynchronous operations. If multiple threads access the Long2ObjectOpenhashmap at the same time, it can fail to work as intended.
In particular it is possible to load chunks that are already loaded. For this there are two different methods.

## Rehash chunk swap
A detailed explanation of rehash chunk swaps is in [cool mann's homework](https://docs.google.com/document/d/1rTKfmVLAtmvBMWW1QSgnetSG8Fuit5CaUvV77T9SgXk/edit)

## Unload chunk swap

# Regular load

## Void's synchronized method
