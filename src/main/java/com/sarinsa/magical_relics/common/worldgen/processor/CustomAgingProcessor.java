package com.sarinsa.magical_relics.common.worldgen.processor;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.block.CamoBlock;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Makes things look old. Doesn't wreak havoc on all sorts of slabs and stairs unlike
 * {@link BlockAgeProcessor} which is a completely insane structure processor.
 * Designed to work with Magical Relics' camo block entities and other blocks,
 * such as camo dispensers, illusionary blocks and crumbling blocks.
 */
public class CustomAgingProcessor extends StructureProcessor {

    public static final Codec<CustomAgingProcessor> CODEC = Codec.FLOAT.fieldOf("oldness")
            .xmap(CustomAgingProcessor::new, (processor) -> processor.oldness)
            .codec();


    private static final Map<Block, Block> REPLACEMENTS = new HashMap<>();


    // Its chewsday innit?
    public static void init() {
        REPLACEMENTS.put(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE);
        REPLACEMENTS.put(Blocks.COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB);
        REPLACEMENTS.put(Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS);
        REPLACEMENTS.put(Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL);
        REPLACEMENTS.put(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        REPLACEMENTS.put(Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB);
        REPLACEMENTS.put(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS);
        REPLACEMENTS.put(Blocks.STONE_BRICK_WALL, Blocks.MOSSY_COBBLESTONE_WALL);
        REPLACEMENTS.put(MRBlocks.CRUMBLING_COBBLESTONE.get(), MRBlocks.CRUMBLING_MOSSY_COBBLESTONE.get());
        REPLACEMENTS.put(MRBlocks.CRUMBLING_STONE_BRICKS.get(), MRBlocks.CRUMBLING_MOSSY_STONE_BRICKS.get());
        REPLACEMENTS.put(Blocks.BOOKSHELF, Blocks.COBWEB);
    }

    private final float oldness;


    public CustomAgingProcessor(float oldness) {
        this.oldness = oldness;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Nullable
    public StructureTemplate.StructureBlockInfo process(LevelReader level, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings structureSettings, @Nullable StructureTemplate template) {
        RandomSource random = structureSettings.getRandom(blockInfo.pos());
        BlockState blockState = blockInfo.state();
        BlockPos blockpos = blockInfo.pos();
        BlockState newState = null;

        if (blockState.getBlock() instanceof CamoBlock) {
            CompoundTag blockEntityTag = blockInfo.nbt();

            // Don't bother checking for camo blocks
            // that don't have an existing camo.
            if (blockEntityTag != null && random.nextFloat() < oldness) {
                CompoundTag camoTag = blockEntityTag.getCompound("CamoState");
                BlockState nbtBlockState = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), camoTag);

                if (nbtBlockState.is(Blocks.COBBLESTONE)) {
                    writeToCamo(blockEntityTag, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
                }
                else if (nbtBlockState.is(Blocks.STONE_BRICKS)) {
                    writeToCamo(blockEntityTag, Blocks.MOSSY_STONE_BRICKS.defaultBlockState());
                }
            }
        }
        else {
            if (REPLACEMENTS.containsKey(blockInfo.state().getBlock())) {
                if (random.nextFloat() < oldness) {
                    newState = REPLACEMENTS.get(blockState.getBlock()).defaultBlockState();

                    try {
                        for (Property property : blockState.getProperties()) {
                            newState = newState.setValue(property, blockState.getValue(property));
                        }
                    }
                    catch (Exception e) {
                        MagicalRelics.LOG.error("Aging processor failed to copy block state properties from state '" + blockState + "' to state '" + newState + "'");
                    }
                }
            }
        }
        return newState != null ? new StructureTemplate.StructureBlockInfo(blockpos, newState, blockInfo.nbt()) : blockInfo;
    }

    private static void writeToCamo(CompoundTag tag, BlockState state) {
        tag.put("CamoState", NbtUtils.writeBlockState(state));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return MRStructureProcessors.CUSTOM_MOSSIFIER.get();
    }
}
