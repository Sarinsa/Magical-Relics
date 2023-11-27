package com.sarinsa.magical_relics.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.sarinsa.magical_relics.common.blockentity.BaseCamoBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class CamoTrapRenderer<T extends BaseCamoBlockEntity> implements BlockEntityRenderer<T> {

    private static final ResourceLocation overlay = MagicalRelics.resLoc("textures/block/arrow_trap_overlay.png");

    private ModelPart[] overlays = new ModelPart[4];

    public CamoTrapRenderer(BlockEntityRendererProvider.Context context) {

    }


    @Override
    public void render(T trap, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int textureOverlay) {
        BlockState camoState = trap.getCamoState();

        if (camoState != null) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(camoState, poseStack, bufferSource, packedLight, textureOverlay, ModelData.EMPTY, RenderType.cutout());
        }
        renderTrapOverlay(poseStack, bufferSource, packedLight, textureOverlay);
    }

    private void renderTrapOverlay(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlayTexture) {
        poseStack.pushPose();
        // Move the overlay model a tiny bit up to avoid Z-fighting at close ranges (hardly noticeable at longer ranges)
        poseStack.translate(0.5D, 0.5002D, 0.5D);
        //topOverlay.render(poseStack, bufferSource.getBuffer(RenderType.entityCutout(overlay)), packedLight, overlayTexture);
        poseStack.popPose();
    }
}
