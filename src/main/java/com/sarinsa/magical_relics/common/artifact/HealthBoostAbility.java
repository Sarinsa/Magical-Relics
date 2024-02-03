package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.artifact.misc.AttributeBoost;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

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

    private static final AttributeBoost HEALTH_BOOST = new AttributeBoost(
            () -> Attributes.MAX_HEALTH,
            "MRHealthBoost",
            UUID.fromString("e3182201-176b-4f62-837d-694251c8f97d"),
            AttributeModifier.Operation.ADDITION,
            2.0D,
            7.0D,
            (min, max, random) -> min + random.nextInt((int) (max + 1) - (int) min)
    );



    public HealthBoostAbility() {
        super("health_boost");
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
    public TriggerType getTriggerType() {
        return TriggerType.PASSIVE;
    }
}
