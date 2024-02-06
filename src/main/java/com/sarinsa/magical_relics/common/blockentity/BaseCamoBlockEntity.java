package com.sarinsa.magical_relics.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public abstract class BaseCamoBlockEntity extends BlockEntity implements CamoBlockEntity {

    private BlockState camoState;

    public BaseCamoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Nullable
    public BlockState getCamoState() {
        return camoState;
    }

    public void setCamoState(BlockState state) {
        camoState = state;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        writeUpdateData(compoundTag, camoState);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        readCamoState(compoundTag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        writeUpdateData(compoundTag, camoState);
        return compoundTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if(level.isClientSide) {
            super.onDataPacket(net, pkt);

            CompoundTag compoundTag = pkt.getTag();

            if (compoundTag == null)
                return;

            readCamoState(compoundTag);
        }
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }
}
