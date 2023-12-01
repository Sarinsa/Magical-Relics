package com.sarinsa.magical_relics.common.artifact;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public abstract class BaseArtifactAbility implements ArtifactAbility {

    public BaseArtifactAbility() {
    }

    /**
     * @return The ActiveType of this artifact ability.
     */
    public abstract ActiveType getActiveType();

    /**
     * @return A description of what this artifact ability does. Appended to
     * {@link net.minecraft.world.item.Item#appendHoverText(ItemStack, Level, List, TooltipFlag)}
     */
    public abstract Component getAbilityDescription();

    /**
     * @return An array of enchantments that cannot be applied to
     * an artifact item with this ability.
     */
    public abstract Supplier<Enchantment[]> incompatibleEnchantments();

    @Override
    public boolean onUse() {
        return false;
    }

    @Override
    public boolean onClickBlock() {
        return false;
    }

    @Override
    public boolean onSneakClickBlock() {
        return false;
    }

    @Override
    public boolean onSneak() {
        return false;
    }

    @Override
    public boolean onDamageMob() {
        return false;
    }

    @Override
    public boolean onUserDamaged() {
        return false;
    }

    @Override
    public void tickPassiveEffect() {

    }

    /**
     * Represents which inventory slot an artifact must be in
     * for a specific ArtifactAbility to activate.
     */
    public enum ActiveType {
        /** Main hand only. */
        MAIN_HAND,
        /** Can be active in both hands. */
        ANY_HAND,
        /** Only active when equipped as armor. */
        ARMOR,
        /** Active if held, or in hotbar. */
        HOTBAR,
        /** Active always, regardless of inventory slot. */
        INVENTORY
    }
}
