package com.sarinsa.magical_relics.common.ability.misc;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.Supplier;

public record AttributeBoost(Supplier<Attribute> attribute, String name, UUID modifierUUID, AttributeModifier.Operation operation, RangedValueProvider valueProvider) {


    public AttributeBoost(Supplier<Attribute> attribute, String name, UUID modifierUUID, AttributeModifier.Operation operation, RangedValueProvider valueProvider) {
        this.attribute = attribute;
        this.name = name;
        this.modifierUUID = modifierUUID;
        this.operation = operation;
        this.valueProvider = valueProvider;
    }

    public interface RangedValueProvider {
        double getRangedValue(RandomSource random);
    }
}
