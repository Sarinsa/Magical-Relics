package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
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

public class ReachBoostAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("reach_boost", "lengthy"),
            createPrefix("reach_boost", "extending")
    };

    private static final String[] SUFFIXES = {
            createSuffix("reach_boost", "stretching"),
            createSuffix("reach_boost", "reaching")
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

    private static final AttributeBoost REACH_BOOST = new AttributeBoost(
            ForgeMod.ENTITY_REACH,
            "MREntityReachBoost",
            AttributeModifier.Operation.ADDITION,
            (random) -> (1.0D + random.nextInt(ReachBoostAbility.maxBoost.get())) / 10,
            AttributeBoost.ActiveType.HELD_OR_EQUIPPED
    );

    private static ForgeConfigSpec.IntValue maxBoost;


    public ReachBoostAbility() {}


    @AbilityConfig(abilityId = "magical_relics:reach_boost")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        maxBoost = configBuilder.comment("The maximum amount of extra reach the boost of this ability can grant (for example, a value of 3 would equal 3/10 of a block more range). When this ability is applied to an artifact, a random value between 1 and maxBoost is picked.")
                .defineInRange("maxBoost", 4, 1, 100);
    }

    @Override
    public AttributeBoost getAttributeWithBoost() {
        return REACH_BOOST;
    }

    @Override
    public String[] getPrefixes() {
        return PREFIXES;
    }

    @Override
    public String[] getSuffixes() {
        return SUFFIXES;
    }

    @Nullable
    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor, boolean isCurio) {
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
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
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        TriggerType triggerType = ArtifactUtils.getTriggerFromStack(artifact, this);

        if (triggerType == TriggerType.ARMOR_TICK) {
            return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.reach_boost.description.armor_tick");
        }
        else if (triggerType == TriggerType.HELD) {
            return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.reach_boost.description.held");
        }

        return null;
    }
}
