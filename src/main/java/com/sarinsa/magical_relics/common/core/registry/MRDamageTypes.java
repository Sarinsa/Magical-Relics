package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;

public class MRDamageTypes {

    public static final DeferredRegister<DamageType> DAMAGE_TYPES = DeferredRegister.create(Registries.DAMAGE_TYPE, MagicalRelics.MODID);


    public static final ResourceKey<DamageType> QUICKSAND = create("quicksand");
    public static final ResourceKey<DamageType> SPIKES = create("spikes");
    public static final ResourceKey<DamageType> SWUNG_SWORD = create("swung_sword");


    public static DamageSource of(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }

    private static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, MagicalRelics.resLoc(name));
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        register(context, QUICKSAND, new DamageType(msg("quicksand"), 0.0F));
        register(context, SPIKES, new DamageType(msg("spikes"), 0.0F));
        register(context, SWUNG_SWORD, new DamageType(msg("swung_sword"), 0.0F));
    }

    protected static void register(BootstapContext<DamageType> context, ResourceKey<DamageType> damageTypeKey, DamageType damageType) {
        context.register(damageTypeKey, damageType);
    }

    private static String msg(String name) {
        return MagicalRelics.MODID + "." + name;
    }
}
