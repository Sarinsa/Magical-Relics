package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.core.registry.MRDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings( "all" )
public class SpikeTrapBlock extends Block implements SimpleWaterloggedBlock {
    
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    
    private static final VoxelShape shape = Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D );
    private static final VoxelShape collisionShape = Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D );
    
    public SpikeTrapBlock() {
        super( BlockBehaviour.Properties.of()
                .sound( SoundType.METAL )
                .strength( 1.2F, 1.0F )
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .noCollission() );
        registerDefaultState( stateDefinition.any().setValue( WATERLOGGED, false ) );
    }
    
    @Override
    public FluidState getFluidState( BlockState state ) {
        return state.getValue( WATERLOGGED ) ? Fluids.WATER.getSource( false ) : super.getFluidState( state );
    }
    
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        final Level level = context.getLevel();
        final FluidState fluidState = context.getLevel().getFluidState( context.getClickedPos() );
        
        return defaultBlockState().setValue( WATERLOGGED, Boolean.valueOf( fluidState.getType() == Fluids.WATER ) );
    }
    
    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return shape;
    }
    
    @Override
    public VoxelShape getCollisionShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return collisionShape;
    }
    
    @Override
    public void entityInside( BlockState state, Level level, BlockPos pos, Entity entity ) {
        if( entity instanceof LivingEntity livingEntity ) {
            entity.hurt( MRDamageTypes.of( level, MRDamageTypes.SPIKES ), 2.0F );
        }
    }
    
    @Override
    public boolean isPathfindable( BlockState state, BlockGetter level, BlockPos pos, PathComputationType pathType ) {
        return false;
    }
    
    @Nullable
    @Override
    public BlockPathTypes getBlockPathType( BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob ) {
        return BlockPathTypes.DAMAGE_OTHER;
    }
    
    @Override
    public boolean canSurvive( BlockState state, LevelReader level, BlockPos pos ) {
        return level.getBlockState( pos.below() ).isSolidRender( level, pos.below() );
    }
    
    @Override
    public BlockState updateShape( BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos pos1 ) {
        return !state.canSurvive( level, pos )
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape( state, direction, neighborState, level, pos, pos1 );
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( WATERLOGGED ) );
    }
}
