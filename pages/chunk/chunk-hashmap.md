This page is about the chunk hashmap.


# Introduction

In each dimension the loaded chunks are stored in a [`Long2ObjectOpenHashmap`, from fast-utils version 7.1.0](../../resources/Long2ObjectOpenHashmap.java).

In carpet mod one can print out the chunk hashmap using the command `/loadedChunks dump`. The command creates an excel file in the server folder, which looks like this

![hashmap dump](../../images/chunkHashmap.PNG)

Every entry in this excel file is a loaded chunk.

The length of the excel file is the *hashsize* of the chunk hashmap. The hashsize is always a power of 2, and can increase or decrease if many chunks are loaded or unloaded. See [resizing](#rehash).

Every loaded chunk has *x and z coordinates*, a *key*, a *hash value*, and an *index* that it occupies in the chunk hashmap.

- The x and z coordinates are chunk coordinates, which means they are 16 times the block coordinates of the chunk.
For example the chunk with the chunk coordinates -152 -15 has block coordinates x=-2432, z=-240.

- The key of a chunk depends solely on the x and z coordinates of the chunk, and is calculated by the formula
`(long)x & 4294967295L | ((long)z & 4294967295L) << 32`.

- The hash value of a chunk depends on the key of the chunk and the hashsize of the chunk hashmap.
Using the key one first calculates the `HashCommon.mix` function. TODO: Find source code for that function.

The hash value of the chunk is then calculated by the formula `(int)HashCommon.mix(k) & this.mask`
where `mask` is an integer that depends on the hashsize of the chunk hashmap.
The hash value that a chunk has in the chunk hashmap has nothing to do with the hash value the chunk has in the [chunk unload order](chunk.md#unloading).

- The index of a chunk depends on when exactly the chunk is entered into the hashmap.

## `insert` - Loading Chunks <a name="insert"/>
When a chunk gets loaded, the game adds it to the chunk hashmap,
which happens using the following `insert` function, 
where `v` is the chunk and `k` is the key of the chunk.

```
 private int insert(long k, V v) {
        int pos;
        if (k == 0L) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            long[] key = this.key;
            long curr;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) != 0L) {
                if (curr == k) {
                    return pos;
                }
                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (curr == k) {
                        return pos;
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return -1;
    }
```

The game will calculate the hash value of the chunk,
and then check whether the index equal to the hash value is unoccupied in the chunk hashmap.
If it is unoccupied, the chunk will be entered into that index.
Otherwise the chunk will try to enter the next index.
It will repeatedly increase the index by 1 until it finds an empty spot, and then enters that spot in the chunk hashmap.

For example in the picture in the introduction the chunk at position -145 -8 has hash value 5,
but it entered index 6, because when it was added index 5 was already occupied by the chunk at position -139 -9,
so the chunk at -145 -8 had to take the next index.

After every `insert` operation the chunk hashmap will check whether it should [upsize](#rehash).

## `get` - Getting Chunks <a name="get"/>
If the game wants to do anything with a chunk at a certain position, it first needs to get that chunk from the chunk hashmap.
This happens for example every time the game does a `getBlockState` call or `setBlockState` call in the chunk.

When the game tries to get a chunk from the chunk hashmap it calls the following `get` function, where `k` is the key of the chunk.
```
public V get(long k) {
        if (k == 0L) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        } else {
            long[] key = this.key;
            long curr;
            int pos;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
                return this.defRetValue;
            } else if (k == curr) {
                return this.value[pos];
            } else {
                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (k == curr) {
                        return this.value[pos];
                    }
                }
                return this.defRetValue;
            }
        }
    }
```
It calculates the hash value of the chunk, and then tries to see whether a chunk with the correct key is at the index corresponding to the hash value.
If it finds such a chunk it returns it. Otherwise it will look for the chunk at the next index.
It will continually increase the index where it looks for the chunk by 1, until it has either found a chunk with the correct key and returns it,
or it has found an empty spot in the chunk hashmap.
If it finds an empty spot, it will return `defRetValue`, and the game will assume that the chunk in question is not loaded.

This `get` method can be slowed down using [cluster chunks](#cluster-chunks).

## `remove` - Unloading Chunks <a name="remove"/>
When a chunk gets unloaded, the game calls the following `remove` function, where `k` is the key of the chunk.
```
  public V remove(long k) {
        if (k == 0L) {
            return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
        } else {
            long[] key = this.key;
            long curr;
            int pos;
            if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
                return this.defRetValue;
            } else if (k == curr) {
                return this.removeEntry(pos);
            } else {
                while((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (k == curr) {
                        return this.removeEntry(pos);
                    }
                }
                return this.defRetValue;
            }
        }
    }
```

The game first tries to find the chunk, similar to the `get` function.
Once it finds the chunk in the chunk hashmap, it calls the `removeEntry` function, where `pos` is the index of the chunk.

```
   private V removeEntry(int pos) {
        V oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }

        return oldValue;
    }
```
In that function it calls the `shiftKeys` function, where `pos` is the index of the chunk.
```
  protected final void shiftKeys(int pos) {
        long[] key = this.key;
        while(true) {
            int last = pos;
            pos = pos + 1 & this.mask;
            long curr;
            while(true) {
                if ((curr = key[pos]) == 0L) {
                    key[last] = 0L;
                    this.value[last] = null;
                    return;
                }
                int slot = (int)HashCommon.mix(curr) & this.mask;
                if (last <= pos) {
                    if (last >= slot || slot > pos) {
                        break;
                    }
                } else if (last >= slot && slot > pos) {
                    break;
                }
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }
```
The `shiftKeys` method removes the chunk from the chunk hashmap. It then checks whether moving any other chunk in the hashmap to the index of the removed chunk can reduce the difference between index and hash value of those chunks.
It repeatedly moves chunks from one index to the index of the previously moved chunk, until it is no longer possible to reduce the difference between index and hash value of a chunk without increasing that of another chunk.

After it has completed the `shiftKeys` function, the chunk hashmap will check whether it should [downsize](#rehash).


## `rehash` - Resizing Chunk Hashmap <a name="rehash"/>

# Race Conditions
The `Long2ObjectOpenhashmap` is a data structure that does not support asynchronous operations. If multiple threads access the `Long2ObjectOpenhashmap` at the same time, it can fail to work as intended.
In minecraft, the chunk hashmap can be accessed simultaneously by both the [main thread](../threads.md#main-thread) and the [stained glass threads](../threads.md#stained-glass-threads).
This makes many race conditions with the chunk hashmap possible in minecraft.

The race conditions occur when pairs of the functions `get`, `insert`, `remove` and `rehash` are called on two different threads at the same time.

Out of all possible pairs of these four functions, only the the combinations `get`+`get` and `insert`+`get` cause no race conditions. All other combinations do lead to some kind of race condition.

The combination `remove`+`remove` is not possible to in minecraft, because the `remove` function can only be called on the main thread, and not on stained glass threads.
All other pairs of the above four functions are possible in minecraft.

## `get` + `remove` - Unload Chunk Swap
If one thread calls the `remove` method while another thread calls the `get` method, then it can happen that the `get` method fails to find a chunk, even when the chunk is in the chunk hashmap.
This is the basis for [unload chunk swaps](async-chunk-loading.md#unload-chunk-swap).

## `get` + `rehash` - Rehash Chunk Swap
If one thread calls the `rehash` method while another thread calls the `get` method, then it can happen that the `get` method fails to find a chunk, even when the chunk is in the chunk hashmap.
This is the basis for [rehash chunk swaps](async-chunk-loading.md#rehash-chunk-swap).

## `insert` + `insert` - Changing Chunk Positions

In the `insert` code we have the code lines
```
        this.key[pos] = k;
        this.value[pos] = v;
```
If two threads simultaneously load chunks that would get assigned the same index in the chunk hashmap, then these two lines of code can be executed by both threads simultaneously.

If the first thread does `this.key[pos] = k;`, and then the second thread does `this.key[pos] = k;` and `this.value[pos] = v;`,
and then the first thread does `this.value[pos] = v;`, then at the index in which both chunks tried to enter, we have the key of the chunk from the second thread, but the value at this index is the chunk from the first thread.

This means that the chunk that the first thread loaded will be at the position where the second thread tried to load its chunk.

This race condition is very rare.

## `insert` + `remove`

## `insert` + `rehash`

## `remove` + `rehash` - Wormhole Chunk

If the async thread upsizes the chunk hashmap while the main thread unloads a chunk, then it can happen that a single chunk instance has two keys in the chunk hashmap, so that this single chunk instance appears at two different positions in the game.

## `rehash` + `rehash`


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



