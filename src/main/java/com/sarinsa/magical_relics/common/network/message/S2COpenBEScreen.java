package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ClientWork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2COpenBEScreen {

    public static final int ALTERATION_NEGATOR_ID = 0;


    public final int x, y, z;
    public final int screenType;


    public S2COpenBEScreen(int x, int y, int z, int screenType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.screenType = screenType;
    }

    public static void handle(S2COpenBEScreen message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleOpenBEScreen(message));
        }
        context.setPacketHandled(true);
    }

    public static S2COpenBEScreen decode(FriendlyByteBuf buffer) {
        return new S2COpenBEScreen(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void encode(S2COpenBEScreen message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
        buffer.writeInt(message.screenType);
    }
}
