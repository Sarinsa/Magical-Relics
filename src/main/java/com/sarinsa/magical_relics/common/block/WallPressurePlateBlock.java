package com.sarinsa.magical_relics.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class WallPressurePlateBlock extends PressurePlateBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // South, west, north, east
    private static final VoxelShape[] shapes = {
            Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 1.0D),
            Block.box(15.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D),
            Block.box(1.0D, 1.0D, 15.0D, 15.0D, 15.0D, 16.0D),
            Block.box(0.0D, 1.0D, 1.0D, 1.0D, 15.0D, 15.0D)
    };

    // South, west, north, east
    private static final VoxelShape[] pressedShapes = {
            Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 0.5D),
            Block.box(15.5D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D),
            Block.box(1.0D, 1.0D, 15.5D, 15.0D, 15.0D, 16.0D),
            Block.box(0.0D, 1.0D, 1.0D, 0.5D, 15.0D, 15.0D)
    };

    private static final AABB[] touchShapes = {
            new AABB(0.1D, 0.1D, 0.0D, 0.9375D, 0.9375D, 0.1D),
            new AABB(0.9375D, 0.1D, 0.1D, 1.0D, 0.9375D, 0.9375D),
            new AABB(0.1D, 0.1D, 0.9375D, 0.9375D, 0.9375D, 1.0D),
            new AABB(0.0D, 0.1D, 0.1D, 0.1D, 0.9375D, 0.9375D)
    };



    public WallPressurePlateBlock(Properties properties) {
        super(Sensitivity.EVERYTHING, properties.noOcclusion().noCollission());
        registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        AABB aabb = touchShapes[level.getBlockState(pos).getValue(FACING).get2DDataValue()].move(pos);
        List<? extends Entity> list = level.getEntities(null, aabb);

        if (!list.isEmpty()) {
            for(Entity entity : list) {
                if (!entity.isIgnoringBlockTriggers()) {
                    return 15;
                }
            }
        }
        return 0;
    }

    @Override
    protected void updateNeighbours(Level level, BlockPos pos) {
        if (level.getBlockState(pos).is(this)) {
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.relative(level.getBlockState(pos).getValue(FACING).getOpposite()), this);
        }
        else {
            super.updateNeighbours(level, pos);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return getSignalForState(state) > 0 ? pressedShapes[direction.get2DDataValue()] : shapes[direction.get2DDataValue()];
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState changedState, LevelAccessor level, BlockPos pos, BlockPos changedPos) {
        return !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos behindPos = pos.relative(facing.getOpposite());
        return level.getBlockState(behindPos).isFaceSturdy(level, behindPos, facing);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return (direction != Direction.UP && direction != Direction.DOWN) ? getSignalForState(state) : 0;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN
                ? defaultBlockState().setValue(FACING, context.getClickedFace())
                : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }
}
