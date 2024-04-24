package com.sarinsa.magical_relics.common.worldgen.processor;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Purely a marker for structure pieces that should not fall victim to
 * getting certain blocks waterlogged. Thanks to TelepathicGrunt for showing this solution!
 */
public class NoWaterloggingProcessor extends StructureProcessor {

    public static final Codec<NoWaterloggingProcessor> CODEC = Codec.unit(NoWaterloggingProcessor::new);

    private NoWaterloggingProcessor() { }

    @Override
    @SuppressWarnings("deprecation")
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pos2, StructureTemplate.StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, StructurePlaceSettings settings) {
        return infoIn2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return MRStructureProcessors.NO_WATERLOGGING.get();
    }
}
