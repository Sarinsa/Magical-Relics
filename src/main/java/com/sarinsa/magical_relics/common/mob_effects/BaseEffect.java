package com.sarinsa.magical_relics.common.mob_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * "Empty" mob effect implementation. Used for mob effects that are purely
 * markers and don't do anything on their own.
 */
public class BaseEffect extends MobEffect {
    
    public BaseEffect( MobEffectCategory category, int color ) {
        super( category, color );
    }
    
    @Override
    public void applyEffectTick( LivingEntity livingEntity, int amplifier ) { }
    
    @Override
    public void applyInstantenousEffect( @Nullable Entity sourceEntity, @Nullable Entity involvedEntity, LivingEntity affectedEntity, int amplifier, double potency ) { }
    
    @Override
    public boolean isDurationEffectTick( int duration, int amplifier ) {
        return false;
    }
}
