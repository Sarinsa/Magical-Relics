package com.sarinsa.magical_relics.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.sarinsa.magical_relics.common.entity.SwungSword;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SwungSwordRenderer extends EntityRenderer<SwungSword> {

    public SwungSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SwungSword sword) {
        return null;
    }


    // TODO - Make this a bit cooler looking, maybe
    @Override
    public void render(SwungSword sword, float rot, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ItemStack itemStack = sword.getSwordItem();
        Direction direction = sword.getAttackDirection();

        poseStack.pushPose();

        poseStack.translate(0.0D, 0.5D, 0.0D);

        if (direction == Direction.UP) {
            poseStack.mulPose(Vector3f.ZN.rotationDegrees(45.0F));
        }
        else if (direction == Direction.DOWN) {
            poseStack.mulPose(Vector3f.ZN.rotationDegrees(225.0F));
        }
        else {
            poseStack.mulPose(Vector3f.XN.rotationDegrees(90.0F));
            poseStack.mulPose(Vector3f.ZN.rotationDegrees(direction.toYRot() - 135.0F));
        }
        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, sword.getId());

        poseStack.popPose();
    }

    @Override
    protected void renderNameTag(SwungSword sword, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int p_114502_) {

    }
}
