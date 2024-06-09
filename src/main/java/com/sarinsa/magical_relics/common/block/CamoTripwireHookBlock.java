package com.sarinsa.magical_relics.common.block;

import com.google.common.base.MoreObjects;
import com.sarinsa.magical_relics.common.blockentity.BaseCamoBlockEntity;
import com.sarinsa.magical_relics.common.blockentity.CamoBlockEntity;
import com.sarinsa.magical_relics.common.blockentity.CamoDispenserBlockEntity;
import com.sarinsa.magical_relics.common.blockentity.CamoTripwireBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CamoTripwireHookBlock extends TripWireHookBlock implements EntityBlock, CamoBlock {

    public CamoTripwireHookBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(1.5F, 1.0F));

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(ATTACHED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        CamoTripwireBlockEntity be = new CamoTripwireBlockEntity(pos, state);
        be.setCamoState(CamoBlockEntity.defaultCamoState.get());
        return be;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }


    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * Slightly modified code from {@link TripWireHookBlock#calculateState(Level, BlockPos, BlockState, boolean, boolean, int, BlockState)}
     */
    @Override
    public void calculateState(Level level, BlockPos pos, BlockState state, boolean p_57689_, boolean p_57690_, int maxDist, @Nullable BlockState p_57692_) {
        Direction direction = state.getValue(FACING);
        boolean attached = state.getValue(ATTACHED);
        boolean powered = state.getValue(POWERED);
        boolean flag2 = !p_57689_;
        boolean flag3 = false;
        int i = 0;
        BlockState[] ablockstate = new BlockState[42];

        for(int checkDist = 1; checkDist < 42; ++checkDist) {
            BlockPos blockpos = pos.relative(direction, checkDist);
            BlockState blockstate = level.getBlockState(blockpos);
            
            if (blockstate.is(MRBlocks.CAMO_TRIPWIRE_HOOK.get())) {
                if (blockstate.getValue(FACING) == direction.getOpposite()) {
                    i = checkDist;
                }
                break;
            }

            if (!blockstate.is(MRBlocks.THICK_TRIPWIRE.get()) && checkDist != maxDist) {
                ablockstate[checkDist] = null;
                flag2 = false;
            } 
            else {
                if (checkDist == maxDist) blockstate = MoreObjects.firstNonNull(p_57692_, blockstate);
                
                boolean flag4 = !blockstate.getValue(TripWireBlock.DISARMED);
                boolean flag5 = blockstate.getValue(TripWireBlock.POWERED);
                flag3 |= flag4 && flag5;
                ablockstate[checkDist] = blockstate;
                if (checkDist == maxDist) {
                    level.scheduleTick(pos, this, 10);
                    flag2 &= flag4;
                }
            }
        }
        flag2 &= i > 1;
        flag3 &= flag2;
        BlockState hookState = defaultBlockState().setValue(ATTACHED, flag2).setValue(POWERED, flag3);
       
        if (i > 0) {
            BlockPos blockpos1 = pos.relative(direction, i);
            Direction direction1 = direction.getOpposite();
            level.setBlock(blockpos1, hookState.setValue(FACING, direction1), 3);
            notifyNeighbors(level, blockpos1, direction1);
            emitState(level, blockpos1, flag2, flag3, attached, powered);
        }
        emitState(level, pos, flag2, flag3, attached, powered);
        
        if (!p_57689_) {
            level.setBlock(pos, hookState.setValue(FACING, direction), 3);

            if (p_57690_) {
                notifyNeighbors(level, pos, direction);
            }
        }

        if (attached != flag2) {
            for(int k = 1; k < i; ++k) {
                BlockPos blockpos2 = pos.relative(direction, k);
                BlockState blockstate2 = ablockstate[k];

                if (blockstate2 != null) {
                    if (!level.getBlockState(blockpos2).isAir()) { // FORGE: fix MC-129055
                        level.setBlock(blockpos2, blockstate2.setValue(ATTACHED, flag2), 3);
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean b) {
        super.onRemove(state, level, pos, newState, b);

        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            level.removeBlockEntity(pos);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return getLightEmission(level, pos, super.getLightEmission(state, level, pos));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return use(level, pos, player, hand, null, null);
    }
}
