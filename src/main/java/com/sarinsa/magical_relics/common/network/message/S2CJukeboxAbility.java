package com.sarinsa.magical_relics.common.network.message;

import com.sarinsa.magical_relics.common.network.work.ClientWork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class S2CJukeboxAbility {

    public final int x, y, z;
    public final boolean play;


    public S2CJukeboxAbility(BlockPos pos, boolean play) {
        this(pos.getX(), pos.getY(), pos.getZ(), play);
    }

    public S2CJukeboxAbility(int x, int y, int z, boolean play) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.play = play;
    }

    public static void handle(S2CJukeboxAbility message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleJukeboxAbilityUse(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CJukeboxAbility decode(FriendlyByteBuf buffer) {
        return new S2CJukeboxAbility(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    }

    public static void encode(S2CJukeboxAbility message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
        buffer.writeBoolean(message.play);
    }
}
