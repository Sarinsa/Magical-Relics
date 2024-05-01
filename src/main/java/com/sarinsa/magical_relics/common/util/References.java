package com.sarinsa.magical_relics.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class References {

    // TRANSLATION KEYS
    public static final MutableComponent ALTNEG_BLOCK_MESSAGE = Component.translatable("magical_relics.anti_builder.blocked_message");

    public static final MutableComponent ALTNEG_X_SIZE = Component.translatable("magical_relics.anti_builder.screen.x_size");
    public static final MutableComponent ALTNEG_Y_SIZE = Component.translatable("magical_relics.anti_builder.screen.y_size");
    public static final MutableComponent ALTNEG_Z_SIZE = Component.translatable("magical_relics.anti_builder.screen.z_size");

    public static final String ARTIFACT_CREATE_CMD = "magical_relics.command.artifact.create.message";
    public static final String ARTIFACT_CREATE_ERROR_0 = "magical_relics.command.artifact.create.error.invalid_variation";
    public static final String ABILITY_APPLY_ERROR_0 = "magical_relics.command.ability.apply.error.already_exists";
    public static final String ABILITY_APPLY_ERROR_1 = "magical_relics.command.ability.apply.error.trigger_occupied";
    public static final String ABILITY_APPLY_ERROR_2 = "magical_relics.command.ability.apply.error.invalid_item";
    public static final String ABILITY_APPLY_ERROR_3 = "magical_relics.command.ability.apply.error.unsupported_trigger";
    public static final String ABILITY_REMOVE_CMD = "magical_relics.command.ability.remove.message";
    public static final String ABILITY_REMOVE_ERROR_0 = "magical_relics.command.ability.remove.error";
    public static final String PLAYER_ONLY_CMD = "magical_relics.command.failure.player_only";
    public static final String ERROR_INVALID_CATEGORY = "magical_relics.command.argument.artifact_category.error.invalid_category";
    public static final String ERROR_INVALID_ABILITY = "magical_relics.command.argument.artifact_ability.error.invalid_ability";
    public static final String ERROR_INVALID_TRIGGER = "magical_relics.command.argument.trigger_type.error.invalid_trigger";

    public static final String MUNDANE_ABILITY_PREFIX = "magical_relics.ability.mundane_prefix";
}
