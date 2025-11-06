package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.mob_effects.CloudyVisionEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MRMobEffects {
    
    public static DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create( ForgeRegistries.MOB_EFFECTS, MagicalRelics.MODID );
    
    
    public static final RegistryObject<MobEffect> CLOUDY_VISION = MOB_EFFECTS.register( "cloudy_vision", CloudyVisionEffect::new );
}
