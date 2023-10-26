# Terrain Population

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
- [getBlockState() to setBlockState() exploits](#getblockstate---to-setblockstate---exploits)
  * [Redstone Power Flag Suppression](#redstone-power-flag-suppression)
  * [Pulling immovable blocks](#pulling-immovable-blocks)
  * [Beacon threads causing async updates](#beacon-threads-causing-async-updates)
- [Version 1.8](#version-18)
  * [End Crystal Generation](#end-crystal-generation)
  * [Bedrock Item](#bedrock-item)

# Introduction

A video explanation of terrain population is in [Panda's Survival #22: End Crystal Generation. Time: 7:00](https://www.youtube.com/watch?v=EeobLrHkfYI&t=420s)

Minecraft has procedural world generation, consisting of two steps. The first step is called *Chunk Generation* and happens as soon as the chunk is loaded for the first time.
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
[Invisible Chunks](#invisible-chunks) allow you to place immovable blocks inside unpopulated chunks at the chunk border. This makes invisible chunks the most powerful method for building contraptions around unpopulated chunks, but this method is not completely self-sufficient, because to create an invisible chunk you need to manipulate terrain population, using flying machines or savestates.

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
Whenever a liquid pocket is placed, the game turns on the [instant tile tick flag](../global-flags.md#instant-tile-ticks) right before placing the liquid pocket, then places the liquid pocket, then updates the liquid pocket, and then turns the instant tile tick flag off again.
If an update suppression occurs while the liquid pocket gets updated, the instant tile tick flag stays on permanently.
Since the instant tile tick flag is on while the liquid pocket gets updated, one can very easily cause an update suppression without requiring thousands of rails.

## Instant Falling

## Invisible Chunks

## Igloo Barrier Block
https://www.youtube.com/watch?v=zQUBR8dSUlA

# getBlockState() to setBlockState() exploits
A getBlockState() call can trigger chunk loading.
Chunk loading can trigger terrain population.
Terrain population triggers setBlockState() calls.

So thanks to terrain population, it is possible for a getBlockState() call to trigger a setBlockState() call during its execution.

## Redstone Power Flag Suppression

## Pulling immovable blocks
A video explanation of pulling immovable blocks is in [Panda's Generating a Pig Spawner in 1.11](https://www.youtube.com/watch?v=cVvB53sWETg).
The most important application of this technique is pulling end gateways to create dataless gateways.

## Beacon threads causing async updates

# Version 1.8

## End Crystal Generation

## Bedrock Item
