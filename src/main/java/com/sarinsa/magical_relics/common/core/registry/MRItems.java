package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.util.ArtifactSet;
import com.sarinsa.magical_relics.common.item.*;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class MRItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicalRelics.MODID);

    public static final List<ArtifactSet<List<RegistryObject<Item>>>> ALL_ARTIFACTS = new ArrayList<>();

    public static class MRCreativeTab extends CreativeModeTab {

        public static final CreativeModeTab MOD_TAB = new MRCreativeTab("all", () -> new ItemStack(MRBlocks.DISPLAY_PEDESTAL.get()));


        private MRCreativeTab(String label, Supplier<ItemStack> itemIcon) {
            super(MagicalRelics.MODID + "_" + label);
            this.itemIcon = itemIcon;
        }
        private final Supplier<ItemStack> itemIcon;

        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return itemIcon.get();
        }
    }


    public static final RegistryObject<Item> RANDOM_ARTIFACT = register("random_artifact", () -> new RandomArtifactItem(new Item.Properties().stacksTo(1).tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> QUICKSAND_BUCKET = register("quicksand_bucket", () -> new BlockBucketItem(MRBlocks.QUICKSAND, new Item.Properties().stacksTo(1).tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> THICK_TRIPWIRE = register("thick_tripwire", () -> new BlockItem(MRBlocks.THICK_TRIPWIRE.get(), new Item.Properties().tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> RAW_MANAESSENCE = register("raw_manaessence", () -> new Item(new Item.Properties().tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> WOOD_MANAESSENCE = register("wood_manaessence", () -> new Item(new Item.Properties().tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> LEATHER_MANAESSENCE = register("leather_manaessence", () -> new Item(new Item.Properties().tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> STONE_MANAESSENCE = register("stone_manaessence", () -> new Item(new Item.Properties().tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> IRON_MANAESSENCE = register("iron_manaessence", () -> new Item(new Item.Properties().tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> GOLD_MANAESSENCE = register("gold_manaessence", () -> new Item(new Item.Properties().tab(MRCreativeTab.MOD_TAB)));
    public static final RegistryObject<Item> DIAMOND_MANAESSENCE = register("diamond_manaessence", () -> new Item(new Item.Properties().tab(MRCreativeTab.MOD_TAB)));

    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> LEATHER_ARTIFACT_ARMOR = artifactArmorSet("leather", ArtifactArmorMaterials.LEATHER);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> IRON_ARTIFACT_ARMOR = artifactArmorSet("iron", ArtifactArmorMaterials.IRON);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> GOLD_ARTIFACT_ARMOR = artifactArmorSet("gold", ArtifactArmorMaterials.GOLD);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> DIAMOND_ARTIFACT_ARMOR = artifactArmorSet("diamond", ArtifactArmorMaterials.DIAMOND);

    public static final ArtifactSet<List<RegistryObject<Item>>> AMULETS = artifactSet(ArtifactCategory.AMULET, 7);
    public static final ArtifactSet<List<RegistryObject<Item>>> BELTS = artifactSet(ArtifactCategory.BELT, 6);
    public static final ArtifactSet<List<RegistryObject<Item>>> DAGGERS = artifactSet(ArtifactCategory.DAGGER, 6);
    public static final ArtifactSet<List<RegistryObject<Item>>> FIGURINES = artifactSet(ArtifactCategory.FIGURINE, 5);
    public static final ArtifactSet<List<RegistryObject<Item>>> RINGS = artifactSet(ArtifactCategory.RING, 8);
    public static final ArtifactSet<List<RegistryObject<Item>>> STAFFS = artifactSet(ArtifactCategory.STAFF, 6);
    public static final ArtifactSet<List<RegistryObject<Item>>> SWORDS = artifactSet(ArtifactCategory.SWORD, 7);
    public static final ArtifactSet<List<RegistryObject<Item>>> TRINKETS = artifactSet(ArtifactCategory.TRINKET, 10);
    public static final ArtifactSet<List<RegistryObject<Item>>> WANDS = artifactSet(ArtifactCategory.WAND, 7);


    private static ArtifactSet<List<RegistryObject<Item>>> artifactSet(ArtifactCategory category, int variations) {
        ArtifactSet<List<RegistryObject<Item>>> artifactSet = new ArtifactSet<>(category, variations, new ArrayList<>());
        artifactSet.dataStructure().add(ITEMS.register("wood_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.WOOD, category)));
        artifactSet.dataStructure().add(ITEMS.register("stone_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.STONE, category)));
        artifactSet.dataStructure().add(ITEMS.register("iron_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.IRON, category)));
        artifactSet.dataStructure().add(ITEMS.register("gold_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.GOLD, category)));
        artifactSet.dataStructure().add(ITEMS.register("diamond_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.DIAMOND, category)));
        ALL_ARTIFACTS.add(artifactSet);
        return artifactSet;
    }

    private static Map<EquipmentSlot, RegistryObject<ArmorItem>> artifactArmorSet(String name, ArmorMaterial material) {
        Map<EquipmentSlot, RegistryObject<ArmorItem>> armorSet = new HashMap<>();
        armorSet.put(EquipmentSlot.FEET, register(name + "_boots_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.BOOTS, EquipmentSlot.FEET, new Item.Properties().stacksTo(1))));
        armorSet.put(EquipmentSlot.LEGS, register(name + "_leggings_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.LEGGINGS, EquipmentSlot.LEGS, new Item.Properties().stacksTo(1))));
        armorSet.put(EquipmentSlot.CHEST, register(name + "_chestplate_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.CHESTPLATE, EquipmentSlot.CHEST, new Item.Properties().stacksTo(1))));
        armorSet.put(EquipmentSlot.HEAD, register(name + "_helmet_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.HELMET, EquipmentSlot.HEAD, new Item.Properties().stacksTo(1))));
        return armorSet;
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
