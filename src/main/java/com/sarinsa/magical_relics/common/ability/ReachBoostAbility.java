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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ReachBoostAbility extends BaseArtifactAbility<ReachBoostAbility.ReachBoostAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "reach_boost", "lengthy" ),
            createPrefix( "reach_boost", "extending" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "reach_boost", "stretching" ),
            createSuffix( "reach_boost", "reaching" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.HELD
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.RING,
            ArtifactCategory.AMULET,
            ArtifactCategory.SWORD,
            ArtifactCategory.AXE
    );
    
    private final Supplier<AttributeBoost> REACH_BOOST = Suppliers.memoize( () -> new AttributeBoost(
            ForgeMod.ENTITY_REACH,
            "MREntityReachBoost",
            AttributeModifier.Operation.ADDITION,
            ( random ) -> getConfig().REACH_BOOST.boost.next( random ) / 10,
            AttributeBoost.ActiveType.HELD_OR_EQUIPPED
    ) );
    
    
    public ReachBoostAbility() { }
    
    
    public static class ReachBoostAbilityConfig extends AbilityConfig {
        
        public ReachBoost REACH_BOOST;
        
        public ReachBoostAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                        double minBoost, double maxBoost ) {
            super( cfgManager, abilityId );
            
            REACH_BOOST = new ReachBoost( this, minBoost, maxBoost );
        }
        
        public static class ReachBoost extends AbstractConfigCategory<ReachBoostAbilityConfig> {
            
            public DoubleField.RandomRange boost;
            
            public ReachBoost( ReachBoostAbilityConfig parent, double minBoost, double maxBoost ) {
                super( parent, "reach_boost", "Options for the reach boost this ability provides" );
                
                boost = new DoubleField.RandomRange( SPEC, "boost", minBoost, maxBoost, 0.01, 10.0,
                        "The minimum and maximum (inclusive) amount of extra reach the boost of this ability can grant (for example, a value of 0.5 would equal half a block more range).",
                        "When this ability is applied to an artifact, a random value between minimum and maximum is picked." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new ReachBoostAbilityConfig( cfgManager, abilityId, 0.1, 0.4 );
    }
    
    @Override
    public AttributeBoost getAttributeWithBoost() {
        return REACH_BOOST.get();
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
        if( type == TriggerType.ARMOR_TICK || type == TriggerType.HELD ) {
            return descComponent( type );
        }
        return null;
    }
}
