package com.sarinsa.magical_relics.common.core.registry;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.loot.glm.AddArtifactModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRGlobalLootMods {
    
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODS = DeferredRegister.create( ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MagicalRelics.MODID );
    
    
    public static final RegistryObject<Codec<AddArtifactModifier>> ADD_ARTIFACT = register( "add_artifact", AddArtifactModifier.CODEC );
    
    
    private static <T extends IGlobalLootModifier> RegistryObject<Codec<T>> register( String name, Supplier<Codec<T>> codecSupplier ) {
        return GLOBAL_LOOT_MODS.register( name, codecSupplier );
    }
}
