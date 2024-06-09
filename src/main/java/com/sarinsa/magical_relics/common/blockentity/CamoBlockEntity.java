package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.util.NbtHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface CamoBlockEntity {

    Supplier<BlockState> defaultCamoState = Blocks.STONE_BRICKS::defaultBlockState;

    @Nullable
    BlockState getCamoState();

    void setCamoState(@Nullable BlockState state);

    @Nullable
    default BlockState readCamoState(CompoundTag compoundTag) {
        if (compoundTag.contains("CamoState", Tag.TAG_COMPOUND)) {
            BlockState camo = null;

            try {
                camo = NbtHelper.readBlockState(compoundTag.getCompound("CamoState"));
            }
            catch (Exception ignored) {

            }
            return (camo == null || camo == Blocks.AIR.defaultBlockState()) ? defaultCamoState.get() : camo;
        }
        return null;
    }

    default void writeUpdateData(CompoundTag compoundTag, @Nullable BlockState camoState) {
        if (camoState != null) {
            compoundTag.put("CamoState", NbtUtils.writeBlockState(camoState));
        }
    }
}
