package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class GlowVisionAbility extends BaseArtifactAbility<GlowVisionAbility.GlowVisionAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "glow_vision", "revealing" ),
            createPrefix( "glow_vision", "glowing" ),
            createPrefix( "glow_vision", "seekers" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "glow_vision", "silhouettes" ),
            createSuffix( "glow_vision", "spotting" ),
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.TRINKET,
            ArtifactCategory.STAFF,
            ArtifactCategory.WAND,
            ArtifactCategory.DAGGER,
            ArtifactCategory.SWORD
    );
    
    
    public GlowVisionAbility() { }
    
    
    public static class GlowVisionAbilityConfig extends CooldownAbilityConfig {
        
        public GlowVision GLOW_VISION;
        
        public GlowVisionAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                        int cooldown, double radius, int effectDuration ) {
            super( cfgManager, abilityId, cooldown );
            
            GLOW_VISION = new GlowVision( this, radius, effectDuration );
        }
        
        public static class GlowVision extends AbstractConfigCategory<GlowVisionAbilityConfig> {
            
            public DoubleField radius;
            
            public IntField effectDuration;
            
            public GlowVision( GlowVisionAbilityConfig parent, double rad, int effctDuration ) {
                super( parent, "glow_vision", "Options for the glow effect this ability applies to mobs." );
                
                radius = SPEC.define( new DoubleField( "radius", rad, DoubleField.Range.NON_NEGATIVE,
                        "The radius of the spherical area around the player in which mobs should start glowing." ) );
                
                effectDuration = SPEC.define( new IntField( "effect_duration", effctDuration, IntField.Range.TOKEN_NEGATIVE,
                        "The duration of the glowing effect given to mobs.",
                        "Setting this to -1 effectively makes the mobs glow forever." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new GlowVisionAbilityConfig( cfgManager, abilityId, 400, 30.0, 180 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            final double radius = getConfig().GLOW_VISION.radius.get();
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass( LivingEntity.class, player.getBoundingBox().inflate( radius, radius, radius ) );
            
            if( !nearbyEntities.isEmpty() ) {
                // Skip the player.
                nearbyEntities.remove( player );
                
                for( LivingEntity livingEntity : nearbyEntities ) {
                    // Skip entities that are not within a spherical area of the radius.
                    if( livingEntity.distanceTo( player ) > radius )
                        continue;
                    
                    livingEntity.addEffect( new MobEffectInstance( MobEffects.GLOWING, getConfig().GLOW_VISION.effectDuration.get() ) );
                }
                level.playSound( null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0F, 0.9F + (level.random.nextFloat() / 3) );
                artifact.hurtAndBreak( 3, player, ( p ) -> p.broadcastBreakEvent( EquipmentSlot.MAINHAND ) );
            }
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            return true;
        }
        return false;
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
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        return Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.glow_vision.description" );
    }
}
