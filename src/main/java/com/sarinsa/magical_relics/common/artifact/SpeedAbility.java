package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.artifact.misc.AttributeBoost;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

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

    private static final AttributeBoost SPEED_BOOST = new AttributeBoost(
            () -> Attributes.MOVEMENT_SPEED,
            "MRSpeedBoost",
            UUID.fromString("fd3d9614-7433-4314-8ad0-83ea10c6bc0b"),
            AttributeModifier.Operation.MULTIPLY_BASE,
            (random) -> (3.0D + random.nextInt(8)) / 100
    );


    public SpeedAbility() {
        super("speed_boost");
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
    public TriggerType getTriggerType() {
        return TriggerType.HELD;
    }
}
