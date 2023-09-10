/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package io.github.mg138.ijo_pona_poki.blocks;

import appeng.api.orientation.BlockOrientation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipe;
import io.github.mg138.ijo_pona_poki.IjoPonaPoki;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import org.quiltmc.loader.api.minecraft.ClientOnly;

/**
 * Renders the dynamic parts of an inscriber (the presses, the animation and the item being smashed)
 */
@ClientOnly
public final class AdvancedInscriberTESR implements BlockEntityRenderer<AdvancedInscriberBlockEntity> {
    private static final float ITEM_RENDER_SCALE = 1.0f / 1.2f;

    private static final SpriteIdentifier TEXTURE_INSIDE = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
            IjoPonaPoki.INSTANCE.id("block/advanced_inscriber/inscriber_inside"));

    public AdvancedInscriberTESR(BlockEntityRendererFactory.Context ignoredContext) {
    }

    @Override
    public void render(AdvancedInscriberBlockEntity blockEntity, float partialTicks, MatrixStack ms, VertexConsumerProvider buffers,
                       int combinedLight, int combinedOverlay) {

        // render inscriber

        ms.push();
        ms.translate(0.5F, 0.5F, 0.5F);
        BlockOrientation orientation = BlockOrientation.get(blockEntity.getFront(), blockEntity.getTop());
        ms.multiply(orientation.getQuaternion());
        ms.translate(-0.5F, -0.5F, -0.5F);

        // render sides of stamps

        long absoluteProgress = 0;

        if (blockEntity.isSmash()) {
            final long currentTime = System.currentTimeMillis();
            absoluteProgress = currentTime - blockEntity.getClientStart();
            if (absoluteProgress > 800) {
                blockEntity.setSmash(false);
            }
        }

        final float relativeProgress = absoluteProgress % 800 / 400.0f;
        float progress = relativeProgress;

        if (progress > 1.0f) {
            progress = 1.0f - easeDecompressMotion(progress - 1.0f);
        } else {
            progress = easeCompressMotion(progress);
        }

        float press = 0.2f;
        press -= progress / 5.0f;

        float middle = 0.5f;
        middle += 0.02f;
        final float TwoPx = 2.0f / 16.0f;
        final float base = 0.4f;

        final Sprite tas = TEXTURE_INSIDE.getSprite();

        VertexConsumer buffer = buffers.getBuffer(RenderLayer.getSolid());

        // Bottom of Top Stamp
        addVertex(buffer, ms, tas, TwoPx, middle + press, TwoPx, 2, 13, combinedOverlay, combinedLight, Direction.DOWN);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + press, TwoPx, 14, 13, combinedOverlay, combinedLight,
                Direction.DOWN);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + press, 1.0f - TwoPx, 14, 2, combinedOverlay, combinedLight,
                Direction.DOWN);
        addVertex(buffer, ms, tas, TwoPx, middle + press, 1.0f - TwoPx, 2, 2, combinedOverlay, combinedLight,
                Direction.DOWN);

        // Front of Top Stamp
        addVertex(buffer, ms, tas, TwoPx, middle + base, TwoPx, 2, 3 - 16 * (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + base, TwoPx, 14, 3 - 16 * (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + press, TwoPx, 14, 3, combinedOverlay, combinedLight,
                Direction.NORTH);
        addVertex(buffer, ms, tas, TwoPx, middle + press, TwoPx, 2, 3, combinedOverlay, combinedLight, Direction.NORTH);

        // Top of Bottom Stamp
        middle -= 2.0f * 0.02f;
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - press, TwoPx, 2, 13, combinedOverlay, combinedLight,
                Direction.UP);
        addVertex(buffer, ms, tas, TwoPx, middle - press, TwoPx, 14, 13, combinedOverlay, combinedLight, Direction.UP);
        addVertex(buffer, ms, tas, TwoPx, middle - press, 1.0f - TwoPx, 14, 2, combinedOverlay, combinedLight,
                Direction.UP);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - press, 1.0f - TwoPx, 2, 2, combinedOverlay, combinedLight,
                Direction.UP);

        // Front of Bottom Stamp
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - base, TwoPx, 2, 3 - 16 * (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, TwoPx, middle - base, TwoPx, 14, 3 - 16 * (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, TwoPx, middle - press, TwoPx, 14, 3, combinedOverlay, combinedLight,
                Direction.NORTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - press, TwoPx, 2, 3, combinedOverlay, combinedLight,
                Direction.NORTH);

        // render items.

        var inv = blockEntity.getInternalInventory();

        int items = 0;
        if (!inv.getStackInSlot(0).isEmpty()) {
            items++;
        }
        if (!inv.getStackInSlot(1).isEmpty()) {
            items++;
        }
        if (!inv.getStackInSlot(2).isEmpty()) {
            items++;
        }

        boolean renderPresses;
        if (relativeProgress > 1.0f || items == 0) {
            // When crafting completes, don't render the presses (they may have been
            // consumed, see below)
            renderPresses = false;

            ItemStack is = inv.getStackInSlot(3);

            if (is.isEmpty()) {
                final InscriberRecipe ir = blockEntity.getTask();
                if (ir != null) {
                    // The "PRESS" type will consume the presses, so they should not render after
                    // completing
                    // the press animation
                    renderPresses = ir.getProcessType() == InscriberProcessType.INSCRIBE;
                    is = ir.getOutput().copy();
                }
            }
            this.renderItem(ms, is, 0.0f, buffers, combinedLight, combinedOverlay);
        } else {
            renderPresses = true;
            this.renderItem(ms, inv.getStackInSlot(2), 0.0f, buffers, combinedLight, combinedOverlay);
        }

        if (renderPresses) {
            this.renderItem(ms, inv.getStackInSlot(0), press, buffers, combinedLight, combinedOverlay);
            this.renderItem(ms, inv.getStackInSlot(1), -press, buffers, combinedLight, combinedOverlay);
        }

        ms.pop();
    }

    private static void addVertex(VertexConsumer vb, MatrixStack ms, Sprite sprite, float x, float y,
                                  float z, double texU, double texV, int overlayUV, int lightmapUV, Direction front) {
        vb.vertex(ms.peek().getModel(), x, y, z);
        vb.color(1.0f, 1.0f, 1.0f, 1.0f);
        vb.uv(sprite.getFrameU(texU), sprite.getFrameV(texV));
        vb.overlay(overlayUV);
        vb.light(lightmapUV);
        vb.normal(ms.peek().getNormal(), front.getOffsetX(), front.getOffsetY(), front.getOffsetZ());
        vb.next();
    }

    private void renderItem(MatrixStack ms, ItemStack stack, float o, VertexConsumerProvider buffers,
                            int combinedLight, int combinedOverlay) {
        if (!stack.isEmpty()) {
            ms.push();
            // move to center
            ms.translate(0.5f, 0.5f + o, 0.5f);
            ms.multiply(Quaternion.fromEulerXyz(1.570796f, 0, 0));
            // set scale
            ms.scale(ITEM_RENDER_SCALE, ITEM_RENDER_SCALE, ITEM_RENDER_SCALE);

            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

            // heuristic to scale items down much further than blocks,
            // the assumption here is that the generated item models will return their faces
            // for direction=null, while a block-model will have their faces for
            // cull-faces, but not direction=null
            var model = itemRenderer.getModels().getModel(stack);
            var quads = model.getQuads(null, null, RandomGenerator.createLegacy());
            // Note: quads may be null for mods implementing FabricBakedModel without caring about getQuads.
            if (quads != null && !quads.isEmpty()) {
                ms.scale(0.5f, 0.5f, 0.5f);
            }

            RenderSystem.applyModelViewMatrix();
            itemRenderer.renderItem(stack, ModelTransformation.Mode.FIXED, combinedLight, combinedOverlay, ms,
                    buffers, 0);
            ms.pop();
        }
    }

    // See https://easings.net/#easeOutBack
    private static float easeCompressMotion(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;

        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    // See https://easings.net/#easeInQuint
    private static float easeDecompressMotion(float x) {
        return x * x * x * x * x;
    }

}
