Time in minecraft's [main thread](threads.md#main-thread) is separated into gameticks.

# Introduction

In every gametick the following phases happen in the following order:

1. Player Inputs
2. Mob Spawning
3. Chunk Unloading
4. Tile Ticks
5. Random Updates
6. Block Modification Packets
7. Block Events
8. Entities
9. Tile Entities
10. Networking Phase

This is not a complete list, but includes the most important phases.

If multiple dimensions are loaded,
then in each tick the game first does the following things:

1. Player Inputs in all dimensions
2. Tick-Phases 2-9 in the Overowlrd
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

## Random Updates
Wheat grows.

## Block Modification Packets
Your client is informed about all block changes that happened since the last block modification packet phase.

## Block Events
All scheduled block events get processed.

Blocks using block events include: Pistons, Beacons, Ender Chests.

## Entities
Entities get processed.

## Tile Entity
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
