# Game Tick ☆

## Table of Contents

- [Introduction](#introduction)
  * [Player Inputs](#player-inputs)
  * [Mob Spawning](#mob-spawning)
  * [Chunk Unloading](#chunk-unloading)
  * [Tile Ticks](#tile-ticks)
  * [Random Ticks](#random-ticks)
  * [Update Chunk Map](#update-chunk-map)
  * [Block Events](#block-events)
  * [Entities](#entities)
  * [Tile Entities](#tile-entities)
  * [Networking Phase](#networking-phase)
- [Immediate Updates](#immediate-updates)

# Introduction

The [main thread](threads.md#main-thread) in minecraft is repeatedly executing gameticks, and under lag-free circumstances waits 0.05 seconds between gameticks.

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
It also processes beacon actions that were scheduled by [stained glass threads](threads.md#stained-glass-threads).
Exceptions that are thrown during this phase are ignored, which leads to [update suppression](update-suppression.md).

Player inputs include: Pressing buttons, flicking levers, placing blocks, instantmining blocks.

If the client-server connection is lag-free, it also includes: Mining blocks, falling on pressure plates.

## Mob Spawning
Mobs spawn around players. This tick phase takes less processing time if mob switches are built.

## Chunk Unloading
All chunks which have previously been scheduled to unload get [unloaded](chunk/chunk.md#unloading).

## Tile Ticks
All scheduled tile ticks get processed.

Blocks using tile ticks for all their scheduled actions include: Repeaters, comparators, redstone torches, observers, [gravity-affected blocks](falling-block/gravity-affected-block.md), liquids, dispensers, droppers.

Blocks that use tile ticks for "resetting" or "unpowering" purposes only include: Buttons, pressure plates, redstone lamps, tripwire, tripwire hooks, frosted ice.

## Random Ticks
Wheat grows.

## Update Chunk Map
The `ChunkMap`, also known in MCP as the `PlayerChunkMap`, gets updated.

- Block modification packets are send.
This means, your client gets informed about all block changes that happened since the last PlayerChunkMap update.
If less than 64 block changes happened in a chunk since the last time these packets were send, these block changes will be send in `BlockUpdateS2CPacket` or `BlocksUpdateS2CPacket` packets.
If more than 64 block changes happened, it will send a `WorldChunkS2CPacket` which sends the whole chunk again.

- Up to 49 ungenerated chunks in view distance of players get generated and [loaded](chunk/chunk.md#loading). This operation stops as soon as it takes more than 50 milliseconds.

## Block Events
All scheduled block events get processed.

Blocks using block events include: Pistons, Beacons, Ender Chests.

## Entities
All non-player entities which are in [entity processing chunks](chunk/chunk.md#entity-processing) get processed.

## Tile Entities
Tickable Tile Entities get processed.

The most interesting tickable tile entities are: Hoppers, Block 36

## Networking Phase
- Players joining or leaving the server get added or removed.

- Every 900 ticks an autosave occurs. During an autosave, all loaded chunks get [saved](chunk/chunk.md#saving), and all non-spawn chunks which are outside of view distance of all players get scheduled to be [unloaded](chunk/chunk.md#unloading).

# Immediate Updates
When a block gets updated it can either perform an action *immediately* or it can *schedule* an action to happen in a certain tick phase.
For example, if you power a repeater it schedules a tile tick to turn on in the tile tick phase in two gameticks.
If you power a piston it schedules a block event to extend in the next block event phase.

But there are also blocks that react immediately to block updates. These blocks include:
- Redstone dust
- Powered rails
- Trapdoors

If the [instant tile tick flag](global-flags.md#instant-tile-ticks) is on, then all tile tick blocks perform their action immediately instead of scheduling it to happen in the tile tick phase.
