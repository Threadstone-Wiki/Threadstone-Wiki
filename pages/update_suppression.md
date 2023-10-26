This is a page about update suppression.
To understand update suppression one should first know about tick phases.

# Introduction

1. **If an exception is thrown in the player phase of a tick, the game just ignores the exception and continues running instead of shutting down the server.**

2. **One can easily cause StackOverflow exceptions using long chains of immediate block updates.**

An *update suppressor* is a contraption that immediately causes a StackOverflow exception when its input block receives a block update.
Update suppressors that do not exploit instant tile ticks usually use several thousand rails to do this.

![Picture of Update Suppressor](/images/UpdateSuppressor.PNG)

An update suppressor can be used during the player phase to create blocks in invalid states, e.g. nether portals without a valid obsidian frame around them.

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
