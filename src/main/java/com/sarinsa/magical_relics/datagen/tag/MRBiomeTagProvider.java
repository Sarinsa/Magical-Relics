package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.tag.MRBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MRBiomeTagProvider extends BiomeTagsProvider {

    public MRBiomeTagProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper fileHelper) {
        super(generator.getPackOutput(), lookupProvider, MagicalRelics.MODID, fileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider provider) {
        tag(MRBiomeTags.CAN_HAVE_QUICKSAND_LAKES).addTags(
                BiomeTags.IS_FOREST,
                BiomeTags.IS_JUNGLE,
                BiomeTags.IS_SAVANNA,
                BiomeTags.IS_TAIGA,
                Tags.Biomes.IS_DESERT,
                Tags.Biomes.IS_PLAINS
        );

        tag(MRBiomeTags.HAS_WIZARD_TOWERS).addTags(
                BiomeTags.IS_HILL,
                BiomeTags.IS_MOUNTAIN
        );

        tag(MRBiomeTags.HAS_BURIED_DUNGEONS).addTags(
                Tags.Biomes.IS_PLAINS,
                BiomeTags.IS_FOREST,
                BiomeTags.IS_JUNGLE,
                BiomeTags.IS_TAIGA,
                BiomeTags.IS_SAVANNA,
                Tags.Biomes.IS_DESERT
        );
    }
}
