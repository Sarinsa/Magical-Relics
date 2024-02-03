package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.tag.MRBiomeTags;
import com.sarinsa.magical_relics.common.tag.MRBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class MRBiomeTagProvider extends BiomeTagsProvider {

    public MRBiomeTagProvider(DataGenerator generator, @Nullable ExistingFileHelper fileHelper) {
        super(generator, MagicalRelics.MODID, fileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags() {
        tag(MRBiomeTags.CAN_HAVE_QUICKSAND_LAKES).addTags(
                BiomeTags.IS_FOREST,
                BiomeTags.IS_JUNGLE,
                BiomeTags.IS_SAVANNA,
                BiomeTags.IS_TAIGA,
                Tags.Biomes.IS_DESERT,
                Tags.Biomes.IS_PLAINS
        );
    }
}
