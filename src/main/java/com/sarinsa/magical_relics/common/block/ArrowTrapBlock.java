package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.CamoDispenserBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.tag.MRBlockTags;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ArrowTrapBlock extends DispenserBlock implements EntityBlock {

    public ArrowTrapBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(1.5F, 1.0F));

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getExistingBlockEntity(pos) instanceof CamoDispenserBlockEntity arrowTrap) {
            ItemStack handStack = player.getItemInHand(hand);

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
                    return InteractionResult.SUCCESS;
                }
            }
            // TODO - remove this, used for testing
            else if (handStack.getItem() == Items.WOODEN_PICKAXE) {
                if (!level.isClientSide) {
                    player.setItemInHand(hand, ArtifactUtils.generateRandomArtifact(level.random));
                }
                return InteractionResult.CONSUME;
            }
            else {
                player.openMenu(arrowTrap);
                player.awardStat(Stats.INSPECT_DISPENSER);
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        CamoDispenserBlockEntity arrowTrap = new CamoDispenserBlockEntity(pos, state);
        arrowTrap.setCamoState(Blocks.COBBLESTONE.defaultBlockState());
        return arrowTrap;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == MRBlockEntities.CAMO_DISPENSER.get()
                ? (_level, _pos, _state, blockEntity) -> CamoDispenserBlockEntity.tick(_level, _pos, _state, (CamoDispenserBlockEntity) blockEntity)
                : null;
    }
}
