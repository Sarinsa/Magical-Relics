package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallPressurePlateBlock extends PressurePlateBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // South, west, north, east
    private static final VoxelShape[] shapes = {
            Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0F, 1.0D),
            Block.box(15.0D, 1.0D, 1.0D, 16.0D, 15.0F, 15.0D),
            Block.box(1.0D, 1.0D, 15.0D, 15.0D, 15.0F, 16.0D),
            Block.box(0.0D, 1.0D, 1.0D, 1.0D, 15.0F, 15.0D)
    };

    // South, west, north, east
    private static final VoxelShape[] pressedShapes = {
            Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0F, 1.0D),
            Block.box(15.0D, 1.0D, 1.0D, 16.0D, 15.0F, 15.0D),
            Block.box(1.0D, 1.0D, 15.0D, 15.0D, 15.0F, 16.0D),
            Block.box(0.0D, 1.0D, 1.0D, 1.0D, 15.0F, 15.0D)
    };



    public WallPressurePlateBlock(Properties properties) {
        super(Sensitivity.EVERYTHING, properties.noOcclusion().noCollission());
        registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);

        if (state.getValue(POWERED) && direction == Direction.EAST) {
            return Block.box(0.0D, 1.0D, 1.0D, 1.0D, 15.0F, 15.0D);
        }

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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN
                ? defaultBlockState().setValue(FACING, context.getClickedFace())
                : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }
}
