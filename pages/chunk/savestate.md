This page is about Chunk Savestates.

# Introduction

If the game tries to save a chunk while the chunk contains more than 1 Mega-Byte of data, the game will not save the chunk at all.

By using multiple books filled with random unicode characters, one can easily create more than 1 Mega-Byte of data in-game.

A book that is filled with unicode characters on every page is called a savestate book.

![A screenshot showing page 1 of a 50 page book filled with random chinese characters](../images/SavestateBook.PNG)

# Data compression

The game uses data compression algorithms to minimize the amount of data it has to save.
If multiple identical savestate books are in an inventory with no other items inbetween, then the total data will not be much larger than that of a single savestate book.
If an inventory contains two different types of savestate books, and these savestates books are arranged in an alternating fahsion in the inventory,
then the data compression algorithm will not be able to compress the data, so the total amount of data will be sum of the data of each individual savestate book.
This means one can greatly increase the amount of data in a chunk simply by changing the order in which savestate books appear in an inventory.









# History

Chunk Savestates were discovered by Earthcomputer and Skyrising: https://www.youtube.com/watch?v=uw7vEGhKoH8
