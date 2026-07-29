package com.sarinsa.magical_relics.common.network.work;

import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.network.NetworkHelper;
import com.sarinsa.magical_relics.common.network.message.C2SPlaceSolidAir;
import com.sarinsa.magical_relics.common.network.message.C2SRemoveSolidAir;
import com.sarinsa.magical_relics.common.network.message.C2SSaveAntiBuilderData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

@Mod.EventBusSubscriber( modid = MagicalRelics.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public class ServerWork {
    
    @SuppressWarnings( "resource" )
    public static void handleAntiBuilderData( C2SSaveAntiBuilderData message ) {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if( server == null ) return;
        
        if( message.boxDimensions().length != 6 ) {
            MagicalRelics.LOG.warn( "Received anti-builder 'update bounds' packet with invalid coordinates array length. Expected 6, got {}", message.boxDimensions().length );
            return;
        }
        final ServerPlayer player = server.getPlayerList().getPlayer( message.playerUUID() );
        if( player == null ) return;
        
        final ServerLevel level = player.serverLevel();
        final BlockEntity blockEntity = level.getBlockEntity( message.blockEntityPos() );
        
        if( blockEntity instanceof AntiBuilderBlockEntity antiBuilder ) {
            antiBuilder.recalculateEffectiveArea( message.boxDimensions() );
        }
    }
    
    public static void handlePlaceSolidAir( C2SPlaceSolidAir message ) {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if( server == null ) return;
        
        final ServerPlayer player = server.getPlayerList().getPlayer( message.playerUUID() );
        if( player == null ) return;
        
        final ServerLevel level = player.serverLevel();
        // TODO - Maybe log this?
        // Make sure the message is requesting placement
        // in the same dimension as the player is currently in
        if( message.placePos().dimension() != level.dimension() ) return;
        
        final BlockPos placePos = message.placePos().pos();
        
        // TODO - Maybe log this?
        // Do not mess around in an unloaded chunk
        if( !level.isLoaded( placePos ) ) return;
        
        final BlockState currentState = level.getBlockState( placePos );
        
        boolean allowPlacement = false;
        
        if( Mth.sqrt( (float) player.blockPosition().distSqr( placePos ) ) <= 5.0F ) {
            allowPlacement = true;
        }
        else if( MRArtifactAbilities.AIR_SNEAK.get().getConfig().AIR_SNEAK.allowReplacing.field().get()
                ? (currentState.canBeReplaced() && !currentState.isFaceSturdy( level, placePos, Direction.UP ))
                : currentState.isAir() ) {
            allowPlacement = true;
        }
        // If checks didn't pass, tell client to revert placement
        if( !allowPlacement ) {
            level.setBlock( placePos, level.getBlockState( placePos ), Block.UPDATE_CLIENTS );
            return;
        }
        level.setBlock( placePos, MRBlocks.SOLID_AIR.get().defaultBlockState(), Block.UPDATE_ALL ^ Block.UPDATE_CLIENTS );
        level.scheduleTick( placePos, MRBlocks.SOLID_AIR.get(), 10 );
        
        // Hurt the artifact item that was used by the player to place solid air, if it exists
        if( !message.slotIdentifier().equals( NetworkHelper.NULL_SLOT_IDENTIFIER ) ) {
            // First check if the identifier is numeric, in which case we
            // expect it to be the ordinal of an EquipmentSlot
            try {
                final int slotOrdinal = Integer.parseInt( message.slotIdentifier() );
                final EquipmentSlot slot = EquipmentSlot.values()[slotOrdinal];
                player.getItemBySlot( slot ).hurtAndBreak( 1, player, ( p ) -> player.broadcastBreakEvent( slot ) );
            }
            catch( NumberFormatException e ) {
                // If the identifier was not numeric, it is almost definitely a curio identifier
                CuriosApi.getCuriosInventory( player ).ifPresent( ( itemHandler ) -> {
                    final List<SlotResult> list = itemHandler.findCurios( message.slotIdentifier() );
                    // TODO - Maybe log this?
                    if( list.isEmpty() ) return;
                    final SlotResult slot = list.get( 0 );
                    slot.stack().hurtAndBreak( 1, player, ( p ) -> CuriosApi.broadcastCurioBreakEvent( slot.slotContext() ) );
                } );
            }
        }
    }
    
    @SuppressWarnings( "resource" )
    public static void handleRemoveSolidAir( C2SRemoveSolidAir message ) {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if( server == null ) return;
        
        final ServerPlayer player = server.getPlayerList().getPlayer( message.playerUUID() );
        if( player == null ) return;
        
        final ServerLevel level = player.serverLevel();
        // TODO - Maybe log this?
        // Make sure the message is requesting placement
        // in the same dimension as the player is currently in
        if( message.pos().dimension() != level.dimension() ) return;
        
        final BlockPos removePos = message.pos().pos();
        
        // TODO - Maybe log this?
        // Do not mess around in an unloaded chunk
        if( !level.isLoaded( removePos ) ) return;
        
        final BlockState currentState = level.getBlockState( removePos );
        
        if( currentState.is( MRBlocks.SOLID_AIR.get() ) ) {
            level.removeBlock( removePos, false );
        }
    }
}
