package io.github.mg138.ijo_pona_poki.disk

import appeng.api.config.Actionable
import appeng.api.networking.security.IActionSource
import appeng.api.stacks.AEKey
import appeng.api.stacks.KeyCounter
import appeng.api.storage.cells.CellState
import appeng.api.storage.cells.StorageCell
import io.github.mg138.ijo_pona_poki.world.DISKStorage
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import java.util.UUID

class DISKCellInventory(
    val item: DISKCellItem,
    private val uuid: UUID,
    private val itemStack: ItemStack
) : StorageCell {
    companion object {
        const val BYTES_LEFT = "bytes_left"
    }

    private val capacity: Long = this.item.capacity()

    private fun inventory() = DISKCellHandler.getCellStorage(this.item, this.uuid).also(this::writeBytesLeft)
    fun usedBytes() = this.itemStack.orCreateNbt.getLong(Companion.BYTES_LEFT)
    private fun writeBytesLeft(storage: DISKCellIStorage) {
        this.itemStack.orCreateNbt.putLong(Companion.BYTES_LEFT, storage.usedBytes())
    }

    override fun getStatus() =
        when (this.usedBytes()) {
            0L -> CellState.EMPTY
            this.capacity -> CellState.FULL
            else -> CellState.NOT_EMPTY
        }
    override fun getDescription(): Text = this.item.asItem().name
    override fun getIdleDrain(): Double = this.item.idleDrain



    private var persist = false
    private fun markDirty() {
        this.persist = true
    }

    private fun markPersisted() {
        this.persist = false
    }


    override fun persist() {
        if (!this.persist) return

        DISKStorage.markDirty()

        this.markPersisted()
    }
    private fun saveChanges() {
        this.markDirty()
        this.writeBytesLeft(this.inventory())
    }

    override fun insert(key: AEKey?, amount: Long, mode: Actionable, source: IActionSource?): Long {
        val result = this.inventory().insert(key, amount, mode)

        if (result > 0 && mode == Actionable.MODULATE) {
            this.saveChanges()
        }

        return result
    }

    override fun extract(key: AEKey?, amount: Long, mode: Actionable, source: IActionSource?): Long {
        val result = this.inventory().extract(key, amount, mode)

        if (result > 0 && mode == Actionable.MODULATE) {
            this.saveChanges()
        }

        return result
    }

    override fun getAvailableStacks(out: KeyCounter) {
        this.inventory().getAvailableStacks(out)
    }
}