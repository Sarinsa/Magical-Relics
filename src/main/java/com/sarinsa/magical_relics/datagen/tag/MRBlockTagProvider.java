package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.tag.MRBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class MRBlockTagProvider extends BlockTagsProvider {

    public MRBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper fileHelper) {
        super(generator, MagicalRelics.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        MRBlocks.BLOCK_TAGS.forEach((regObj, tagArray) -> {
            for (TagKey<Block> tag : tagArray) {
                tag(tag).add(regObj.get());
            }
        });
    }
}
