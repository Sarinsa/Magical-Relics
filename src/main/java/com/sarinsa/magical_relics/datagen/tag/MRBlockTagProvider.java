package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.tag.MRBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class MRBlockTagProvider extends BlockTagsProvider {

    public MRBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper fileHelper) {
        super(generator, MagicalRelics.MODID, fileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags() {
        tag(MRBlockTags.CAMO_BLOCKS).addTags(
                BlockTags.STONE_BRICKS,
                BlockTags.BASE_STONE_OVERWORLD,
                BlockTags.BASE_STONE_NETHER,
                BlockTags.PLANKS,
                Tags.Blocks.COBBLESTONE,
                Tags.Blocks.STONE,
                Tags.Blocks.END_STONES,
                Tags.Blocks.OBSIDIAN,
                Tags.Blocks.SAND
        );
    }
}
