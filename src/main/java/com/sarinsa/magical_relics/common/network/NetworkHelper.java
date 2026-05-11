package com.sarinsa.magical_relics.common.network;

import com.sarinsa.magical_relics.common.network.message.C2SSaveAntiBuilderData;
import com.sarinsa.magical_relics.common.network.message.S2CCamoBlockUpdate;
import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.network.message.S2COpenAntiBuilderScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NetworkHelper {
    
    
    public static void sendJukeboxAbilityUse( @Nonnull ServerPlayer player, int x, int y, int z, boolean play ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2CJukeboxAbility( x, y, z, play ), player );
    }
    
    public static void sendOpenBEScreen( @Nonnull ServerPlayer player, BlockPos pos, int screenId ) {
        Objects.requireNonNull( player );
        PacketHandler.sendToClient( new S2COpenAntiBuilderScreen( pos.getX(), pos.getY(), pos.getZ(), screenId ), player );
    }
    
    public static void sendRecalcAntiBuilderBounds( @Nonnull Player player, BlockPos pos, int[] bbCoordinates ) {
        Objects.requireNonNull( player );
        PacketHandler.CHANNEL.sendToServer( new C2SSaveAntiBuilderData( player.getUUID(), pos, bbCoordinates ) );
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
}
