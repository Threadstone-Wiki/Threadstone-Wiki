This page is about hyperlink.

# Introduction

Hyperlinks are discussed in [Falling Block Episode 8](https://www.youtube.com/watch?v=-4UhrzA-F2g), and in the video [What to do with your falling blocks from 1.12 after updating to 1.18](https://www.youtube.com/watch?v=l9aw_Db_4q4).

A hyperlink network is a transportation system in minecraft, based on end portals and end gateways and nether portals in the end.

The hyperlink network consists of hyperlink nodes. You can go from anywhere in the world to any hyperlink node in a few seconds.

![Hyperlink](/images/Hyperlink.PNG)

A hyperlink node consists of two end gateways and a nether portal in the end. The first end gateway is near the main end island, and leads to the second end gateway. The second end gateway leads to the nether portal in the end. The nether portal leads to the desired location.

For any location in the nether whose coordinates do not exceed 1/8 of the world border coordinates, it is possible to build a hyperlink node leading to that location.

# Requirements

To build a hyperlink node you need:

1. End Portal Frame Items
2. A Falling End Gateway
3. A Falling Nether Portal

The first two can be obtained using the [generic falling block swap method](falling-block/generic-method.md). The falling nether portal can be obtained using a [specific falling block swap method for nether portals](falling-block/falling-block-swaps.md#nether-portal-1).

With a single falling end gateway and falling nether portal one can create infinitely many hyperlink nodes, both in 1.12 and recent versions, by [end portal duping the falling blocks](falling-block/falling-block-entity.md#end-portal-duping).


# Reaching arbitrary positions

To build a hyperlink node leading to a desired location, one first needs to reach that location as a player. In recent versions this can be difficult and time consuming, while in 1.12 one can do this quickly by using multiply by 8 glitches.

## Multiply by 8 glitch

A multiply by 8 glitch is a glitch that multiplies the coordinates of the player by 8.
These glitches allow one to easily reach arbitrary locations in the overworld.

### Portal Pearl Warp
The portal pearal warp is a very easy multiply by 8 glitch, that is shown in RaysWorks' video [Portal Pearl Warp- FASTEST travel in Minecraft! (~1,100,000 m/s) 1.12.1-1.9+ Survival](https://www.youtube.com/watch?v=ITMnUkZz-8I).

The glitch works in versions 1.9 - 1.12.1.

The glitch does NOT work in 1.12.2.

The glitch does work again in 1.13.0 - 1.13.2.

The glitch is permanently fixed in 1.14+.

### Update Suppression
In 1.12.2 one can perform a multiply by 8 glitch by using [update suppression](update-suppression.md#multiply-by-8-teleportation).
This does not work in recent versions.

## Dolphinman end portal glitch
If one has reached an arbitrary location in the overworld one can easily reach an arbitrary location in the end by using the glitch shown in dolphinman's video [
DREAM'S NEW MANHUNT STRATEGY | New Craziest Glitch in Minecraft](https://www.youtube.com/watch?v=9Z0fqYy8S5g).
If a piston pushes a player sidewards below an end portal, the player goes through the end portal, but the movement code continues to act on the player after the player arrived in the end,
and then the player gets placed at the location in the end to which the piston was pushing him in the overworld.

# Generating Dataless Gateways
After the player has reached the location in the end where he wants to place the nether portal for the hyperlink node, he needs to create a permanent end gateway connection between his location and the main end island.
After this connection has been established, one can transport the falling nether portal from the main end island through this gateway connection to the desired location.

The permanent gateway connection is created by generating a [dataless gateway](gateway.md#dataless-gateways). This can be done by [pulling a gateway block during terrain population](chunk/population.md#pulling-immovable-blocks).

When the dataless gateway A at our desired location is used for the first time, it will create another gateway B roughly 1000 blocks away from the main end island.
One can then place a falling end gatewayat the main end island, at the coordinates of B divided by 100. The falling end gateway will become a dataless gateway C, which when used creates another gateway D, such that B and D are very close to each other.
One then has a permanent 2-way gateway connection between the main end island and the desired location.

# Transporting Falling Blocks

Next, one can use various [falling block transportation methods](falling-block/falling-block-entity.md#preservation-and-transportation) to transport a falling nether portal from the main end island through the gateway connection to the desired location.
If one then places down the falling nether portal, one has a complete hyperlink node.

