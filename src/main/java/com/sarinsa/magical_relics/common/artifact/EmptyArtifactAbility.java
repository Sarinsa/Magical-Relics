package com.sarinsa.magical_relics.common.artifact;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

public class EmptyArtifactAbility extends BaseArtifactAbility {

    @Override
    public ActiveType getActiveType() {
        return ActiveType.INVENTORY;
    }

    @Override
    public Component getAbilityDescription() {
        return Component.empty();
    }

    @Override
    public Supplier<Enchantment[]> incompatibleEnchantments() {
        return null;
    }
}
