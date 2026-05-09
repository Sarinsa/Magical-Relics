package com.sarinsa.magical_relics.common.ability;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TerrainWalkerAbility extends BaseArtifactAbility<TerrainWalkerAbility.TerrainWalkerAbilityConfig> {
    
    
    private static final String[] PREFIXES = {
            createPrefix( "terrain_walker", "long_legs" ),
            createPrefix( "terrain_walker", "mountain_goat" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "terrain_walker", "traversal" ),
            createSuffix( "terrain_walker", "long_steps" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.LEGGINGS
    );
    
    private final Supplier<AttributeBoost> STEP_BOOST = Suppliers.memoize( () -> new AttributeBoost(
            ForgeMod.STEP_HEIGHT_ADDITION,
            "MREntityStepHeightBoost",
            AttributeModifier.Operation.ADDITION,
            getConfig().TERRAIN_WALKER.boost::next,
            AttributeBoost.ActiveType.EQUIPPED
    ) );
    
    
    public TerrainWalkerAbility() { }
    
    
    public static class TerrainWalkerAbilityConfig extends AbilityConfig {
        
        public final TerrainWalker TERRAIN_WALKER;
        
        public TerrainWalkerAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                           double minBoost, double maxBoost ) {
            super( cfgManager, abilityId );
            
            TERRAIN_WALKER = new TerrainWalker( this, minBoost, maxBoost );
        }
        
        public static class TerrainWalker extends AbstractConfigCategory<TerrainWalkerAbilityConfig> {
            
            public final DoubleField.RandomRange boost;
            
            public TerrainWalker( TerrainWalkerAbilityConfig parent, double minBoost, double maxBoost ) {
                super( parent, "terrain_walker", "Options for the step-boost this ability provides" );
                
                boost = new DoubleField.RandomRange( SPEC, "boost", minBoost, maxBoost, DoubleField.Range.NON_NEGATIVE,
                        "The minimum and maximum (inclusive) percentage multiplier of boosted step-height this ability can apply to an artifact item, .",
                        "When this ability is applied to an artifact, a random modifier between minimum and maximum is picked." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new TerrainWalkerAbilityConfig( cfgManager, abilityId, 0.2, 1.6 );
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
    public AttributeBoost getAttributeWithBoost() {
        return STEP_BOOST.get();
    }
}
