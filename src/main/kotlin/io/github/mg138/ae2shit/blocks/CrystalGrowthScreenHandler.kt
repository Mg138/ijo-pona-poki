package io.github.mg138.ae2shit.blocks

import appeng.api.inventories.InternalInventory
import appeng.api.stacks.AEFluidKey
import appeng.menu.SlotSemantics
import appeng.menu.guisync.GuiSync
import appeng.menu.implementations.UpgradeableMenu
import appeng.menu.interfaces.IProgressProvider
import appeng.menu.slot.AppEngSlot
import appeng.menu.slot.InaccessibleSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemStack

class CrystalGrowthScreenHandler(id: Int, playerInventory: PlayerInventory, host: CrystalGrowthBlockEntity)
    : UpgradeableMenu<CrystalGrowthBlockEntity>(CrystalGrowth.SCREEN_HANDLER, id, playerInventory, host),
        IProgressProvider {
    @GuiSync(2)
    var maxProcessingTime = -1

    @GuiSync(3)
    var processingTime = -1

    private class WaterSlot(inv: InternalInventory, i: Int) : InaccessibleSlot(inv, i) {
        override fun getDisplayStack(): ItemStack {
            return AEFluidKey.of(Fluids.WATER).wrapForDisplayOrFilter()
        }
    }

    init {
        val inv = host.internalInventory

        (0 until inv.size())
            .forEach { i ->
                this.addSlot(
                    if (i % 2 == 0) {
                        AppEngSlot(inv, i)
                    } else {
                        WaterSlot(inv, i)
                    },
                    SlotSemantics.STORAGE
                )
            }
    }

    override fun standardDetectAndSendChanges() {
        if (this.isServerSide) {
            this.maxProcessingTime = this.host.getMaxProcessingTime()
            this.processingTime = this.host.getProcessingTime()
        }
        super.standardDetectAndSendChanges()
    }

    override fun getCurrentProgress() = this.processingTime
    override fun getMaxProgress() = this.maxProcessingTime
}