package io.github.mg138.ae2shit.world

import io.github.mg138.ae2shit.disk.DISKCellHandler
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.PersistentState

object DISKStorage: PersistentState() {
    private const val DISK_STORAGE = "disk_storage"

    private fun diskStorage(nbt: NbtCompound): NbtCompound {
        if (!nbt.contains(this.DISK_STORAGE)) {
            nbt.put(this.DISK_STORAGE, NbtCompound())
        }

        return nbt.getCompound(this.DISK_STORAGE)
    }

    fun readFromNbt(nbt: NbtCompound): DISKStorage {
        val storage = this.diskStorage(nbt)

        DISKCellHandler.readFromNbt(storage)

        return this
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val storage = this.diskStorage(nbt)

        DISKCellHandler.writeToNbt(storage)

        return nbt
    }
}