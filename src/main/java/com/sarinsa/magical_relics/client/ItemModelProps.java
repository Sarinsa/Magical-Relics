package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.util.Mth;

public class ItemModelProps {

    protected static void register() {

        ItemProperties.registerGeneric(MagicalRelics.resLoc("amulet_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.AMULETS.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("belt_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.BELTS.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("trinket_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.TRINKETS.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("wand_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.WANDS.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("staff_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.STAFFS.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("dagger_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.DAGGERS.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("sword_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.SWORDS.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("figurine_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.FIGURINES.variants());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("ring_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.RINGS.variants());
        });
    }
}
