This page is about falling block entities.

# Introduction

When a floating [gravity affected block](gravity-affected-block.md) does [normal falling behavior](gravity-affected-block.md#normal-falling-behavior), it creates a *falling block entity*,
corresponding to the block at its own position.

Usually it is only possible to get falling block entities of sand, red sand, gravel, anvils and dragon eggs. However one can obtain other falling block entities using [falling block swaps](falling-block-swaps.md).

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


# Preserving and Transporting

Falling Block Entities cannot use nether portals. Falling Block Entities in the nether can be transported to other dimensions by using end portals.
Falling Block Entities outside the nether cannot enter the nether.

![Dimension Diagram](/images/FallingBlockDimensionTravel.PNG)


## Into the End
When a falling block arrives in the end, it will be placed above the center of the obsidian platform with a one block gap.

<img src="/images/FallingBlockToEnd.PNG" width="380" height="300">  

In 1.12, a falling block arriving at the obsidian platform will not regenerate the obsidian platform. Only a player causes the obsidian platform to regenerate.

## Out of the End

In 1.12, a falling block going from the end to the overworld will be placed directly on top of the highest solid block at the world spawn location.
The falling block can easily be kept alive by having a block 36 above that solid block.

## Through End Gateways
When a falling block goes through an end gateway, it will search for the highest solid non-bedrock block that is less than 5 blocks away in x and z direction, and place the falling block 0.5 blocks above that solid block.

<img src="/images/FallingBlockThroughGateway.PNG" width="400" height="270"> 
