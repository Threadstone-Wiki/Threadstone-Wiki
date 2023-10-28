# Update Suppression

An excellent video explanation is [Update Suppression Explained](https://www.youtube.com/watch?v=IJhZpK-8p54) by Xcom.

Update suppression was discovered by Panda4994 and RedstoneSpire and shown in the video [Update Suppression, Block Duplication, Skull Converter and More](https://www.youtube.com/watch?v=mzfLHNeqjuY).

## Table of Contents

- [Introduction](#introduction)
- [Basic Applications](#basic-applications)
  * [Blocks in invalid states](#blocks-in-invalid-states)
  * [Item Duplication](#item-duplication)
  * [Skeleton Skulls](#skeleton-skulls)
- [Population Suppression](#population-suppression)
- [Suppressing comparator updates](#suppressing-comparator-updates)
  * [Tile Entity Swap](#tile-entity-swap)
  * [Item Shadowing](#item-shadowing)
  * [Igloo Barrier](#igloo-barrier)
  * [1.13 Threadstone](#113-threadstone)
- [Miscellaneous](#miscellaneous)
  * [Multiply by 8 teleportation](#multiply-by-8-teleportation)



# Introduction

1. **If an exception is thrown in the [player phase](tick-phases.md) of a tick, the game just ignores the exception and continues running instead of shutting down the server.**

2. **One can easily cause StackOverflow exceptions using long chains of [immediate block updates](tick-phases.md#immediate-updates).**

An *update suppressor* is a contraption that immediately causes a StackOverflow exception when its input block receives a block update.
Update suppressors usually use several thousand rails to do this.

This is a picture of an update suppressor.

![Picture of Update Suppressor](/images/UpdateSuppressor.PNG)

If [instant tile ticks](global-flags.md#instant-tile-ticks) are on, then any tile tick based clock is an update suppressor.


# Basic Applications

## Blocks in invalid states
When a block is changed it usually sends out block updates to adjacent blocks in the following directions  in the following order:

-x +x -y +y -z +z

If one of the early block updates, updates one of the input rails of an update suppressor,
then the update suppressor will throw a stack overflow exception,
and then all of the later block updates no longer happen.

This can be used to create blocks in invalid states.

For example, in the left picture below, there is a comparator on top of an iron block, and the iron block is next to an update suppressor.

![Creating a floating comparator](../images/Floating%20Comparator.png)

If the iron block is broken, then it will send out a block update in -x direction. This updates the input rails of the update suppressor and causes a stack overflow.
Therefore all the other block updates from the iron block do not get send out. In particular there will be no update send in +y direction to the comparator.
Since the comparator receives no block update, it will not pop off but just continue to float in mid-air, like in the picture on the right.
Furthermore if the iron block was broken by a player, then the server will not be shut down.

In this way we can create a floating comparator without shutting down the whole server. If the floating comparator ever receives a block udpate after this, it will immediately pop off.

## Item Duplication

When a player places down a block, the block will first be placed and send block updates before the item count in the players inventory gets decreased.
If one of the block updates of the placed block causes an update suppression, then the item of the block will stay in the players inventory.
The block is then both in the world and in the players inventory, so the block got duplicated.

## Skeleton Skulls

When a player places down a wither skeleton skull, then the game will first place down a normal skeleton skull block, then send out block updates,
and then create tile entity data for the normal skeleton skull block to make it into a wither skeleton skull.
If one of the block updates of the skull causes an update suppression, then the tile entity data will never get created, so the wither skeleton skull will be converted into a normal skeleton skull.
Just like above we also get an item duplication here and the wither skeleton skull item will stay in the players inventory.

# Population Suppression
Update suppression during [Terrain Population](chunk/population.md) can be used to manipulate [global-flags](global-flags.md), create invisible chunks and create barrier blocks.

See [Terrain Population - Population Suppression](chunk/population.md#population-suppression).

# Suppressing comparator updates

When a tile entity changes state it often sends out comparator updates. Update suppressing comparator updates leads to additional powerful exploits.
To be able to update suppress a comparator update, we need some way to immediately convert a comparator update into a normal block update.
This can be done by using floating comparators. If a floating comparator receives a comparator update it will immediately pop off and send out normal block updates. This is called the *floating comparator trick*.

Another way to convert comparator updates immediately into block updates without the floating comparator trick is to use the [instant tile tick flag](global-flags.md#instant-tile-ticks). But that is overkill for some of the below applications.

Applications of the floating comparator trick include the following.

## Tile Entity Swap
A video explanation of Tile Entity Swaps is in [Tile Entity Swap](https://www.youtube.com/watch?v=EpTaffAuVz4) by JKM and punchster2.

When a tile entity block gets destroyed, it will first delete the block, then send out comparator updates, and only then delete the tile entity.
If the comparator updates get suppressed, then the tile entity of the block will stay, even though the block is already gone.

Tile Entity Swaps are applied in:
- [Class Cast Suppressors](https://www.youtube.com/watch?v=f4ty-PZcvrI)-
- Population method for Falling Barrier

## Item Shadowing
A video showcase of Item Shadowing is in [Magic reveal: Item Shadowing](https://www.youtube.com/watch?v=mTeYwq7HaEA) by Fallen_Breath and [Item Shadowing Explained - Wireless item transfer - Vanilla Minecraft 1.12+](https://www.youtube.com/watch?v=i8_FPyn20ns) FX-Process.

When you swap an item stack into a chest using the 1-9 hotkeys, then the item stack will be placed in the chest, then the chest will send out comparator updates,
and only then will the item stack be removed from the players inventory. If the comparator updates get supressed, then the item stack will stay in the players inventory.
In this case one item stack instance will simultaneously be both in the players inventory and in the chest.
This enables a very versatile form of wireless item transfer.

## Igloo Barrier
The floating comparator trick can be used to update suppress an igloo population in such a way that it creates a barrier block.
See [Terrain Population - Igloo Barrier Block](chunk/igloo-barrier-block.md).

## 1.13 Threadstone
The floating comparator trick is of crucial importance to 1.13 threadstone.
The only way to get a block update during a terrain population in 1.13 is by populating an end city which places an item frame with an elytra, which can send comparator updates into already populated chunks and update a floating comparator which immediately pops off and sends out normal block updates.

Update suppressing these updates turns on the [instant falling flag in 1.13](https://www.youtube.com/watch?v=CfMSatbWyfo).

If the end city population is triggered on an async glass thread this is a source of async block updates in 1.13.

# Miscellaneous

## Multiply by 8 teleportation
Update suppressing a trapped chest while a player goes through a nether portal can multiply the x and z coordinates of the player by 8.

See Xcom's video [Fake players in vanilla, at 9:18](https://www.youtube.com/watch?v=091mFU3d8m0&t=558s).

Another almost tutorial-like explanation by JKM is in ilmango's video [SciCraft 167: Hyperlink Network / World Border, at 10:15](https://www.youtube.com/watch?v=F5SFmu_3WVg&t=615s)

In this case the stack overflow does not happen in the player phase. It happens during the [Networking Phase](tick-phases.md#networking-phase).
If an exception is thrown during the Networking phase it does not shut down the server, but instead just kicks the responsible player from the server.

In singleplayer it does fully crash the game. But it will also save the new position of the player. So after restarting the game, the player will have x and z coordinates multiplied by 8.

Using update suppression for times 8 teleportation is only necessary in 1.12.2.
In 1.12.0 and 1.12.1 one can peform times 8 teleportation with the much simpler [PortalPearlWarp](https://www.youtube.com/watch?v=ITMnUkZz-8I) by RaysWorks, without requiring any update suppression.
