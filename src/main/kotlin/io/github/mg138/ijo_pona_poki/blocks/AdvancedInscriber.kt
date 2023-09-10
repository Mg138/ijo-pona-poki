package io.github.mg138.ijo_pona_poki.blocks

import appeng.api.upgrades.Upgrades
import appeng.block.AEBaseBlockItem
import appeng.core.AppEng
import appeng.core.definitions.AEItems
import appeng.menu.implementations.MenuTypeBuilder
import io.github.mg138.ijo_pona_poki.IjoPonaPoki
import io.github.mg138.ijo_pona_poki.IjoPonaPoki.defaultSettings
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object AdvancedInscriber {
    const val advanced_inscriber = "advanced_inscriber"
    private val SCREEN_ID: Identifier = AppEng.makeId(this.advanced_inscriber)
    private val ID = IjoPonaPoki.id(this.advanced_inscriber)

    val BLOCK_ENTITY: BlockEntityType<AdvancedInscriberBlockEntity>
        = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            this.ID,
            QuiltBlockEntityTypeBuilder.create(::AdvancedInscriberBlockEntity, AdvancedInscriberBlock).build()
        )

    val SCREEN_HANDLER: ScreenHandlerType<AdvancedInscriberScreenHandler>
        = MenuTypeBuilder
            .create(::AdvancedInscriberScreenHandler, AdvancedInscriberBlockEntity::class.java)
            .build(this.advanced_inscriber)

    init {
        Registry.register(Registries.BLOCK, this.ID, AdvancedInscriberBlock)
        Registry.register(Registries.ITEM, this.ID, AEBaseBlockItem(AdvancedInscriberBlock, defaultSettings()))

        AdvancedInscriberBlock.setBlockEntity(AdvancedInscriberBlockEntity::class.java, this.BLOCK_ENTITY, null, null)

        Registry.register(
            Registries.SCREEN_HANDLER_TYPE,
            this.SCREEN_ID,
            this.SCREEN_HANDLER
        )

        Upgrades.add(AEItems.SPEED_CARD, AdvancedInscriberBlock, 5)
    }
}