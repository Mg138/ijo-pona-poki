package io.github.mg138.ae2shit.blocks

import appeng.api.upgrades.Upgrades
import appeng.block.AEBaseBlockItem
import appeng.core.AppEng
import appeng.core.definitions.AEItems
import appeng.menu.implementations.MenuTypeBuilder
import io.github.mg138.ae2shit.AE2Shit
import io.github.mg138.ae2shit.AE2Shit.defaultSettings
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object AdvancedInscriber {
    const val advanced_inscriber = "advanced_inscriber"
    private val SCREEN_ID: Identifier = AppEng.makeId(this.advanced_inscriber)
    private val ID = AE2Shit.id(this.advanced_inscriber)

    val BLOCK_ENTITY: BlockEntityType<AdvancedInscriberBlockEntity>
        = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            this.ID,
            QuiltBlockEntityTypeBuilder.create(::AdvancedInscriberBlockEntity, AdvancedInscriberBlock).build()
        )

    val SCREEN_HANDLER: ScreenHandlerType<AdvancedInscriberScreenHandler>
        = MenuTypeBuilder
            .create(::AdvancedInscriberScreenHandler, AdvancedInscriberBlockEntity::class.java)
            .build(this.advanced_inscriber)

    init {
        Registry.register(Registry.BLOCK, this.ID, AdvancedInscriberBlock)
        Registry.register(Registry.ITEM, this.ID, AEBaseBlockItem(AdvancedInscriberBlock, defaultSettings()))

        AdvancedInscriberBlock.setBlockEntity(AdvancedInscriberBlockEntity::class.java, this.BLOCK_ENTITY, null, null)

        Registry.register(
            Registry.SCREEN_HANDLER,
            this.SCREEN_ID,
            this.SCREEN_HANDLER
        )

        Upgrades.add(AEItems.SPEED_CARD, AdvancedInscriberBlock, 5)
    }
}