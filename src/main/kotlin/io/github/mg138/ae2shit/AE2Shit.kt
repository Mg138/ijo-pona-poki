package io.github.mg138.ae2shit

import io.github.mg138.ae2shit.items.DISKDrives
import io.github.mg138.ae2shit.recipe.CrystalGrowthRecipe
import io.github.mg138.ae2shit.world.DISKStorage
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.item.group.api.QuiltItemGroup
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents

object AE2Shit : ModInitializer {
    const val MOD_ID = "ae2shit"

    fun id(name: String) = Identifier(this.MOD_ID, name)

    private val ITEM_GROUP: QuiltItemGroup = QuiltItemGroup.builder(this.id("item_group")).icon { ItemStack(DISKDrives.DISK_HOUSING) }.build()
    fun defaultSettings(): QuiltItemSettings = QuiltItemSettings().group(this.ITEM_GROUP).maxCount(64)

    override fun onInitialize(mod: ModContainer?) {
        CrystalGrowthRecipe

        ServerLifecycleEvents.READY.register { server ->
            val persistentManager = server.overworld.persistentStateManager

            persistentManager.getOrCreate(DISKStorage::readFromNbt, { DISKStorage }, this.MOD_ID)
        }
    }
}