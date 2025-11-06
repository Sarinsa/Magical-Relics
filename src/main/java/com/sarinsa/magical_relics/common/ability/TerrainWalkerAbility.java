package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TerrainWalkerAbility extends BaseArtifactAbility {
    
    
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
    
    private static final AttributeBoost STEP_BOOST = new AttributeBoost(
            ForgeMod.STEP_HEIGHT_ADDITION,
            "MREntityStepHeightBoost",
            AttributeModifier.Operation.ADDITION,
            ( random ) -> TerrainWalkerAbility.stepIncrease.get(),
            AttributeBoost.ActiveType.EQUIPPED
    );
    
    private static ForgeConfigSpec.IntValue stepIncrease;
    
    
    public TerrainWalkerAbility() { }
    
    
    @AbilityConfig( abilityId = "magical_relics:terrain_walker" )
    public static void buildEntries( ForgeConfigSpec.Builder configBuilder ) {
        stepIncrease = configBuilder.comment( "The max height a player can step up. A value of 3 will allow a player to walk up a 3 high block pillar for example." )
                .defineInRange( "stepIncrease", 1, 1, 100 );
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
    public @Nullable TriggerType getRandomTrigger( ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio ) {
        return isArmor ? TriggerType.ARMOR_TICK : null;
    }
    
    @Override
    public @NotNull List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
    
    @Override
    public @Nullable AttributeBoost getAttributeWithBoost() {
        return STEP_BOOST;
    }
    
    @Override
    public MutableComponent getAbilityDescription( TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        return Component.translatable( MagicalRelics.MODID + ".artifact_ability.magical_relics.terrain_walker.description" );
    }
}
