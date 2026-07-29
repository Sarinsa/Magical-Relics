package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ServerWork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record C2SRemoveSolidAir(GlobalPos pos, UUID playerUUID) {
    
    public C2SRemoveSolidAir( Player player, BlockPos pos ) {
        // noinspection resource
        this( GlobalPos.of( player.level().dimension(), pos ), player.getUUID() );
    }
    
    public static void handle( C2SRemoveSolidAir message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> ServerWork.handleRemoveSolidAir( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static C2SRemoveSolidAir decode( FriendlyByteBuf buffer ) {
        return new C2SRemoveSolidAir(
                buffer.readGlobalPos(),
                buffer.readUUID()
        );
    }
    
    public static void encode( C2SRemoveSolidAir message, FriendlyByteBuf buffer ) {
        buffer.writeGlobalPos( message.pos );
        buffer.writeUUID( message.playerUUID );
    }
}
