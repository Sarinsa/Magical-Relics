package com.sarinsa.magical_relics.common.item;

import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtifactArmorItem extends ArmorItem {

    public ArtifactArmorItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties) {
        super(armorMaterial, equipmentSlot, properties.rarity(Rarity.UNCOMMON));
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
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (book.getEnchantmentLevel(Enchantments.MENDING) > 0)
            return false;

        return super.isBookEnchantable(stack, book);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        Component alteredName = ArtifactUtils.getItemName(itemStack);

        if (alteredName == null)
            return super.getName(itemStack);

        return alteredName;
    }
}
