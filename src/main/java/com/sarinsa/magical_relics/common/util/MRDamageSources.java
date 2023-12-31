package com.sarinsa.magical_relics.common.util;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.world.damagesource.DamageSource;

public class MRDamageSources {

    public static final DamageSource SPIKES = new DamageSource(name("spikes")).bypassArmor().bypassEnchantments().bypassMagic();


    private static String name(String name) {
        return MagicalRelics.MODID + "." + name;
    }

    private MRDamageSources() {}

    public static void init() {}
}
