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


public class NightVisionAbility extends BaseArtifactAbility<NightVisionAbility.NightVisionAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "night_vision", "sensing" ),
            createPrefix( "night_vision", "sighted" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "night_vision", "seeing" ),
            createSuffix( "night_vision", "night_vision" )
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
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.STAFF,
            ArtifactCategory.RING,
            ArtifactCategory.WAND,
            ArtifactCategory.BELT,
            ArtifactCategory.CHESTPLATE,
            ArtifactCategory.HELMET
    );
    
    public NightVisionAbility() { }
    
    
    public static class NightVisionAbilityConfig extends CooldownAbilityConfig {
        
        public NightVision NIGHT_VISION;
        
        public NightVisionAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity,
                                         int cooldown, int useDuration, int passiveDuration ) {
            super( cfgManager, abilityId, rarity, cooldown );
            
            NIGHT_VISION = new NightVision( this, useDuration, passiveDuration );
        }
        
        public static class NightVision extends AbstractConfigCategory<NightVisionAbilityConfig> {
            
            public IntField useDuration;
            public IntField passiveDuration;
            
            public NightVision( NightVisionAbilityConfig parent, int useDur, int passiveDur ) {
                super( parent, "night_vision", "Options for the night vision effect applied by this ability." );
                
                useDuration = SPEC.define( new IntField( "use_duration", useDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a use trigger." ) );
                passiveDuration = SPEC.define( new IntField( "passive_duration", passiveDur, IntField.Range.POSITIVE,
                        "The duration (in ticks) of the potion effect when this ability has a passive trigger." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new NightVisionAbilityConfig( cfgManager, abilityId, Rarity.RARE,
                2400, 2400, 1 );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            player.addEffect( new MobEffectInstance( MobEffects.NIGHT_VISION, getConfig().NIGHT_VISION.useDuration.get() ) );
            
            artifact.hurtAndBreak( 1, player, ( p ) -> p.broadcastBreakEvent( hand ) );
            
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            return true;
        }
        return false;
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        // noinspection resource
        if( !player.level().isClientSide )
            player.addEffect( new MobEffectInstance( MobEffects.NIGHT_VISION, getConfig().NIGHT_VISION.passiveDuration.get() ) );
    }
    
    @Override
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) {
        // noinspection ConstantConditions
        onArmorTick( artifact, level, player, null );
    }
    
    @Override
    public void onInventoryTick( ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem ) {
        if( !level.isClientSide && entity instanceof Player player ) {
            player.addEffect( new MobEffectInstance( MobEffects.NIGHT_VISION, getConfig().NIGHT_VISION.passiveDuration.get() ) );
        }
    }
    
    @Override
    public void onArmorTick( ItemStack stack, Level level, Player player, EquipmentSlot slot ) {
        if( !level.isClientSide ) {
            player.addEffect( new MobEffectInstance( MobEffects.NIGHT_VISION, getConfig().NIGHT_VISION.passiveDuration.get() ) );
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
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return switch( type ) {
            case ARMOR_TICK, CURIO_TICK, HELD -> descComponent( type );
            case USE -> durationDescComponent( type, getConfig().NIGHT_VISION.useDuration.get() );
            default -> null;
        };
    }
}
