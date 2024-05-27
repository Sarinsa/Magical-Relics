package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MRBlockTagProvider extends BlockTagsProvider {

    public MRBlockTagProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper fileHelper) {
        super(generator.getPackOutput(), lookupProvider, MagicalRelics.MODID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        MRBlocks.BLOCK_TAGS.forEach((regObj, tagArray) -> {
            for (TagKey<Block> tag : tagArray) {
                tag(tag).add(regObj.get());
            }
        });
    }
}
