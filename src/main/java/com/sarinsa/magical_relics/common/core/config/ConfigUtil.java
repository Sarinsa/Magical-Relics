package com.sarinsa.magical_relics.common.core.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.function.Predicate;

public class ConfigUtil {

    public static final Predicate<Object> IS_RESOURCE_LOCATION = (object) -> {
        if (object instanceof String s) {
            ResourceLocation rl = ResourceLocation.tryParse(s);
            return rl != null;
        }
        return false;
    };


    public static <T> void addToList(List<String> list, IForgeRegistry<T> registry, T object) {
        if (registry.containsValue(object)) {
            String id = registry.getKey(object).toString();

            if (!list.contains(id))
                list.add(id);
        }
        else {
            throw new IllegalArgumentException("Attempted to add ID of non-registered object to String list. Problematic object: " + object.toString());
        }
    }
}
