package com.sarinsa.magical_relics.common.item;

import com.google.common.collect.Multimap;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Attr;

import java.util.List;

public class ArtifactItem extends TieredItem {

    public ArtifactItem(Tier tier) {
        super(tier, new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, components, flag);

        List<Component> descriptions = ArtifactUtils.getDescriptions(itemStack);
        components.addAll(descriptions);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.MENDING)
            return false;

        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> artifactModifiers = ArtifactUtils.getAttributeMods(stack);

        if (artifactModifiers != null && slot == EquipmentSlot.MAINHAND)
            return artifactModifiers;

        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (book.getEnchantmentLevel(Enchantments.MENDING) > 0)
            return false;

        return super.isBookEnchantable(stack, book);
    }

    /**
     * Overriding this so the equip/use animation does not constantly play
     * when dealing with artifact ability cooldown ticks changing NBT.
     */
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public Component getName(ItemStack itemStack) {
        Component alteredName = ArtifactUtils.getItemDisplayName(itemStack);

        if (alteredName == null)
            return super.getName(itemStack);

        return alteredName;
    }
}
