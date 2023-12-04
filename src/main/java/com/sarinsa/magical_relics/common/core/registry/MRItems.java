package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicalRelics.MODID);



    public static final RegistryObject<Item> RAW_MANAESSENCE = register("raw_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> WOOD_MANAESSENCE = register("wood_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> LEATHER_MANAESSENCE = register("leather_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> STONE_MANAESSENCE = register("stone_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> IRON_MANAESSENCE = register("iron_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> GOLD_MANAESSENCE = register("gold_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> DIAMOND_MANAESSENCE = register("diamond_manaessence", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));



    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
