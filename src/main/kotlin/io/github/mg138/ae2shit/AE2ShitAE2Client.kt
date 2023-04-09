package io.github.mg138.ae2shit

import appeng.api.IAEAddonEntrypoint
import appeng.client.gui.style.StyleManager
import io.github.mg138.ae2shit.blocks.AdvancedInscriberScreen
import io.github.mg138.ae2shit.blocks.AdvancedInscriber
import io.github.mg138.ae2shit.blocks.CrystalGrowth
import io.github.mg138.ae2shit.blocks.CrystalGrowthScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.quiltmc.loader.api.minecraft.ClientOnly

@Suppress("unused")
@ClientOnly
object AE2ShitAE2Client : IAEAddonEntrypoint {
    override fun onAe2Initialized() {
        HandledScreens.register(AdvancedInscriber.SCREEN_HANDLER) { menu, playerInv, title ->
            val style = StyleManager.loadStyleDoc("/screens/inscriber.json")

            AdvancedInscriberScreen(menu, playerInv, title, style)
        }

        HandledScreens.register(CrystalGrowth.SCREEN_HANDLER) { menu, playerInv, title ->
            val style = StyleManager.loadStyleDoc("/screens/crystal_growth.json")

            CrystalGrowthScreen(menu, playerInv, title, style)
        }
    }
}