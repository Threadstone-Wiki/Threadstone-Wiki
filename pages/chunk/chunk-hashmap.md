This page is about the chunk hashmap.


# Introduction

In each dimension the loaded chunks are stored in a [`Long2ObjectOpenHashmap`](https://github.com/karussell/fastutil/blob/master/src/it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap.java).

In carpet mod one can print out the chunk hashmap using the command `/loadedChunks dump`. The command creates an excel file in the server folder, which looks like this

![hashmap dump](../../images/chunkHashmap.PNG)

## Adding Chunks

## Getting Chunks

## Removing Chunks

## Resizing

# Race Conditions
The `Long2ObjectOpenhashmap` is a data structure that does not support asynchronous operations. If multiple threads access the `Long2ObjectOpenhashmap` at the same time, it can fail to work as intended.
In minecraft, the chunk hashmap can be accessed simultaneously by both the [main thread](../threads.md#main-thread) and the [stained glass threads](../threads.md#stained-glass-threads).
This makes many race conditions with the chunk hashmap possible in minecraft.

## Chunk Swaps

## Chunk Linking

Punchster has theorized, that if the async thread upsizes the chunk hashmap, while the main thread unloads a chunk, then additional race conditions can occur that can change the positions of chunks, or link two chunk positions to a single chunk instance.

# Cluster Chunks
An explanation of cluster chunks is in [Falling Block Episode 5, at 3:45](https://www.youtube.com/watch?v=DhohUJiJ1E8&t=225s).

*Cluster chunks* are a set of chunks that occupy subsequent entries in the chunk hashmap without any holes.

In the `/loadedChunks dump` excel sheet cluster chunks look like this:

![cluster chunks](../../images/ClusterChunks.PNG)

If cluster chunks are loaded, and one loads an additional chunk which has a hash value that would usually place it somewhere in the beginning of the cluster,
then the cluster chunks will force that chunk to be placed somewhere at the end of the cluster. This then results in a loaded chunk whose hash value is very different from its index in the chunk hashmap.

We say that a chunk is *clustered* if its hash value is very different from its index in the chunk hashmap.
The difference between the index and the hash value is called the *amount of clustering* the chunk has.

For example in the above picture the chunk at position 90 90 has a hash value of 613, but an index of 1834.
This means that the chunk at position 90 90 has 1221 clustering, which is a lot.

When a chunk has a lot of clustering, then this slows down all `get` and `containsKey` calls that try to get that chunk from the hashmap, because whenever a `get` call for that chunk is made, the game will first try to find the chunk at the index corresponding to the hash value of the chunk,
and then iterate one by one through all the cluster chunks before it finds the desired chunk.

For example, if we have the chunk hashmap from the above picture, and the game does a `getBlockState` call in the chunk with chunk coordinates 90 90, then it has to iterate through 1221 cluster chunks before it finds that chunk. This slows down the `getBlockState` call by a lot.

This slowdown makes cluster chunks a quite subtle but incredibly versatile method for influencing race conditions.

## Applications
Cluster Chunks are used for:
- Increasing the lifetime of stained glass threads in various [async chunk load setups](async-chunk-loading.md).
- Increasing the number of positions in which [rehash chunk swaps](async-chunk-loading.md#rehash-chunk-swap) can be done without crashing the asnyc thread.
- Increasing the success chances of [unload chunk swaps](async-chunk-loading.md#unload-chunk-swap).
- Increasing the success chances of [falling block swaps](../falling-block/falling-block-swaps.md#optimizing-chances-with-cluster-chunks).
- Increasing the success chances of player head creation.

## Cluster Finder Programs

The positions of cluster chunks are usually calculated using third party programs.

### Earthcomputer
Earthcomputer's Cluster Chunk Finder can be downloaded [here](https://github.com/Earthcomputer/FallingClusterFinderJava/releases).

A superficial tutorial for this cluster finder is in [Falling Block Episode 3, at 12:37](https://www.youtube.com/watch?v=8-AumLja16A&t=757s).

The cluster finder is primarily intended for [unload chunk swaps](async-chunk-loading.md#unload-chunk-swap), but can also be used for other purposes.

### Vastech
The [vastech edition of carpet mod](https://github.com/Void-Skeleton/Carpet-Vastech-Addition).
contains a cluster finder for improving [falling block swap success rates](../falling-block/falling-block-swaps.md#optimizing-chances-with-cluster-chunks).

To use it you need to set a few values first:

`/cluster set targetStart <x> <z>`

`/cluster set targetEnd <x> <z>`

This would be the two corners in which you want to search for the optimal chunk to be clustered.

Then `/cluster set hashSize <n>`, `/cluster set clusterSize <n>`
to set the hash size and the number of cluster chunks.

Then `/cluster set clusterWidth <w>` to set the width of the cluster grid
and `/cluster set clusterSearchStart <x> <z>` to set the northwest corner of the cluster grid.

Now you run `/cluster compute optimalClustering` so that it will compute the optimal target chunk.

Then `/cluster compute clusterChunks`, this will compute for you the cluster chunks for a few optimal and suboptimal hash starts, and tell you how large the cluster should be
you should choose the cluster with the smallest size, and record its hash start
then run `/cluster set desiredHashStart <hash start>` to set this hash start
then everything is ready.

`/cluster construct loadCluster` will load all the cluster chunks.

`/cluster construct makeClusterLoader` will make you a basic loader with chest and hoppers that will load the cluster.

The cluster finder assumes that you pre-load the 2x2 area of chunks that are within 8 blocks of the sand block with which you do the falling block swaps.
The cluster finder does not ensure that such a pre-loaded 2x2 remains unclustered when downsizing the chunk hashmap from a larger hashsize. Sometimes one has to manually remove a few of the calculated cluster chunks to ensure this.

### Cheater Codes
A cluster chunk finder by Cheater Codes intended for [rehash chunk swaps](async-chunk-loading.md#rehash-chunk-swap) can be found [here](https://github.com/CheaterCodes/easy-cluster/tree/main).

The cluster finder calculates a [rectilinear minimum spanning tree](https://en.wikipedia.org/wiki/Rectilinear_minimum_spanning_tree) containing the cluster chunks, in order to minimize the number of non-cluster chunks that are loaded whenever one loads the cluster chunks. This is necessary to prevent the chunk hashmap from upsizing too early.

The following picture shows how such a cluster looks on a carpet chunk debug screen:

![Cheater Cluster in chunk debug screen](../../images/CheaterCodesCluster2.PNG)

The following images shows which of those chunks are cluster chunks and which ones are not.
The white chunks are cluster chunks. The grey chunks are non-cluster chunks that need to be loaded to load the cluster:

![Cheater Cluster](../../images/CheaterCodesCluster.png)



