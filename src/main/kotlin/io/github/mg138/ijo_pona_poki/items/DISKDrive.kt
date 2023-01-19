package io.github.mg138.ijo_pona_poki.items

import appeng.api.storage.cells.CellState
import appeng.hooks.AEToolItem
import appeng.util.InteractionUtil
import io.github.mg138.ijo_pona_poki.disk.DISKCellHandler
import io.github.mg138.ijo_pona_poki.disk.DISKCellHandler.getCellInventory
import io.github.mg138.ijo_pona_poki.disk.DISKCellItem
import io.github.mg138.ijo_pona_poki.items.DISKDrives.diskSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World


class DISKDrive(
    private val coreItem: ItemConvertible,
    private val housingItem: ItemConvertible,
    private val capacity: Long,
    override val idleDrain: Double,
) : Item(diskSettings()), DISKCellItem, AEToolItem {
    companion object {
        fun disassembleDrive(
            drive: DISKCellItem,
            coreItem: ItemConvertible,
            housingItem: ItemConvertible,
            stack: ItemStack, world: World, player: PlayerEntity?,
        ): Boolean {
            if (world.isClient()) return false
            if (!InteractionUtil.isInAlternateUseMode(player)) return false
            if (player?.mainHandStack != stack) return false

            val playerInv = player.inventory ?: return false
            val inventory = getCellInventory(stack, null) ?: return false

            if (inventory.status == CellState.EMPTY && inventory.usedBytes() == 0L) {
                playerInv.setStack(playerInv.selectedSlot, ItemStack.EMPTY)
                playerInv.offerOrDrop(ItemStack(coreItem))
                drive.getUpgrades(stack).forEach { playerInv.offerOrDrop(it) }
                playerInv.offerOrDrop(ItemStack(housingItem))

                return true
            }

            return false
        }
    }

    override fun capacity() = this.capacity

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        Companion.disassembleDrive(this, this.coreItem, this.housingItem, player.getStackInHand(hand), world, player)

        return TypedActionResult(ActionResult.success(world.isClient()), player.getStackInHand(hand))
    }

    override fun onItemUseFirst(itemStack: ItemStack, context: ItemUsageContext): ActionResult {
        val result = Companion.disassembleDrive(this, this.coreItem, this.housingItem, itemStack, context.world, context.player)

        return if (result) ActionResult.success(context.world.isClient()) else ActionResult.PASS
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?,
    ) {
        DISKCellHandler.addCellInfo(stack, tooltip)
    }
}