package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdrenalineAbility extends BaseArtifactAbility<AdrenalineAbility.AdrenalineAbilityConfig> {
    
    public static final String TAG_ABILITY_DATA = "AdrenalineAbilityData";
    public static final String TAG_SPEED_DURATION = "SpeedDuration";
    public static final String TAG_SPEED_AMPLIFIER = "SpeedAmplifier";
    public static final String TAG_DAMAGE_DURATION = "DamageDuration";
    public static final String TAG_DAMAGE_AMPLIFIER = "DamageAmplifier";
    public static final String TAG_DAMAGE_RES_DURATION = "DamageResDuration";
    public static final String TAG_DAMAGE_RES_AMPLIFIER = "DamageResAmplifier";
    
    private static final String[] PREFIXES = {
            createPrefix( "adrenaline", "brawling" ),
            createPrefix( "adrenaline", "skirmish" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "adrenaline", "rage" ),
            createSuffix( "adrenaline", "fury" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USER_DAMAGED
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.CHESTPLATE
    );
    
    
    public AdrenalineAbility() { }
    
    
    public static class AdrenalineAbilityConfig extends CooldownAbilityConfig {
        
        public Adrenaline ADRENALINE;
        
        public AdrenalineAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity,
                                        int cooldown,
                                        int minMoveSpeedDur, int maxMoveSpeedDur, int minMoveSpeedAmp, int maxMoveSpeedAmp,
                                        int minDamageBoostDur, int maxDamageBoostDur, int minDamageBoostAmp, int maxDamageBoostAmp,
                                        int minDamageResDur, int maxDamageResDur, int minDamageResAmp, int maxDamageResAmp ) {
            super( cfgManager, abilityId, rarity, cooldown );
            
            ADRENALINE = new Adrenaline( this, minMoveSpeedDur, maxMoveSpeedDur, minMoveSpeedAmp, maxMoveSpeedAmp,
                    minDamageBoostDur, maxDamageBoostDur, minDamageBoostAmp, maxDamageBoostAmp,
                    minDamageResDur, maxDamageResDur, minDamageResAmp, maxDamageResAmp );
        }
        
        public static class Adrenaline extends AbstractConfigCategory<AdrenalineAbilityConfig> {
            
            public IntField.RandomRange moveSpeedDuration;
            public IntField.RandomRange moveSpeedAmplifier;
            
            public IntField.RandomRange damageBoostDuration;
            public IntField.RandomRange damageBoostAmplifier;
            
            public IntField.RandomRange damageResDuration;
            public IntField.RandomRange damageResAmplifier;
            
            public Adrenaline( AdrenalineAbilityConfig parent, int minMoveSpeedDur, int maxMoveSpeedDur, int minMoveSpeedAmp, int maxMoveSpeedAmp,
                               int minDamageBoostDur, int maxDamageBoostDur, int minDamageBoostAmp, int maxDamageBoostAmp,
                               int minDamageResDur, int maxDamageResDur, int minDamageResAmp, int maxDamageResAmp ) {
                super( parent, "adrenaline", "Options for the potion effects applied by this ability." );
                
                moveSpeedDuration = new IntField.RandomRange( SPEC, "speed_duration", minMoveSpeedDur, maxMoveSpeedDur, IntField.Range.POSITIVE,
                        "The minimum and maximum (inclusive) number of ticks that the speed effect lasts for." );
                moveSpeedAmplifier = new IntField.RandomRange( SPEC, "speed_amplifier", minMoveSpeedAmp, maxMoveSpeedAmp, IntField.Range.NON_NEGATIVE,
                        "The minimum and maximum (inclusive) amplifier for the speed effect." );
                
                SPEC.newLine();
                
                damageBoostDuration = new IntField.RandomRange( SPEC, "damage_boost_duration", minDamageBoostDur, maxDamageBoostDur, IntField.Range.POSITIVE,
                        "The minimum and maximum (inclusive) number of ticks that the damage boost effect lasts for." );
                damageBoostAmplifier = new IntField.RandomRange( SPEC, "damage_boost_amplifier", minDamageBoostAmp, maxDamageBoostAmp, IntField.Range.NON_NEGATIVE,
                        "The minimum and maximum (inclusive) amplifier for the damage boost effect." );
                
                SPEC.newLine();
                
                damageResDuration = new IntField.RandomRange( SPEC, "resistance_duration", minDamageResDur, maxDamageResDur, IntField.Range.POSITIVE,
                        "The minimum and maximum (inclusive) number of ticks that the damage resistance effect lasts for." );
                damageResAmplifier = new IntField.RandomRange( SPEC, "resistance_amplifier", minDamageResAmp, maxDamageResAmp, IntField.Range.NON_NEGATIVE,
                        "The minimum and maximum (inclusive) amplifier for the damage resistance effect." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new AdrenalineAbilityConfig( cfgManager, abilityId, Rarity.RARE, 600,
                80, 120, 0, 1,
                80, 120, 0, 1,
                60, 140, 0, 2 );
    }
    
    @Override
    public void onAbilityAttached( ItemStack artifact, RandomSource random ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
        
        abilityData.putInt( TAG_SPEED_DURATION, getConfig().ADRENALINE.moveSpeedDuration.next( random ) );
        abilityData.putInt( TAG_SPEED_AMPLIFIER, getConfig().ADRENALINE.moveSpeedAmplifier.next( random ) );
        
        abilityData.putInt( TAG_DAMAGE_DURATION, getConfig().ADRENALINE.damageBoostDuration.next( random ) );
        abilityData.putInt( TAG_DAMAGE_AMPLIFIER, getConfig().ADRENALINE.damageBoostAmplifier.next( random ) );
        
        abilityData.putInt( TAG_DAMAGE_RES_DURATION, getConfig().ADRENALINE.damageResDuration.next( random ) );
        abilityData.putInt( TAG_DAMAGE_RES_AMPLIFIER, getConfig().ADRENALINE.damageResAmplifier.next( random ) );
    }
    
    @Override
    public void onUserDamaged( Level level, Player player, DamageSource damageSource, ItemStack artifact ) {
        if( damageSource.getEntity() != null && !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            applyEffectsTo( player, artifact );
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
        }
    }
    
    private void applyEffectsTo( Player player, ItemStack artifact ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
        
        player.addEffect( new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                abilityData.getInt( TAG_SPEED_DURATION ),
                abilityData.getInt( TAG_SPEED_AMPLIFIER )
        ) );
        player.addEffect( new MobEffectInstance(
                MobEffects.DAMAGE_BOOST,
                abilityData.getInt( TAG_DAMAGE_DURATION ),
                abilityData.getInt( TAG_DAMAGE_AMPLIFIER )
        ) );
        player.addEffect( new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE,
                abilityData.getInt( TAG_DAMAGE_RES_DURATION ),
                abilityData.getInt( TAG_DAMAGE_RES_AMPLIFIER )
        ) );
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
        return isArmor ? TriggerType.USER_DAMAGED : null;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
}
