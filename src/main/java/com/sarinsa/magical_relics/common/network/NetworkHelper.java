package com.sarinsa.magical_relics.common.network;

import com.sarinsa.magical_relics.common.network.message.C2SSaveALTNEGData;
import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.network.message.S2COpenBEScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NetworkHelper {

    public static void sendJukeboxAbilityUse(@Nonnull ServerPlayer player, int x, int y, int z, boolean play) {
        Objects.requireNonNull(player);
        PacketHandler.sendToClient(new S2CJukeboxAbility(x, y, z, play), player);
    }

    public static void sendOpenBEScreen(@Nonnull ServerPlayer player, BlockPos pos, int screenId) {
        Objects.requireNonNull(player);
        PacketHandler.sendToClient(new S2COpenBEScreen(pos.getX(), pos.getY(), pos.getZ(), screenId), player);
    }

    public static void sendSaveALTNEGData(@Nonnull Player player, BlockPos pos, int xSize, int ySize, int zSize) {
        PacketHandler.CHANNEL.sendToServer(new C2SSaveALTNEGData(player.getUUID(), pos, xSize, ySize, zSize));
    }
}
