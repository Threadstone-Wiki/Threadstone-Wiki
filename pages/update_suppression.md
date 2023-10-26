This is a page about update suppression.
To understand update suppression one should first know about tick phases.

# Introduction

1. ** If an exception is thrown in the player phase of a tick, the game just ignores the exception and continues running instead of shutting down the server. **

2. ** One can easily cause StackOverflow exceptions using long chains of immediate block updates. **

An *update suppressor* is a contraption that immediately causes a StackOverflow exception when its input block receives a block update.

An update suppressor can be used during the player phase to create blocks in invalid blockstates.

# Applications

## Population Suppression
See [Terrain Population][chunk/population.md]

## Multiply by 8 teleportation
## Tile Entity Swap
## Item Shadowing
