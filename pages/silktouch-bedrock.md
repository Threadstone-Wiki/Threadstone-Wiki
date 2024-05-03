This page is about silk touch mining bedrock blocks.

# Introduction

Bedrock blocks usually cannot be mined. 

But if one *instantmines* a block and replaces the block during a specific part of the code by a bedrock block, then the bedrock block gets instantmined.

The only feasible method for doing this is to use *fire extinguishing*:
When one instantmines a block and there is fire in front of the block, then one will first exitnguish the fire and then instantmine the block.
The fire extinguishing happens in the crucial part of the code, in which replacing the block by a bedrock block causes the bedrock to be mined. The fire extinguishing trick was discovered by landmining and explained in Earthcomputer's video [[1.12] How to get the Bedrock Item](https://www.youtube.com/watch?v=YHdSpO-Gsvc).

If one mines a bedrock block it usually drops no item.
But if one mines a bedrock block using a *silk touch* pickaxe, then in 1.12 the bedrock block drops as an item.
This is explained in Earthcomputer's video [[1.12] The Quest for the Bedrock Item](https://www.youtube.com/watch?v=l988pdLw8O4).

If one can use the block updates from fire existinguishing to [immediately](tick-phases.md#immediate-updates) replace an instantmineable block by a bedrock block,
then one can obtain the bedrock item in 1.12 without having to perform a [falling block swap](falling-block/falling-block-swaps.md).

There are 2 methods for immediately replacing an instantmineable block by a bedrock block:
- We can trigger a [terrain population](chunk/population.md) that generates an [end gateway](gateway.md) one of whose bedrock blocks replaces the instantmineable block. This is the [Classical Population Method using End Gateways](#classical-population-method-using-end-gateways)
- If the instantmineable block is a powered rail, we can repeatedly activate and deactivate the rail, and use [hashmap word tearing](word-tearing.md#hashmap-word-tearing) to convert it into bedrock. This is the [Async Method using Hashmap Word Tearing](#async-method-using-hashmap-word-tearing)

# Classical Population Method using End Gateways

The End Gateway population method for silk touch mining bedrock is explained in Earthcomputer's video [[1.12] How to get the Bedrock Item](https://www.youtube.com/watch?v=YHdSpO-Gsvc) and in Xcom's video [How to get a bedrock item in survival 1.9 to 1.12](https://www.youtube.com/watch?v=ajUea-FnRrc).

At the start we have a slimeblock within the population area of an ungenerated chunk in the end, and fire in front of the slimeblock. The chunk is chosen, such that populating the chunk generates an end gateway that replaces the slimeblock by bedrock.
We then perform he following steps within the [entity phase](tick-phases.md#entities) of a single gametick:

1. We use TNT to give the player a lot of motion.
2. We use TNT to shoot an ender pearl as close to the slimeblock as one can get while still being in entity-processing chunks.
3. When the ender pearl arrives, the ungenerated chunk will be within view distance of the player. Since the chunk is ungenerated, it will not be loaded immediately, but only get [scheduled to be loaded later](chunk/chunk.md#loading). This is why we use an ungenerated chunk instead of merely a generated unpopulated chunk.
4. After the ender pearl arrives, we use TNT to create a large lag spike.
5. During the lag spike, the player walks towards the slimeblock. Usually a player can only walk 10 blocks in a single gametick, because of anti-cheat measures, but if the player has a lot of motion he may walk more than that. This is why we gave the player a lot of motion.
6. Still during the lag spike, the player instantmines the slimeblock through the fire block.

The actions the player executes clientside during the lag spike get executed serverside in the [player phase](tick-phases.md#player-inputs) of the next gametick.
The fire block then gets extinguished. The block updates from the fire block get send through a powered rail line towards the ungenerated chunk to immediately load and populate it. Then the slimeblock gets replaced by a bedrock block, and this bedrock block gets mined by the player.

At the time this method was developed, TNT was the preferred solution to all problems. For this reason, TNT was used for 3 different things (giving the player motion, shooting the ender pearl, creating lag), even though theoretically an end gateway population method for bedrock silk touch mining doesn't necessarily have to use any TNT.

# Async Method using Hashmap Word Tearing
