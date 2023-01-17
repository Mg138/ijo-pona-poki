package io.github.mg138.ijo_pona_poki.blocks

import appeng.api.upgrades.Upgrades
import appeng.block.AEBaseBlockItem
import appeng.blockentity.AEBaseBlockEntity
import appeng.core.AppEng
import appeng.core.definitions.AEItems
import appeng.menu.implementations.MenuTypeBuilder
import io.github.mg138.ijo_pona_poki.IjoPonaPoki
import io.github.mg138.ijo_pona_poki.IjoPonaPoki.DEFAULT_SETTINGS
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object AdvancedInscriber {
    const val advanced_inscriber = "advanced_inscriber"
    val ADVANCED_INSCRIBER_SCREEN_ID = AppEng.makeId(this.advanced_inscriber)
    val ADVANCED_INSCRIBER_ID = IjoPonaPoki.id(this.advanced_inscriber)

    val ADVANCED_INSCRIBER_BE: BlockEntityType<AdvancedInscriberBlockEntity>
        = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            this.ADVANCED_INSCRIBER_ID,
            QuiltBlockEntityTypeBuilder.create(::AdvancedInscriberBlockEntity, AdvancedInscriberBlock).build()
        )

    val ADVANCED_INSCRIBER_SCREEN_HANDLER: ScreenHandlerType<AdvancedInscriberScreenHandler>
        = MenuTypeBuilder
            .create(::AdvancedInscriberScreenHandler, AdvancedInscriberBlockEntity::class.java)
            .build(this.advanced_inscriber)

    init {
        Registry.register(Registry.BLOCK, this.ADVANCED_INSCRIBER_ID, AdvancedInscriberBlock)
        val advancedInscriberItem = AEBaseBlockItem(AdvancedInscriberBlock, DEFAULT_SETTINGS)

        Registry.register(Registry.ITEM, this.ADVANCED_INSCRIBER_ID, advancedInscriberItem)
        AEBaseBlockEntity.registerBlockEntityItem(this.ADVANCED_INSCRIBER_BE, advancedInscriberItem)

        AdvancedInscriberBlock.setBlockEntity(AdvancedInscriberBlockEntity::class.java, this.ADVANCED_INSCRIBER_BE, null, null)

        Registry.register(
            Registry.SCREEN_HANDLER,
            this.ADVANCED_INSCRIBER_SCREEN_ID,
            this.ADVANCED_INSCRIBER_SCREEN_HANDLER
        )

        Upgrades.add(AEItems.SPEED_CARD, AdvancedInscriberBlock, 5)
    }
}