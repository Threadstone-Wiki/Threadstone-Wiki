# Double Tile Tick Scheduling

## Table of Contents

- [Introduction](#introduction)
- [Applications](#applications)
  * [Instant Wire](#instant-wire)
  * [Anvil Repairing](#anvil-repairing)
  * [Crashing the game](#crashing-the-game)
  * [Falling Block Swaps in the End](#falling-block-swaps-in-the-end)
- [Accidental fix in 1.13](#accidental-fix-in-113)

# Introduction

Tile ticks are called NextTickListEntry in 1.12 MCP.
The NextTickListEntry class implements the [Comparable interface](https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html).

This means the NextTickListEntry class has both an equals() method
and a compareTo() method.

The ordering induced by the compareTo() method is called "consistent with equals"
if for any two objects for which equals() is true, compareTo() returns 0.

The NextTickListEntry class in minecraft has an ordering which is not consistent with equals.

Two tile ticks are equal in the sense of equals() if they have the same position and block type.
But the compareTo() method tells you which tile tick comes first in the update order.
Since two tile ticks can be at the same position and be scheduled by the same block without having the same update order,
the tile tick ordering is not consistent with equals.

Now the HashSet that stores the tile ticks assumes that the ordering should be consistent with equality.
Since it is not, the HashSet can sometimes get confused and this makes it possible to add duplicate tile ticks.
How exactly does this work?

In the HashSet there is a bucket for each possible hash value. If two tile ticks get added to the same bucket, then they usually get put into a list.
However if the list in a bucket gets too long, then the bucket gets treeified, and the list gets replaced by a binary tree.
The compareTo() method of the objects get used to decide which objects get put where in the binary tree.
Small objects get put further to the left, large objects further to the right.
The tree tries to prevent duplicate objects from being entered into the tree, but this only works if the ordering is consistent with equals.

If we have two tile ticks T1 and T2 which are equal according to equals(), but where T2 is larger than T1 according to compareTo(),
then it is possible to enter both into a binary tree in a hash bucket. We could enter T1 somewhere on the left half of the tree,
and make sure that T2 is larger than the root of the tree. If you then try to put T2 into the tree it will compare it to the root and then compare it to a bunch of objects on the right half of the tree,
but it will never compare it with T1. So in the end T2 gets entered into the tree, even though T1 is already in the tree. And then you have scheduled a duplicate tile tick.
To get this to work in practice the main thing you need to do is treeify one of the buckets of the tile tick hashset.
For this you need to schedule a lot of tile ticks with the same hash value.

The contraption you can see at 1:25 of the vieo [wireless redstone again i guess xd](https://www.youtube.com/watch?v=9KZ8i7fVbr4) is a double tile tick scheduler.
It goes a few hundred blocks into the distance, and every 32 blocks  or so it activates an observer which schedules a tile tick.
All the tile ticks from the observers have the same hash value and go into the same hash bucket.
The contraption schedules enough tile ticks to treeify that hash bucket.

Once the bucket is treeified, it is possible to schedule duplicate tile ticks in that hash bucket.

A video explanation of HashSets is in Earthcomputer's video [What is a HashSet?](https://www.youtube.com/watch?v=y5Cx07OHaOI).

# Applications

## Instant Wire
In the video [wireless redstone again i guess xd](https://www.youtube.com/watch?v=9KZ8i7fVbr4) by Earthcomputer one can see how to use a double tile tick scheduler for wireless redstone.
The sending contraption is a double tile tick scheduler,
the receiving contraption is a tile tick clock in which an observer can manage to schedule a duplicate tile tick or not depending on whether the sender was activated or not.
The receiving contraption detects the duplicate tile tick and thereby receives a wireless signal.

The double tile tick scheduler is unreliable in the sense that it can be broken by having other tile tick blocks active in the world.

## Anvil Repairing

In the video [Slime Block Glitch and Sand Converter (14w02c)](https://www.youtube.com/watch?v=ulh6-HvscTo) by test137e29, he shows a sand converter that can convert normal sand into red sand.
This sand conversion trick can be combined with duplicate tile ticks to convert damanged anvils into repaired anvils or vice versa.

Sand converters work as follows:

If you update a sand block, it will schedule a tile tick.
When a sand block processes the tile tick then it will spawn a falling sand entity inside of itself.
If a falling sand entity gets processed for the first time, it will check whether it is inside a sand block. If it is, then it deletes the sand block, and then get processed like normal. If it is not in a sand block, then it deletes itself.

The same holds for other gravity affected blocks.
Noteworthy here is that if you have a red sand entity being processed the first time inside a white sand block, then it will delete that white sand block and will not delete itself. A falling sand entity only checks whehter it´s inside a sand block, and not whether the sand block has the same metadata.
This is the basis of the sand converter shown by test137e29.
In the sand converter, we first let a red sand block create a red sand entity. Then before that entity gets processed we push the red sand block away, and push a white sand block into its place. Then the red sand entity gets processed in the white sand block, and deletes the white sand block. The end result is: We started with a white and a red sand block, and we end up with two red sand blocks.

The basic idea behind anvil repairing is to do a similar thing with anvils. If a repaired anvil entity gets processed inside a damaged anvil block, then it will delete the damaged anvil block instead of deleting the repaired anvil entity.
Since anvils cannot be pushed by pistons it is not clear how one can perform this bug, because the repaired anvil block cannot easily be moved out of the way.

With duplicate tile ticks however a new method becomes possible:
We can start with a repaired anvil block. We make that repaired anvil block schedule two tile ticks. When the tile ticks get processed, we get two repaired anvil entities inside the repiared anvil block. When the first of these entities gets processed, then it will delete the repaired anvil block. We are left with an air space, and inside that air space is still a repaired anvil entity that has not been processed yet.
If we now let a damaged anvil fall into that space before that second repaired anvil entity gets processed, then it will delete that damaged anvil, and not delete itself. The end result is: We started with a damaged anvil block and a repaird anvil block, and in the end we have two repaired anvil blocks.

This leads to the following over-complicated anvil repairing method in the video [Repairing Anvils](https://www.youtube.com/watch?v=YOPJrZJV09Q) by Myren Eario.

This is ultimately a very unimportant application because letting anvils instantfall in lazy chunks also repairs them.

## Crashing the game
The game has actually not one but two lists of tile ticks: The pendingTickListEntriesHashSet and the pendingTickListEntriesTreeSet.
Both are vulnerable to double tile tick scheduling.
If one manages to enter a duplicate tile tick into the hashset without entering it into the treeset, the game will crash in the next tile tick phase.

## Falling Block Swaps in the End

This is a purely theoretical application whose details have not been worked out.

[Cool mann's chunk swap setup for the end](https://www.youtube.com/watch?v=VTbpUjK-A74) can be used to perform falling block swaps in the end.
In the video he uses instant tile ticks, which is not vanilla-friendly, but the contraption could easily be modified to not require instant tile ticks.
To improve the falling block swap chances one should use cluster chunks, but additionally one should use double tile tick scheduling to schedule multiple tile ticks on the sand blocks and further increase the falling block swap chances.
This could lead to a population based setup for creating falling end gateways, without using word tearing.

# Accidental fix in 1.13

In 1.13 mojang fixed double tile tick scheduling by complete accident.
They made the NextTickListEntry class into a generic type class.
So it's now NextTickListEntry\<T\> instead of just NextTickListEntry.
There is a java bug that makes it so the comparable class doesn't work properly for generic type classes, and this bug makes it so the game never ever uses the equals() method of the NextTickListEntry class, but instead just uses equality of objects.
This fixed double tile tick scheduling.
So mojang didn't even know about double tile tick scheduling,
but they accidentally introduced a java bug which cancels out the double tile tick scheduling bug.
