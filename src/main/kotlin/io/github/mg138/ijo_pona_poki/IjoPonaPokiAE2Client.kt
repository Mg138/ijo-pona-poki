package io.github.mg138.ijo_pona_poki

import appeng.api.IAEAddonEntrypoint
import appeng.api.ids.AEBlockIds
import appeng.client.gui.style.StyleManager
import appeng.client.render.model.AutoRotatingBakedModel
import appeng.hooks.ModelsReloadCallback
import io.github.mg138.ijo_pona_poki.blocks.AdvancedInscriberScreen
import io.github.mg138.ijo_pona_poki.blocks.AdvancedInscriber
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.minecraft.ClientOnly

@Suppress("unused")
@ClientOnly
object IjoPonaPokiAE2Client : IAEAddonEntrypoint {
    override fun onAe2Initialized() {
        HandledScreens.register(AdvancedInscriber.ADVANCED_INSCRIBER_SCREEN_HANDLER) { menu, playerInv, title ->
            val style = StyleManager.loadStyleDoc("/screens/inscriber.json")

            AdvancedInscriberScreen(menu, playerInv, title, style)
        }
        /*
        HandledScreens.register((CrystalGrowthMenu.CRYSTAL_GROWTH_SHT, { menu, playerInv, title ->
            val style: ScreenStyle
            style = try {
                StyleManager.loadStyleDoc("/screens/crystal_growth.json")
            } catch (e: Exception) {
                throw RuntimeException("Failed to read Screen JSON file", e)
            }
            CrystalGrowthRootPanel(menu, playerInv, title, style)
        })
         */
    }
}