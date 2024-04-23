package com.sarinsa.magical_relics.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sarinsa.magical_relics.common.blockentity.BaseCamoBlockEntity;
import com.sarinsa.magical_relics.common.blockentity.CamoBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class CamoTrapRenderer<T extends BlockEntity & CamoBlockEntity> implements BlockEntityRenderer<T> {

    public CamoTrapRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(T trap, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int textureOverlay) {
        BlockState camoState = trap.getCamoState();

        if (camoState != null) {
            Minecraft.getInstance().getBlockRenderer().renderBatched(
                    camoState,
                    trap.getBlockPos(),
                    Minecraft.getInstance().level,
                    poseStack,
                    bufferSource.getBuffer(RenderType.cutout()),
                    false,
                    Minecraft.getInstance().level.random,
                    ModelData.EMPTY,
                    RenderType.cutout());
        }
    }

    @Override
    public int getViewDistance() {
        return 192;
    }
}
