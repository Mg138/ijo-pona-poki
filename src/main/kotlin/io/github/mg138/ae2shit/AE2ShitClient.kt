package io.github.mg138.ae2shit

import appeng.client.render.model.AutoRotatingBakedModel
import appeng.hooks.ModelsReloadCallback
import io.github.mg138.ae2shit.blocks.AdvancedInscriber
import io.github.mg138.ae2shit.blocks.AdvancedInscriber.advanced_inscriber
import io.github.mg138.ae2shit.blocks.AdvancedInscriberTESR
import io.github.mg138.ae2shit.disk.DISKCellHandler
import io.github.mg138.ae2shit.items.DISKDrives
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.loader.api.minecraft.ClientOnly
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

@Suppress("unused")
@ClientOnly
object AE2ShitClient : ClientModInitializer {
    override fun onInitializeClient(mod: ModContainer?) {
        BlockEntityRendererFactories.register(
            AdvancedInscriber.BLOCK_ENTITY,
            ::AdvancedInscriberTESR
        )

        ColorProviderRegistry.ITEM.register(
            DISKCellHandler::getDiskColor,
            DISKDrives.DISK_DRIVE_1K,
            DISKDrives.DISK_DRIVE_4K,
            DISKDrives.DISK_DRIVE_16K,
            DISKDrives.DISK_DRIVE_64K,
            DISKDrives.PORTABLE_DISK_1K,
            DISKDrives.PORTABLE_DISK_4K,
            DISKDrives.PORTABLE_DISK_16K,
            DISKDrives.PORTABLE_DISK_64K
        )

        ModelsReloadCallback.EVENT.register { modelRegistry ->
            modelRegistry
                .filterKeys { it.namespace == AE2Shit.MOD_ID }
                .forEach { (id, model) ->
                    when (id.path) {
                        advanced_inscriber -> modelRegistry[id] = AutoRotatingBakedModel(model)
                    }
                }
        }
    }
}