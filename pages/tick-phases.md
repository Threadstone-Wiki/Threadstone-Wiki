Time in minecraft's [main thread](threads.md#main-thread) is separated into gameticks.

# Introduction

In every gametick the following phases happen in the following order:

1. [Player Inputs](#player-inputs)
2. [Mob Spawning](#mob-spawning)
3. [Chunk Unloading](#chunk-unloading)
4. [Tile Ticks](#tile-ticks)
5. [Random Ticks](#random-ticks)
6. [Update Chunk Map](#update-chunk-map)
7. [Block Events](#block-events)
8. [Entities](#entities)
9. [Tile Entities](#tile-entities)
10. [Networking Phase](#networking-phase)

This is not a complete list, but includes the most important phases.

If multiple dimensions are loaded,
then in each tick the game first does the following things:

1. Player Inputs in all dimensions
2. Tick-Phases 2-9 in the Overworld
3. Tick-Phases 2-9 in the Nether
4. Tick-Phases 2-9 in the End
5. Networking Phase in all dimensions

## Player Inputs
The game processes all the packets that it received from players since the last player phase.
It also processes beacon actions that were scheduled by stained glass threads.
Exceptions that are thrown during this phase are ignored, which leads to [update suppression](update-suppression.md).

Player inputs include: Pressing buttons, flicking levers, placing blocks, instantmining blocks.
If the client-server connection is lag-free, it also includes: Mining blocks, falling on pressure plates.

## Mob Spawning
It spawns mobs. This tick phase takes less processing time if mob switches are built.

## Chunk Unloading
All chunks which have previously been scheduled to unload get unloaded.

## Tile Ticks
All scheduled tile ticks get processed.
Blocks using Tile Ticks include: Repeaters, comparators, observers, redstone torches, gravity-affected blocks, liquids, and many more.

## Random Ticks
Wheat grows.

## Update Chunk Map
The `ChunkMap`, also known in MCP as the `PlayerChunkMap`, gets updated.

- Block modification packets are send.
This means, your client gets informed about all block changes that happened since the last PlayerChunkMap update.
If less than 64 block changes happened in a chunk since the last time these packets were send, these block changes will be send in `BlockUpdateS2CPacket` or `BlocksUpdateS2CPacket` packets.
If more than 64 block changes happened, it will send a `WorldChunkS2CPacket` which sends the whole chunk again.

- Up to 49 ungenerated chunks in view distance of players get generated and loaded. This operation stops as soon as it takes more than 50 milliseconds.

## Block Events
All scheduled block events get processed.

Blocks using block events include: Pistons, Beacons, Ender Chests.

## Entities
Entities get processed.

## Tile Entities
Tickable Tile Entities get processed.
Tickable Tile Entities include: Hoppers, Block 36.

## Networking Phase
????

# Immediate Updates
When a block gets updated it can either perform an action *immediately* or *schedule* an action to happen in a certain tick phase.
If you power a repeater it schedules an action to happen in the tile tick phase.
If you power a piston it schedules an action to happen in the block event phase.

Blocks which react immediately to block updates include: Redstone dust, powered rails.

If the [instant tile tick flag](global-flags.md) is on, then all tile tick blocks perform their action immediately instead of scheduling it to happen in the next tile tick phase.
