package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class HealthBoostAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("health_boost", "hardy"),
            createPrefix("health_boost", "bulky"),
            createPrefix("health_boost", "sturdy")
    };

    private static final String[] SUFFIXES = {
            createSuffix("health_boost", "toughness"),
            createSuffix("health_boost", "vitality"),
            createSuffix("health_boost", "health")
    };

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.RING, ArtifactCategory.BELT, ArtifactCategory.CHESTPLATE, ArtifactCategory.LEGGINGS, ArtifactCategory.HELMET
    );

    private static final AttributeBoost HEALTH_BOOST = new AttributeBoost(
            () -> Attributes.MAX_HEALTH,
            "MRHealthBoost",
            UUID.fromString("e3182201-176b-4f62-837d-694251c8f97d"),
            AttributeModifier.Operation.ADDITION,
            (random) -> 1.0D + random.nextInt(3)
    );



    public HealthBoostAbility() {
    }


    @Override
    public AttributeBoost getAttributeWithBoost() {
        return HEALTH_BOOST;
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
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.INVENTORY_TICK;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.health_boost.description");
    }
}
