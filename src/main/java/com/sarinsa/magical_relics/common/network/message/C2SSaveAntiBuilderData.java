package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ServerWork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SSaveAntiBuilderData {
    
    public final UUID playerUUID;
    public final int[] bbDimensions;
    public final BlockPos blockEntityPos;
    
    
    public C2SSaveAntiBuilderData( UUID playerUUID, BlockPos blockPos, int[] bbCoordinates ) {
        this.playerUUID = playerUUID;
        this.blockEntityPos = blockPos;
        this.bbDimensions = bbCoordinates;
    }
    
    private C2SSaveAntiBuilderData( UUID playerUUID, BlockPos blockPos, int minX, int minY, int minZ, int maxX, int maxY, int maxZ ) {
        this( playerUUID, blockPos, new int[] { minX, minY, minZ, maxX, maxY, maxZ } );
    }
    
    public static void handle( C2SSaveAntiBuilderData message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> ServerWork.saveAntiBuilderData( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static C2SSaveAntiBuilderData decode( FriendlyByteBuf buffer ) {
        return new C2SSaveAntiBuilderData(
                buffer.readUUID(),
                buffer.readBlockPos(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt()
        );
    }
    
    public static void encode( C2SSaveAntiBuilderData message, FriendlyByteBuf buffer ) {
        buffer.writeUUID( message.playerUUID );
        buffer.writeBlockPos( message.blockEntityPos );
        buffer.writeInt( message.bbDimensions[0] );
        buffer.writeInt( message.bbDimensions[1] );
        buffer.writeInt( message.bbDimensions[2] );
        buffer.writeInt( message.bbDimensions[3] );
        buffer.writeInt( message.bbDimensions[4] );
        buffer.writeInt( message.bbDimensions[5] );
    }
}
