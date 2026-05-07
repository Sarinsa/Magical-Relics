package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.MarkedMobEffectInstance;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class WaterBreathingAbility extends BaseArtifactAbility<WaterBreathingAbility.WaterBreathingAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "water_breathing", "aerated" ),
            createPrefix( "water_breathing", "breathy" ),
            createPrefix( "water_breathing", "oxygenated" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "water_breathing", "breathing" ),
            createSuffix( "water_breathing", "fresh_air" ),
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.USER_DAMAGED, TriggerType.USE, TriggerType.HELD, TriggerType.USER_ATTACKING, TriggerType.CURIO_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.RING,
            ArtifactCategory.STAFF,
            ArtifactCategory.BELT,
            ArtifactCategory.DAGGER,
            ArtifactCategory.HELMET
    );
    
    
    public WaterBreathingAbility() { }
    
    
    public static class WaterBreathingAbilityConfig extends CooldownAbilityConfig {
        
        public WaterBreathing WATER_BREATHING;
        
        public WaterBreathingAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity,
                                            int cooldown,
                                            int useDuration, int passiveDuration, int attackDuration, int drownDuration ) {
            super( cfgManager, abilityId, rarity, cooldown );
            
            WATER_BREATHING = new WaterBreathing( this, useDuration, passiveDuration, attackDuration, drownDuration );
        }
        
        public static class WaterBreathing extends AbstractConfigCategory<WaterBreathingAbilityConfig> {
            
            public IntField useDuration;
            public IntField passiveDuration;
            public IntField attackDuration;
            public IntField drownDuration;
            
            public WaterBreathing( WaterBreathingAbilityConfig parent, int useDur, int passiveDur, int attackDur, int drownDur ) {
                super( parent, "water_breathing", "Options for the water breathing effect applied by this ability." );
                
                useDuration = SPEC.define( new IntField( "use_duration", useDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a use trigger." ) );
                passiveDuration = SPEC.define( new IntField( "passive_duration", passiveDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a passive trigger." ) );
                attackDuration = SPEC.define( new IntField( "attack_duration", attackDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has an attack trigger." ) );
                drownDuration = SPEC.define( new IntField( "drowning_duration", drownDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability is triggered by drowning." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new WaterBreathingAbilityConfig( cfgManager, abilityId, Rarity.RARE,
                1200, 1200, MarkedMobEffectInstance.TICK_THRESHOLD, 120, 50 );
    }
    
    @Override
    public void onDamageMob( ItemStack artifact, Player player, LivingEntity attackedMob ) {
        // noinspection resource
        if( !player.level().isClientSide )
            player.addEffect( new MobEffectInstance( MobEffects.WATER_BREATHING, getConfig().WATER_BREATHING.attackDuration.get() ) );
    }
    
    @Override
    public void onUserDamaged( Level level, Player player, DamageSource damageSource, ItemStack artifact ) {
        if( !level.isClientSide && damageSource == level.damageSources().drown() ) {
            player.addEffect( new MobEffectInstance( MobEffects.WATER_BREATHING, getConfig().WATER_BREATHING.drownDuration.get() ) );
        }
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        onHeld( level, player, artifact, slot );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            artifact.hurtAndBreak( 1, player, ( entity ) -> entity.broadcastBreakEvent( hand ) );
            
            if( !level.isClientSide )
                player.addEffect( new MobEffectInstance( MobEffects.WATER_BREATHING, getConfig().WATER_BREATHING.useDuration.get() ) );
            
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            return true;
        }
        return false;
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        if( !level.isClientSide )
            player.addEffect( new MobEffectInstance( MobEffects.WATER_BREATHING, getConfig().WATER_BREATHING.passiveDuration.get() ) );
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
        if( isCurio ) return random.nextInt( 3 ) == 0 ? TriggerType.CURIO_TICK : TriggerType.USER_DAMAGED;
        if( isArmor ) return random.nextInt( 3 ) == 0 ? TriggerType.ARMOR_TICK : TriggerType.USER_DAMAGED;
        
        return switch( random.nextInt( 3 ) ) {
            case 1 -> TriggerType.HELD;
            case 2 -> TriggerType.USER_ATTACKING;
            default -> TriggerType.USE;
        };
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
        if( type == null ) return null;
        
        return switch( type ) {
            case ARMOR_TICK, HELD -> descComponent( type );
            case USER_DAMAGED -> durationDescComponent( type, getConfig().WATER_BREATHING.drownDuration.get() );
            case USE -> durationDescComponent( type, getConfig().WATER_BREATHING.useDuration.get() );
            case USER_ATTACKING -> durationDescComponent( type, getConfig().WATER_BREATHING.attackDuration.get() );
            default -> null;
        };
    }
}
