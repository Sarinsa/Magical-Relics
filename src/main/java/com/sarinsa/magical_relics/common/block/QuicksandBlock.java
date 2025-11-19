package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.client.ClientUtils;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRDamageTypes;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class QuicksandBlock extends Block implements BucketPickup {
    
    public static final int MAX_HEIGHT = 16;
    private static final int MAX_FLOW_AMOUNT = 2;
    private static final int TICK_DELAY = 20;
    
    public static final IntegerProperty LAYERS = IntegerProperty.create( "layers", 1, 16 );
    
    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[] {
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D )
    };
    
    protected static final AABB[] TOUCH_SHAPE_BY_LAYER = new AABB[] {
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D ).toAabbs().get( 0 ),
            Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D ).toAabbs().get( 0 )
    };
    
    
    public QuicksandBlock() {
        super( BlockBehaviour.Properties.of()
                .mapColor( MapColor.TERRACOTTA_BROWN )
                .strength( 0.8F, 1.0F )
                .sound( SoundType.PACKED_MUD )
                .noLootTable()
                .noCollission()
                .isViewBlocking( ( state, level, pos )
                        -> LogicalSidedProvider.CLIENTWORLD.get( LogicalSide.CLIENT ).isEmpty() || ClientUtils.isQuicksandViewBlocking()
                )
        );
        registerDefaultState( stateDefinition.any().setValue( LAYERS, MAX_HEIGHT ) );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE_BY_LAYER[state.getValue( LAYERS ) - 1];
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public VoxelShape getBlockSupportShape( BlockState state, BlockGetter level, BlockPos pos ) {
        return SHAPE_BY_LAYER[state.getValue( LAYERS ) - 1];
    }
    
    @Deprecated
    @SuppressWarnings( "deprecation" )
    public VoxelShape getVisualShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE_BY_LAYER[state.getValue( LAYERS ) - 1];
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public boolean useShapeForLightOcclusion( BlockState state ) {
        return true;
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void entityInside( BlockState state, Level level, BlockPos pos, Entity entity ) {
        AABB touchShape = TOUCH_SHAPE_BY_LAYER[state.getValue( LAYERS ) - 1].move( pos );
        
        if( touchShape.intersects( entity.getBoundingBox() ) ) {
            entity.makeStuckInBlock( state, new Vec3( 0.5D, 0.2D, 0.5D ) );
        }
        
        if( entity instanceof LivingEntity livingEntity && areEyesInQuicksand( livingEntity ) ) {
            livingEntity.hurt( MRDamageTypes.of( level, MRDamageTypes.QUICKSAND ), 1.0F );
        }
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void onPlace( BlockState state, Level level, BlockPos pos, BlockState oldBlockState, boolean idkWhatThisIs ) {
        level.scheduleTick( pos, this, TICK_DELAY );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        if( !tryFlowDownwards( level, state, pos ) && canFlow( state ) ) {
            tryFlowHorizontally( level, state, pos );
        }
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public BlockState updateShape( BlockState state, Direction direction, BlockState p_60543_, LevelAccessor level, BlockPos pos, BlockPos neighborPos ) {
        if( !level.getBlockTicks().hasScheduledTick( pos, this ) )
            level.scheduleTick( pos, this, TICK_DELAY );
        return state;
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public boolean isPathfindable( BlockState state, BlockGetter level, BlockPos pos, PathComputationType pathType ) {
        return false;
    }
    
    @Override
    public @Nullable BlockPathTypes getBlockPathType( BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob ) {
        return BlockPathTypes.DANGER_OTHER;
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( LAYERS ) );
    }
    
    /**
     * Attempts to make quicksand flow downwards.
     *
     * @return True if successful.
     */
    private boolean tryFlowDownwards( Level level, BlockState state, BlockPos pos ) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState( belowPos );
        
        // Try flowing down all at once, if the block below is replaceable or air
        if( belowState.canBeReplaced() ) {
            level.setBlock( belowPos, defaultBlockState().setValue( LAYERS, state.getValue( LAYERS ) ), Block.UPDATE_ALL );
            level.scheduleTick( belowPos, this, TICK_DELAY );
            level.removeBlock( pos, false );
            return true;
        }
        
        boolean hasBelowQuicksand = belowState.is( this ) && belowState.getValue( LAYERS ) < MAX_HEIGHT;
        
        // Try flowing down into quicksand below, if there is any
        if( hasBelowQuicksand ) {
            int flowAmount = Math.min( MAX_HEIGHT - belowState.getValue( LAYERS ), Math.min( state.getValue( LAYERS ), MAX_FLOW_AMOUNT ) );
            
            if( flowAmount > 0 ) {
                level.setBlock( belowPos, belowState.setValue( LAYERS, belowState.getValue( LAYERS ) + flowAmount ), Block.UPDATE_ALL );
                level.scheduleTick( belowPos, this, TICK_DELAY );
                
                if( state.getValue( LAYERS ) - flowAmount <= 0 ) {
                    level.removeBlock( pos, false );
                }
                else {
                    level.setBlock( pos, state.setValue( LAYERS, state.getValue( LAYERS ) - flowAmount ), Block.UPDATE_ALL );
                    level.scheduleTick( pos, this, TICK_DELAY );
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Attempts to make quicksand flow in every horizontal direction.
     */
    private void tryFlowHorizontally( Level level, BlockState state, BlockPos pos ) {
        boolean flowed = false;
        int flowCapacity = state.getValue( LAYERS );
        
        for( Direction dir : DirectionUtils.HORIZONTAL ) {
            if( flowCapacity - MAX_FLOW_AMOUNT <= 0 )
                break;
            
            BlockPos neighborPos = pos.relative( dir );
            BlockState neighborState = level.getBlockState( neighborPos );
            
            if( neighborState.canBeReplaced() ) {
                level.setBlock( neighborPos, defaultBlockState().setValue( LAYERS, MAX_FLOW_AMOUNT ), Block.UPDATE_ALL );
                flowCapacity -= MAX_FLOW_AMOUNT;
                flowed = true;
            }
            else if( neighborState.is( this ) && flowCapacity - neighborState.getValue( LAYERS ) > MAX_FLOW_AMOUNT ) {
                level.setBlock( neighborPos, neighborState.setValue( LAYERS, neighborState.getValue( LAYERS ) + MAX_FLOW_AMOUNT ), Block.UPDATE_ALL );
                flowCapacity -= MAX_FLOW_AMOUNT;
                flowed = true;
            }
        }
        
        if( flowCapacity <= 0 ) {
            level.removeBlock( pos, false );
        }
        else {
            level.setBlock( pos, state.setValue( LAYERS, flowCapacity ), Block.UPDATE_ALL );
            
            if( flowed )
                level.scheduleTick( pos, this, TICK_DELAY );
        }
    }
    
    
    private static boolean canFlow( BlockState state ) {
        return state.getValue( LAYERS ) > 2;
    }
    
    /**
     * Checks if the given entity's eyes are in quicksand.
     */
    public static boolean areEyesInQuicksand( LivingEntity livingEntity ) {
        Level level = livingEntity.level();
        double eyeY = livingEntity.getEyeY();
        double partialEyeY = eyeY - Mth.floor( eyeY );
        BlockPos eyePos = new BlockPos( Mth.floor( livingEntity.getX() ), Mth.floor( eyeY ), Mth.floor( livingEntity.getZ() ) );
        BlockState stateAtEye = level.getBlockState( eyePos );
        
        return level.getBlockState( eyePos ).is( MRBlocks.QUICKSAND.get() ) && (partialEyeY <= (1.0D / MAX_HEIGHT) * stateAtEye.getValue( LAYERS ));
    }
    
    @Override
    public ItemStack pickupBlock( LevelAccessor level, BlockPos pos, BlockState state ) {
        if( state.getValue( LAYERS ) == MAX_HEIGHT ) {
            level.setBlock( pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE );
            return new ItemStack( MRItems.QUICKSAND_BUCKET.get() );
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of( SoundEvents.PACKED_MUD_BREAK );
    }
}
