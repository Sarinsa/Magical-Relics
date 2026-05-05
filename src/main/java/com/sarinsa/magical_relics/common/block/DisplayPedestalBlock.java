package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.DisplayPedestalBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.TranslationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings( "deprecation" )
public class DisplayPedestalBlock extends Block implements EntityBlock {
    
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LOCKED = BooleanProperty.create( "locked" );
    
    
    private static final VoxelShape shape = Shapes.or( Shapes.or(
            Block.box( 3.0F, 0.0F, 3.0F, 13.0F, 2.0F, 13.0F ),
            Block.box( 5.0F, 2.0F, 5.0F, 11.0F, 10.0F, 11.0F ),
            Block.box( 3.0F, 10.0F, 3.0F, 13.0F, 16.0F, 13.0F ) ) );
    
    
    public DisplayPedestalBlock() {
        super( BlockBehaviour.Properties.of()
                .strength( 1.0F, 0.5F )
                .noOcclusion()
                .requiresCorrectToolForDrops()
                .sound( SoundType.STONE ) );
        
        registerDefaultState( stateDefinition.any()
                .setValue( FACING, Direction.NORTH )
                .setValue( POWERED, false )
                .setValue( LOCKED, false )
        );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public InteractionResult use( BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult ) {
        BlockEntity blockEntity = level.getExistingBlockEntity( pos );
        
        if( blockEntity instanceof DisplayPedestalBlockEntity displayPedestal ) {
            // Check if the pedestal is locked
            if( state.getValue( LOCKED ) ) {
                if( player.getItemInHand( hand ).getItem() == MRItems.PEDESTAL_KEY.get() ) {
                    level.setBlock( pos, state.setValue( LOCKED, false ), Block.UPDATE_CLIENTS );
                    level.playSound( null, pos, SoundEvents.CHAIN_BREAK, SoundSource.MASTER, 1.0F, 0.8F );
                    
                    if( !player.isCreative() ) {
                        player.getItemInHand( hand ).shrink( 1 );
                    }
                }
                else {
                    player.playSound( SoundEvents.ARMOR_EQUIP_CHAIN );
                    player.displayClientMessage( TranslationUtil.PEDESTAL_LOCKED, true );
                }
                return InteractionResult.sidedSuccess( level.isClientSide );
            }
            // Any other interactions only happen if the player is sneaking
            else if( !player.isShiftKeyDown() ) {
                // Pop the contained item, if not empty
                if( !displayPedestal.getArtifact().isEmpty() ) {
                    Block.popResourceFromFace( level, pos, Direction.UP, displayPedestal.getArtifact() );
                    displayPedestal.setArtifact( ItemStack.EMPTY );
                    
                    level.setBlock( pos, state.setValue( POWERED, true ), Block.UPDATE_ALL );
                    level.scheduleTick( pos, this, 10 );
                    
                    if( !level.isClientSide ) {
                        level.playSound( null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.7F, 1.0F );
                    }
                    return InteractionResult.sidedSuccess( level.isClientSide );
                }
                // If empty, try and place the held item into the pedestal
                else {
                    if( !player.getItemInHand( hand ).isEmpty() ) {
                        displayPedestal.setArtifact( player.getItemInHand( hand ) );
                        player.setItemInHand( hand, ItemStack.EMPTY );
                        
                        if( !level.isClientSide ) {
                            level.playSound( null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.7F, 1.0F );
                        }
                        return InteractionResult.sidedSuccess( level.isClientSide );
                    }
                    return InteractionResult.PASS;
                }
            }
        }
        return super.use( state, level, pos, player, hand, hitResult );
    }
    
    @Override
    public void playerWillDestroy( Level level, BlockPos pos, BlockState state, Player player ) {
        BlockEntity blockEntity = level.getBlockEntity( pos );
        
        if( blockEntity instanceof DisplayPedestalBlockEntity displayPedestal && state.getValue( LOCKED ) ) {
            if( !level.isClientSide && !player.isCreative() ) {
                ItemStack itemStack = new ItemStack( MRBlocks.DISPLAY_PEDESTAL.get() );
                displayPedestal.saveToItem( itemStack );
                
                ItemEntity droppedItem = new ItemEntity(
                        level,
                        (double) pos.getX() + 0.5D,
                        (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D,
                        itemStack
                );
                droppedItem.setDefaultPickUpDelay();
                level.addFreshEntity( droppedItem );
            }
        }
        super.playerWillDestroy( level, pos, state, player );
    }
    
    @Override
    public void tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        if( state.getValue( POWERED ) )
            level.setBlock( pos, state.setValue( POWERED, false ), Block.UPDATE_ALL );
    }
    
    @Override
    public boolean isSignalSource( BlockState state ) {
        return true;
    }
    
    @Override
    public int getSignal( BlockState state, BlockGetter level, BlockPos pos, Direction direction ) {
        return state.getValue( POWERED ) ? 3 : 0;
    }
    
    @Override
    public int getDirectSignal( BlockState state, BlockGetter level, BlockPos pos, Direction direction ) {
        return direction != Direction.UP ? state.getSignal( level, pos, direction ) : 0;
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void onRemove( BlockState state, Level level, BlockPos pos, BlockState newState, boolean uh ) {
        if( !state.is( newState.getBlock() ) ) {
            BlockEntity blockEntity = level.getBlockEntity( pos );
            
            if( blockEntity instanceof DisplayPedestalBlockEntity displayPedestal && !state.getValue( LOCKED ) ) {
                if( level instanceof ServerLevel && !displayPedestal.getArtifact().isEmpty() ) {
                    Block.popResource( level, pos, displayPedestal.getArtifact() );
                }
                level.updateNeighbourForOutputSignal( pos, this );
            }
            super.onRemove( state, level, pos, newState, uh );
        }
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return shape;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
        return new DisplayPedestalBlockEntity( pos, state );
    }
    
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext useContext ) {
        return defaultBlockState().setValue( FACING, useContext.getHorizontalDirection() );
    }
    
    @Override
    public List<ItemStack> getDrops( BlockState state, LootParams.Builder builder ) {
        BlockEntity blockEntity = builder.getOptionalParameter( LootContextParams.BLOCK_ENTITY );
        
        if( blockEntity instanceof DisplayPedestalBlockEntity && state.getValue( LOCKED ) ) {
            return List.of();
        }
        return super.getDrops( state, builder );
    }
    
    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable BlockGetter level, List<Component> components, TooltipFlag tooltipFlag ) {
        super.appendHoverText( itemStack, level, components, tooltipFlag );
        CompoundTag blockEntityData = BlockItem.getBlockEntityData( itemStack );
        
        if( blockEntityData != null ) {
            if( blockEntityData.contains( DisplayPedestalBlockEntity.LOCKED_KEY, Tag.TAG_BYTE ) ) {
                if( blockEntityData.getBoolean( DisplayPedestalBlockEntity.LOCKED_KEY ) ) {
                    components.add( Component.translatable( TranslationUtil.PEDESTAL_LOCKED_TOOLTIP ).withStyle( ChatFormatting.GRAY ) );
                }
            }
        }
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public BlockState rotate( BlockState state, Rotation rotation ) {
        return state.setValue( FACING, rotation.rotate( state.getValue( FACING ) ) );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public BlockState mirror( BlockState state, Mirror mirror ) {
        return state.rotate( mirror.getRotation( state.getValue( FACING ) ) );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public boolean isPathfindable( BlockState state, BlockGetter level, BlockPos pos, PathComputationType pathType ) {
        return false;
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> stateBuilder ) {
        stateBuilder.add( FACING, POWERED, LOCKED );
    }
}
