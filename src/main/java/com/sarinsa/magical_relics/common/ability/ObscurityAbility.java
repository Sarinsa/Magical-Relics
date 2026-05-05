package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.core.registry.MRMobEffects;
import com.sarinsa.magical_relics.common.event.ServerEventListener;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class ObscurityAbility extends BaseArtifactAbility<ObscurityAbility.ObscurityAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "obscurity", "obscure" ),
            createPrefix( "obscurity", "hidden" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "obscurity", "obscurity" ),
            createSuffix( "obscurity", "cloaking" ),
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USER_DAMAGED,
            TriggerType.USER_ATTACKING,
            TriggerType.USE
    );
    
    
    public ObscurityAbility() { }
    
    
    public static class ObscurityAbilityConfig extends CooldownAbilityConfig {
        
        public Obscurity OBSCURITY;
        
        public ObscurityAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity,
                                       int cooldown,
                                       int invisUseDuration, int invisDamagedDuration, int invisAttackDuration,
                                       int cloudyUseDuration, int cloudyDamagedDuration, int cloudyAttackDuration,
                                       double resetAggroRange ) {
            super( cfgManager, abilityId, rarity, cooldown );
            
            OBSCURITY = new Obscurity( this, invisUseDuration, invisDamagedDuration, invisAttackDuration,
                    cloudyUseDuration, cloudyDamagedDuration, cloudyAttackDuration,
                    resetAggroRange );
        }
        
        public static class Obscurity extends AbstractConfigCategory<ObscurityAbilityConfig> {
            
            public IntField invisUseDuration;
            public IntField invisDamagedDuration;
            public IntField invisAttackDuration;
            
            public IntField cloudyUseDuration;
            public IntField cloudyDamagedDuration;
            public IntField cloudyAttackDuration;
            
            public DoubleField resetAggroRange;
            
            public Obscurity( ObscurityAbilityConfig parent, int invisUseDur, int invisDamagedDur, int invisAttackDur,
                              int cloudyUseDur, int cloudyDamagedDur, int cloudyAttackDur,
                              double resetAggroRng ) {
                super( parent, "obscurity", "Options for the potion effects applied by this ability." );
                
                invisUseDuration = SPEC.define( new IntField( "invisibility_use_duration", invisUseDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the invisibility potion effect when this ability has a use trigger." ) );
                invisDamagedDuration = SPEC.define( new IntField( "invisibility_damaged_duration", invisDamagedDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the invisibility potion effect when this ability has a hurt trigger." ) );
                invisAttackDuration = SPEC.define( new IntField( "invisibility_attack_duration", invisAttackDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the invisibility potion effect when this ability has an attack trigger." ) );
                
                SPEC.newLine();
                
                cloudyUseDuration = SPEC.define( new IntField( "cloudy_vision_use_duration", cloudyUseDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the cloudy vision potion effect when this ability has a use trigger." ) );
                cloudyDamagedDuration = SPEC.define( new IntField( "cloudy_vision_damaged_duration", cloudyDamagedDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the cloudy vision potion effect when this ability has a hurt trigger." ) );
                cloudyAttackDuration = SPEC.define( new IntField( "cloudy_vision_attack_duration", cloudyAttackDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the cloudy vision potion effect when this ability has an attack trigger." ) );
                
                SPEC.newLine();
                
                resetAggroRange = SPEC.define( new DoubleField( "reset_aggro_range", resetAggroRng, DoubleField.Range.NON_NEGATIVE,
                        "If greater than 0.0, pathfinder mobs (e.g. Creepers and Zombies) withing this range will lose aggro on the player." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new ObscurityAbilityConfig( cfgManager, abilityId, Rarity.RARE, 610,
                810, 610, 610,
                810, 610, 610,
                30.0 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( hand ) );
            // noinspection resource
            if( !player.level().isClientSide ) {
                player.addEffect( new MobEffectInstance( MobEffects.INVISIBILITY, getConfig().OBSCURITY.invisUseDuration.get() ) );
                player.addEffect( new MobEffectInstance( MRMobEffects.CLOUDY_VISION.get(), getConfig().OBSCURITY.cloudyUseDuration.get() ) );
                ServerEventListener.queuePlayerForDeaggro( (ServerPlayer) player );
            }
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            return true;
        }
        return false;
    }
    
    @Override
    public void onUserDamaged( Level level, Player player, DamageSource damageSource, ItemStack artifact ) {
        artifact.hurtAndBreak( 1, player, ( entity ) -> entity.broadcastBreakEvent( player.getUsedItemHand() ) );
        // noinspection resource
        if( !player.level().isClientSide ) {
            player.addEffect( new MobEffectInstance( MobEffects.INVISIBILITY, getConfig().OBSCURITY.invisDamagedDuration.get() ) );
            player.addEffect( new MobEffectInstance( MRMobEffects.CLOUDY_VISION.get(), getConfig().OBSCURITY.cloudyDamagedDuration.get() ) );
        }
    }
    
    @Override
    public void onDamageMob( ItemStack artifact, Player player, LivingEntity attackedMob ) {
        artifact.hurtAndBreak( 1, player, ( entity ) -> entity.broadcastBreakEvent( player.getUsedItemHand() ) );
        // noinspection resource
        if( !player.level().isClientSide ) {
            player.addEffect( new MobEffectInstance( MobEffects.INVISIBILITY, getConfig().OBSCURITY.invisAttackDuration.get() ) );
            player.addEffect( new MobEffectInstance( MRMobEffects.CLOUDY_VISION.get(), getConfig().OBSCURITY.cloudyAttackDuration.get() ) );
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
        if( isArmor )
            return TriggerType.USER_DAMAGED;
        
        return random.nextInt( 2 ) == 0
                ? TriggerType.USE
                : TriggerType.USER_ATTACKING;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return ArtifactCategory.ALL;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return switch( type ) {
            case USER_DAMAGED -> getDescComponent( type, getConfig().OBSCURITY.invisDamagedDuration.get() );
            case USER_ATTACKING -> getDescComponent( type, getConfig().OBSCURITY.invisAttackDuration.get() );
            case USE -> getDescComponent( type, getConfig().OBSCURITY.invisUseDuration.get() );
            default -> null;
        };
    }
}
