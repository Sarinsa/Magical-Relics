package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MRParticles {
    
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES, MagicalRelics.MODID );
    
    
    public static final RegistryObject<SimpleParticleType> ORE_PING = registerSimple( "ore_ping", true );
    
    
    private static RegistryObject<SimpleParticleType> registerSimple( String name, boolean overrideLimiter ) {
        return PARTICLES.register( name, () -> new SimpleParticleType( overrideLimiter ) );
    }
}
