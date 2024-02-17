package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ClientWork;
import com.sarinsa.magical_relics.common.network.work.ServerWork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SSaveALTNEGData {

    public final UUID playerUUID;
    public final int xSize, ySize, zSize;
    public final BlockPos blockEntityPos;


    public C2SSaveALTNEGData(UUID playerUUID, BlockPos blockPos, int xSize, int ySize, int zSize) {
        this.playerUUID = playerUUID;
        this.blockEntityPos = blockPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public static void handle(C2SSaveALTNEGData message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> ServerWork.handleSaveALTNEGData(message));
        }
        context.setPacketHandled(true);
    }

    public static C2SSaveALTNEGData decode(FriendlyByteBuf buffer) {
        return new C2SSaveALTNEGData(buffer.readUUID(), buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void encode(C2SSaveALTNEGData message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.playerUUID);
        buffer.writeBlockPos(message.blockEntityPos);
        buffer.writeInt(message.xSize);
        buffer.writeInt(message.ySize);
        buffer.writeInt(message.zSize);
    }
}
