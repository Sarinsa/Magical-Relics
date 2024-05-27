package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.util.CreativeTabRegObj;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicalRelics.MODID);


    public static final CreativeTabRegObj MOD_TAB = register("all", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(MRBlocks.DISPLAY_PEDESTAL.get()))
            .title(Component.translatable("itemGroup.magical_relics.all"))
            .build());


    private static CreativeTabRegObj register(String name, Supplier<CreativeModeTab> supplier) {
        RegistryObject<CreativeModeTab> regObj = CREATIVE_TABS.register(name, supplier);
        return new CreativeTabRegObj(regObj, ResourceKey.create(Registries.CREATIVE_MODE_TAB, MagicalRelics.resLoc(name)));
    }
}
