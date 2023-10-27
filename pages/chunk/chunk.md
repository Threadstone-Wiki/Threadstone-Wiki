Space in Minecraft is separated into Chunks
The following mechanics are related to chunks.

# Loading
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
