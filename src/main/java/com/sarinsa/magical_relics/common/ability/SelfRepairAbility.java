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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SelfRepairAbility extends BaseArtifactAbility<SelfRepairAbility.RepairSelfAbilityConfig> {
    
    
    private static final String[] PREFIXES = {
            createPrefix( "self_repair", "repairing" ),
            createPrefix( "self_repair", "recharging" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "self_repair", "renewal" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.HELD, TriggerType.INVENTORY_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.RING,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.TRINKET,
            ArtifactCategory.LEGGINGS,
            ArtifactCategory.BOOTS,
            ArtifactCategory.DAGGER,
            ArtifactCategory.SWORD,
            ArtifactCategory.AXE
    );
    
    public SelfRepairAbility() { }
    
    
    public static class RepairSelfAbilityConfig extends CooldownAbilityConfig {
        
        public RepairSelf REPAIR_SELF;
        
        public RepairSelfAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                        int cooldown, int durabilityRestored ) {
            super( cfgManager, abilityId, cooldown );
            
            REPAIR_SELF = new RepairSelf( this, durabilityRestored );
        }
        
        public static class RepairSelf extends AbstractConfigCategory<RepairSelfAbilityConfig> {
            
            public IntField durabilityRestored;
            
            public RepairSelf( RepairSelfAbilityConfig parent, int durRestored ) {
                super( parent, "repair_self", "Options for this ability repairing its host artifact." );
                
                durabilityRestored = SPEC.define( new IntField( "durability_restoration", durRestored, IntField.Range.POSITIVE,
                        "The amount of durability this ability restores for its host artifact per repair cycle." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new RepairSelfAbilityConfig( cfgManager, abilityId, 120, 1 );
    }
    
    @Override
    public void onInventoryTick( ItemStack artifact, Level level, Entity entity, int slot, boolean isSelectedItem ) {
        handleRepair( artifact, level, entity );
    }
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        handleRepair( artifact, level, player );
    }
    
    @Override
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) {
        handleRepair( artifact, level, player );
    }
    
    private void handleRepair( ItemStack artifact, Level level, Entity entity ) {
        if( !level.isClientSide && artifact.getDamageValue() > 0 ) {
            if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
                artifact.hurt( -getConfig().REPAIR_SELF.durabilityRestored.get(), level.random, entity instanceof ServerPlayer serverPlayer ? serverPlayer : null );
                
                ArtifactUtils.setAbilityOnCooldown( artifact, this );
            }
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
        if( isArmor ) {
            return random.nextInt( 2 ) == 0 ? TriggerType.ARMOR_TICK : TriggerType.HELD;
        }
        return random.nextInt( 2 ) == 0 ? TriggerType.INVENTORY_TICK : TriggerType.HELD;
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
    public boolean showCooldownSymbol() {
        return false;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        if( type == null ) return null;
        
        return switch( type ) {
            case INVENTORY_TICK, HELD, ARMOR_TICK -> descComponent( type );
            default -> null;
        };
    }
}
