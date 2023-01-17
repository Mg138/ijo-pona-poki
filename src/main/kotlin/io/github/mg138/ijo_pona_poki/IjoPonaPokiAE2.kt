package io.github.mg138.ijo_pona_poki

import appeng.api.IAEAddonEntrypoint
import appeng.api.ids.AEBlockIds
import appeng.api.storage.StorageCells
import appeng.client.render.model.AutoRotatingBakedModel
import appeng.hooks.ModelsReloadCallback
import io.github.mg138.ijo_pona_poki.blocks.AdvancedInscriber
import io.github.mg138.ijo_pona_poki.disk.DISKCellHandler
import io.github.mg138.ijo_pona_poki.items.DISKDrives

@Suppress("unused")
object IjoPonaPokiAE2 : IAEAddonEntrypoint {
    override fun onAe2Initialized() {
        DISKDrives
        AdvancedInscriber

        StorageCells.addCellHandler(DISKCellHandler)
    }
}