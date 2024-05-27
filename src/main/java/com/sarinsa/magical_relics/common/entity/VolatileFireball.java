package com.sarinsa.magical_relics.common.entity;

import com.sarinsa.magical_relics.common.core.registry.MREntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

public class VolatileFireball extends LargeFireball {

    int timeUntilDetonation = 60;


    public VolatileFireball(EntityType<? extends LargeFireball> entityType, Level level) {
        super(entityType, level);
    }

    public VolatileFireball(Level level, LivingEntity caster, double xPower, double yPower, double zPower, int explosionPower) {
        super(level, caster, xPower, yPower, zPower, explosionPower);
    }

    @Override
    public EntityType<?> getType() {
        return MREntities.VOLATILE_FIREBALL.get();
    }

    @Override
    public void tick() {
        super.tick();

        if (--timeUntilDetonation <= 0) {
            if (!level().isClientSide) {
                boolean allowGriefing = ForgeEventFactory.getMobGriefingEvent(level(), getOwner());
                level().explode(null, getX(), getY(), getZ(), explosionPower, allowGriefing, allowGriefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
                discard();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (isInvulnerableTo(damageSource)) {
            return false;
        }
        else {
            if (!level().isClientSide) {
                boolean allowGriefing = ForgeEventFactory.getMobGriefingEvent(level(), getOwner());
                discard();
                level().explode(null, getX(), getY(), getZ(), explosionPower, allowGriefing, allowGriefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
            }
            return true;
        }
    }
}
