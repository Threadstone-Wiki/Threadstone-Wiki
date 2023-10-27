This contains various bits of technical minecraft history.

# Slimeblocks, tnt duping and pig spawner generation

When mojang initially added slimeblocks, they were bouncy, but they did not stick to other slimeblocks and did not allow for easy flying machines.
CodeCrafted made a troll video [Minecraft: "Sticky" slime block | Pull multiple blocks](https://www.youtube.com/watch?v=IPm4m1zBd9Q) pretending that slimeblocks had a connectivity property, allowing sticky pistons to pull multiple slimeblocks at once. This was made in a snapshot during which slimeblocks did not have that property.
After that video a large part of the community wanted it to be real.

Panda4994 and KaboPC coded a mod that implemented the slimeblock connectivity properties we know today. They submitted it to mojang,
and mojang included it in the 1.8 snapshots. Panda4994 showcased the new slimeblock properties in [Sticky Slime Blocks](https://www.youtube.com/watch?v=EIK2l4KGB_8).

Since the 1.8 piston code was coded by Panda4994 and not Mojang, 1.8 is the first version since the introduction of pistons to not include a piston based TNT duplication bug.
TNT duplication exists in all version from [1.7.3 beta](https://www.youtube.com/watch?v=CmoM_Um8Ltw) to [1.7.10](https://www.youtube.com/watch?v=mIdaap8O8Vg), but not in 1.8.

At the same time, Panda4994's piston code makes it easy to [generate pig spawner in 1.8](https://www.youtube.com/watch?v=A5inDOkNUQo).

TNT duplication and [pig spawner generation](chunk/population.md#moving-immovable-blocks) are in some sense opposite bugs.

TNT duplication means that if a piston wants to push a block (like TNT), but the block then gets replaced by a different block (like air, because the TNT got ignited) the piston will still push the old block (the TNT).

Pig spawner generation means that if a piston wants to push a block (like stone), but the block then gets replaced by a different block (like a spawner, because of a terrain population) the piston will push the new block (the spawner).

Naively fixing one bug makes the other bug easier to perform.

The coding style of mojang makes tnt duplication easy and pig spawner generation difficult.

The coding style of Panda4994 makes tnt duplication difficult and pig spawner generation easy.

In 1.9 mojang changed the code of the BlockPistonBase class and re-implemented TNT duplication.
But they left the BlockPistonStructureHelper class by Panda4994 and KaboPC unchanged. For this reason it is still possible to pull spawners in 1.9-1.12., but the update order is much more precise and requires the [getBlockState() to setBlockState() property of terrain population](chunk/population.md#get-to-set).

In early 2017, before the technique for pulling spawners was discovered, Panda4994 and RedstoneSpire were trying to find a way to generate pig spawners.
Their idea was to cause an update suppression right after the spawner placement.
But before they finished and published their contraption, Myren Eario found and published the pulling spawners technique, and became the first to generate pig spawners.
Myren only found the technique because of the slimeblock code that Panda4994 made for 1.8.
If Panda4994 had included TNT duping in the 1.8 code, then he would have been the first one to generate pig spawners. Since he did not include TNT duping in his code, he was not the first.
After Myren's publication, Panda4994 and RedstoneSpire decided to not publish their update suppression contraption at all. The reasons for this only became clear much later.

Since any update suppression during terrain population necessarily turns on the instant falling flag, Panda4994 and RedstoneSpire inevitably discovered the instant falling flag during their research,
and also found the instant tile tick flag. They have one unlisted video about it: [Weird weird behaviour :D](https://www.youtube.com/watch?v=rjoaEOhIV6E).
The rest of the community only learned about instant tile ticks in 2019 when [cool mann discovered and published it](https://www.youtube.com/watch?v=hViDrnDCIwc).
Panda4994 and RedstoneSpire not only discovered instant tile ticks, but also realized that there was a way of turning it on that was independent of the stacksize of the server.
This means you do not need 10000 rails to turn it on, but only need a small tile tick clock to cause the update suppression.
This insight was only re-discovered by punchster2 in late 2021 during falling block research, and was published in [Falling Block Episode 1](https://www.youtube.com/watch?v=KU3lN1IUhuE).
Before that multiple servers build instant tile tick switches in survival using 10000 rails. for example [this server](https://www.youtube.com/watch?v=6VKNOyuqFnQ&t=510s) and [this server](https://www.youtube.com/watch?v=T7YRu9Yohec).

Since a stack-independent method for turning on instant tile ticks allows you to crash a server without too much effort, Panda4994 decided that the topic required responsible disclosure.
He probably intended to inform mojang about it, and get instant tile ticks fixed in some 1.11 version before publishing his contraption.

So if Myren had not published his pig spawner generation technique in early 2017, then the community would have found out about instant tile ticks in 2017 instead of 2019,
they would have found out about stack-independent methods for turning it on in 2017 instead of 2022, and instant tile ticks would probably have been fixed in 2017 in some 1.11 version. 

# Falling Block History
## Early History
The earliest appearance of the idea is in a private scicraft discord chat in 2017.
It is only worth reading cool mann's comments in this conversation, because the others do not yet understand his highly speculative idea.
![Coolmann1](../images/Coolmann1.PNG)

Coolmann mentions not only the idea of falling block swaps, but in the last comment also mentions the instant tile tick flag.

## Backdoor Tearm



## Threadstone Discord





