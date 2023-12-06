package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.util.Mth;

public class ItemModelProps {

    protected static void register() {

        ItemProperties.registerGeneric(MagicalRelics.resLoc("amulet_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.AMULETS.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("belt_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.BELTS.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("trinket_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.TRINKETS.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("wand_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.WANDS.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("staff_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.STAFFS.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("dagger_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.DAGGERS.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("sword_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.SWORDS.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("figurine_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.FIGURINES.getVariations());
        });
        ItemProperties.registerGeneric(MagicalRelics.resLoc("ring_variant"), (itemStack, level, livingEntity, seed) -> {
            return (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, MRItems.RINGS.getVariations());
        });
    }
}
