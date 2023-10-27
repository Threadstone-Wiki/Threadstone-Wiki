This page is about global boolean flags that can be turned on or off.

![global flags](../images/GlobalFlags.png)


# Instant Tile Ticks
Instant Tile Ticks can be turned on by [update suppressing the population of a liquid pocket](chunk/population.md#instant-tile-ticks).
Instant Tile Ticks are sometimes abbreviated as ITT.

When ITT is on all tile tick blocks perform their actions [immediately](tick-phases.md#immediate-updates) when they get updated, instead of scheduling their action for the [tile tick phase](tickphases.md#tile-ticks).

There are many ways for the game to crash while ITT is on. The following things cause crashes:

- Non-player entities stepping on non-floating pressure plates
- Non-player entities stepping on string
- Minecarts driving on detector rails
- Frosted ice being created with light level 11 or lower

Wooden pressure plates are naturally generated in villages.
Before turning on ITT it is recommended to remove all wooden pressure plates from all villages in loaded chunks, to prevent villagers from randomly crashing the server.

Some interesting behaviors of blocks on ITT include the following.

## Observers
If an observer is updated it turns on and off and then sends out block updates.

If a chain of n observers is updated, then the last observer of the chain sends out 2^n block updates.
The stacksize required for this is proportional to n.
This makes it possible to create exponential lag without running into a stack overflow.
The block updates at the end of the observer chain are send out at a very high frequency.
This makes observer chains very useful async lines.

## Dispensers
When an unpowered dispenser receives power and is updated it will do the following things:
1. Use one of the items in the dispenser.
2. Decrement the used item stack / Damage flint and steel / Replace liquid bucket by empty bucket
3. Replace the dispenser block by a powered dispenser block.

The fact that the dispenser uses the item before it replaces itself by a powered block and before it decrements the item stack can cause dispensers to be stuck in update suppressing loops.

A dispenser containing only a liquid bucket will place the liquid in front of itself. The placement of the liquid sends block updates. One of the block updates goes to the dispenser.
Since the dispenser is still an unpowered dispenser block and since the bucket it contains still contains the liquid, the disepsner will place the liquid again in a loop, and cause an update suppression.

A similar loop and update suppression occurs if the dispenser has a flint and steel and a fire block cannot survive on the block directly in front of the dispenser.
The dispenser will create a fire block in front of itself. Since the fire block cannot survive, it will instantly delete itself, update the dispenser and create a loop.
In this loop the durability of the flint and steel never gets decreased.

## Falling Blocks
If ITT is on and instant falling is off, updating a floating falling block in an [entity-processing chunk](chunk/chunk.md#entity-processing) will create a falling block entity.
Updating the falling block multiple times in a single gametick will create multiple falling block entities.
Once the falling block entities get processed, each will check whether it is in a falling block of the corresponding type.
If it is, it deletes the falling block and survives.
If it is not, the falling block entity will delete itself.

So if multiple falling block entites get created, usually all but one of the falling block entities will delete themselves when they first get processed.

A falling block which gets updated in a [lazy chunk](chunk/chunk.md#entity-processing) will do instantfalling behavior.

# Instant Falling
Instant Falling is turned on whenever a [population is update suppressed](chunk/population.md#instant-falling).

If a falling block processes a tile tick while instant falling is on, or is updated while instant falling and ITT is on, then it will do the instantfalling behavior.
It will replace itself by air, then check the blocks below itself one by one until it finds a block which is neitehr air, fire, water nor lava, and then places itself directly above that block.

Dragon eggs that do instantfalling land one block to low and delete the block on which they were supposed to land. This can be used to delete bedrock.




# Redstone Power Flag
