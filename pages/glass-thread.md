This page is about stained glass threads.

# Introduction

Every time you place or break a stained glass block in minecraft, the game starts a new [thread](threads.md#stained-glass-thread), called the *stained glass thread*.

Every stained glass thread executes the following code:
```
public static void updateBeam(World world, BlockPos pos) {
		HttpUtil.DOWNLOAD_THREAD_FACTORY.submit(new Runnable() {
			@Override
			public void run() {
				WorldChunk c_6849228 = world.getChunk(pos);

				for (int i = pos.getY() - 1; i >= 0; --i) {
					final BlockPos c_3674802 = new BlockPos(pos.getX(), i, pos.getZ());
					if (!c_6849228.hasSkyAccess(c_3674802)) {
						break;
					}

					BlockState c_2441996 = world.getBlockState(c_3674802);
					if (c_2441996.getBlock() == Blocks.BEACON) {
						((ServerWorld)world).submit(new Runnable() {
							@Override
							public void run() {
								BlockEntity c_3622326 = world.getBlockEntity(c_3674802);
								if (c_3622326 instanceof BeaconBlockEntity) {
									((BeaconBlockEntity)c_3622326).update();
									world.addBlockEvent(c_3674802, Blocks.BEACON, 1, 0);
								}
							}
						});
					}
				}
			}
		});
	}
```

The thread checks for beacon blocks below the stained glass, and whenever it finds a beacon, it schedules the beacon to change its beacon beam color on the main thread in the next [player phase](tick-phases.md#player-phase).

The `getBlockState` call in this piece of code can under unusual circumstances cause [async chunk loading](async-chunk-loading.md), which can trigger an [async terrain population](population.md#glass-threads-causing-async-updates),
which can trigger async block updates, which enables many powerful [threadstone exploits](async-line.md#applications).



# Slowing down stained glass threads
If one wants to perform [async chunk loading](async-chunk-loading.md), it is often important to slow down stained glass threads.
There are only two methods that can be used for slowing down stained glass threads:
- [Cluster chunks](chunk/chunk-hashmap.md#cluster-chunks) can slow down the `getBlockState` call in the stained glass thread code.
- Beacons can force the stained glass thread code to call the `((ServerWorld)world).submit` function, which slows down the thread.

The `((ServerWorld)world).submit` function
calls the following code in the `MinecraftServer` class
```
public <V> ListenableFuture<V> submit(Callable<V> event) {
		Validate.notNull(event);
		if (!this.isOnSameThread() && !this.hasStopped()) {
			ListenableFutureTask<V> listenableFutureTask = ListenableFutureTask.create(event);
			synchronized (this.pendingEvents) {
				this.pendingEvents.add(listenableFutureTask);
				return listenableFutureTask;
			}
		} else {
			try {
				return Futures.immediateFuture(event.call());
			} catch (Exception var6) {
				return Futures.immediateFailedCheckedFuture(var6);
			}
		}
	}
```


