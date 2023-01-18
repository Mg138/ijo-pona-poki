package io.github.mg138.ijo_pona_poki.disk

import appeng.api.storage.cells.CellState
import appeng.api.storage.cells.ICellHandler
import appeng.api.storage.cells.ISaveProvider
import appeng.core.localization.Tooltips
import io.github.mg138.ijo_pona_poki.IjoPonaPoki
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*

object DISKCellHandler: ICellHandler {
    private val storages: MutableMap<UUID, DISKCellIStorage> = mutableMapOf()
    private const val DISK_UUID = "disk_uuid"

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

        return DISKCellInventory(diskItem, this.getOrPutUuid(itemStack))
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

    fun addCellInfo(itemStack: ItemStack, tooltip: MutableList<Text>) {
        tooltip.add(Text.translatable("disk.${IjoPonaPoki.MOD_ID}.desc").styled { it.withColor(Formatting.AQUA).withItalic(true) })
        tooltip.add(Text.empty())

        val diskItem = this.asDiskCell(itemStack) ?: return
        val capacity = diskItem.capacity()

        if (itemStack.orCreateNbt.containsUuid(this.DISK_UUID)) {
            val cell = this.getCellInventory(itemStack, null) ?: return
            val uuid = cell.uuid
            val usedBytes = cell.usedBytes()

            tooltip.add(Tooltips.bytesUsed(usedBytes, capacity))
            tooltip.add(Text.literal(uuid.toString()).styled { it.withColor(Formatting.DARK_GRAY) })
        } else {
            tooltip.add(Tooltips.bytesUsed(0, capacity))
        }

        // TODO needed?
        /*
        if (inventory.isPreformatted()) {
            val list =
                (if (inventory.getPartitionListMode() === IncludeExclude.WHITELIST) GuiText.Included else GuiText.Excluded)
                    .text()
            if (inventory.isFuzzy()) {
                lines.add(GuiText.Partitioned.withSuffix(" - ").append(list).append(" ").append(GuiText.Fuzzy.text()))
            } else {
                lines.add(
                    GuiText.Partitioned.withSuffix(" - ").append(list).append(" ").append(GuiText.Precise.text())
                )
            }
        }
         */
    }
}