package com.sarinsa.magical_relics.common.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LootRandomArtifactItem extends Item {

    public LootRandomArtifactItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem) {
        if (!level.isClientSide) {
            if (itemStack.is(this) && entity != null) {

            }
        }
    }
}
