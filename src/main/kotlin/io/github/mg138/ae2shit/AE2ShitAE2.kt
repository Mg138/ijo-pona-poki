package io.github.mg138.ae2shit

import appeng.api.IAEAddonEntrypoint
import appeng.api.storage.StorageCells
import io.github.mg138.ae2shit.blocks.AdvancedInscriber
import io.github.mg138.ae2shit.blocks.CrystalGrowth
import io.github.mg138.ae2shit.disk.DISKCellHandler
import io.github.mg138.ae2shit.items.DISKDrives

@Suppress("unused")
object AE2ShitAE2 : IAEAddonEntrypoint {
    override fun onAe2Initialized() {
        DISKDrives
        AdvancedInscriber
        CrystalGrowth

        StorageCells.addCellHandler(DISKCellHandler)
    }
}