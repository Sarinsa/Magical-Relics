package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.util.ArtifactSet;
import com.sarinsa.magical_relics.common.item.ArtifactArmorItem;
import com.sarinsa.magical_relics.common.item.ArtifactArmorMaterials;
import com.sarinsa.magical_relics.common.item.ArtifactItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class MRItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicalRelics.MODID);

    public static final List<ArtifactSet<List<RegistryObject<Item>>>> ALL_ARTIFACTS = new ArrayList<>();


    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> LEATHER_ARTIFACT_ARMOR = artifactArmorSet("leather", ArtifactArmorMaterials.LEATHER);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> IRON_ARTIFACT_ARMOR = artifactArmorSet("iron", ArtifactArmorMaterials.IRON);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> GOLD_ARTIFACT_ARMOR = artifactArmorSet("gold", ArtifactArmorMaterials.GOLD);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> DIAMOND_ARTIFACT_ARMOR = artifactArmorSet("diamond", ArtifactArmorMaterials.DIAMOND);

    public static final ArtifactSet<List<RegistryObject<Item>>> AMULETS = artifactSet("amulet", 7);
    public static final ArtifactSet<List<RegistryObject<Item>>> BELTS = artifactSet("belt", 6);
    public static final ArtifactSet<List<RegistryObject<Item>>> DAGGERS = artifactSet("dagger", 6);
    public static final ArtifactSet<List<RegistryObject<Item>>> FIGURINES = artifactSet("figurine", 5);
    public static final ArtifactSet<List<RegistryObject<Item>>> RINGS = artifactSet("ring", 8);
    public static final ArtifactSet<List<RegistryObject<Item>>> STAFFS = artifactSet("staff", 6);
    public static final ArtifactSet<List<RegistryObject<Item>>> SWORDS = artifactSet("sword", 7);
    public static final ArtifactSet<List<RegistryObject<Item>>> TRINKETS = artifactSet("trinket", 10);
    public static final ArtifactSet<List<RegistryObject<Item>>> WANDS = artifactSet("wand", 7);

    public static final RegistryObject<Item> RAW_MANAESSENCE = register("raw_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> WOOD_MANAESSENCE = register("wood_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> LEATHER_MANAESSENCE = register("leather_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> STONE_MANAESSENCE = register("stone_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> IRON_MANAESSENCE = register("iron_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> GOLD_MANAESSENCE = register("gold_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> DIAMOND_MANAESSENCE = register("diamond_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));




    private static ArtifactSet<List<RegistryObject<Item>>> artifactSet(String name, int variations) {
        ArtifactSet<List<RegistryObject<Item>>> artifactSet = new ArtifactSet<>(name, variations, new ArrayList<>());

        artifactSet.getDataStructure().add(ITEMS.register("wood_" + name + "_artifact", () -> new ArtifactItem(Tiers.WOOD)));
        artifactSet.getDataStructure().add(ITEMS.register("stone_" + name + "_artifact", () -> new ArtifactItem(Tiers.STONE)));
        artifactSet.getDataStructure().add(ITEMS.register("iron_" + name + "_artifact", () -> new ArtifactItem(Tiers.IRON)));
        artifactSet.getDataStructure().add(ITEMS.register("gold_" + name + "_artifact", () -> new ArtifactItem(Tiers.GOLD)));
        artifactSet.getDataStructure().add(ITEMS.register("diamond_" + name + "_artifact", () -> new ArtifactItem(Tiers.DIAMOND)));

        ALL_ARTIFACTS.add(artifactSet);
        return artifactSet;
    }

    private static Map<EquipmentSlot, RegistryObject<ArmorItem>> artifactArmorSet(String name, ArmorMaterial material) {
        Map<EquipmentSlot, RegistryObject<ArmorItem>> armorSet = new HashMap<>();
        armorSet.put(EquipmentSlot.FEET, register(name + "_boots_artifact", () -> new ArtifactArmorItem(material, EquipmentSlot.FEET, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC))));
        armorSet.put(EquipmentSlot.LEGS, register(name + "_leggings_artifact", () -> new ArtifactArmorItem(material, EquipmentSlot.LEGS, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC))));
        armorSet.put(EquipmentSlot.CHEST, register(name + "_chestplate_artifact", () -> new ArtifactArmorItem(material, EquipmentSlot.CHEST, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC))));
        armorSet.put(EquipmentSlot.HEAD, register(name + "_helmet_artifact", () -> new ArtifactArmorItem(material, EquipmentSlot.HEAD, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC))));
        return armorSet;
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
