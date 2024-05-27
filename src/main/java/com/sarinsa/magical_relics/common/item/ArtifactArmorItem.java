package com.sarinsa.magical_relics.common.item;

import com.sarinsa.magical_relics.common.ability.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtifactArmorItem extends ArmorItem implements ItemArtifact {

    private final ArtifactCategory type;

    public ArtifactArmorItem(ArmorMaterial armorMaterial, ArtifactCategory type, ArmorItem.Type armorType, Properties properties) {
        super(armorMaterial, armorType, properties.rarity(ArtifactUtils.MAGICAL));
        this.type = type;
    }

    @Override
    public ArtifactCategory getCategory() {
        return type;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, components, flag);

        ArtifactUtils.addDescriptionsToTooltip(itemStack, level, components, flag);
    }


    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        BaseArtifactAbility ability = ArtifactUtils.getAbilityWithTrigger(TriggerType.ARMOR_TICK, stack);

        if (ability != null)
            ability.onArmorTick(stack, level, player);
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
        Component alteredName = ArtifactUtils.getItemDisplayName(itemStack);

        if (alteredName == null)
            return super.getName(itemStack);

        return alteredName;
    }
}
