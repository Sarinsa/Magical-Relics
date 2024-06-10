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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

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
     * @param containerOpener Optional consumer used to open the block entity's inventory if it has one.
     * @param postStateLogic Optional consumer to make block state changes if a camo has been applied.
     */
    @SuppressWarnings("ConstantConditions")
    default InteractionResult use(Level level, BlockPos pos, Player player, InteractionHand hand, @Nullable BiConsumer<Level, BlockPos> postStateLogic, @Nullable BiConsumer<Player, MenuProvider> containerOpener) {
        if (level.getExistingBlockEntity(pos) instanceof CamoBlockEntity camoBlockEntity) {
            ItemStack handStack = player.getItemInHand(hand);

            if (handStack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                if (block == null) return InteractionResult.PASS;

                BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                BlockState camoState = block.getStateForPlacement(new BlockPlaceContext(player, hand, handStack, result));

                if (camoState == null) return InteractionResult.PASS;

                if (camoState.isSolidRender(level, pos) && !(camoState.getBlock() instanceof CamoBlock)) {
                    BlockState oldCamoState = camoBlockEntity.getCamoState();

                    // Makes placing blocks around the camo block easier
                    if (camoBlockEntity.getCamoState() != null && camoBlockEntity.getCamoState() == camoState)
                        return InteractionResult.PASS;

                    camoBlockEntity.setCamoState(camoState);

                    // Force light update in case the camo state is a light source
                    if (oldCamoState == null || camoState.getLightEmission(level, pos) != oldCamoState.getLightEmission(level, pos))
                        level.getLightEngine().checkBlock(pos);

                    if (postStateLogic != null)
                        postStateLogic.accept(level, pos);

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, pos, block.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            else {
                if (containerOpener != null) {
                    containerOpener.accept(player, (MenuProvider) camoBlockEntity);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
