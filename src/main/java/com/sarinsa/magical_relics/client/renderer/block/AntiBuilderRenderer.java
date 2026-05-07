package com.sarinsa.magical_relics.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sarinsa.magical_relics.common.block.AntiBuilderBlock;
import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class AntiBuilderRenderer implements BlockEntityRenderer<AntiBuilderBlockEntity> {
    
    public static final ResourceLocation MODEL_ID = MagicalRelics.rl( "blockentity/anti_builder_dir_indicator" );
    
    @SuppressWarnings( "unused" )
    public AntiBuilderRenderer( BlockEntityRendererProvider.Context context ) { }
    
    @Override
    public void render( AntiBuilderBlockEntity antiBuilder, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int textureOverlay ) {
        if( Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative() ) {
            final BakedModel model = Minecraft.getInstance().getModelManager().getModel( MODEL_ID );
            final BlockState state = antiBuilder.getBlockState();
            final Direction facing = state.getValue( AntiBuilderBlock.FACING );
            
            poseStack.translate( 0.5D, 0.5D, 0.5D );
            poseStack.mulPose( facing.getRotation() );
            poseStack.translate( -0.5D, -0.5D, -0.5D );
            
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    poseStack.last(),
                    bufferSource.getBuffer( RenderType.cutout() ),
                    state,
                    model,
                    1.0F,
                    1.0F,
                    1.0F,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    RenderType.cutout()
            );
        }
    }
    
    @Override
    public int getViewDistance() {
        return 16;
    }
}
