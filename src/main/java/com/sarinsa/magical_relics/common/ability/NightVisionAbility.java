package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class NightVisionAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("night_vision", "sensing"),
            createPrefix("night_vision", "sighted")
    };

    private static final String[] SUFFIXES = {
            createSuffix("night_vision", "seeing"),
            createSuffix("night_vision", "night_vision")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.INVENTORY_TICK, TriggerType.USE, TriggerType.HELD
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.STAFF,
            ArtifactCategory.RING,
            ArtifactCategory.WAND,
            ArtifactCategory.BELT,
            ArtifactCategory.CHESTPLATE,
            ArtifactCategory.HELMET
    );

    private static final int USE_EFFECT_DURATION = 2400;
    private static final int PASSIVE_EFFECT_DURATION = 310;


    public NightVisionAbility() {
    }


    @Override
    public boolean onUse(Level level, Player player, ItemStack itemStack) {
        if (!ArtifactUtils.isAbilityOnCooldown(itemStack, this)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, USE_EFFECT_DURATION));

            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

            ArtifactUtils.setAbilityCooldown(itemStack, this, USE_EFFECT_DURATION);
            return true;
        }
        return false;
    }

    @Override
    public void onHeld(Level level, Player player, ItemStack artifact) {
        if (!player.level.isClientSide)
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, PASSIVE_EFFECT_DURATION));
    }

    @Override
    public void onInventoryTick(ItemStack itemStack, Level level, Entity entity, int slot, boolean isSelectedItem) {
        if (!level.isClientSide && entity instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, PASSIVE_EFFECT_DURATION));
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, PASSIVE_EFFECT_DURATION));
        }
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
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
        if (isArmor) return TriggerType.ARMOR_TICK;

        return switch (random.nextInt(3)) {
            default -> TriggerType.USE;
            case 1 -> TriggerType.INVENTORY_TICK;
            case 2 -> TriggerType.HELD;
        };
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
        TriggerType type = ArtifactUtils.getTriggerFromStack(artifact, this);

        if (type == null) return null;

        return switch (type) {
            default -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.night_vision.description.inventory_tick");
            case ARMOR_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.night_vision.description.armor_tick");
            case USE -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.night_vision.description.use", (USE_EFFECT_DURATION / 20) / 60);
            case HELD -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.night_vision.description.held");
        };
    }
}
