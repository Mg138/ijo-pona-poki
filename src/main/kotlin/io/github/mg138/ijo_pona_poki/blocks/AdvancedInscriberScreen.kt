package io.github.mg138.ijo_pona_poki.blocks

import appeng.client.gui.implementations.UpgradeableScreen
import appeng.client.gui.style.ScreenStyle
import appeng.client.gui.widgets.ProgressBar
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.quiltmc.loader.api.minecraft.ClientOnly

@ClientOnly
class AdvancedInscriberScreen(
    handler: AdvancedInscriberScreenHandler,
    playerInventory: PlayerInventory,
    title: Text,
    style: ScreenStyle,
) : UpgradeableScreen<AdvancedInscriberScreenHandler>(handler, playerInventory, title, style) {
    private val progressBar = ProgressBar(handler, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL)

    override fun updateBeforeRender() {
        super.updateBeforeRender()
        val progress = this.handler.currentProgress * 100 / this.handler.maxProgress
        this.progressBar.setFullMsg(Text.of("$progress%"))
    }

    init {
        this.widgets.add("progressBar", this.progressBar)
    }
}