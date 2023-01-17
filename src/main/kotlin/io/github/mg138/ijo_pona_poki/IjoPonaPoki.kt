package io.github.mg138.ijo_pona_poki

import appeng.client.render.model.AutoRotatingBakedModel
import appeng.hooks.ModelsReloadCallback
import appeng.mixins.ModelsReloadMixin
import io.github.mg138.ijo_pona_poki.blocks.AdvancedInscriber
import io.github.mg138.ijo_pona_poki.items.DISKDrives
import io.github.mg138.ijo_pona_poki.world.DISKStorage
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.item.group.api.QuiltItemGroup
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents

@Suppress("unused")
object IjoPonaPoki : ModInitializer {
    const val MOD_ID = "ijo_pona_poki"

    fun id(name: String) = Identifier(this.MOD_ID, name)

    private val ITEM_GROUP: QuiltItemGroup = QuiltItemGroup.builder(this.id("item_group")).icon { ItemStack(DISKDrives.DISK_HOUSING) }.build()
    val DEFAULT_SETTINGS: QuiltItemSettings = QuiltItemSettings().group(this.ITEM_GROUP).maxCount(64)

    override fun onInitialize(mod: ModContainer?) {
        ServerLifecycleEvents.READY.register { server ->
            val persistentManager = server.overworld.persistentStateManager

            persistentManager.getOrCreate(DISKStorage::readFromNbt, { DISKStorage }, MOD_ID)
        }
    }
}