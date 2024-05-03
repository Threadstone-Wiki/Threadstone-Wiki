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
1. One can trigger a [terrain population](chunk/population.md) to immediately generate a gateway, whose bedrock replaces the instantmineable block.
2. One can use [hashmap word tearing](word-tearing.md#hashmap-word-tearing) to transform the instantmineable block into bedrock.

# Population Method using End Gateways

# Async Method using Hashmap Word Tearing
