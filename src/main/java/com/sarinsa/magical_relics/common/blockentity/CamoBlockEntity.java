package com.sarinsa.magical_relics.common.blockentity;

import net.minecraft.nbt.CompoundTag;
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
        if (compoundTag.contains("CamoState", Tag.TAG_STRING)) {
            BlockState state = Blocks.STONE_BRICKS.defaultBlockState();
            ResourceLocation blockId = ResourceLocation.tryParse(compoundTag.getString("CamoState"));

            if (blockId != null && ForgeRegistries.BLOCKS.containsKey(blockId))
                state = ForgeRegistries.BLOCKS.getValue(blockId).defaultBlockState();

            return state;
        }
        return null;
    }

    default void writeUpdateData(CompoundTag compoundTag, @Nullable BlockState camoState) {
        if (camoState != null) {
            compoundTag.putString("CamoState", ForgeRegistries.BLOCKS.getKey(camoState.getBlock()).toString());
        }
    }
}
