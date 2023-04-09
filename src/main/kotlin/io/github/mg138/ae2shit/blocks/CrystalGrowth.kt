package io.github.mg138.ae2shit.blocks

import appeng.api.upgrades.Upgrades
import appeng.block.AEBaseBlockItem
import appeng.core.AppEng
import appeng.core.definitions.AEItems
import appeng.menu.implementations.MenuTypeBuilder
import io.github.mg138.ae2shit.AE2Shit
import io.github.mg138.ae2shit.AE2Shit.defaultSettings
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

@Suppress("UnstableApiUsage")
object CrystalGrowth {
    const val crystal_growth = "crystal_growth"
    private val SCREEN_ID: Identifier = AppEng.makeId(this.crystal_growth)
    private val ID = AE2Shit.id(this.crystal_growth)

    val BLOCK_ENTITY: BlockEntityType<CrystalGrowthBlockEntity>
        = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            this.ID,
            QuiltBlockEntityTypeBuilder.create(::CrystalGrowthBlockEntity, CrystalGrowthBlock).build()
        )

    val SCREEN_HANDLER: ScreenHandlerType<CrystalGrowthScreenHandler>
        = MenuTypeBuilder
            .create(::CrystalGrowthScreenHandler, CrystalGrowthBlockEntity::class.java)
            .build(this.crystal_growth)

    init {
        Registry.register(Registry.BLOCK, this.ID, CrystalGrowthBlock)
        Registry.register(Registry.ITEM, this.ID, AEBaseBlockItem(CrystalGrowthBlock, defaultSettings()))

        CrystalGrowthBlock.setBlockEntity(CrystalGrowthBlockEntity::class.java, this.BLOCK_ENTITY, null, null)
        FluidStorage.SIDED.registerSelf(this.BLOCK_ENTITY)

        Registry.register(
            Registry.SCREEN_HANDLER,
            this.SCREEN_ID,
            this.SCREEN_HANDLER
        )

        Upgrades.add(AEItems.SPEED_CARD, CrystalGrowthBlock, 3)
    }
}