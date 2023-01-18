package io.github.mg138.ijo_pona_poki

import appeng.api.IAEAddonEntrypoint
import appeng.api.storage.StorageCells
import io.github.mg138.ijo_pona_poki.blocks.AdvancedInscriber
import io.github.mg138.ijo_pona_poki.blocks.CrystalGrowth
import io.github.mg138.ijo_pona_poki.disk.DISKCellHandler
import io.github.mg138.ijo_pona_poki.items.DISKDrives

@Suppress("unused")
object IjoPonaPokiAE2 : IAEAddonEntrypoint {
    override fun onAe2Initialized() {
        DISKDrives
        AdvancedInscriber
        CrystalGrowth

        StorageCells.addCellHandler(DISKCellHandler)
    }
}