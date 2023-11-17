# Terrain Population ☆

- [Introduction](#introduction)
- [Manipulating Population](#manipulating-population)
  * [Flying machines](#flying-machines)
  * [Savestates](#savestates)
  * [Invisible Chunks](#invisible-chunks)
- [Population RNG](#population-rng)
  * [Nether and End](#nether-and-end)
  * [Liquid Pocket RNG](#liquid-pocket-rng)
- [Population Suppression](#population-suppression)
  * [Instant Tile Ticks](#instant-tile-ticks)
  * [Instant Falling](#instant-falling)
  * [Invisible Chunks](#invisible-chunks-1)
  * [Igloo Barrier Block](#igloo-barrier-block)
- [getBlockState() to setBlockState() exploits](#get-to-set)
  * [Redstone Power Flag Suppression](#redstone-power-flag-suppression)
  * [Pulling immovable blocks](#pulling-immovable-blocks)
  * [Glass threads causing async updates](#glass-threads-causing-async-updates)
- [Miscellaneous](#miscellaneous)
  * [1.12 Bedrock Item from Gateways](#112-bedrock-item-from-gateways)
  * [1.8 Bedrock Item from End Crystal Towers](#18-bedrock-item-from-end-crystal-towers)

# Introduction

A video explanation of terrain population is in [Panda's Survival #22: End Crystal Generation. Time: 7:00](https://www.youtube.com/watch?v=EeobLrHkfYI&t=420s)

Minecraft has procedural world generation, consisting of two steps. The first step is called *Chunk Generation* and happens as soon as the [chunk](chunk.md) is loaded for the first time.
The second step is called *Terrain Population* and only occurs if a 2x2 grid of chunks is loaded. When such a 2x2 grid of chunks is loaded, the chunk with the lowest x and z coordinates in the 2x2 grid immediately gets *populated*.

Chunk Generation places in the overworld things like bedrock, dirt, grass, stone, in oceans water.
Terrain population places things like trees, dungeons, ores, small caves and lakes, liquid pockets, and all structures, like strongholds, villages, mineshafts and so on.

Chunks which have been generated but not yet populated are called unpopulated chunks.

When a chunk is populated, most of the blocks that get placed are placed in the 16x16 area that is 8 block offset in +x and +z direction from the chunk itself.
This means the blocks mostly get placed in the 16x16 area which is at the center of the 2x2 grid of chunks that need to be loaded for the population to occur.

For example if we have 3 unpopulated chunks like on the left in the picture below, and we generate another chunk completing the 2x2 grid,
then the chunk on the bottom left of the 2x2 grid will become populated, and new blocks will be placed in the center of the 2x2 grid.

![Population image](/images/Population.PNG)

This system allows the game to easily place trees and dungeons on chunk borders without cutting them off.

# Manipulating Population

The player can interact with chunks that have been generated but not yet populated, and this can change what blocks get placed during population.
The following methods allow changing unpopulated chunks.

## Flying machines
One can build flying machines that fly to unpopulated chunks and put blocks there to manipulate population.

Before flying machines were invented, Panda4994, KaboPC and DanielKotes used flying-machine-like Kobra contraptions
to generate a quadruple skeleton spawner in a player-built structure, in the video [Panda's survival #20: Quadruple Skeleton Spawner](https://www.youtube.com/watch?v=9iaU1TvIQqM).

## Savestates
[Savestates](savestate.md#unpopulated-chunks) are the most important method for building contraptions next to unpopulated chunks.

## Invisible Chunks
[Invisible Chunks](#invisible-chunks-1) allow you to place immovable blocks inside unpopulated chunks at the chunk border. This makes invisible chunks the most powerful method for building contraptions around unpopulated chunks, but this method is not completely self-sufficient, because to create an invisible chunk you need to manipulate terrain population, using flying machines or savestates.

# Population RNG
To effectively exploit terrain population one needs to predict where it will place blocks.
Every dimension has a random number generator that is used solely for chunk generation and terrain population. We will call this the *population RNG* of the dimension.

## Nether and End
In the overworld the population RNG is seeded at the beginning of every chunk generation and terrain population.
In the nether and end the population RNG is seeded at the beginning of every chunk generation, but it does not get seeded during terrain population. A video explanation is [Earthcomputer's End Generation Bug](https://www.youtube.com/watch?v=nqyILYLu1Zo )
The blocks that get placed during a terrain population in the nether or end depend not only on the blocks in the area, but also on which chunk last got generated in the dimension, and what chunks were populated since the last chunk generation.

## Liquid Pocket RNG
It is often important to predict liquid pocket locations, because they enable [instant tile ticks](#instant-tile-ticks).
Liquid pockets locations are highly volative, because they get generated near the end of terrain population, and their location depends on how many random calls were made during all other parts of terrain population.
For example replacing one grass block visible from the sky by a stone block in the population area will change how many random calls the tree and tallgrass generators make, and this then changes the liquid pocket locations.
The best way to keep liquid pocket locations consistent is to make sure that the same blocks remain visible from the sky. Beyond that a bit of trial and error is inevitable.

# Population Suppression
One can cause an [update suppression](../update-suppression.md) in the middle of a terrain population to cause additional powerful exploits.
A video explanation of this is in [Falling Block Episode 1](https://www.youtube.com/watch?v=KU3lN1IUhuE).

## Instant Tile Ticks
Whenever a liquid pocket is placed during population, the game turns on the [instant tile tick flag](../global-flags.md#instant-tile-ticks) right before placing the liquid pocket, then places the liquid pocket, then updates the liquid pocket, and then turns the instant tile tick flag off again.
If an update suppression occurs while the liquid pocket gets updated, the instant tile tick flag stays on permanently.
Since the instant tile tick flag is on while the liquid pocket gets updated, one can very easily cause an update suppression without requiring thousands of rails.
The instant tile tick flag will turn back off again if another population successfully finishes placing a liquid pocket.

## Instant Falling
Whenever any terrain population occurs, the game turns on the [instant falling flag](../global-flags.md#instant-falling) right at the beginning of the population, and turns it back off at the end of the population.
If any update suppression occurs in any kind of population, the instant falling flag stays on permanently.
The instant falling flag will turn back off again if another population successfully finishes.

## Invisible Chunks
When a chunk gets loaded, it first tries to populate itself, and then tries to populate the chunks adjacent in the negative x and z direction.
If you update supress the population of the chunk, it will not try to populate the adjacent chunks.
This can be used to get fully loaded unpopulated chunks.

These unpopulated chunks are invisible.

## Igloo Barrier Block
Suppressing the population of an igloo can create barrier blocks.

This was done by Earthcomputer, Kerbaras and Cheater Codes on Prototech in the video [\[1.12\] Getting the Barrier Block](https://www.youtube.com/watch?v=zQUBR8dSUlA).

When an igloo gets placed during population it uses structure block code to place the igloo.
Whenever a structure block has to replace a block with a tile entity by another tile entity block, it first replaces the block by a barrier, and then replaces the barrier by the new tile entity block.
Igloos contain a furnace which is a tile entity block. If there is already a tile entity at the position where the furnace gets placed, the igloo population will replace it by a barrier block before replacing it by a furnace.
If one causes an update suppression right after the barrier is placed, one can permanently create a barrier block.

The barrier block placement does not send out normal block updates, so to update suppress it one either needs to use an observer with [instant tile ticks](../global-flags.md#instant-tile-ticks), or [suppress the comparator updates](../update-suppression.md#suppressing-comparator-updates) when the previous tile entity gets removed.

# `getBlockState` to `setBlockState` exploits <a name="get-to-set"/>
A `getBlockState` call can immediately trigger chunk loading.
Chunk loading can immediately trigger terrain population.
Terrain population immediately triggers `setBlockState` calls.

So thanks to terrain population, it is possible for a `getBlockState` call to trigger a `setBlockState` call during its execution.

## Redstone Power Flag Suppression
There is a [global redstone power flag](../global-flags.md#redstone-power-flag). Whenever redstone dust is updated, it turns off the redstone power flag, then checks whether it is receiving power from adjacent blocks, and then turns the redstone power flag back on again.
This system is necessary to prevent redstone dust from powering itself.
If an update suppression occurs while the redstone checks whether it is receiving power, then the redstone power flag stays on permanently.
While the redstone dust checks whether it is receiving power it is only doing `getBlockState` calls and does not send out any block updates, unless one of the `getBlockState` calls triggers a terrain population.
Without terrain population it is thus very difficult to cause an update suppression while redstone dust is checking whether it is receiving power.
With terrain population however the `getBlockState` call can trigger a `setBlockState` call which can send block updates which can be easily update suppressed and permanently turn on the redstone power flag.

The redstone power flag will turn back off again if a piece of redstone dust gets successfully updated.

## Pulling immovable blocks
A video explanation of pulling immovable blocks is in [Panda's Generating a Pig Spawner in 1.11](https://www.youtube.com/watch?v=cVvB53sWETg).

If a population occurs while a piston is starting to move blocks, the piston can sometimes move the newly placed blocks even if they are immovable.
If this is done to tile entity blocks like spawners or end gateways, the tile entity data gets deleted and one obtains pig spawners or dataless gateways.

When a piston processes a block event and wants to start moving blocks, it first creates a list toMove of all the block positions it wants to move and a list toDestroy of all the block positions it wants to destroy.
Whenever it tries to enter a position into the toMove list it first checks whether the block at that position is movable before entering the position into the list.
Once the two lists have been completed, the piston then creates an additional list containing all the blockstates of the blocks it wants to move.
It iterates through all positions it wants to move, and adds the blockstate at that position to its blockstate list, without checking whether that block is still movable.

During the creation of the list no block updates get send out, however a lot of `getBlockState` calls are made. If one of the `getBlockState` calls triggers a terrain population,
the blocks that the piston wants to move can be changed, and this makes it possible for the piston to then move immovable blocks.

The [history of this bug](../history.md#slimeblocks--tnt-duping-and-pig-spawner-generation) is quite involved.

## Glass threads causing async updates
Whenever you place a stained glass block, it starts a new async thread which does `getBlockState` calls below itself to check for beacons which need to change their beacon beam color.
If one of those `getBlockState` calls triggers an [async chunk loading](async-chunk-loading.md), then this can trigger an async terrain population and cause async block updates. These async block updates can then activate an [async line](../async-line.md).
This is the basis for all threadstone exploits.

To make the async chunk load trigger a terrain population there are two possible methods:

- One can use an invisible chunk next to the asyncly loaded chunk, in such a way that the asyncly loaded chunk is in the population 2x2 grid of the invisible chunk.
The async chunk load can then populate the invisible chunk on the async thread.

- The asyncly loaded chunk can itself be unpopulated on disk. Then loading the chunk on the async thread can populate the chunk on the async thread.

# Miscellaneous

## 1.12 Bedrock Item from Gateways
The first method for obtaining bedrock items in 1.12 that was discovered works by silk touch instantmining a slimeblock, extinguishing a fire block while doing so,
and then triggering a population that replaces the slimeblock by the bedrock block of an end gateway.
Video explanations are in [Earthcomputer's \[1.12\] How to get the Bedrock Item](https://www.youtube.com/watch?v=YHdSpO-Gsvc) and [Xcom's How to get a bedrock item in survival 1.9 to 1.12](https://www.youtube.com/watch?v=ajUea-FnRrc).

## 1.8 Bedrock Item from End Crystal Towers
In 1.8 and earlier versions one can use terrain population to generate end crystal towers in the end.
This is the only way to get additional end crystals in those versions.
That idea started out on this [reddit thread](https://www.reddit.com/r/ZipKrowd/comments/1rwxj5/minecraft_qa_for_game_mechanics/cfhouft/),
and was then done in survival in [ElRichMC's Survival 1.7 Ep103, Generador de EnderCrystals!](https://www.youtube.com/watch?v=Xx1AYgcl1eY).

The top of the end crystal towers generate a bedrock block.
Using [piston bugs](https://www.youtube.com/watch?v=wI7VMt3419M) one can destroy the bedrock block and obtain the bedrock item.
Predicting the end crystal tower location is very difficult, but was successfully done by [Xcom and Matthew Bolan](https://www.youtube.com/watch?v=tC4YEm6-gmE).
