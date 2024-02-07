package com.sarinsa.magical_relics.common.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class MRBlockTags {

    private static TagKey<Block> modTag(String name) {
        return BlockTags.create(MagicalRelics.resLoc(name));
    }

    private static TagKey<Block> forgeTag(String name) {
        return BlockTags.create(new ResourceLocation("forge", name));
    }

    public static void init() {
        MRBiomeTags.init();
    }
    private MRBlockTags() {}
}
