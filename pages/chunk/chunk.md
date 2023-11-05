Space in Minecraft is separated into Chunks
The following mechanics are related to chunks.

# Terminology

Usually the word *chunk* refers ambiguously to two very different things:
- It refers to an object of what Ornithe calls the `WorldChunk` class, and what MCP calls the `Chunk` class.
- It refers to a certain kind of 16 x ∞ x 16 region in 3-dimensional space.

In this article we will use the word "chunk" only in the second sense, to refer to certain specific regions of 3-dimensional space. We use the word "`Chunk` instance" to refer to the object of the java class.
So a "chunk" is not a java object on your computer, it is an abstract mathematical object.

Definition: A *chunk* is an infinite rectangular cuboid in 3-dimensional space with the following properties:
- It extends 16 units along the x axis, 16 units along the z axis, infinite unites along the y axis
- Its 4 edges all have integer x and z coordinates
- The integer x and z coordinates of the edges are divisible by 16

Given a chunk, the x, z coordinates of the edge with the lowest x, z coordinates, are called the *block coordinates* of the chunk.
The *chunk coordinates* of a chunk are defined as the *block coordinates* of the chunk divided by 16.
Note that the *chunk coordinates* of a chunk are always integers. 

In minecraft there is a [chunk hashmap](chunk-hashmap.md), which stores `Chunk` instances, and associated to each `Chunk` instance it contains an x and z coordinate.

If we have minecraft running on a computer, then a chunk with chunk coordinates x and z is called *loaded* with respect to that minecraft instance, if in the chunk hashmap in that minecraft instance there is a `Chunk` instance with the same x and z coordinates as our chunk.
Otherwise the chunk is called *unloaded* with respect to that minecraft instance.

When a chunk gets unloaded and then loaded again, it usually is associated to a different `Chunk` instance. But it is still the same chunk.
Similarly, if you close your minecraft game, and re-open it again, all `Chunk` instances will be different, but all chunks stay the same chunks they were before.

In minecraft all blocks and entities have x, y, z coordinates, with which they can be mapped into 3-dimensional space.
So if we have a block or entity in minecraft and we have a chunk, we can talk about whether the block or entity is in the chunk or not.


# Loading

An unloaded chunk becomes loaded in the following situations:
- If the game requests any kind of information from the chunk, or tries to make any kind of change in the chunk, then the chunk is loaded [immediately](../tick-phases.md#immediate-updates). This happens for example every time the game does a `setBlockSate` or `getBlockState` call at a position contained in the chunk.
- If the chunk comes within view distance of a player, and the chunk is already generated on disk, then the chunk is loaded immediately.
- If the chunk comes within wiew distance of a player, but the chunk is not yet generated, then the chunk is scheduled to be generated and loaded in some future [chunk map phase](../tick-phases.md#update-chunk-map).





When a chunk is loaded it immediately checks whether a [terrain population](population.md) should occur, and immediately executes it if it should occur.

## Chunk swaps: Loading already loaded chunks
## Overworld RNG manipulation via Chunk Loading

# Unloading

## Saving
After a chunk is unloaded, it gets saved to disk.
Chunk saving can be prevented using [chunk savestates](savestate.md).

# Entity-processing

A chunk which is in the center of a 5x5 grid of loaded chunks is called an *entity-processing chunk*.
A chunk which is not an entity-processing chunk is called a *lazy chunk*.

Entities only get processed in entity-processing chunks.
Falling Blocks only create falling block entities in entity-processing chunks,
and do instantfalling in non-entity-processing chunks.
