This page is about async lines.

# Introduction

An *async line* is a long [update multiplier chain](update-multiplier.md), that has been activated on an async thread.
Usually the update multiplier chain is an observer line.

In an async line one can keep an async thread alive for a long time, while the main thread continues running as normal.
While the async line is running it constantly sends out async block updates which can be used for [threadstone exploits](#applications).

To activate an async line on the async glass thread in survival one needs to perform an [async chunk load](chunk/async-chunk-loading.md).
This async chunk load needs to trigger a [terrain population](chunk/population.md#glass-threads-causing-async-updates).
The terrain population then sends out async block updates which are used to activate the async line.

In [carpet mod](https://github.com/gnembon/carpetmod112/releases) one can for testing purposes create an async line using commands.
The command `/carpet asyncBeaconUpdates true` makes it so that beacons that receive redstone power send out async block updates around them every time they get updated.
The command `/carpet instantScheduling true` turn on ITT.
One can then create an async line by having a beacon leading into a long chain of observers, and then powering the beacon.
Such an async line looks like this

![async line](../images/CarpetAsyncLine.png)

The further an observer is to the end of the line, the faster it flickers between its powered and unpowered state.

Extending the observer line by one additional observer doubles the lifetime of the async line.

# Applications

The async block updates that an async line sends out can be used for many threadstone exploits, like:

- creating unobtainable blocks with registry word tearing
- obtaining unobtainable items and falling blocks with [falling block swaps](../falling-block/falling-block-swaps.md)
- creating player heads
- obtaining bedrock items by silk touch mining bedrock created through hashmap word tearing

# Side Effects

## Disabled Packets

If an async line is running in a subchunk for a few seconds, it will often disable all block modification packets from that subchunk.
This means that if a block changes in the subchunk, one will not be able to see it.
One can force the server to send a block modification packet for a particular position if one right clicks the block.
If one wants to see what happens in a subchunk in which an async line is or was running, one needs to right click every block one wants to accurately see.

Block event packets are not disabled, so piston actions will always be visible.

## Random Palette Corruptions

## Server Crashes

### `ConcurrentModificationException` in the PlayerChunkMap

### TickNextTickList ouf of synch crash

### Updating Block 36 while modifying Tile Entities

## Async Line Death

### Running Out

An async line that is over 40 blocks long theoretically takes weeks to run out.
However it can run out much faster if:
- Blocks from the async lines are broken
- Observers get stuck in a permanently powered state, due to word tearing or [random palette corruptions](#random-palette-corruptions)
- Chunks get unloaded in such a way that parts of the async line are within 8 blocks of unloaded chunks
- ITT is turned off


### Async Thread Crashing

There are many different ways in which an async thread can crash, depending on what is being updated at the end of the async line.

When nothing is updated at the end of the async line, it can crash in the following ways:
- If the observer chain is several thousand blocks long, it crashes due to a `StackOverflow`.
- If the chunk hashmap is resized, it can do a bad [rehash chunk swap](chunk/async-chunk-loading.md#rehash-chunk-swap) that ends in an `ArrayIndexOutOfBoundsException`.



