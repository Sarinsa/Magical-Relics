package com.sarinsa.magical_relics.common.worldgen.processor;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.blockentity.DisplayPedestalBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class DisplayPedestalProcessor extends StructureProcessor {

    public static final Codec<DisplayPedestalProcessor> CODEC = Codec.INT.fieldOf("legendary_chance")
            .xmap(DisplayPedestalProcessor::new, (processor) -> processor.legendaryChance)
            .codec();


    // TODO - Do something with this
    private final int legendaryChance;

    public DisplayPedestalProcessor(int legendaryChance) {
        this.legendaryChance = legendaryChance;
    }


    @SuppressWarnings("ConstantConditions")
    @Nullable
    public StructureTemplate.StructureBlockInfo process(LevelReader level, BlockPos pos, BlockPos p_74142_, StructureTemplate.StructureBlockInfo info, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings structureSettings, @Nullable StructureTemplate template) {
        RandomSource random = structureSettings.getRandom(blockInfo.pos);
        BlockState blockstate = blockInfo.state;
        BlockPos blockpos = blockInfo.pos;

        boolean isDisplayPedestal = blockstate.is(MRBlocks.DISPLAY_PEDESTAL.get());
        CompoundTag tag = blockInfo.nbt;

        if (isDisplayPedestal) {
            if (tag == null) tag = new CompoundTag();
            CompoundTag itemStackTag = new CompoundTag();
            ItemStack itemStack = ArtifactUtils.generateRandomArtifact(random);
            itemStack.save(itemStackTag);

            tag.put(DisplayPedestalBlockEntity.ITEM_KEY, itemStackTag);
        }
        return isDisplayPedestal ? new StructureTemplate.StructureBlockInfo(blockpos, blockstate, tag) : blockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return MRStructureProcessors.DISPLAY_PEDESTAL.get();
    }
}
