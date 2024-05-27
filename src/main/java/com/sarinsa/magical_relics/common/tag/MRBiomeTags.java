package com.sarinsa.magical_relics.common.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class MRBiomeTags {

    public static final TagKey<Biome> CAN_HAVE_QUICKSAND_LAKES = modTag("can_have_quicksand_lakes");

    public static final TagKey<Biome> HAS_WIZARD_TOWERS = modTag("has_structure/wizard_tower");
    public static final TagKey<Biome> HAS_BURIED_DUNGEONS = modTag("has_structure/buried_dungeons");

    private static TagKey<Biome> modTag(String name) {
        return create(MagicalRelics.resLoc(name));
    }

    private static TagKey<Biome> forgeTag(String name) {
        return create(new ResourceLocation("forge", name));
    }

    private static TagKey<Biome> create(ResourceLocation resourceLocation) {
        return TagKey.create(Registries.BIOME, resourceLocation);
    }

    public static void init() {}
    private MRBiomeTags() {}
}
