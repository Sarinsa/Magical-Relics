package com.sarinsa.magical_relics.common.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public interface CamoBlockEntity {


    @Nullable
    BlockState getCamoState();

    void setCamoState(@Nullable BlockState state);

    @Nullable
    default BlockState readCamoState(CompoundTag compoundTag) {
        if (compoundTag.contains("CamoState", Tag.TAG_COMPOUND)) {
            BlockState camo = null;

            try {
                camo = NbtUtils.readBlockState(compoundTag.getCompound("CamoState"));
            }
            catch (Exception ignored) {

            }
            return camo == null || camo == Blocks.AIR.defaultBlockState() ? Blocks.STONE_BRICKS.defaultBlockState() : camo;
        }
        return null;
    }

    default void writeUpdateData(CompoundTag compoundTag, @Nullable BlockState camoState) {
        if (camoState != null) {
            compoundTag.put("CamoState", NbtUtils.writeBlockState(camoState));
        }
    }
}
