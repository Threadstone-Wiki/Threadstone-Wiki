This page is about async chunk loading.

Whenever you place or break a stained glass block, the game starts a new asynchronous thread which checks for beacons below the stained glass block.
More precisely it runs the following code in the BlockBeacon class.

```
public static void updateColorAsync(final World worldIn, final BlockPos glassPos)
    {
        HttpUtil.DOWNLOADER_EXECUTOR.submit(new Runnable()
        {
            public void run()
            {
                Chunk chunk = worldIn.getChunkFromBlockCoords(glassPos);

                for (int i = glassPos.getY() - 1; i >= 0; --i)
                {
                    final BlockPos blockpos = new BlockPos(glassPos.getX(), i, glassPos.getZ());

                    if (!chunk.canSeeSky(blockpos))
                    {
                        break;
                    }

                    IBlockState iblockstate = worldIn.getBlockState(blockpos);

                    if (iblockstate.getBlock() == Blocks.BEACON)
                    {
                        ((WorldServer)worldIn).addScheduledTask(new Runnable()
                        {
                            public void run()
                            {
                                TileEntity tileentity = worldIn.getTileEntity(blockpos);

                                if (tileentity instanceof TileEntityBeacon)
                                {
                                    ((TileEntityBeacon)tileentity).updateBeacon();
                                    worldIn.addBlockEvent(blockpos, Blocks.BEACON, 1, 0);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
```
In the middle of that code block there is a getBlockState() call, which is run on the async thread.
This getBlockState() call can load chunks on the async thread.
When a chunk is loaded on an async thread, this is called an *async chunk load*.

Making the getBlockState() call of the above code load chunks is quite difficult,
because the chunk in which all of these calls happen is already loaded when the thread is started.
To load a chunk with these getBlockState() calls one either needs to unload the chunk containing the stained glass, while the async thread is running,
or load the chunk even though it is already loaded.

If the chunk gets loaded even though it is already loaded, then this is called a *chunk swap*.

If the chunk gets unloaded while the async thread is running, then the resulting async chunk load this is called a *regular load*.

Unloading the chunk while async threads are running is difficult, because if the chunk is scheduled to unload in the next [unload phase](../tick-phases.md), and the async thread does a getBlockState() call before the chunk is actually unloaded, then the scheduled unloading gets cancelled, and the chunk does not get unloaded in the next unload phase.

# Chunk swap

In each dimension the loaded chunks are stored in a [Long2ObjectOpenHashmap](https://github.com/karussell/fastutil/blob/master/src/it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap.java).
The Long2ObjectOpenhashmap does not support asynchronous operations. If multiple threads access the Long2ObjectOpenhashmap at the same time, it can fail to work as intended.
In particular it is possible to load chunks that are already loaded. For this there are two different methods.

## Rehash chunk swap
A detailed explanation of rehash chunk swaps is in [cool mann's homework](https://docs.google.com/document/d/1rTKfmVLAtmvBMWW1QSgnetSG8Fuit5CaUvV77T9SgXk/edit)

## Unload chunk swap

# Regular load

## Void's synchronized method
