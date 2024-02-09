package com.sarinsa.magical_relics.common.forge.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.item.ItemEvent;

/**
 * Fired AFTER an item has been tossed out of the player's
 * inventory, so the ItemEntity exists in the world by now.
 */
public class ItemPostTossedEvent extends ItemEvent {

    /**
     * Creates a new event for an {@link ItemEntity}.
     *
     * @param itemEntity The ItemEntity for this event
     */
    public ItemPostTossedEvent(ItemEntity itemEntity) {
        super(itemEntity);
    }
}
