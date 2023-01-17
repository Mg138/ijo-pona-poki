package io.github.mg138.ijo_pona_poki.items

import appeng.api.storage.StorageCells
import appeng.api.upgrades.IUpgradeInventory
import appeng.items.tools.powered.AbstractPortableCell
import io.github.mg138.ijo_pona_poki.disk.DISKCellHandler
import io.github.mg138.ijo_pona_poki.disk.DISKCellItem
import io.github.mg138.ijo_pona_poki.items.DISKDrive.Companion.DISK_SETTINGS
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World

class PortableDISK(
    private val capacity: Long,
    override val idleDrain: Double,
    private val recipeId: Identifier,
    menuType: ScreenHandlerType<*>,
) : AbstractPortableCell(menuType, DISK_SETTINGS), DISKCellItem {
    override fun getChargeRate(p0: ItemStack?): Double {
        return 80.0 // default of normal ae2 portal drives
    }

    override fun getRecipeId() = this.recipeId

    override fun capacity() = this.capacity

    override fun getUpgrades(stack: ItemStack?): IUpgradeInventory {
        return super<DISKCellItem>.getUpgrades(stack)
    }

    override fun appendTooltip(
        itemStack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        advancedTooltips: TooltipContext?
    ) {
        super.appendTooltip(itemStack, world, tooltip, advancedTooltips)

        DISKCellHandler.addCellInfo(itemStack, tooltip)
    }
}