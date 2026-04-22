package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSimpleCfgDoubleSync {
    
    public final double value;
    public byte valueId;
    
    
    public S2CSimpleCfgDoubleSync( double value, byte valueId ) {
        this.value = value;
        this.valueId = valueId;
    }
    
    public static void handle( S2CSimpleCfgDoubleSync message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleCfgValueSync( message.value, message.valueId ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CSimpleCfgDoubleSync decode( FriendlyByteBuf buffer ) {
        return new S2CSimpleCfgDoubleSync( buffer.readDouble(), buffer.readByte() );
    }
    
    public static void encode( S2CSimpleCfgDoubleSync message, FriendlyByteBuf buffer ) {
        buffer.writeDouble( message.value );
        buffer.writeByte( message.valueId );
    }
}