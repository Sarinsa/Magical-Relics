package com.sarinsa.magical_relics.common.network;

import com.sarinsa.magical_relics.common.network.message.C2SSaveAntiBuilderData;
import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.network.message.S2COpenAntiBuilderScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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
}
