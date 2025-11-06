package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.util.Mth;

public class ItemModelProps {
    
    protected static void register() {
        // Artifacts
        MRItems.ARTIFACTS_BY_CATEGORY.forEach( ( category, list ) -> {
            ItemProperties.registerGeneric( MagicalRelics.rl( category.getName() + "_variant" ),
                    ( itemStack, level, livingEntity, seed ) -> (float) Mth.clamp( ArtifactUtils.getVariant( itemStack ), 1, category.getVariations() ) );
        } );
    }
}
