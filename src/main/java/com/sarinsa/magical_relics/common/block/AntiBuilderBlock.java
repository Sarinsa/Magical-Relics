package com.sarinsa.magical_relics.common.block;

import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.network.NetworkHelper;
import com.sarinsa.magical_relics.common.network.message.S2COpenAntiBuilderScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings( "deprecation" )
public class AntiBuilderBlock extends HorizontalDirectionalBlock implements EntityBlock {
    
    public AntiBuilderBlock() {
        super( BlockBehaviour.Properties.of()
                .strength( 1.0F, 3600000.0F )
                .sound( SoundType.STONE )
                .destroyTime( 0.5F )
                .lightLevel( ( state ) -> 8 )
                .noLootTable() );
        
        registerDefaultState( stateDefinition.any().setValue( FACING, Direction.NORTH ) );
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
        return new AntiBuilderBlockEntity( pos, state );
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return type == MRBlockEntities.ANTI_BUILDER.get()
                ? ( lvl, pos, blockState, blockEntity ) -> AntiBuilderBlockEntity.tick( (AntiBuilderBlockEntity) blockEntity )
                : null;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        if( context.getPlayer() == null ) return defaultBlockState();
        return defaultBlockState().setValue( FACING, context.getHorizontalDirection().getOpposite() );
    }
    
    @Override
    public boolean onDestroyedByPlayer( BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid ) {
        if( super.onDestroyedByPlayer( state, level, pos, player, willHarvest, fluid ) ) {
            level.playSound( null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, ((float) level.random.nextDouble() / 2) + 0.75F );
            return true;
        }
        return false;
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public InteractionResult use( BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult ) {
        BlockEntity blockEntity = level.getExistingBlockEntity( pos );
        
        if( blockEntity instanceof AntiBuilderBlockEntity ) {
            if( !level.isClientSide && player.isCreative() ) {
                NetworkHelper.sendOpenBEScreen( (ServerPlayer) player, pos, S2COpenAntiBuilderScreen.ANTI_BUILDER_ID );
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        builder.add( FACING );
    }
}
