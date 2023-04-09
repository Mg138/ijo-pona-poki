package io.github.mg138.ae2shit.blocks

import appeng.client.gui.implementations.UpgradeableScreen
import appeng.client.gui.style.ScreenStyle
import appeng.client.gui.widgets.ProgressBar
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.quiltmc.loader.api.minecraft.ClientOnly

@ClientOnly
class CrystalGrowthScreen(
    handler: CrystalGrowthScreenHandler,
    playerInventory: PlayerInventory,
    title: Text,
    style: ScreenStyle,
) : UpgradeableScreen<CrystalGrowthScreenHandler>(handler, playerInventory, title, style) {
    private val progressBar = ProgressBar(this.handler, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL)

    override fun updateBeforeRender() {
        super.updateBeforeRender()
        val progress = this.handler.currentProgress * 100 / this.handler.maxProgress
        this.progressBar.setFullMsg(Text.of("$progress%"))
    }

    init {
        this.widgets.add("progressBar", this.progressBar)
    }
}