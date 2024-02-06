package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.CamoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public interface CamoBlock {

    default int getLightEmission(BlockGetter level, BlockPos pos, int superValue) {
        if (level.getExistingBlockEntity(pos) instanceof CamoBlockEntity camoBlockEntity) {
            if (camoBlockEntity.getCamoState() != null)
                return camoBlockEntity.getCamoState().getLightEmission(level, pos);
        }
        return superValue;
    }

    /**
     * Hook used by blocks that have camo block entities. Handles camo picking.
     * <p></p>
     * @param consumer Optional consumer used to open the block entity's inventory if it has one.
     */
    @SuppressWarnings("ConstantConditions")
    default InteractionResult use(Level level, BlockPos pos, Player player, InteractionHand hand, @Nullable BiConsumer<Player, MenuProvider> consumer) {
        if (level.getExistingBlockEntity(pos) instanceof CamoBlockEntity camoBlockEntity) {
            ItemStack handStack = player.getItemInHand(hand);

            if (handStack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                // This can be null, IntelliJ is lying >:(
                if (block == null) return InteractionResult.PASS;

                BlockState camoState = block.getStateForPlacement(new BlockPlaceContext(player, hand, handStack, Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE)));
                if (camoState == null) return InteractionResult.PASS;

                if (camoState.isSolidRender(level, pos) && !(camoState.getBlock() instanceof CamoBlock)) {
                    // Makes placing blocks around the trap easier
                    if (camoBlockEntity.getCamoState() != null && camoBlockEntity.getCamoState() == camoState) return InteractionResult.PASS;

                    camoBlockEntity.setCamoState(camoState);
                    // Force light update in case the camo state is a light source
                    level.getLightEngine().checkBlock(pos);

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, pos, block.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            else {
                if (consumer != null)
                    consumer.accept(player, (MenuProvider) camoBlockEntity);
            }
        }
        return InteractionResult.PASS;
    }
}
