package com.sarinsa.magical_relics.common.worldgen.processor;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PotPlantProcessor extends StructureProcessor {

    public static final Codec<PotPlantProcessor> CODEC = Codec.unit(PotPlantProcessor::new);
    private static final List<Block> pottedPlants = new ArrayList<>();


    public PotPlantProcessor() {
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            if (block instanceof FlowerPotBlock)
                pottedPlants.add(block);
        }
    }


    @Nullable
    public StructureTemplate.StructureBlockInfo process(LevelReader level, BlockPos pos, BlockPos p_74142_, StructureTemplate.StructureBlockInfo info, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings structureSettings, @Nullable StructureTemplate template) {
        RandomSource random = structureSettings.getRandom(blockInfo.pos());
        BlockState state = blockInfo.state();

        if (state.is(Blocks.FLOWER_POT)) {
            Block flowerPot = null;

            try {
                flowerPot = pottedPlants.get(random.nextInt(pottedPlants.size()));
            }
            catch (Exception e) {
                MagicalRelics.LOG.error("PotPlantProcessor failed to pick random flower pot block!");
            }

            if (flowerPot != null) {
                return new StructureTemplate.StructureBlockInfo(blockInfo.pos(), flowerPot.defaultBlockState(), null);
            }
        }
        return blockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return MRStructureProcessors.POT_PLANT.get();
    }
}
