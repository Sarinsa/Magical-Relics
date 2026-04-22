package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSimpleCfgIntSync {
    
    public final int value;
    public byte propertyId;
    
    
    public S2CSimpleCfgIntSync( int value, byte propertyId ) {
        this.value = value;
        this.propertyId = propertyId;
    }
    
    public static void handle( S2CSimpleCfgIntSync message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleCfgValueSync( message.value, message.propertyId ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CSimpleCfgIntSync decode( FriendlyByteBuf buffer ) {
        return new S2CSimpleCfgIntSync( buffer.readInt(), buffer.readByte() );
    }
    
    public static void encode( S2CSimpleCfgIntSync message, FriendlyByteBuf buffer ) {
        buffer.writeInt( message.value );
        buffer.writeByte( message.propertyId );
    }
}
