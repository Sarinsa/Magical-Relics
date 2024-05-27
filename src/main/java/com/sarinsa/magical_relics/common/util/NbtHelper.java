package com.sarinsa.magical_relics.common.util;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class NbtHelper {

    /**
     * Essentially does the same thing as {@link net.minecraft.nbt.NbtUtils#writeBlockState(BlockState)}, except
     * registry lookup is done with the Forge block registry to avoid needing a level object.
     */
    public static BlockState readBlockState(CompoundTag compoundTag) {
        if (!compoundTag.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        }
        else {
            ResourceLocation resourcelocation = new ResourceLocation(compoundTag.getString("Name"));
            Block block = ForgeRegistries.BLOCKS.getValue(resourcelocation);

            if (block == null) {
                return Blocks.AIR.defaultBlockState();
            }
            else {
                BlockState state = block.defaultBlockState();

                if (compoundTag.contains("Properties", 10)) {
                    CompoundTag propertiesTag = compoundTag.getCompound("Properties");
                    StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();

                    for(String key : propertiesTag.getAllKeys()) {
                        Property<?> property = stateDefinition.getProperty(key);
                        if (property != null) {
                            state = setValueHelper(state, property, key, propertiesTag, compoundTag);
                        }
                    }
                }
                return state;
            }
        }
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S state, Property<T> property, String key, CompoundTag propertiesTag, CompoundTag compoundTag) {
        Optional<T> optional = property.getValue(propertiesTag.getString(key));

        if (optional.isPresent()) {
            return state.setValue(property, optional.get());
        }
        else {
            MagicalRelics.LOG.warn("Unable to read property: {} with value: {} for blockstate: {}", key, propertiesTag.getString(key), compoundTag.toString());
            return state;
        }
    }
}
