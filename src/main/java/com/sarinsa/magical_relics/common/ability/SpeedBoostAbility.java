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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;


public class SpeedBoostAbility extends BaseArtifactAbility<SpeedBoostAbility.SpeedBoostAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "speed_boost", "fast" ),
            createPrefix( "speed_boost", "quick" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "speed_boost", "swiftness" ),
            createSuffix( "speed_boost", "mobility" ),
            createSuffix( "speed_boost", "speed" ),
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.HELD
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.RING,
            ArtifactCategory.STAFF,
            ArtifactCategory.BELT,
            ArtifactCategory.DAGGER,
            ArtifactCategory.SWORD,
            ArtifactCategory.AXE
    );
    
    private final Supplier<AttributeBoost> SPEED_BOOST = Suppliers.memoize( () -> new AttributeBoost(
            () -> Attributes.MOVEMENT_SPEED,
            "MRSpeedBoost",
            AttributeModifier.Operation.MULTIPLY_BASE,
            getConfig().SPEED_BOOST.boost::next,
            AttributeBoost.ActiveType.HELD_OR_EQUIPPED
    ) );
    
    
    public SpeedBoostAbility() { }
    
    
    public static class SpeedBoostAbilityConfig extends AbilityConfig {
        
        public SpeedBoost SPEED_BOOST;
        
        public SpeedBoostAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                        double minBoost, double maxBoost ) {
            super( cfgManager, abilityId );
            
            SPEED_BOOST = new SpeedBoost( this, minBoost, maxBoost );
        }
        
        public static class SpeedBoost extends AbstractConfigCategory<SpeedBoostAbilityConfig> {
            
            public DoubleField.RandomRange boost;
            
            public SpeedBoost( SpeedBoostAbilityConfig parent, double minBoost, double maxBoost ) {
                super( parent, "speed", "Options for the speed boost this ability provides" );
                
                boost = new DoubleField.RandomRange( SPEC, "boost", minBoost, maxBoost, DoubleField.Range.PERCENT,
                        "The minimum and maximum (inclusive) percentage multiplier of speed boost this ability can apply to an artifact item, .",
                        "When this ability is applied to an artifact, a random modifier between minimum and maximum is picked." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new SpeedBoostAbilityConfig( cfgManager, abilityId, 0.04, 0.12 );
    }
    
    @Override
    public AttributeBoost getAttributeWithBoost() {
        return SPEED_BOOST.get();
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
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
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
        if( type == TriggerType.ARMOR_TICK || type == TriggerType.HELD )
            return descComponent( type );
        return null;
    }
}
