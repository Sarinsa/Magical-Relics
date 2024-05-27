package com.sarinsa.magical_relics.common.item;

import com.google.common.base.Suppliers;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ArtifactArmorMaterials implements ArmorMaterial {

    LEATHER("artifact_leather", 5, new int[]{1, 2, 3, 1}, 10, () -> SoundEvents.ARMOR_EQUIP_LEATHER, Suppliers.memoize(() -> Ingredient.of(MRItems.LEATHER_MANAESSENCE.get())), 0.0F, 0.0F),
    IRON("artifact_iron", 13, new int[]{2, 5, 6, 2}, 7, () -> SoundEvents.ARMOR_EQUIP_IRON, Suppliers.memoize(() -> Ingredient.of(MRItems.IRON_MANAESSENCE.get())), 0.0F, 0.0F),
    GOLD("artifact_gold", 7, new int[]{1, 3, 5, 2}, 20, () -> SoundEvents.ARMOR_EQUIP_GOLD, Suppliers.memoize(() -> Ingredient.of(MRItems.GOLD_MANAESSENCE.get())), 0.0F, 0.0F),
    DIAMOND("artifact_diamond", 30, new int[]{3, 6, 8, 3}, 8, () -> SoundEvents.ARMOR_EQUIP_DIAMOND, Suppliers.memoize(() -> Ingredient.of(MRItems.DIAMOND_MANAESSENCE.get())), 2.0F, 0.0F);



    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};

    private final int durabilityMult;
    private final int[] defense;
    private final int enchantmentValue;
    private final Supplier<SoundEvent> equipSound;
    private final Supplier<Ingredient> repairIngredient;
    private final String name;
    private final float toughness;
    private final float knockbackRes;


    ArtifactArmorMaterials(String name, int durabilityMult, int[] defense, int enchantmentValue, Supplier<SoundEvent> equipSound,
                           Supplier<Ingredient> repairIngredient, float toughness, float knockbackRes) {

        this.name = MagicalRelics.MODID + ":" + name;
        this.durabilityMult = durabilityMult;
        this.defense = defense;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.repairIngredient = repairIngredient;
        this.toughness = toughness;
        this.knockbackRes = knockbackRes;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case LEGGINGS -> durabilityMult * HEALTH_PER_SLOT[1];
            case CHESTPLATE -> durabilityMult * HEALTH_PER_SLOT[2];
            case HELMET -> durabilityMult * HEALTH_PER_SLOT[3];
            case BOOTS -> durabilityMult * HEALTH_PER_SLOT[0];
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case LEGGINGS -> defense[1];
            case CHESTPLATE -> defense[2];
            case HELMET -> defense[3];
            case BOOTS -> defense[0];
        };
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound.get();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackRes;
    }
}
