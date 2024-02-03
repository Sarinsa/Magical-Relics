package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CamoDispenserBlockEntity extends DispenserBlockEntity implements CamoBlockEntity {

    private BlockState camoState;


    public CamoDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.CAMO_DISPENSER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CamoDispenserBlockEntity arrowTrap) {

    }

    @Nullable
    @Override
    public BlockState getCamoState() {
        return camoState;
    }

    @Override
    public void setCamoState(@Nullable BlockState state) {
        this.camoState = state;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        writeUpdateData(compoundTag, getCamoState());
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        setCamoState(readCamoState(compoundTag));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        writeUpdateData(compoundTag, getCamoState());
        return compoundTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt != null)
            super.onDataPacket(net, pkt);

        if(level.isClientSide) {
            CompoundTag compoundTag = pkt.getTag();

            if (compoundTag == null)
                return;

            setCamoState(readCamoState(compoundTag));
        }
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
}
