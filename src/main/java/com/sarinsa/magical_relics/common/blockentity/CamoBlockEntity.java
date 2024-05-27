package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.NbtHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;

public interface CamoBlockEntity {


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
            return (camo == null || camo == Blocks.AIR.defaultBlockState()) ? Blocks.STONE_BRICKS.defaultBlockState() : camo;
        }
        return null;
    }

    default void writeUpdateData(CompoundTag compoundTag, @Nullable BlockState camoState) {
        if (camoState != null) {
            compoundTag.put("CamoState", NbtUtils.writeBlockState(camoState));
        }
    }
}
