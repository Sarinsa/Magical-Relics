package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.util.Mth;

import java.util.Locale;

public class ItemModelProps {

    protected static void register() {
        // ItemArtifact Items
        MRItems.ALL_ARTIFACTS.forEach((artifactSet) -> {
            ItemProperties.registerGeneric(MagicalRelics.resLoc(artifactSet.category().getName() + "_variant"),
                    (itemStack, level, livingEntity, seed) -> (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, artifactSet.variants()));
        });
    }
}
