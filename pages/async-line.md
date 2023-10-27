This page is about async lines.

# Introduction

An async line is a redstone contraption that is
- activated on an async glass thread
- keeps the async glass thread alive for a long time
- sends out async block updates at a high frequency

Async lines usually require [instant tile ticks](global-flags.md#instant-tile-ticks) to work.
If ITT is on, then a long chain of observers makes for an excellent async line.

To activate an async line on the async glass thread one needs to perform an [async chunk load](chunk/async-chunk-loading.md).
This async chunk load needs to trigger a [terrain population](chunk/population.md#glass-threads-causing-async-updates).
The terrain population then sends out async block updates which are used to activate the async line.

# Applications

The async block updates that an async line sends out can be used for many threadstone exploits, like:

- creating unobtainable blocks with word tearing
- obtaining unobtainable items with [falling block swaps](../falling-block/falling-block-swaps.md)
- creating player heads

# Async line death

TODO!

# Non-observer async lines
In the vast majority of circumstances observer chains are the best async lines.
But there are also other async lines.

When ITT is on, any chain of tile-tick based XOR gates, in which block updates from later XOR gates cannot travel back to earlier XOR gates, can be used as an async line.
The following XOR gate chain works as an async line:

![XOR Gates](images/XORChain.PNG)

There is also a directional async line that uses only repeaters:

![Repeater Chain](images/RepeaterAsyncLine.PNG)

One can also keep an async thread alive for roughly one minute in a [tile-tick-less rail-based async line](https://www.youtube.com/watch?v=uVfT5w8RSyQ&list=PL8r-bvM9ltXNkjl7IhGQAHygIPfy2niuC&index=50), 
provided another thread is toggling the [redstone power flag](global-flags.md#redstone-power-flag) very quickly. This kind of async line could in principle work without ITT, but in practice it is difficult to get it to work.

Possible reasons for using non-observer async lines are as follows:

- In 1.8 and in skyblock one might not have access to observers, but one might have access to repeaters, and can build the repeater based async line.
- In 1.13 instant tile ticks do not exist, and one needs to use a rail-based async line.
- If a good async line without observers, repeaters and comparators were found, it would make it possible to run async lines in the nether and have fire blocks loaded without crashing the game.

