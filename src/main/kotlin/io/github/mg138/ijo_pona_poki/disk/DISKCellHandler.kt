package io.github.mg138.ijo_pona_poki.disk

import appeng.api.storage.cells.CellState
import appeng.api.storage.cells.ICellHandler
import appeng.api.storage.cells.ISaveProvider
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import java.util.*

object DISKCellHandler: ICellHandler {
    private val storages: MutableMap<UUID, DISKCellIStorage> = mutableMapOf()
    const val DISK_UUID = "disk_uuid"

    fun readFromNbt(nbt: NbtCompound) {
        this.storages.clear()

        nbt.keys
            .mapNotNull parse@{ key ->
                val tag = nbt.get(key) as? NbtCompound ?: return@parse null
                val storage = DISKCellIStorage.fromNbt(tag) ?: return@parse null

                Pair(UUID.fromString(key), storage)
            }
            .forEach { (uuid, storage) ->
                this.storages[uuid] = storage
            }
    }

    fun writeToNbt(nbt: NbtCompound) {
        this.storages.forEach { (uuid, storage) ->
            val tag = NbtCompound()

            if (storage.writeNbt(tag)) {
                nbt.put(uuid.toString(), tag)
            }
        }
    }

    private fun getOrPutUuid(itemStack: ItemStack): UUID {
        val nbt = itemStack.orCreateNbt

        if (!nbt.containsUuid(this.DISK_UUID)) {
            nbt.putUuid(this.DISK_UUID, UUID.randomUUID())
        }

        return nbt.getUuid(this.DISK_UUID)
    }

    fun getCellStorage(item: DISKCellItem, uuid: UUID) =
        this.storages.getOrPut(uuid) { DISKCellIStorage(item, emptyInv()) }

    private fun asDiskCell(itemStack: ItemStack?): DISKCellItem? {
        return itemStack?.item as? DISKCellItem
    }

    override fun isCell(itemStack: ItemStack?): Boolean = itemStack?.item is DISKCellItem

    override fun getCellInventory(itemStack: ItemStack, host: ISaveProvider?): DISKCellInventory? {
        val diskItem = this.asDiskCell(itemStack) ?: return null

        return DISKCellInventory(diskItem, this.getOrPutUuid(itemStack), itemStack)
    }

    fun getDiskColor(itemStack: ItemStack, tintIndex: Int): Int {
        return if (tintIndex == 1 && itemStack.orCreateNbt.containsUuid(this.DISK_UUID)) {
            val inventory = this.getCellInventory(itemStack, null)
            val cellStatus = inventory?.status ?: CellState.EMPTY

            cellStatus.stateColor
        } else {
            0xFFFFFF
        }
    }
}