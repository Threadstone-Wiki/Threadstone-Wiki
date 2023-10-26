# Chunk Savestates

## Table of Contents
- [Introduction](#introduction)
  * [Data compression](#data-compression)
  * [Creating savestate books](#creating-savestate-books)
  * [Simplest savestate contraption](#simplest-savestate-contraption)
- [Unpopulated chunks](#unpopulated-chunks)
  * [Book bans](#book-bans)
  * [Automatic savestate contraption for unpopulated chunks](#automatic-savestate-contraption-for-unpopulated-chunks)
  * [Atomic savestates for unpopulated chunks](#atomic-savestates-for-unpopulated-chunks)
- [Miscellaneous](#miscellaneous)
  * [Duplicated entity UUIDs](#duplicated-entity-uuids)
  * [Quick reloads break savestates](#quick-reloads-break-savestates)
  * [Virus flying machine](#virus-flying-machine)


# Introduction <a name="introduction"></a>

1. **If the game tries to save a chunk to disk while the chunk contains more than 1 Mega-Byte of data, the game will not save the chunk at all**.

A chunk which contains more than 1 Mega-Byte of data is called a *savestated chunk*.
When a chunk is savestated, anything that happens in that chunk will be reversed when the chunk is unloaded and loaded again.
Savestating chunks is a powerful duplication glitch: If you savestate a chunk containing a diamond block, the mine the diamond block, and then unload and reload the savestated chunk, you have two diamond blocks.

2. **By using multiple books filled with random unicode characters, one can easily create more than 1 Mega-Byte of data in-game and savestate chunks**.

A book that is filled with unicode characters on every page is called a *savestate book*.

![A screenshot showing page 1 of a 50 page book filled with random chinese characters](/images/SavestateBook.PNG)

Chunk Savestates were discovered by Earthcomputer and Skyrising: https://www.youtube.com/watch?v=uw7vEGhKoH8

## Data compression

The game uses data compression algorithms to minimize the amount of data it has to save.
If multiple identical savestate books are in an inventory with no other items inbetween, then the total data will not be much larger than that of a single savestate book.

To maximize data, one therefore uses two different kinds of savestate books, called *type 1 savestate books* and *type 2 savestate books*.
All type 1 savestate books contain the same unicode characters, and all type 2 savestate books contain the same unicode characters,
but type 1 savestate books contain different unicode characters than type 2 savestate books.
An inventory containing only a single type of savestate books can be compressed, so that it does not contain a lot of data.
If an inventory contains type 1 and type 2 savestate books in an alternating fahsion, then the data cannot be compressed,
and the total amount of data will be proportional to the number of savestate books.
So the amount of data in a chunk can be greatly increased simply by changing the ordering in which savestate books of different type appear in an inventory.

Two chests filled with an alternating pattern of type 1 and type 2 savestate books is enough to get more than 1 Mega-Byte of data and savestate a chunk.

## Creating savestate books

To create savestate books in-game you can use the following autohotkey script: [Xcoms Savestate Book Script](/resources/Xcoms_Automatic_Bookdupe_Paster.ahk)

To use it, first install [autohotkey](https://www.autohotkey.com/)

Run the script.

Open a writable book in minecraft.

Hover with your mouse over the next page button.

Then press Ctrl+Alt+F5 to create a type 1 savestate book.

The script will paste in unicode symbols on the current page, then left-click with your mouse to go to the next page, and then repeat.

If you do the same steps but press Ctrl+Alt+F9 instead, it will create type 2 savestate books.

The savestate books can then be copied using a crafting table.

For most automatic savestate contraptions it is furthermore required that the type 2 savestate books are given 13 different names, using an anvil.
This renaming is necessary, to make it impossible for the type 2 savestate books to stack together in a single inventory slot.
One usually adds characters 'a', 'b', 'c', and so on to the end of the name of the type 2 savestate books to make them different.

Instead of using AutoHotKey you can also manually copy and paste unicode characters into a book.

## Simplest savestate contraption

The picture below shows a simple automatic savestate contraption.

![A screenshot of a savestate contraption](/images/2023-10-26_16.50.50.png)

The white shulker boxes contain a checker board pattern of type 1 savestate books.

![Checkerboard pattern of type 1 savestate books](/images/InactiveSavestateShulker.PNG)

The chests each contain 13 type 2 savestate books, and within each chest each type 2 savestate book has a unique name.

![Type 2 savestate books](/images/Type2Savestate.PNG)

As long as the lever is not flicked, the data in both the chests and the shulker boxes can be easily compressed.

Flicking the lever will unlock the hopper and transfer type 2 savestate books from the chests into the shulker boxes.
Since the type 2 savestate books all have different names, they will all enter different inventory slots in the shulker box.
The shulker box will then contain an alternating pattern of type 1 and type 2 savestate books.
Two such shulker boxes are enough to savestate the chunk.

# Unpopulated chunks

Savestates are one of the most powerful methods for building contraptions near unpopulated chunks:
One can fly to an unpopulated chunk (and thereby populate it), then savestate the chunk before it gets saved, (so that the chunk is populated in game but unpopulated on disk),
and then build contraptions around the savestated chunk.
After unloading the whole area, one has on disk an unpopulated chunk surrounded by the contraptions that one built around it.

To do this one uses savestate shulker boxes.
An *activated savestate shulker box* is a shulker box containing type 1 and type 2 savestate books in an alternating fahsion.
An *unactivated type 1 savestate shulker box* is a shulker box that contains a checkerboard pattern of type 1 savestate books.
An *unactivated type 2 savestate shulker box* is a shulker box containing 13 type 2 savestate books, where each has a different name.

The process for manually savestating a chunk as unpopulated is as follows:

-Ensure that the chunk you want to savestate has never been generated and populated before.

-Start right out of view distance of that chunk. Put two activated savestate shulker boxes in your inventory.

-Wait for an autosave. Autosaves can be detected either by using the carpet command "/log autosave", or by building an autosave detector.

-After the autosave, within 45 seconds, fly to the chunk and place two active savestate shulker boxes in the chunk.

## Book bans
If one wants to manually savestate multiple unpopulated chunks that are close to each other, one has to additionally circumvent book bans:
If a player carries around more than 3 activated savestate shulker boxes, they get kicked from the server. This behavior is called the book ban.

Book bans can be cheatily disabled by using the carpet command "/carpet disableBookBan true" and using a carpet client.

In vanilla book bans can be circumvented by using multiple players, each of whom only carries around at most 3 activated savestate shulker boxes.

If one wants to savestate more unpopulated chunks than one has players available, one needs to carry around unactivated savestate shulker boxes and hoppers,
and activate the savestates after one has flown to the chunk.
In this case, one waits for an autosave, flies to the chunk, places down two unactivated type 1 savestate shulker boxes in each chunk one wants to savestate, then places a hopper facing into each of those shulker boxes,
and then places unactivated type 2 savestate shulker boxes above the hoppers.
The hoppers will then transfer type 2 savestate books into the unactivated type 1 savestate shulker boxes and thereby activate the savestates.

Since the hoppers take a few seconds to transfer the items, and one only has 45 seconds for the whole operation, this needs to be done quickly.

This process can be seen at the beginning of this video: https://www.youtube.com/watch?v=z7-Fw51WFOc

## Automatic savestate contraption for unpopulated chunks

After one has manually savestated an unpopulated chunks (in the sense that it is populated and savestated in game but unpopulated on disk), one sometimes wants to build a contraption that savestates the unpopulated chunk again every time it gets reloaded. One cannot directly build a savestate contraption in the savestated chunk itself, because such a contraption would disappear after reloading the chunk. But one can build in an adjacent chunk a contraption that can dispense activated savestate shulker boxes into the chunk. In order to not lose these shulker boxes after repeated use of the contraption, one uses savestates to also dupe those savestate shulker boxes.

Here is a picture of an automatic savestate contraption:

![A screenshot of automatic savestate](/images/0xSavestate.png)

The four white shulker boxes contain a checkerboard pattern of type 1 savestate books. The four droppers and the four hoppers below the droppers contain uniquely named type 2 savestate books.
When the contraption is activated the hoppers below the droppers are unlocked, making all four white shulker boxes into activated savestate shulker boxes.
Two of the white shulker boxes get destroyed by pistons, picked up by hoppers, transferred into the dispensers, and then dispensed into the adjacent chunk.
This savestates the adjacent chunk, without requiring any blocks to be in that chunk in the beginning.
The other two white shulker boxes stay with the contraption to savestate the contraption itself and make it infinitely reusable.

These kinds of automatic savestate contraptions for unpopulated chunks were invented by 0x in collaboration with members of the Prototech server: https://www.youtube.com/@xeeebee

The specific design shown above is by PrgmTrouble: https://www.youtube.com/@prgmTrouble

Litematic is [here](../../resources/itt_and_permaloader_safe_savestate.litematic)

## Atomic savestates for unpopulated chunks

The above savestate contraption can fail, if one reloads the unpopulated chunk without reloading the chunk containing the savestate contraption.
Using invisible chunks, it is also possible to build a savestate contraption direclty in the unpopulated chunk and save it to disk.

# Miscellaneous

## Duplicated entity UUIDs
If you use savestates to duplicate an entity, then the two resulting entities will have the exact same UUID.
When multiple entities with the same UUID are loaded in the same dimension, all but one of them will be invisbile and hard to interact with.

## Quick reloads break savestates
When you unload a savestate chunk, you need to wait for a second or two before reloading it,
or the savestates will not work and you simply get the savestated chunk with the activated savestates back.
This happens for the following reason:
When a chunk is unloaded, before it gets saved, it gets put into a cache in the AnvilChunkLoader.
A separate thread in the ThreadedFileIOBase one by one takes chunks from that cache and saves them to disk.
If a chunk is unloaded and then reloaded while it is still in the cache, then the chunk does not get loaded from disk, but simply gets returned from the cache.
Since savestated chunks contain a large amount of data, it takes a long time to (fail to) save them,
which means that it takes much longer for chunks to leave the cache if the cache contains savestated chunks.

## Virus flying machine
Samnrad, Earthcomputer, Pingu and Xyor made a flying machine version of savestates called the virus flying machine: https://www.youtube.com/watch?v=ybr9WtJ-NeE
