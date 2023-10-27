This page is about gravity-affected blocks.

# Introduction
Sand, gravel, anvils and dragon eggs are gravity-affected blocks. Dragon eggs are an unusual gravity-affected block because they do not inherit from the FallingBlock class from which sand, gravel and anvils inherit. 

When a gravity-affected block is updated it schedules a tile tick.

When the tile tick gets processed it will check whether it is floating, in the sense that it has air, fire, water or lava underneath itself.

It it is not floating, it will do nothing. Otherwise it continues as follows:

The gravity-affected block will check whether it is in an [entity-processing chunk](chunk/chunk.md#entity-processing).

If it is in an entity-processing chunk and the [instant falling flag](global-flags.md#instant-falling) is off, then it will do normal falling behavior.

If it is in a lazy chunk or if instant falling is on, then it will do instantfalling behavior.

# Normal falling behavior
When a gravity-affected block does normal falling it will do a getBlockState() call at its position and create a falling block entity of the blockstate it finds.
Usually the blockstate will be the gravity-affected block itself. The rare exceptions are called [falling block swaps](falling-block-swaps.md).

When the falling block entity gets processed for the first time, it will check whether the block at its position matches its own block id.
If the block id does match, then the falling block entity will delete the block at its position.
If it does not match, the falling block entity will delete itself.

Here the falling block entity only checks the block id, and does not check the damage value.
This makes it possible to make sand converters that [convert white sand into red sand, and repair anvils](double-tile-tick-scheduling.md#anvil-repairing).

When [instant-tile-ticks](global-flags.md#gravity-affected-blocks) are on, one can create hundreds of falling block entities by updating a gravity-affected block hundreds of times in one gametick.
But under normal circumstances, all except one of those falling block entities will delete themselves the first time they get processed.

# Instantfalling behavior
When a gravity-affected block does instant falling, it will replace itself by air, then check the blocks below itself one by one until it finds a block which is neither air, fire, water nor lava, and then places itself directly above that block.
The damage value of the gravity-affected block will be reset to 0. So red sand becomes sand and damaged anvils become repaired anvils.

Dragon eggs have a bug in their code which makes it so that after instantfalling they land one block lower than they are supposed to, and delete whatever block was previously in that position.
This can be used to break bedrock, except at y=0. If a dragon egg reaches y=1 it will simply disappear and not delete the block at y=0.

If [instant-tile-ticks](global-flags.md#instant-tile-ticks) are on, instantfalling dragon eggs are one of the most powerful ways to immediately delete a block.


