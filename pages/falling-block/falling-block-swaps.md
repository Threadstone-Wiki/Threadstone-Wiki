This page is about falling block swaps

# Introduction
A video explanation of falling block swaps is in [Falling Block Episode 2](https://www.youtube.com/watch?v=rNcFv5tccrg).
That video first covers pre-requisites for understanding falling block swaps, and starts explaining falling block swaps themselves at [13:50](https://www.youtube.com/watch?v=rNcFv5tccrg&t=830s).

Falling block swaps are a method for creating [falling block entities](falling-block-entity.md) of blocks which are not [gravity-affected blocks](gravity-affected-block.md).

When a falling block entity falls on a stone slab it will drop the item of its blocktype, even when that block would usually not drop as an item.
This makes falling block swaps a method for obtaining items of blocks for which the item is otherwise unobtainable.




TODO!

There are *specific methods* for falling block swaps, which can only create one particular type of falling block,
and there are *generic methods* for falling block swaps, which can create a wide variety of different falling blocks.






The most useful methods are the [generic method using hashmap word tearing](#hashmap-word-tearing),
and the [specific method for nether portals with async portal lighting](#nether-portal-1).

# Specific Methods
We distinguish the specific methods by whether the interesting block is placed on the main thread while the async thread creates sand entities,
or whether the interesting block is placed on the async thread while the main thread creates sand entities.

## setBlockState() on main thread

### Nether Portal
A video explanation for this method is in [Falling Block Episode 2 at 18:50](https://www.youtube.com/watch?v=rNcFv5tccrg&t=1130s).

### End Portal Frame
A video explanation for this method is in [Falling Block Episode 2 at 36:36](https://www.youtube.com/watch?v=rNcFv5tccrg&t=2196s).

### Spawner
This method was performed on KiwiTech, as shown in rpm's video [Getting Falling Blocks on KiwiTech, at 8:30](https://www.youtube.com/watch?v=wiCrgOcSKSE&t=510s).

### Barrier
This method was performed on KiwiTech, as shown in rpm's video [Getting Falling Blocks on KiwiTech, at 13:15](https://www.youtube.com/watch?v=wiCrgOcSKSE&t=795s).

As explained in [Terrain Population - Igloo Barrier Block], when an igloo has to replace a block with a tile entity by a furnace, it will first replace that block by a barrier block before placing the furnace.
If this barrier block replaces a gravity-affected block like sand, one can use this for a falling block swap.

There is one difficulty with this idea: No gravity-affected block has tile entity data, and the igloo population would replace a block without tile entity data directly by the furnace instead of placing the barrier.
To overcome this difficult one needs to use a [tile entity swap](../update-suppression.md#tile-entity-swap).

With the tile entity swap one can create air with tile entity data at the position where the furnace will be generated. One can then start the igloo population and let a sand block instantfall into the position of where the furnace will be generated.
Since the air at that position had tile entity data, we then get a sand block with tile entity data. The igloo population will then replace that sand block by a barrier block, and this can be used to perform a falling block swap for the barrier.

## setBlockState() on async thread

### Nether Portal
A video explanation for this method is in [Falling Block Episode 2 at 27:22](https://www.youtube.com/watch?v=rNcFv5tccrg&t=1642s).

### End Portal Frame
A video explanation for this method is in [Falling Block Episode 2 at 50:45](https://www.youtube.com/watch?v=rNcFv5tccrg&t=3045s).
This one is very difficult, because strongholds are a structure, and structure population is synchronized.

### Spawner
A video showing a falling block swap for spawners using carpet commands is in Myren's unlisted video [Async Population Spawner](https://www.youtube.com/watch?v=CVAoZLED3V4&list=PL8r-bvM9ltXNkjl7IhGQAHygIPfy2niuC&index=44).
Since dungeons are not a structure, dungeon population is not synchronized. This makes this method much easier to pull off without crashing the game than the async stronghold population method for end portal frames.

# Generic Methods

## Hashmap Word Tearing
A video explanation for this method is in [Falling Block Episode 6](https://www.youtube.com/watch?v=N1TuhgjUkc4).

## JKM's True Generic Method
JKM shows in the unlisted video [Yay large fern](https://www.youtube.com/watch?v=4fT3S6vRxSM) a fully general falling block swap method that can be used on every block of the game.
This makes it possible to obtain large fern items, which is the only block item that exists in 1.12 that cannot be obtained using the hashmap word tearing based generic method.

In 1.14+ versions large fern items generate naturally in village chests.

