package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ServerWork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record C2SPlaceSolidAir(GlobalPos placePos, UUID playerUUID, String slotIdentifier) {
    
    public C2SPlaceSolidAir( Player player, BlockPos pos, String slotIdentifier ) {
        // noinspection resource
        this( GlobalPos.of( player.level().dimension(), pos ), player.getUUID(), slotIdentifier );
    }
    
    public static void handle( C2SPlaceSolidAir message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> ServerWork.handlePlaceSolidAir( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static C2SPlaceSolidAir decode( FriendlyByteBuf buffer ) {
        return new C2SPlaceSolidAir(
                buffer.readGlobalPos(),
                buffer.readUUID(),
                buffer.readUtf()
        );
    }
    
    public static void encode( C2SPlaceSolidAir message, FriendlyByteBuf buffer ) {
        buffer.writeGlobalPos( message.placePos );
        buffer.writeUUID( message.playerUUID );
        buffer.writeUtf( message.slotIdentifier );
    }
}
