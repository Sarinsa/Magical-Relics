package com.sarinsa.magical_relics.common.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MRItemTags {


    public static final TagKey<Item> ARTIFACTS = modTag("artifacts");


    private static TagKey<Item> modTag(String name) {
        return ItemTags.create(MagicalRelics.resLoc(name));
    }

    private static TagKey<Item> forgeTag(String name) {
        return ItemTags.create(new ResourceLocation("forge", name));
    }

    public static void init() {}
    private MRItemTags() {}
}
