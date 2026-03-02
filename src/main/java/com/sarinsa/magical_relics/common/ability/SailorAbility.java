package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SailorAbility extends BaseArtifactAbility<SailorAbility.SailorAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "sailor", "sailors" ),
            createPrefix( "sailor", "paddling" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "sailor", "rowing" ),
            createSuffix( "sailor", "boating" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.CHESTPLATE
    );
    
    
    public SailorAbility() { }
    
    
    public static class SailorAbilityConfig extends AbilityConfig {
        
        public Sailor SAILOR;
        
        public SailorAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                    int d ) {
            super( cfgManager, abilityId );
            
            SAILOR = new Sailor( this );
        }
        
        public static class Sailor extends AbstractConfigCategory<SailorAbilityConfig> {
            
            public DoubleField speedMultiplier;
            
            public Sailor( SailorAbilityConfig parent ) {
                super( parent, "sailor", "Options for how this ability affects boat travel." );
                
                speedMultiplier = SPEC.define( new DoubleField( "speed_multiplier", 0.05D, 0.0D, 100.0D,
                        "The row speed multiplier granted by this ability.",
                        "A value of 0.0 would equal 100% of normal boat speed (no change).",
                        "A value of 0.05 would equal a 5% increase. " ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new SailorAbilityConfig( cfgManager, abilityId, 1 );
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
        return isArmor ? TriggerType.ARMOR_TICK : null;
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
        return Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.sailor.description" );
    }
}
