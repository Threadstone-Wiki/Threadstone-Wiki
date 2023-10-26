This page is about terrain population

# Introduction

Minecraft has procedural world generation, consisting of two steps. The first step is called "Generation" and happens as soon as the chunk is loaded for the first time.
The second step is called "Terrain Population" and only occurs if the 2x2 grid of chunks consisting of the chunk and the chunks adjcaent to it in +x and +z direction are also loaded.

Generation places in the overworld things like bedrock, dirt, grass, stone, in oceans water.
Terrain population places things like trees, dungeons, ores, small caves and lakes, liquid pockets, and all structures, like strongholds, villages, mineshafts and so on.

Chunks which have been generated but not yet populated are called unpopulated chunks.

When a chunk is populated, most of the blocks that get placed are placed in the 16x16 area that is 8 block offset in +x and +z direction from the chunk itself.
This means the blocks mostly get placed in the 16x16 area which is at the center of the 2x2 grid of chunks that need to be loaded for the population to occur.

For example if we have 3 unpopulated chunks like on the left in the picture below, and we generate another chunk completing the 2x2 grid,
then the chunk on the bottom left of the 2x2 grid will become populated, and new blocks will be placed in the center of the 2x2 grid.

![Population image](/images/Population.PNG)

This system allows the game to easily place trees and dungeons on chunk borders without cutting them off.

The player can interact with chunks that have been generated but not yet populated, and this can change what blocks get placed during population.
The following methods allow changing unpopulated chunks

## Flying machines
One can build flying machines that place blocks near ungenerated chunks, and change the population of those chunks.

For example, Panda4994, KaboPC and DanielKotes used flying-machine-like Kobra contraptions in 1.7, a version before slimeblocks existed,
to generate a quadruple skeleton spawner in a player-built structure: https://www.youtube.com/watch?v=9iaU1TvIQqM

## Savestates

## Invisible Chunks

# Population Suppression

## Instant Tile Ticks

## Instant Falling

## Invisible Chunks

## Igloo Barrier Block

# GetBlockState() to SetBlockState() exploits

## Pulling immovable blocks

## Redstone Power Flag Suppression

## Beacon threads causing async updates
