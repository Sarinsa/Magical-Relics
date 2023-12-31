package com.sarinsa.magical_relics.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.sarinsa.magical_relics.common.blockentity.DisplayPedestalBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DisplayPedestalRenderer implements BlockEntityRenderer<DisplayPedestalBlockEntity> {

    public DisplayPedestalRenderer(BlockEntityRendererProvider.Context context) {

    }


    @Override
    public void render(DisplayPedestalBlockEntity displayPedestal, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int textureOverlay) {
        BlockState state = displayPedestal.getLevel() == null ? MRBlocks.DISPLAY_PEDESTAL.get().defaultBlockState() : displayPedestal.getBlockState();
        Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        float rotation = direction.toYRot();

        poseStack.translate(0.5D, 0.7F, 0.5D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-rotation));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);

        ItemStack artifact = displayPedestal.getArtifact();
        Minecraft.getInstance().getItemRenderer().renderStatic(artifact, ItemTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, (int) displayPedestal.getBlockPos().asLong());
    }
}
