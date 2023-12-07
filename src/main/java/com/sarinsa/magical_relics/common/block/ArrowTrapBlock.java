package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.ArrowTrapBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.tag.MRBlockTags;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.maven.artifact.Artifact;
import org.jetbrains.annotations.Nullable;

public class ArrowTrapBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public ArrowTrapBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(1.5F, 1.0F));

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getExistingBlockEntity(pos) instanceof ArrowTrapBlockEntity arrowTrap) {
            ItemStack handStack = player.getItemInHand(hand);

            if (handStack.isEmpty()) return InteractionResult.PASS;

            if (handStack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                // This can be null, IntelliJ is lying >:(
                if (block == null) return InteractionResult.PASS;

                if (block.defaultBlockState().is(MRBlockTags.CAMO_BLOCKS)) {
                    // Makes placing blocks around the trap easier
                    if (arrowTrap.getCamoState() != null && arrowTrap.getCamoState().is(block)) return InteractionResult.PASS;

                    arrowTrap.setCamoState(block.defaultBlockState());

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, pos, block.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            // TODO - remove this, used for testing
            else if (!level.isClientSide && handStack.getItem() == Items.WOODEN_PICKAXE) {
                player.setItemInHand(hand, ArtifactUtils.generateRandomArtifact(level.random));
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ArrowTrapBlockEntity arrowTrap = new ArrowTrapBlockEntity(pos, state);
        arrowTrap.setCamoState(Blocks.COBBLESTONE.defaultBlockState());
        return arrowTrap;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == MRBlockEntities.ARROW_TRAP.get() ? (_level, _pos, _state, blockEntity) -> ArrowTrapBlockEntity.tick(_level, _pos, _state, (ArrowTrapBlockEntity) blockEntity): null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
