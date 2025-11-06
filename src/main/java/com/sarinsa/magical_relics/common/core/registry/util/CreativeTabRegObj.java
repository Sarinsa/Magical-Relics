package com.sarinsa.magical_relics.common.core.registry.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;

public record CreativeTabRegObj(RegistryObject<CreativeModeTab> regObj, ResourceKey<CreativeModeTab> key) {
    
    public CreativeModeTab getTab() {
        return regObj.get();
    }
}
