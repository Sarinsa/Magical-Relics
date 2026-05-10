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
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class SlowFallingAbility extends BaseArtifactAbility<SlowFallingAbility.SlowFallingAbilityConfig> {
    
    public static final String TAG_ABILITY_DATA = "SlowFallingAbilityData";
    public static final String TAG_AMPLIFIER = "EffectAmplifier";
    
    private static final String[] PREFIXES = {
            createPrefix( "slow_falling", "gauzy" ),
            createPrefix( "slow_falling", "balloon" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "slow_falling", "helium" ),
            createSuffix( "slow_falling", "slow_descent" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK,
            TriggerType.INVENTORY_TICK,
            TriggerType.USE,
            TriggerType.HELD,
            TriggerType.CURIO_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.RING,
            ArtifactCategory.BELT,
            ArtifactCategory.CHESTPLATE,
            ArtifactCategory.BOOTS,
            ArtifactCategory.DAGGER
    );
    
    public SlowFallingAbility() { }
    
    
    public static class SlowFallingAbilityConfig extends CooldownAbilityConfig {
        
        public final SlowFalling SLOW_FALLING;
        
        public SlowFallingAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity,
                                         int cooldown, int useDuration, int passiveDuration,
                                         int minAmplifier, int maxAmplifier ) {
            super( cfgManager, abilityId, rarity, cooldown );
            
            SLOW_FALLING = new SlowFalling( this, useDuration, passiveDuration, minAmplifier, maxAmplifier );
        }
        
        public static class SlowFalling extends AbstractConfigCategory<SlowFallingAbilityConfig> {
            
            public final IntField useDuration;
            public final IntField passiveDuration;
            
            public final IntField.RandomRange amplifier;
            
            public SlowFalling( SlowFallingAbilityConfig parent, int useDur, int passiveDur,
                                int minAmplifier, int maxAmplifier ) {
                super( parent, "slow_falling", "Options for the slow falling effect applied by this ability." );
                
                useDuration = SPEC.define( new IntField( "use_duration", useDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a use trigger." ) );
                passiveDuration = SPEC.define( new IntField( "passive_duration", passiveDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a passive trigger." ) );
                
                SPEC.newLine();
                
                amplifier = new IntField.RandomRange( SPEC, "amplifier", minAmplifier, maxAmplifier, IntField.Range.NON_NEGATIVE,
                        "The minimum and maximum (inclusive) effect amplifier that is picked for the potion effect granted by this ability." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new SlowFallingAbilityConfig( cfgManager, abilityId, Rarity.RARE,
                2400, 2400, MarkedMobEffectInstance.TICK_THRESHOLD,
                0, 2 );
    }
    
    @Override
    public void onAbilityAttached( ItemStack artifact, RandomSource random ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
        
        int amplifier = getConfig().SLOW_FALLING.amplifier.next( random );
        abilityData.putInt( TAG_AMPLIFIER, amplifier );
    }
    
    private int getEffectMultiplier( ItemStack artifact ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
        
        if( NBTHelper.containsNumber( abilityData, TAG_AMPLIFIER ) ) {
            return Math.max( 0, abilityData.getInt( TAG_AMPLIFIER ) );
        }
        return 0;
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            player.addEffect( new MobEffectInstance( MobEffects.SLOW_FALLING, getConfig().SLOW_FALLING.useDuration.get(), getEffectMultiplier( artifact ) ) );
            
            artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( hand ) );
            
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            return true;
        }
        return false;
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        if( !level.isClientSide )
            player.addEffect( new MobEffectInstance( MobEffects.SLOW_FALLING, getConfig().SLOW_FALLING.passiveDuration.get() ) );
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        // noinspection ConstantConditions
        onArmorTick( artifact, level, player, null );
    }
    
    @Override
    public void onInventoryTick( ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem ) {
        if( !level.isClientSide && entity instanceof Player player ) {
            player.addEffect( new MobEffectInstance( MobEffects.SLOW_FALLING, getConfig().SLOW_FALLING.passiveDuration.get() ) );
        }
    }
    
    @Override
    public void onArmorTick( ItemStack stack, Level level, Player player, EquipmentSlot slot ) {
        if( !level.isClientSide ) {
            player.addEffect( new MobEffectInstance( MobEffects.SLOW_FALLING, getConfig().SLOW_FALLING.passiveDuration.get() ) );
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
        if( isCurio ) return TriggerType.CURIO_TICK;
        if( isArmor ) return TriggerType.ARMOR_TICK;
        
        return switch( random.nextInt( 3 ) ) {
            case 1 -> TriggerType.INVENTORY_TICK;
            case 2 -> TriggerType.HELD;
            default -> TriggerType.USE;
        };
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleCategories() {
        return TYPES;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return switch( type ) {
            case ARMOR_TICK, HELD, CURIO_TICK, INVENTORY_TICK ->
                    potionLevelDescComponent( type, getEffectMultiplier( artifact ) );
            case USE ->
                    potionDescComponent( type, getConfig().SLOW_FALLING.useDuration.get(), getEffectMultiplier( artifact ) );
            default -> null;
        };
    }
}
