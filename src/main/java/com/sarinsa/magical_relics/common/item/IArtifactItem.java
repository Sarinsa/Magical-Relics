package com.sarinsa.magical_relics.common.item;

import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import net.minecraft.world.item.Item;

/** Represents an item that can be assigned abilities and use them. */
public interface IArtifactItem {
    
    /** @return The artifact category of this item. */
    ArtifactCategory getCategory();
    
    /** @return This IArtifactItem as an Item. */
    Item artifactAsItem();
}
