package com.sarinsa.magical_relics.common.artifact.misc;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.Supplier;

public record AttributeBoost(Supplier<Attribute> attribute, String name, UUID modifierUUID, AttributeModifier.Operation operation, double min, double max, RangedValueProvider valueProvider) {


    public AttributeBoost(Supplier<Attribute> attribute, String name, UUID modifierUUID, AttributeModifier.Operation operation, double min, double max, RangedValueProvider valueProvider) {
        this.attribute = attribute;
        this.name = name;
        this.modifierUUID = modifierUUID;
        this.operation = operation;
        this.valueProvider = valueProvider;
        this.min = min;
        this.max = max;

        if (min >= max)
            throw new IllegalStateException("Min value cannot be greater or equal to max value");

        if (min < 1)
            throw new IllegalStateException("Min value cannot be less than 1");
    }

    public interface RangedValueProvider {
        double getRangedValue(double min, double max, RandomSource random);
    }
}
