Space in Minecraft is separated into Chunks
The following mechanics are related to chunks.

# Terminology

A *chunk* is a region in 3-dimensional space. It is 16 units long along the x and z axis, and infinitely long along the y axis.
The four edges of a chunk have integer x and z coordinates which are divisible by 16.

The x and z coordinates of the edge with the least coordinates, are called the *block coordinates* of the chunk.
The block coordinates divided by 16 are called the *chunk coordinates* of the chunk.

In minecraft there is a class called `Chunk` in MCP and `WorldChunk` in Ornithe.
Instances of these classes will be called "`Chunk` instances".

In minecraft there is a [chunk hashmap](chunk-hashmap.md), which stores `Chunk` instances, and associates to each stored `Chunk` instance an x and z coordinate.

A chunk with chunk coordinates x and z is called *loaded* if the chunk hashmap in minecraft contains a `Chunk` instance with the same x and z coordinates as our chunk.
Otherwise the chunk is called *unloaded*.

# Chunks in View Distance

There is a class called the `PlayerChunkMap` in MCP and the `ChunkMap` in Ornithe feather mappings, which keeps track which chunks are in view distance of players.

The `PlayerChunkMap` stores a position for each player. When a player logs on the server, this position is equal to the position where the player is actually at.

The position the player has in the `PlayerChunkMap` gets immediately updated if the player moves more than 8 blocks away from the previous position he had in the `PlayerChunkMap`.

Whenever the position of a player in the `PlayerChunkMap` is updated, it also immediately updates what chunks are within view distance of the player.

If an already generated chunk comes within view distance of a player it gets immediately [loaded](#loading).

If a chunk gets outside of view distance of all players it gets scheduled to be [unloaded](#unloading).


# Loading

An unloaded chunk becomes loaded in the following situations:
- If the game requests any kind of information from the chunk, or tries to make any kind of change in the chunk, then the chunk is loaded [immediately](../tick-phases.md#immediate-updates). This happens for example every time the game does a `setBlockSate` or `getBlockState` call at a position contained in the chunk.
- If the chunk comes within view distance of a player, and the chunk is already generated on disk, then the chunk is loaded immediately.
- If the chunk comes within wiew distance of a player, but the chunk is not yet generated, then the chunk is scheduled to be generated and loaded in some future [chunk map phase](../tick-phases.md#update-chunk-map).

When a chunk is loaded it immediately checks whether a [terrain population](population.md) should occur, and immediately executes it if it should occur.

In the overworld and nether, loading a chunk seeds the world RNG, which enables [easy world RNG manipulation](https://www.youtube.com/watch?v=JAc0DAZRSGI).

## Chest Grids

When a chunk with a chest gets loaded, then in the next tile entity phase, the chest will check blocks around itself to determine whether it is part of a double chest.
If the chest was on a chunk border it will load the chunk on the other side of the chunk border.

This makes chest one of the cheapest ways to load large amounts of chunks.

# Unloading

A chunk gets scheduled to be unloaded in the following situations:
- The chunk gets outside of view distance of a player, and is not in view distance of any other player.
- An autosave occurs, while the chunk is not within view distance of any player.

If a chunk is scheduled to be unloaded, and any chunk access it made to that chunk, for example through a `getBlockState` or `setBlockState` call in that chunk,
then the unloading of that chunk gets canceled.

In every tick in the [unload phase](../tick-phases.md#chunk-unloading) the game unloads up to 100 chunks that were scheduled to be unloaded.

Chunk Unloading can be prevented using permaloaders.

## Gnembon Permaloader

The 100 chunk unload limit can be exploited to build a permaloader.

An excellent explanation of permaloaders is in gnembon's video [Permanent and Remote Chunk Loading with Perma-Loader in Minecraft](https://www.youtube.com/watch?v=JAc0DAZRSGI).

The permaloader consists of 100 chunks on a world diagonal, that get automatically reloaded after every autosave.

The order in which chunks get unloaded is in such a way, that chunks on the world diagonal always get unloaded before other chunks. So if one has 100 chunks loaded on a world diagonal, one can prevent all chunks outside the world diagonal from unloading for one gametick.

While a permaloader is active, chunk unloading can be prevented in any other chunks outside the world diagonal, by placing a tickable tile entity like an unlocked hopper in the chunk. If an autosave occurs, the chunk will be scheduled to be unloaded. In the first unload phase after the autosave, the game will unload 100 chunks on the world diagonal in the permaloader. In the first tile entity phase after that, the unloading of the chunk with the hopper gets canceled, so the chunk stays permanently loaded.

## Mini Permaloader

Mini Permaloaders are shown in Xcom's video [Mini Permaloader](https://www.youtube.com/watch?v=JAc0DAZRSGI).

One can prevent the unloading of a single chunk without requiring a full gnembon permaloader, by using stained glass and beacons to cancel the unloading of the chunk in the [player phase](../tick-phases.md#player-inputs).

# Saving
After a chunk is unloaded, it gets saved to disk.

Whenever an autosave occurs, all loaded chunks get saved to disk.

Chunk saving happens on a seperate thread called the [Chunk Saving Thread](../threads.md#chunk-saving-thread).

Chunk saving can be prevented using [chunk savestates](savestate.md).

# Entity-processing

A chunk which is in the center of a 5x5 grid of loaded chunks is called an *entity-processing chunk*.
A chunk which is not an entity-processing chunk is called a *lazy chunk*.

Entities only get processed in entity-processing chunks.
[Gravity-affected blocks](../falling-block/gravity-affected-block.md) only create [falling block entities](../falling-block/falling-block-entity.md) in entity-processing chunks,
and do [instantfalling](../falling-block/gravity-affected-block.md#instantfalling-behavior) in non-entity-processing chunks.
