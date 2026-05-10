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
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Supplier;

public class HealthBoostAbility extends BaseArtifactAbility<HealthBoostAbility.HealthBoostAbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "health_boost", "hardy" ),
            createPrefix( "health_boost", "bulky" ),
            createPrefix( "health_boost", "sturdy" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "health_boost", "toughness" ),
            createSuffix( "health_boost", "vitality" ),
            createSuffix( "health_boost", "health" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.RING,
            ArtifactCategory.BELT
    );
    
    private final Supplier<AttributeBoost> HEALTH_BOOST = Suppliers.memoize( () -> new AttributeBoost(
            () -> Attributes.MAX_HEALTH,
            "MRHealthBoost",
            AttributeModifier.Operation.ADDITION,
            getConfig().HEALTH_BOOST.boost::next,
            AttributeBoost.ActiveType.EQUIPPED
    ) );
    
    
    public HealthBoostAbility() { }
    
    
    public static class HealthBoostAbilityConfig extends AbilityConfig {
        
        public final HealthBoost HEALTH_BOOST;
        
        public HealthBoostAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                         int minBoost, int maxBoost ) {
            super( cfgManager, abilityId );
            
            HEALTH_BOOST = new HealthBoost( this, minBoost, maxBoost );
        }
        
        public static class HealthBoost extends AbstractConfigCategory<HealthBoostAbilityConfig> {
            
            public final IntField.RandomRange boost;
            
            public HealthBoost( HealthBoostAbilityConfig parent, int minBoost, int maxBoost ) {
                super( parent, "health_boost", "Options for the health boost this ability provides" );
                
                boost = new IntField.RandomRange( SPEC, "boost", minBoost, maxBoost, IntField.Range.NON_NEGATIVE,
                        "The minimum and maximum (inclusive) amount of health boost this ability can apply to an artifact item.",
                        "When this ability is applied to an artifact, a random value between minimum and maximum is picked." );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new HealthBoostAbilityConfig( cfgManager, abilityId, 1, 5 );
    }
    
    @Override
    public AttributeBoost getAttributeWithBoost() {
        return HEALTH_BOOST.get();
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
        return TriggerType.ARMOR_TICK;
    }
    
    @Override
    public void onUnequipped( SlotContext slotContext, ItemStack artifact ) {
        if( slotContext.entity() instanceof Player player ) {
            if( player.getHealth() > player.getMaxHealth() ) {
                player.setHealth( player.getMaxHealth() );
            }
        }
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleCategories() {
        return TYPES;
    }
}
