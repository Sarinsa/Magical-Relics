package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.entity.VolatileFireball;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class FireballAbility extends BaseArtifactAbility<FireballAbility.FireballAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "fireball", "flaming" ),
            createPrefix( "fireball", "burning" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "fireball", "fire" ),
            createSuffix( "fireball", "heat" ),
            createSuffix( "fireball", "incineration" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.RING,
            ArtifactCategory.WAND,
            ArtifactCategory.STAFF,
            ArtifactCategory.DAGGER,
            ArtifactCategory.AXE
    );
    
    
    public FireballAbility() { }
    
    
    public static class FireballAbilityConfig extends CooldownAbilityConfig {
        
        public final Fireball FIREBALL;
        
        public FireballAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                      int cooldown, int explosionPower ) {
            super( cfgManager, abilityId, cooldown );
            
            FIREBALL = new Fireball( this, explosionPower );
        }
        
        public static class Fireball extends AbstractConfigCategory<FireballAbilityConfig> {
            
            public final IntField explosionPower;
            
            public Fireball( FireballAbilityConfig parent, int explosionPwer ) {
                super( parent, "fireball", "Options for the fireball summoned by this ability." );
                
                explosionPower = SPEC.define( new IntField( "explosion_power", explosionPwer, IntField.Range.NON_NEGATIVE,
                        "The explosion power of the fireballs summoned by this ability.",
                        "Be a bit careful with larger numbers, since the fireballs explode automatically after having traveled a good distance." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new FireballAbilityConfig( cfgManager, abilityId, 20, 1 );
    }
    
    @Override
    public InteractionResult onUse( Level level, @Nullable LivingEntity abilityUser, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( abilityUser != null && !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            if( !level.isClientSide ) {
                shootFireball( level, abilityUser );
                artifact.hurtAndBreak( 1, abilityUser, ( p ) -> p.broadcastBreakEvent( hand ) );
                
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
            }
            return InteractionResult.sidedSuccess( level.isClientSide );
        }
        return InteractionResult.PASS;
    }
    
    private void shootFireball( Level level, LivingEntity abilityUser ) {
        final Vec3 viewVec = abilityUser.getViewVector( 1.0F );
        final VolatileFireball fireball = new VolatileFireball( level, abilityUser, 0.0D, 0.0D, 0.0D, getConfig().FIREBALL.explosionPower.get() );
        
        fireball.setPos( abilityUser.getX() + viewVec.x, abilityUser.getY( 0.5D ) + 0.25D, fireball.getZ() + viewVec.z );
        fireball.shootFromRotation( abilityUser, abilityUser.getXRot(), abilityUser.getYRot(), 1.5F, 1.5F, 1.5F );
        level.addFreshEntity( fireball );
        
        level.playSound( null, abilityUser.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F );
    }
    
    @Override
    public String[] getPrefixes() {
        return PREFIXES;
    }
    
    @Override
    public String[] getSuffixes() {
        return SUFFIXES;
    }
    
    @Override
    @Nullable
    public TriggerType getRandomTrigger( ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio ) {
        return isArmor ? null : TriggerType.USE;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleCategories() {
        return TYPES;
    }
}
