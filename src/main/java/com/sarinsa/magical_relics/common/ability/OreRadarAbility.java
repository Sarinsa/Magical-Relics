package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class OreRadarAbility extends BaseArtifactAbility {


    private static final String[] PREFIXES = {
            createPrefix("ore_radar", "revealing"),
    };

    private static final String[] SUFFIXES = {
            createSuffix("ore_radar", "sensing"),
            createSuffix("ore_radar", "dowsing"),
    };

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(ArtifactCategory.HELMET);

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK
    );

    public static ForgeConfigSpec.IntValue scanRange;


    public OreRadarAbility() { }


    @AbilityConfig(abilityId = "magical_relics:ore_radar")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        scanRange = configBuilder.comment("The scan range of the ore radar. A range of 5 equals a search area of 5x5x5 blocks around the player." +
                        "Note that larger values may cause poor performance on clients.")
                .defineInRange("scanRange", 7, 1, 50);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
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
    public TriggerType getRandomTrigger(ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio) {
        return isArmor ? TriggerType.ARMOR_TICK : null;
    }

    @NotNull
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.ore_radar.description");
    }
}
