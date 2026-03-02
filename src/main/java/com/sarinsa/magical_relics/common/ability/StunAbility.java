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
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StunAbility extends BaseArtifactAbility<StunAbility.StunAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "stun", "slowing" ),
            createPrefix( "stun", "immobilizing" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "stun", "trapping" ),
            createSuffix( "stun", "halting" ),
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.DROPPED,
            TriggerType.USER_ATTACKING
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.FIGURINE,
            ArtifactCategory.TRINKET,
            ArtifactCategory.AXE,
            ArtifactCategory.SWORD,
            ArtifactCategory.DAGGER
    );
    
    
    public StunAbility() { }
    
    
    public static class StunAbilityConfig extends CooldownAbilityConfig {
        
        public Stun STUN;
        
        public StunAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, int cooldown,
                                  int useDuration, int dropDuration, double radius ) {
            super( cfgManager, abilityId, cooldown );
            
            STUN = new Stun( this, useDuration, dropDuration, radius );
        }
        
        public static class Stun extends AbstractConfigCategory<StunAbilityConfig> {
            
            public IntField attackDuration;
            public IntField dropDuration;
            
            public DoubleField radius;
            
            public Stun( StunAbilityConfig parent, int useDur, int dropDur, double rad ) {
                super( parent, "stun", "Options for the slowness effect this ability inflicts on nearby mobs." );
                
                attackDuration = SPEC.define( new IntField( "attack_duration", useDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the slowness effect when this ability has an attack trigger." ) );
                dropDuration = SPEC.define( new IntField( "drop_duration", dropDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the slowness effect when this ability has a drop trigger." ) );
                
                radius = SPEC.define( new DoubleField( "radius", rad, DoubleField.Range.NON_NEGATIVE,
                        "The radius of the spherical area around the player in which mobs should be inflicted with slowness." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new StunAbilityConfig( cfgManager, abilityId, 500, 100, 120, 20.0 );
    }
    
    @Override
    public boolean onDropped( Level level, ItemEntity itemEntity, Player player ) {
        if( !ArtifactUtils.isAbilityOnCooldown( itemEntity.getItem(), this ) ) {
            final double radius = getConfig().STUN.radius.get();
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass( LivingEntity.class, player.getBoundingBox().inflate( radius, radius, radius ) );
            // Skip the player
            nearbyEntities.remove( player );
            
            for( LivingEntity entity : nearbyEntities ) {
                // Skip entities that are not within a spherical area of the radius.
                if( entity.distanceTo( player ) > radius )
                    continue;
                
                entity.addEffect( new MobEffectInstance( MobEffects.MOVEMENT_SLOWDOWN, getConfig().STUN.dropDuration.get(), 1 ) );
            }
            ArtifactUtils.setAbilityOnCooldown( itemEntity.getItem(), this );
        }
        return false;
    }
    
    @Override
    public void onDamageMob( ItemStack artifact, Player player, LivingEntity attackedMob ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            attackedMob.addEffect( new MobEffectInstance( MobEffects.MOVEMENT_SLOWDOWN, getConfig().STUN.attackDuration.get(), 1 ) );
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
        }
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
        if( isArmor ) return null;
        
        return random.nextBoolean() ? TriggerType.USER_ATTACKING : TriggerType.DROPPED;
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
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        return type == TriggerType.USER_ATTACKING
                ? Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.stun.description.user_attacking", getConfig().STUN.attackDuration.get() / 20 )
                : Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.stun.description.dropped", getConfig().STUN.dropDuration.get() / 20 );
    }
}
