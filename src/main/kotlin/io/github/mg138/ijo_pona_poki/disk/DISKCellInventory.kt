package io.github.mg138.ijo_pona_poki.disk

import appeng.api.config.Actionable
import appeng.api.networking.security.IActionSource
import appeng.api.stacks.AEKey
import appeng.api.stacks.KeyCounter
import appeng.api.storage.cells.CellState
import appeng.api.storage.cells.StorageCell
import io.github.mg138.ijo_pona_poki.world.DISKStorage
import net.minecraft.text.Text
import java.util.UUID

class DISKCellInventory(
    val item: DISKCellItem,
    val uuid: UUID,
) : StorageCell {
    private val capacity: Long = this.item.capacity()

    private fun inventory() = DISKCellHandler.getCellStorage(this.item, this.uuid)
    fun availableBytes() = this.capacity
    fun usedBytes() = this.inventory().usedBytes()

    override fun getStatus() =
        when (this.usedBytes()) {
            0L -> CellState.EMPTY
            this.availableBytes() -> CellState.FULL
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

    override fun insert(key: AEKey?, amount: Long, mode: Actionable, source: IActionSource?): Long {
        val result = this.inventory().insert(key, amount, mode)

        if (result > 0 && mode == Actionable.MODULATE) {
            this.markDirty()
        }

        return result
    }

    override fun extract(key: AEKey?, amount: Long, mode: Actionable, source: IActionSource?): Long {
        val result = this.inventory().extract(key, amount, mode)

        if (result > 0 && mode == Actionable.MODULATE) {
            this.markDirty()
        }

        return result
    }

    override fun getAvailableStacks(out: KeyCounter) {
        this.inventory().getAvailableStacks(out)
    }
}