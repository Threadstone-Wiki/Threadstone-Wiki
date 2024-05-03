This page is about falling block entities.

# Introduction

When a floating [gravity affected block](gravity-affected-block.md) does [normal falling behavior](gravity-affected-block.md#normal-falling-behavior), it creates a *falling block entity*,
corresponding to the block at its own position.

Usually it is only possible to get falling block entities of sand, red sand, gravel, anvils and dragon eggs. However one can obtain falling block entities of any block in the game by using [falling block swaps](falling-block-swaps.md).

When a falling block entity lands on a slab, or becomes too old without having ground underneath it, it drops its block as an item, if such an item exists. This makes falling block entities the most important source of [unobtainable items](../unobtainables.md#items).

Some unobtainable falling block entities are interesting even if their block does not have an item. An overview of such interesting unobtainable falling blocks is at [here](../unobtainables.md#falling-block-entities).

# Age

Every falling block entity has an *age*, which is a number specifying how often the falling block entity has been processed.

Just like every other entity, when the falling block entity is in [entity processing chunks](../chunk/chunk.md#entity-processing), it gets processed once per gametick during the [entity phase](../tick-phases.md#entities).
Every time it gets processed it increases its age by 1.

At certain ages the behavior of the falling block can change:

- Unticked: When a falling block entity gets processed for the first time, so that its age reaches 1, then it checks whether the id of the block at its position matches the id of the block of the falling block entity.
If the block ids match, then the block gets deleted. If they do not match, the falling block entity gets deleted.

- Young: If a falling block entity is less than 30 seconds old it can fall normally.

- Old: If a falling block entity is more than 30 seconds but less than 3.4 years old, it needs to be on ground whenever it gets processed, or it will die.

- Overflowed: If a falling block entity is more than 3.4 years but less than 6.8 years old, its age overflows and becomes negative. The falling block entity can fall normally again, just like a young falling block. After 6.8 years it becomes unticked again.


# Preservation and Transportation

To effectively use unobtainable falling blocks that drop no item, one needs to preserve and transport them.

Falling Block Transportation methods for 1.12 are disccused in [Falling Block Episode 8, at 19:16](https://www.youtube.com/watch?v=-4UhrzA-F2g&t=1156s).

The basics of Falling Block Transportation in recent versions are discussed in the video [What to do with your falling blocks from 1.12 after updating to 1.18](https://www.youtube.com/watch?v=l9aw_Db_4q4).

In 1.12 to preserve an old falling block entity, it needs to be on ground, and either the block below the center of the falling block entity needs to be air, or the center of the falling block entity needs to be in block 36.
If a falling block entity is on top of a cobble wall or fence, it is on ground and considers the block below itself to be air. This makes walls and fences an excellent way to preserve old falling block entities.

![Falling Block on Wall](/images/FallingBlockOnWall.PNG)

In 1.12 falling block entities can also be stored on the edge of a block, as long as the center of the falling block remains above air.

Falling block entities on walls or block edges can be moved by using fishing rods. This transportation method is very efficient, because the falling block entities do not have any ground frictions as long as their center is above air.

In recent versions to preserve an old falling block entitiy, it needs to be on ground and inside block 36. The easiest way to do this, is to have a piston clock pushing rails or trapdoors back and forth, and storing the falling block in the block 36 of those rails or trapdoors.
In recent versions, old falling block entities on ground will always have ground friction, even if the block underneath them is air, which makes fishing rods less efficient.

Falling Block Entities cannot use nether portals. Falling Block Entities in the nether can be transported to other dimensions by using end portals.
Falling Block Entities outside the nether cannot enter the nether.

![Dimension Diagram](/images/FallingBlockDimensionTravel.PNG)


## Into the End
When a falling block arrives in the end, it will be placed above the center of the obsidian platform with a one block gap.

<img src="/images/FallingBlockToEnd.PNG" width="380" height="300">  

In 1.12, a falling block arriving at the obsidian platform will not regenerate the obsidian platform. Only a player causes the obsidian platform to regenerate.

To keep the falling block alive after it arrives in the end one can use the following methods:

1. Push it away from the obsidian platform, while it is in [lazy chunks](../chunk/chunk.md#entity-processing). This method is shown in [Falling Block Episode 8, 25:58](https://www.youtube.com/watch?v=-4UhrzA-F2g&t=1558s).
2. Generate a block 36 at the location where the falling block arrives, and have a block below the block 36. Block 36 can be generated using [Smokeydog's Tile Entity Data Deletion](https://www.youtube.com/watch?v=WlYPR-Z_dyg). After the falling block arrives, it can be transported out of the falling block by using fishing rods.
3. Use an instant triple piston extender to instantly push a block below the falling block when it arrives, and also generate a block 36 (with tile entity data) where the falling block arrives. A contraption for this has been built by Skye and can be found in the world download from the video [End Portal Line Placer](https://www.youtube.com/watch?v=Azz04fiD_Jg).

## Out of the End

A falling block going from the end to the overworld will be placed directly on top of the highest solid block at the world spawn location.
The falling block can easily be kept alive by having a block 36 above that solid block. Block 36 can be generated using [Smokeydog's Tile Entity Data Deletion](https://www.youtube.com/watch?v=WlYPR-Z_dyg).

## Through End Gateways
When a falling block goes through an end gateway, it will search for the highest solid non-bedrock block that is less than 5 blocks away in x and z direction, and place the falling block 0.5 blocks above that solid block.

<img src="/images/FallingBlockThroughGateway.PNG" width="400" height="270">  <img src="/images/PistonSlabClock.png" width="550" height="270"> 

The falling block can be kept alive by having a piston clock pushing slabs back and forth above the solid block above which the falling block arrives.
The falling block will then arrive right on top of the block 36 of the slabs, so it will be on ground and inside block 36.


# Duplication

## Savestates

In 1.12 falling block entities can be duplicated using [savestates](../chunk/savestate.md). However the duplicated falling blocks will have the exact same UUID as the original falling blocks, which may render some of them [invisible](../chunk/savestate.md#duplicated-entity-uuids).

## End Portal Duping
One can use end portals to place down a falling block, while still keeping the falling blocks.
This is discussed in the video [What to do with your falling blocks from 1.12 after updating to 1.18, at 3:16](https://www.youtube.com/watch?v=l9aw_Db_4q4&t=196s). It works both in 1.12 and in recent versions. A server that has already updated to recent versions and which has end portal frame items and an unobtainable falling block can re-use the unobtainable falling block infinitely many times.


