package com.sarinsa.magical_relics.common.network;

import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NetworkHelper {

    public static void sendJukeboxAbilityUse(@Nonnull ServerPlayer player, int x, int y, int z, boolean play) {
        Objects.requireNonNull(player);
        PacketHandler.sendToClient(new S2CJukeboxAbility(x, y, z, play), player);
    }
}
