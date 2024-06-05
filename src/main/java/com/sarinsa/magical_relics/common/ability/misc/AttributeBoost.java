package com.sarinsa.magical_relics.common.ability.misc;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public record AttributeBoost(Supplier<Attribute> attribute, String name, AttributeModifier.Operation operation, RangedValueProvider valueProvider, ActiveType activeType) {

    public interface RangedValueProvider {
        double getRangedValue(RandomSource random);
    }

    public enum ActiveType {
        HELD("held"),
        EQUIPPED("equipped"),
        HELD_OR_EQUIPPED("held_or_equipped");

        ActiveType(String name) {
            this.name = name;
        }
        final String name;

        public String getName() {
            return name;
        }

        @Nullable
        public static ActiveType getFromName(String name) {
            for (ActiveType type : values()) {
                if (type.getName().equals(name))
                    return type;
            }
            return null;
        }
    }
}
