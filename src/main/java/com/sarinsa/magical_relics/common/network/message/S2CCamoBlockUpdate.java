package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ClientWork;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record S2CCamoBlockUpdate(BlockPos pos, @Nullable BlockState camoState) {
    
    public S2CCamoBlockUpdate( BlockPos pos, BlockState camoState ) {
        this.pos = pos;
        this.camoState = camoState;
    }
    
    private S2CCamoBlockUpdate( BlockPos pos, @Nullable CompoundTag stateTag ) {
        this( pos, stateTag == null ? null : NBTHelper.readBlockState( stateTag ) );
    }
    
    public static void handle( S2CCamoBlockUpdate message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleCamoStateUpdate( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CCamoBlockUpdate decode( FriendlyByteBuf buffer ) {
        return new S2CCamoBlockUpdate( buffer.readBlockPos(), buffer.readNbt() );
    }
    
    public static void encode( S2CCamoBlockUpdate message, FriendlyByteBuf buffer ) {
        buffer.writeBlockPos( message.pos );
        buffer.writeNbt( message.camoState == null ? new CompoundTag() : NBTHelper.writeBlockState( message.camoState ) );
    }
}
