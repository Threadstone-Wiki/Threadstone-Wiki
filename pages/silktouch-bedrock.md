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

# Async Method using Hashmap Word Tearing
