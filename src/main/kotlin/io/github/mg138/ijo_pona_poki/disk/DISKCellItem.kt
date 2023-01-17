package io.github.mg138.ijo_pona_poki.disk

import appeng.api.config.FuzzyMode
import appeng.api.storage.cells.ICellWorkbenchItem
import appeng.api.upgrades.IUpgradeInventory
import appeng.api.upgrades.UpgradeInventories
import net.minecraft.item.ItemStack

interface DISKCellItem : ICellWorkbenchItem {
    /**
     * The number of bytes that can be stored on this type of storage cell.
     *
     * @return capacity, in bytes
     */
    fun capacity(): Long

    /**
     * Might have usage later. For now, it's not configurable.
     */
    override fun getUpgrades(stack: ItemStack?): IUpgradeInventory {
        return UpgradeInventories.empty()
    }

    override fun getFuzzyMode(itemStack: ItemStack?): FuzzyMode? {
        val fuzzyMode = itemStack?.orCreateNbt?.getString("FuzzyMode") ?: return FuzzyMode.IGNORE_ALL

        if (fuzzyMode.isEmpty()) return FuzzyMode.IGNORE_ALL

        return try {
            FuzzyMode.valueOf(fuzzyMode)
        } catch (_: Throwable) {
            FuzzyMode.IGNORE_ALL
        }
    }

    override fun setFuzzyMode(itemStack: ItemStack?, fuzzyMode: FuzzyMode) {
        itemStack?.orCreateNbt?.putString("FuzzyMode", fuzzyMode.name)
    }

    val idleDrain: Double
}