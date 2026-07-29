package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CJukeboxAbility(int x, int y, int z, boolean play) {
    
    public static void handle( S2CJukeboxAbility message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleJukeboxAbilityUse( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CJukeboxAbility decode( FriendlyByteBuf buffer ) {
        return new S2CJukeboxAbility( buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean() );
    }
    
    public static void encode( S2CJukeboxAbility message, FriendlyByteBuf buffer ) {
        buffer.writeInt( message.x );
        buffer.writeInt( message.y );
        buffer.writeInt( message.z );
        buffer.writeBoolean( message.play );
    }
}
