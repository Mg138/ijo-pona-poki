package io.github.mg138.ae2shit.blocks

import appeng.block.AEBaseEntityBlock
import appeng.core.definitions.AEBlocks
import appeng.menu.MenuOpener
import appeng.menu.locator.MenuLocators
import appeng.util.InteractionUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings


object AdvancedInscriberBlock : AEBaseEntityBlock<AdvancedInscriberBlockEntity>(QuiltBlockSettings.copyOf(AEBlocks.INSCRIBER.block())) {
    override fun onActivated(
        world: World,
        pos: BlockPos,
        playerEntity: PlayerEntity,
        hand: Hand?,
        heldItem: ItemStack?,
        hit: BlockHitResult,
    ): ActionResult {
        if (!InteractionUtil.isInAlternateUseMode(playerEntity)) {
            this.getBlockEntity(world, pos)?.let { target ->
                if (!world.isClient()) {
                    MenuOpener.open(AdvancedInscriber.SCREEN_HANDLER, playerEntity, MenuLocators.forBlockEntity(target))
                }
                return ActionResult.success(world.isClient())
            }
        }
        return ActionResult.PASS
    }
}