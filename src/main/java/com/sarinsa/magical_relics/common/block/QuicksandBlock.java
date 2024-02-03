package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class QuicksandBlock extends Block {

    public static final int MAX_HEIGHT = 16;
    private static final int MAX_FLOW_AMOUNT = 2;
    private static final int TICK_DELAY = 20;

    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 16);

    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[] {
            Shapes.empty(),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };



    public QuicksandBlock() {
        super(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.RAW_IRON)
                .strength(0.8F, 1.0F)
                .sound(SoundType.PACKED_MUD)
                .noLootTable()
                .noCollission()
        );
        registerDefaultState(stateDefinition.any().setValue(LAYERS, MAX_HEIGHT));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.5D, 0.2D, 0.5D));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(LAYERS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldBlockState, boolean idkWhatThisIs) {
        level.scheduleTick(pos, this, TICK_DELAY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        if (!tryFlowDownwards(level, state, pos) && canFlow(state)) {
            tryFlowHorizontally(level, state, pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState p_60543_, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        level.scheduleTick(pos, this, TICK_DELAY);
        return state;
    }

    /**
     * Attempts to make quicksand flow downwards.
     * @return True if successful.
     */
    private boolean tryFlowDownwards(Level level, BlockState state, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        // Try flowing down all at once, if the block below is replaceable or air
        if (belowState.getMaterial().isReplaceable()) {
            level.setBlock(belowPos, defaultBlockState().setValue(LAYERS, state.getValue(LAYERS)), Block.UPDATE_ALL);
            level.scheduleTick(belowPos, this, TICK_DELAY);
            level.removeBlock(pos, false);
            return true;
        }

        boolean hasBelowQuicksand = belowState.is(this) && belowState.getValue(LAYERS) < MAX_HEIGHT;

        // Try flowing down into quicksand below, if there is any
        if (hasBelowQuicksand) {
            int flowAmount = Math.min(MAX_HEIGHT - belowState.getValue(LAYERS), Math.min(state.getValue(LAYERS), MAX_FLOW_AMOUNT));

            if (flowAmount > 0) {
                level.setBlock(belowPos, belowState.setValue(LAYERS, belowState.getValue(LAYERS) + flowAmount), Block.UPDATE_ALL);
                level.scheduleTick(belowPos, this, TICK_DELAY);

                if (state.getValue(LAYERS) - flowAmount <= 0) {
                    level.removeBlock(pos, false);
                } else {
                    level.setBlock(pos, state.setValue(LAYERS, state.getValue(LAYERS) - flowAmount), Block.UPDATE_ALL);
                    level.scheduleTick(pos, this, TICK_DELAY);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to make quicksand flow in every horizontal direction.
     */
    private void tryFlowHorizontally(Level level, BlockState state, BlockPos pos) {
        boolean flowed = false;
        int flowCapacity = state.getValue(LAYERS);

        for (Direction dir : DirectionUtil.HORIZONTAL) {
            if (flowCapacity - MAX_FLOW_AMOUNT <= 0)
                break;

            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.getMaterial().isReplaceable()) {
                level.setBlock(neighborPos, defaultBlockState().setValue(LAYERS, MAX_FLOW_AMOUNT), Block.UPDATE_CLIENTS);
                flowCapacity -= MAX_FLOW_AMOUNT;
                flowed = true;
            }
            else if (neighborState.is(this) && flowCapacity - neighborState.getValue(LAYERS) > MAX_FLOW_AMOUNT) {
                level.setBlock(neighborPos, neighborState.setValue(LAYERS, neighborState.getValue(LAYERS) + MAX_FLOW_AMOUNT), Block.UPDATE_CLIENTS);
                flowCapacity -= MAX_FLOW_AMOUNT;
                flowed = true;
            }
        }

        if (flowCapacity <= 0) {
            level.removeBlock(pos, false);
        }
        else {
            level.setBlock(pos, state.setValue(LAYERS, flowCapacity), Block.UPDATE_CLIENTS);

            if (flowed)
                level.scheduleTick(pos, this, TICK_DELAY);
        }
    }

    private static boolean canFlow(BlockState state) {
        return state.getValue(LAYERS) > 2;
    }
}
