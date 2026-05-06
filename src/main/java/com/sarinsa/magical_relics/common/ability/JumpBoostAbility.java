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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class JumpBoostAbility extends BaseArtifactAbility<JumpBoostAbility.JumpBoostAbilityConfig> {
    
    public static final String TAG_ABILITY_DATA = "JumpBoostAbilityData";
    public static final String TAG_AMPLIFIER = "EffectAmplifier";
    
    private static final String[] PREFIXES = {
            createPrefix( "jump_boost", "jumpy" ),
            createPrefix( "jump_boost", "bouncing" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "jump_boost", "leaping" ),
            createSuffix( "jump_boost", "lightness" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK,
            TriggerType.USE,
            TriggerType.USER_ATTACKING,
            TriggerType.INVENTORY_TICK,
            TriggerType.CURIO_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.RING,
            ArtifactCategory.BELT,
            ArtifactCategory.LEGGINGS,
            ArtifactCategory.STAFF,
            ArtifactCategory.TRINKET
    );
    
    
    public JumpBoostAbility() { }
    
    
    public static class JumpBoostAbilityConfig extends CooldownAbilityConfig {
        
        public JumpBoost JUMP_BOOST;
        
        public JumpBoostAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity,
                                       int cooldown,
                                       int useDuration, int passiveDuration, int attackDuration,
                                       int minAmplifier, int maxAmplifier ) {
            super( cfgManager, abilityId, rarity, cooldown );
            
            JUMP_BOOST = new JumpBoost( this, useDuration, passiveDuration, attackDuration, minAmplifier, maxAmplifier );
        }
        
        public static class JumpBoost extends AbstractConfigCategory<JumpBoostAbilityConfig> {
            
            public IntField useDuration;
            public IntField passiveDuration;
            public IntField attackDuration;
            
            public IntField.RandomRange amplifier;
            
            public JumpBoost( JumpBoostAbilityConfig parent, int useDur, int passiveDur, int attackDur,
                              int minAmplifier, int maxAmplifier ) {
                super( parent, "jump_boost", "Options for the jump boost effect applied by this ability." );
                
                useDuration = SPEC.define( new IntField( "use_duration", useDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a use trigger." ) );
                passiveDuration = SPEC.define( new IntField( "passive_duration", passiveDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a passive trigger." ) );
                attackDuration = SPEC.define( new IntField( "attack_duration", attackDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has an attack trigger." ) );
                
                SPEC.newLine();
                
                amplifier = new IntField.RandomRange( SPEC, "amplifier", minAmplifier, maxAmplifier, IntField.Range.NON_NEGATIVE,
                        "The minimum and maximum (inclusive) effect amplifier that is picked for the potion effect granted by this ability." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new JumpBoostAbilityConfig( cfgManager, abilityId, Rarity.RARE,
                900,
                900, 310, 125,
                0, 2 );
    }
    
    @Override
    public void onAbilityAttached( ItemStack artifact, RandomSource random ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
        
        int amplifier = getConfig().JUMP_BOOST.amplifier.next( random );
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
            artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( hand ) );
            // noinspection resource
            if( !player.level().isClientSide )
                player.addEffect( new MobEffectInstance( MobEffects.JUMP, getConfig().JUMP_BOOST.useDuration.get(), getEffectMultiplier( artifact ) ) );
            
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            return true;
        }
        return false;
    }
    
    @Override
    public void onDamageMob( ItemStack artifact, Player player, LivingEntity attackedMob ) {
        // noinspection resource
        if( !player.level().isClientSide )
            player.addEffect( new MobEffectInstance( MobEffects.JUMP, getConfig().JUMP_BOOST.attackDuration.get(), getEffectMultiplier( artifact ) ) );
    }
    
    @Override
    public void onInventoryTick( ItemStack artifact, Level level, Entity entity, int slot, boolean isSelectedItem ) {
        if( !level.isClientSide ) {
            if( entity instanceof LivingEntity livingEntity ) {
                livingEntity.addEffect( new MobEffectInstance( MobEffects.JUMP, getConfig().JUMP_BOOST.passiveDuration.get(), getEffectMultiplier( artifact ) ) );
            }
        }
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        onInventoryTick( artifact, level, player, 0, false );
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        onInventoryTick( artifact, level, player, 0, true );
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
        if( isArmor ) return TriggerType.ARMOR_TICK;
        if( isCurio ) return TriggerType.CURIO_TICK;
        
        return switch( random.nextInt( 3 ) ) {
            case 1 -> TriggerType.USER_ATTACKING;
            case 2 -> TriggerType.INVENTORY_TICK;
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
            case USE ->
                    potionDescComponent( type, getConfig().JUMP_BOOST.useDuration.get(), getEffectMultiplier( artifact ) );
            case USER_ATTACKING ->
                    potionDescComponent( type, getConfig().JUMP_BOOST.attackDuration.get(), getEffectMultiplier( artifact ) );
            case INVENTORY_TICK, CURIO_TICK, ARMOR_TICK ->
                    potionLevelDescComponent( type, getEffectMultiplier( artifact ) );
            default -> null;
        };
    }
}
