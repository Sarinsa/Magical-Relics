package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.util.ArtifactSet;
import com.sarinsa.magical_relics.common.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class MRItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicalRelics.MODID);

    public static final Map<ResourceKey<CreativeModeTab>, List<RegistryObject<? extends Item>>> TAB_ITEMS = new HashMap<>();

    public static final List<ArtifactSet<List<RegistryObject<Item>>>> ALL_ARTIFACTS = new ArrayList<>();
    public static final Map<ArtifactCategory, List<RegistryObject<? extends Item>>> ARTIFACTS_BY_CATEGORY = new HashMap<>();

    static {
        for (ArtifactCategory category : ArtifactCategory.values()) {
            ARTIFACTS_BY_CATEGORY.put(category, new ArrayList<>());
        }
    }

    public static final RegistryObject<Item> RANDOM_ARTIFACT = register("random_artifact", () -> new RandomArtifactItem(new Item.Properties().stacksTo(1)), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> QUICKSAND_BUCKET = register("quicksand_bucket", () -> new BlockBucketItem(MRBlocks.QUICKSAND, new Item.Properties().stacksTo(1)), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> THICK_TRIPWIRE = register("thick_tripwire", () -> new BlockItem(MRBlocks.THICK_TRIPWIRE.get(), new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> RAW_MANAESSENCE = register("raw_manaessence", () -> new Item(new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> WOOD_MANAESSENCE = register("wood_manaessence", () -> new Item(new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> LEATHER_MANAESSENCE = register("leather_manaessence", () -> new Item(new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> STONE_MANAESSENCE = register("stone_manaessence", () -> new Item(new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> IRON_MANAESSENCE = register("iron_manaessence", () -> new Item(new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> GOLD_MANAESSENCE = register("gold_manaessence", () -> new Item(new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
    public static final RegistryObject<Item> DIAMOND_MANAESSENCE = register("diamond_manaessence", () -> new Item(new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());

    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> LEATHER_ARTIFACT_ARMOR = artifactArmorSet("leather", ArtifactArmorMaterials.LEATHER);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> IRON_ARTIFACT_ARMOR = artifactArmorSet("iron", ArtifactArmorMaterials.IRON);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> GOLD_ARTIFACT_ARMOR = artifactArmorSet("gold", ArtifactArmorMaterials.GOLD);
    public static final Map<EquipmentSlot, RegistryObject<ArmorItem>> DIAMOND_ARTIFACT_ARMOR = artifactArmorSet("diamond", ArtifactArmorMaterials.DIAMOND);

    public static final ArtifactSet<List<RegistryObject<Item>>> AMULETS = artifactSet(ArtifactCategory.AMULET);
    public static final ArtifactSet<List<RegistryObject<Item>>> BELTS = artifactSet(ArtifactCategory.BELT);
    public static final ArtifactSet<List<RegistryObject<Item>>> DAGGERS = artifactSet(ArtifactCategory.DAGGER);
    public static final ArtifactSet<List<RegistryObject<Item>>> FIGURINES = artifactSet(ArtifactCategory.FIGURINE);
    public static final ArtifactSet<List<RegistryObject<Item>>> RINGS = artifactSet(ArtifactCategory.RING);
    public static final ArtifactSet<List<RegistryObject<Item>>> STAFFS = artifactSet(ArtifactCategory.STAFF);
    public static final ArtifactSet<List<RegistryObject<Item>>> SWORDS = artifactSet(ArtifactCategory.SWORD);
    public static final ArtifactSet<List<RegistryObject<Item>>> TRINKETS = artifactSet(ArtifactCategory.TRINKET);
    public static final ArtifactSet<List<RegistryObject<Item>>> WANDS = artifactSet(ArtifactCategory.WAND);
    public static final ArtifactSet<List<RegistryObject<Item>>> AXES = axeSet();


    private static ArtifactSet<List<RegistryObject<Item>>> artifactSet(ArtifactCategory category) {
        ArtifactSet<List<RegistryObject<Item>>> artifactSet = new ArtifactSet<>(category, new ArrayList<>());
        artifactSet.dataStructure().add(ITEMS.register("wood_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.WOOD, category)));
        artifactSet.dataStructure().add(ITEMS.register("stone_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.STONE, category)));
        artifactSet.dataStructure().add(ITEMS.register("iron_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.IRON, category)));
        artifactSet.dataStructure().add(ITEMS.register("gold_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.GOLD, category)));
        artifactSet.dataStructure().add(ITEMS.register("diamond_" + category.getName() + "_artifact", () -> new ArtifactItem(ArtifactItemTiers.DIAMOND, category)));
        ALL_ARTIFACTS.add(artifactSet);
        ARTIFACTS_BY_CATEGORY.get(category).addAll(artifactSet.dataStructure());
        return artifactSet;
    }

    private static ArtifactSet<List<RegistryObject<Item>>> axeSet() {
        ArtifactCategory category = ArtifactCategory.AXE;

        ArtifactSet<List<RegistryObject<Item>>> artifactSet = new ArtifactSet<>(category, new ArrayList<>());
        artifactSet.dataStructure().add(ITEMS.register("wood_" + category.getName() + "_artifact", () -> new ArtifactAxeItem(ArtifactItemTiers.WOOD, 6.0F, -3.2F)));
        artifactSet.dataStructure().add(ITEMS.register("stone_" + category.getName() + "_artifact", () -> new ArtifactAxeItem(ArtifactItemTiers.STONE, 7.0F, -3.2F)));
        artifactSet.dataStructure().add(ITEMS.register("iron_" + category.getName() + "_artifact", () -> new ArtifactAxeItem(ArtifactItemTiers.IRON, 6.0F, -3.1F)));
        artifactSet.dataStructure().add(ITEMS.register("gold_" + category.getName() + "_artifact", () -> new ArtifactAxeItem(ArtifactItemTiers.GOLD, 6.0F, -3.0F)));
        artifactSet.dataStructure().add(ITEMS.register("diamond_" + category.getName() + "_artifact", () -> new ArtifactAxeItem(ArtifactItemTiers.DIAMOND, 5.0F, -3.0F)));
        ALL_ARTIFACTS.add(artifactSet);
        ARTIFACTS_BY_CATEGORY.get(category).addAll(artifactSet.dataStructure());
        return artifactSet;
    }

    private static Map<EquipmentSlot, RegistryObject<ArmorItem>> artifactArmorSet(String name, ArmorMaterial material) {
        Map<EquipmentSlot, RegistryObject<ArmorItem>> armorSet = new HashMap<>();
        armorSet.put(EquipmentSlot.FEET, register(name + "_boots_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.BOOTS, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1))));
        armorSet.put(EquipmentSlot.LEGS, register(name + "_leggings_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.LEGGINGS, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1))));
        armorSet.put(EquipmentSlot.CHEST, register(name + "_chestplate_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.CHESTPLATE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1))));
        armorSet.put(EquipmentSlot.HEAD, register(name + "_helmet_artifact", () -> new ArtifactArmorItem(material, ArtifactCategory.HELMET, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1))));
        ARTIFACTS_BY_CATEGORY.get(ArtifactCategory.BOOTS).add(armorSet.get(EquipmentSlot.FEET));
        ARTIFACTS_BY_CATEGORY.get(ArtifactCategory.LEGGINGS).add(armorSet.get(EquipmentSlot.LEGS));
        ARTIFACTS_BY_CATEGORY.get(ArtifactCategory.CHESTPLATE).add(armorSet.get(EquipmentSlot.CHEST));
        ARTIFACTS_BY_CATEGORY.get(ArtifactCategory.HELMET).add(armorSet.get(EquipmentSlot.HEAD));
        return armorSet;
    }

    @SafeVarargs
    protected static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item, ResourceKey<CreativeModeTab>... creativeTabs) {
        RegistryObject<T> regObj = ITEMS.register(name, item);
        queueForCreativeTabs(regObj, creativeTabs);
        return regObj;
    }

    @SafeVarargs
    protected static void queueForCreativeTabs(RegistryObject<? extends Item> item, ResourceKey<CreativeModeTab>... creativeTabs) {
        for (ResourceKey<CreativeModeTab> tab : creativeTabs) {
            if (!TAB_ITEMS.containsKey(tab)) {
                List<RegistryObject<? extends Item>> list = new ArrayList<>();
                list.add(item);
                TAB_ITEMS.put(tab, list);
            } else {
                TAB_ITEMS.get(tab).add(item);
            }
        }
    }

    /**
     * Called when creative tabs gets populated with items.
     */
    public static void onCreativeTabPopulate(BuildCreativeModeTabContentsEvent event) {
        if (TAB_ITEMS.containsKey(event.getTabKey())) {
            List<RegistryObject<? extends Item>> items = TAB_ITEMS.get(event.getTabKey());
            items.forEach((regObj) -> event.accept(regObj.get()));
        }
    }
}
