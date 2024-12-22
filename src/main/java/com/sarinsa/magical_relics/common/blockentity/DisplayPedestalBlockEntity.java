package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.block.DisplayPedestalBlock;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class DisplayPedestalBlockEntity extends BlockEntity {

    public static final String GENERATE_ARTIFACT_KEY = "GenerateArtifact";
    public static final String ITEM_KEY = "ArtifactItem";
    public static final String LOCKED_KEY = "Locked";
    public static final String WIZARDS_FAVORITE_KEY = "WizardsFavorite";


    private ItemStack artifact;
    private boolean generateWizFavorite = false;
  

    public DisplayPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.DISPLAY_PEDESTAL.get(), pos, state);
    }

    @Nonnull
    public ItemStack getArtifact() {
        return artifact == null ? ItemStack.EMPTY : artifact;
    }

    public void setArtifact(ItemStack artifact) {
        this.artifact = artifact;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        writeUpdateData(compoundTag);
        compoundTag.remove(GENERATE_ARTIFACT_KEY);

        if (getBlockState().is(MRBlocks.DISPLAY_PEDESTAL.get())) {
            compoundTag.putBoolean(LOCKED_KEY, getBlockState().getValue(DisplayPedestalBlock.LOCKED));
        }

        compoundTag.putBoolean(WIZARDS_FAVORITE_KEY, generateWizFavorite);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        readArtifactItem(compoundTag);

        if (compoundTag.contains(GENERATE_ARTIFACT_KEY, Tag.TAG_BYTE)) {
            if (compoundTag.getBoolean(GENERATE_ARTIFACT_KEY) && level != null && !level.isClientSide) {
                setArtifact(ArtifactUtils.generateRandomArtifact(level, level.random, false));
            }
        }

        if (hasLevel()) {
            if (compoundTag.contains(LOCKED_KEY, Tag.TAG_BYTE)) {
                level.setBlock(getBlockPos(), getBlockState().setValue(DisplayPedestalBlock.LOCKED, compoundTag.getBoolean(LOCKED_KEY)), Block.UPDATE_CLIENTS);
            }
        }

        if (compoundTag.contains(WIZARDS_FAVORITE_KEY, Tag.TAG_BYTE)) {
            generateWizFavorite = compoundTag.getBoolean(WIZARDS_FAVORITE_KEY);
        }
    }

    private void readArtifactItem(CompoundTag compoundTag) {
        if (compoundTag.contains(ITEM_KEY, Tag.TAG_COMPOUND)) {
            artifact = ItemStack.of(compoundTag.getCompound(ITEM_KEY));
        }
    }

    private void writeUpdateData(CompoundTag compoundTag) {
        if (!getArtifact().isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            getArtifact().save(itemTag);
            compoundTag.put(ITEM_KEY, itemTag);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        writeUpdateData(compoundTag);
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if(level.isClientSide) {
            super.onDataPacket(net, pkt);

            CompoundTag compoundTag = pkt.getTag();

            if (compoundTag == null)
                return;

            readArtifactItem(compoundTag);
        }
    }
}
