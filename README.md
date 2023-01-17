# ijo-pona-poki 

Complete rewrite of the mod `AE2Things`, a port of `ae2stuff`.

Uses **Quilt**.

There may be new features, or not.

## Why a rewrite?

We wanted it to work with `1.19.2`, but then we realized it barely worked anyway, and the code was insufferable.

No offense, we wanted to contribute back to the codebase, too.

## What the heck even is this name

The name is in [Toki Pona](https://tokipona.org/).

The reason for this is that we couldn't find a name similar to `AE2Things` or `ae2stuff`, the only words left are like, `thingies` or `shit` and we'd rather not call it `ae2shit`.

So we asked a person who knows of `Toki Pona` about naming, they told me a bit about how `Toki Pona` grammar works, and we ended up with `ijo pona poki`.

- `ijo ` - thing
- `pona` - good
- `poki` - container/box/storage

So, quite literally, "thing good storage," or good things about storage.

## Differences and Contents

### Cut Content

- The bigger cells.
    * We don't see a reason to add them,
      some of them already overlap with the new AE2's introduction of 256k cells,
      and then there are many other mods adding bigger ones. 

### Crystal Growth Chamber

Now acts as an easy one-block solution of growing Certus Quartz.

However, there are some caveats.

### Fortune Upgrades

An upgrade for the Crystal Growth Chamber, improves yields, but increases resource usage.

### Advanced Inscriber

Literally: Inscriber that holds a stack instead of a single item, and has more slots for acceleration cards.

Also inserts the output to the ME System directly; this is an ae2things feature that we simply copied over -- not sure whether to keep it or not.

### DISK - "Deep Item Storage disK"

We're not sure how the original was designed, but here's the specification of a DISK:

- Only items allowed.
    * (obviously)
- No types, you can store all items equally.
    * *(This is identical to AE2Things)*
- 1K, 4K, 16K, 64K are in multiples of 1000, instead of 1024.
    * aka: a 64K ae2 cell has 65536 bytes, but a 64K DISK only has 64000.
    * *(This is identical to AE2Things)*
- However, each item takes up one byte.
    * compared to ae2 cells, which can hold *8* items in one single byte: bad!
      if you want bigger storage for a fixed set of items, prefer the ae2 cells!
- Expensive
    * Expensive. *(This is identical to AE2Things)*
- The biggest DISK is only 64K
    * Due to balance reasons, there won't be bigger DISKs.
- Portable DISKs
    * They only have 1/3 the bytes compared to normal DISKs
    * Backpack anyone?

# Credits

The [original AE2Things mod](https://github.com/ProjectET/AE2Things), for the art, concepts and namesake.

The [original ae2stuff mod](https://github.com/bdew-minecraft/ae2stuff), for concepts and namesake.
