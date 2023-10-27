This is a page about update suppression.

An excellent video explanation is [Xcom's Update Suppression Explained](https://www.youtube.com/watch?v=IJhZpK-8p54)

# Introduction

1. **If an exception is thrown in the [player phase](tick-phases.md) of a tick, the game just ignores the exception and continues running instead of shutting down the server.**

2. **One can easily cause StackOverflow exceptions using long chains of [immediate block updates](tick-phases.md#immediate-updates).**

An *update suppressor* is a contraption that immediately causes a StackOverflow exception when its input block receives a block update.
Update suppressors that do not exploit instant tile ticks usually use several thousand rails to do this.

![Picture of Update Suppressor](/images/UpdateSuppressor.PNG)

An update suppressor can be used during the player phase to create blocks in invalid states, e.g. nether portals without a valid obsidian frame around them.
It can also be used to duplicate items, and convert wither skeleton skulls into normal skeleton skulls.

Update suppression was discovered by Panda4994 and RedstoneSpire and published in the video [Update Suppression, Block Duplication, Skull Converter and More](https://www.youtube.com/watch?v=mzfLHNeqjuY).

# Floating Comparator Trick

While it is easy to suppress comparator updates when [instant tile ticks](global-flags.md#instant-tile-ticks) are on, it is also possible to update suppress comparator updates without using instant tile ticks by using a floating comparator. If a floating comparator receives a comparator update, it immediately pops off and sends out normal block updates which an be send to an update suppressor.
That same update suppressor can also be used to previously create that floating comparator.

Applications of the floating comparator trick include:

- [Tile Entity Swaps](#tile-entity-swap)
- [Item Shadowing](#item-shadowing)
- [Igloo Barrier](chunk/population.md#igloo-barrier-block)
- [1.13 Instant Falling Flag](https://www.youtube.com/watch?v=CfMSatbWyfo)

# Applications

## Population Suppression
See [Terrain Population](chunk/population.md)

## Tile Entity Swap
https://www.youtube.com/watch?v=EpTaffAuVz4

Applications of Tile Entity Swaps include:

- Class Cast Suppressors

- Population method for Falling Barrier

## Multiply by 8 teleportation
https://www.youtube.com/watch?v=091mFU3d8m0&t=558s

## Item Shadowing
https://www.youtube.com/watch?v=mTeYwq7HaEA
