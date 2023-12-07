package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.util.Mth;

public class ItemModelProps {

    protected static void register() {
        // Artifact Items
        MRItems.ALL_ARTIFACTS.forEach((artifactSet) -> {
            ItemProperties.registerGeneric(MagicalRelics.resLoc(artifactSet.type() + "_variant"),
                    (itemStack, level, livingEntity, seed) -> (float) Mth.clamp(ArtifactUtils.getVariant(itemStack), 1, artifactSet.variants()));
        });
    }
}
