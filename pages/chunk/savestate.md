This page is about Chunk Savestates.

# Introduction

If the game tries to save a chunk while the chunk contains more than 1 Mega-Byte of data, the game will not save the chunk at all.

By using multiple books filled with random unicode characters, one can easily create more than 1 Mega-Byte of data in-game.

A book that is filled with unicode characters on every page is called a savestate book.

![A screenshot showing page 1 of a 50 page book filled with random chinese characters](/images/SavestateBook.PNG)

## Data compression

The game uses data compression algorithms to minimize the amount of data it has to save.
If multiple identical savestate books are in an inventory with no other items inbetween, then the total data will not be much larger than that of a single savestate book.

To maximize data, one therefore uses two different kinds of savestate books, called type 1 savestate books and type 2 savestate books.
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






# History

Chunk Savestates were discovered by Earthcomputer and Skyrising: https://www.youtube.com/watch?v=uw7vEGhKoH8
