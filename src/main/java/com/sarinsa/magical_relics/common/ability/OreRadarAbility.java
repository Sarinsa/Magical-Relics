package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class OreRadarAbility extends BaseArtifactAbility<OreRadarAbility.OreRadarAbilityConfig> {
    
    
    private static final String[] PREFIXES = {
            createPrefix( "ore_radar", "revealing" ),
            createPrefix( "ore_radar", "scanning" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "ore_radar", "sensing" ),
            createSuffix( "ore_radar", "dowsing" ),
    };
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of( ArtifactCategory.HELMET );
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK
    );
    
    
    public OreRadarAbility() { }
    
    
    public static class OreRadarAbilityConfig extends AbilityConfig {
        
        public OreRadar ORE_RADAR;
        
        public OreRadarAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity,
                                      int radius ) {
            super( cfgManager, abilityId, rarity );
            
            ORE_RADAR = new OreRadar( this, radius );
        }
        
        public static class OreRadar extends AbstractConfigCategory<OreRadarAbilityConfig> {
            
            public IntField radius;
            
            public OreRadar( OreRadarAbilityConfig parent, int rad ) {
                super( parent, "ore_radar", "Options for the in-world visual this ability grants." );
                
                radius = SPEC.define( new IntField( "radius", rad, IntField.Range.NON_NEGATIVE,
                        "The radius of the spherical area around the player in which ore-ping particles are spawned.",
                        "Keep in mind that larger values may cause poor performance on the client." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new OreRadarAbilityConfig( cfgManager, abilityId, Rarity.EPIC, 7 );
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
        return Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.ore_radar.description" );
    }
}
