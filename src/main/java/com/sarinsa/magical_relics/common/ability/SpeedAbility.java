package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


public class SpeedAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("speed_boost", "fast"),
            createPrefix("speed_boost", "quick")
    };

    private static final String[] SUFFIXES = {
            createSuffix("speed_boost", "swiftness"),
            createSuffix("speed_boost", "mobility"),
            createSuffix("speed_boost", "speed"),
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
            ArtifactCategory.BOOTS,
            ArtifactCategory.DAGGER,
            ArtifactCategory.SWORD,
            ArtifactCategory.LEGGINGS
    );

    private static final AttributeBoost SPEED_BOOST = new AttributeBoost(
            () -> Attributes.MOVEMENT_SPEED,
            "MRSpeedBoost",
            UUID.fromString("fd3d9614-7433-4314-8ad0-83ea10c6bc0b"),
            AttributeModifier.Operation.MULTIPLY_BASE,
            (random) -> (4.0D + random.nextInt(12)) / 100
    );


    public SpeedAbility() {
    }


    @Override
    public AttributeBoost getAttributeWithBoost() {
        return SPEED_BOOST;
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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
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

        if (triggerType == TriggerType.ARMOR_TICK)
            return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.speed_boost.description.armor_tick");
        else if (triggerType == TriggerType.HELD)
            return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.speed_boost.description.held");

        return null;
    }
}
