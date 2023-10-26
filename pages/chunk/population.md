# Terrain Population

- [Introduction](#introduction)
- [Manipulating Population](#manipulating-population)
  * [Flying machines](#flying-machines)
  * [Savestates](#savestates)
  * [Invisible Chunks](#invisible-chunks)
- [Population RNG](#population-rng)
- [Population Suppression](#population-suppression)
  * [Instant Tile Ticks](#instant-tile-ticks)
  * [Instant Falling](#instant-falling)
  * [Invisible Chunks](#invisible-chunks-1)
  * [Igloo Barrier Block](#igloo-barrier-block)
- [getBlockState() to setBlockState() exploits](#getblockstate---to-setblockstate---exploits)
  * [Redstone Power Flag Suppression](#redstone-power-flag-suppression)
  * [Pulling immovable blocks](#pulling-immovable-blocks)
  * [Beacon threads causing async updates](#beacon-threads-causing-async-updates)

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
The following methods allow changing unpopulated chunks

## Flying machines
One can build flying machines that place blocks near ungenerated chunks, and change the population of those chunks.

For example, Panda4994, KaboPC and DanielKotes used flying-machine-like Kobra contraptions in 1.7, a version before slimeblocks existed,
to generate a quadruple skeleton spawner in a player-built structure: https://www.youtube.com/watch?v=9iaU1TvIQqM

## Savestates
[Savestates](savestate.md#unpopulated-chunks) are the most important method for building contraptions next to unpopulated chunks.

## Invisible Chunks
[Invisible Chunks](#invisible-chunks) allow you to place immovable blocks inside unpopulated chunks at the chunk border. This makes invisible chunks the most powerful method for building contraptions around unpopulated chunks, but this method is not completely self-sufficient, because to create an invisible chunk you need to manipulate terrain population, using flying machines or savestates.

An explanation of what exactly invisible chunks are and how to create them is below in the section on Population Suppression.

# Population RNG
To effectively exploit terrain population one needs to predict where it will place blocks.
Every dimension has a random number generator that is used solely for chunk generation and terrain population. We will call this the *population RNG* of the dimension.

In the overworld the population RNG is seeded at the beginning of every chunk generation and terrain population.
In the nether and end the population RNG is seeded at the beginning of every chunk generation, but it does not get seeded during terrain population. ( https://www.youtube.com/watch?v=nqyILYLu1Zo )
The blocks that get placed during a terrain population in the nether or end depend not only on the blocks in the area, but also on which chunk last got generated in the dimension.

Even when population is consistent, like in the overworld, it can drastically change when a few blocks are changed in the population area.
It is often important to predict liquid pocket locations, because they enable instant tile ticks.
Liquid pockets locations are highly volative, because they get generated near the end of terrain population, and their location depends on how many random calls were made during all other parts of terrain population.
For example replacing one grass block visible from the sky by a stone block in the population area will change how many random calls the tree and tallgrass generators make, and this then changes the liquid pocket locations.
The best way to keep liquid pocket locations consistent is to make sure that the same blocks remain visible from the sky. Beyond that a bit of trial and error is inevitable.

# Population Suppression
One can cause an [update suppression](../update_suppression.md) in the middle of a terrain population to cause additional powerful exploits.
This is also covered in [Falling Block Episode 1](https://www.youtube.com/watch?v=KU3lN1IUhuE).

## Instant Tile Ticks

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
https://www.youtube.com/watch?v=cVvB53sWETg

## Beacon threads causing async updates
