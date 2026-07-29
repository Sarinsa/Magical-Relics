package com.sarinsa.magical_relics.common.network;

import com.sarinsa.magical_relics.common.network.message.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NetworkHelper {
    
    /** Represents a null slot identifier in the context it is being used. */
    public static final String NULL_SLOT_IDENTIFIER = "-1";
    
    
    //-------------------------------------------------------------------
    //                       Server -> Client
    //-------------------------------------------------------------------
    
    public static void sendJukeboxAbilityUse( @Nonnull ServerPlayer player, int x, int y, int z, boolean play ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CJukeboxAbility( x, y, z, play ), player );
    }
    
    public static void sendOpenBEScreen( @Nonnull ServerPlayer player, BlockPos pos, int screenId ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2COpenAntiBuilderScreen( pos.getX(), pos.getY(), pos.getZ(), screenId ), player );
    }
    
    public static void broadcastCamoBlockUpdate( ServerLevel level, BlockPos pos, BlockState camoState ) {
        Objects.requireNonNull( level );
        Objects.requireNonNull( pos );
        Objects.requireNonNull( camoState );
        
        final S2CCamoBlockUpdate message = new S2CCamoBlockUpdate( pos, camoState );
        
        for( ServerPlayer player : level.players() ) {
            PacketHandler.sendToClient( message, player );
        }
    }
    
    
    //-------------------------------------------------------------------
    //                       Client -> Server
    //-------------------------------------------------------------------
    
    public static void requestRecalcAntiBuilderBounds( @Nonnull Player player, BlockPos pos, int[] bbCoordinates ) {
        Objects.requireNonNull( player );
        PacketHandler.CHANNEL.sendToServer( new C2SSaveAntiBuilderData( player.getUUID(), bbCoordinates, pos ) );
    }
    
    public static void requestSolidAirPlacement( @Nonnull Player player, BlockPos pos, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext ) {
        Objects.requireNonNull( player );
        Objects.requireNonNull( pos );
        
        String slotIdentifier = NULL_SLOT_IDENTIFIER;
        
        if( slot != null ) {
            slotIdentifier = String.valueOf( slot.ordinal() );
        }
        else if( slotContext != null ) {
            slotIdentifier = slotContext.identifier();
        }
        PacketHandler.CHANNEL.sendToServer( new C2SPlaceSolidAir( player, pos, slotIdentifier ) );
    }
    
    public static void requestSolidAirRemoval( Player player, BlockPos pos ) {
        Objects.requireNonNull( player );
        Objects.requireNonNull( pos );
        PacketHandler.CHANNEL.sendToServer( new C2SRemoveSolidAir( player, pos ) );
    }
}
