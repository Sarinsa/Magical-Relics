package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaterBreathingAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("water_breathing", "aerated"),
            createPrefix("water_breathing", "breathy"),
            createPrefix("water_breathing", "oxygenated")
    };

    private static final String[] SUFFIXES = {
            createSuffix("water_breathing", "breathing"),
            createSuffix("water_breathing", "fresh_air"),
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.USER_DAMAGED, TriggerType.USE, TriggerType.HELD, TriggerType.USER_ATTACKING
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.RING,
            ArtifactCategory.STAFF,
            ArtifactCategory.BELT,
            ArtifactCategory.DAGGER,
            ArtifactCategory.HELMET
    );

    private static final int USE_EFFECT_DURATION = 1210;
    private static final int DROWN_EFFECT_DURATION = 50;
    private static final int ATTACK_EFFECT_DURATION = 125;
    private static final int PASSIVE_EFFECT_DURATION = 310;



    public WaterBreathingAbility() {}


    @Override
    public void onDamageMob(ItemStack artifact, Player player, LivingEntity attackedMob) {
        if (!player.level.isClientSide)
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, ATTACK_EFFECT_DURATION));
    }

    @Override
    public void onUserDamaged(Level level, Player player, DamageSource damageSource, ItemStack artifact) {
        if (damageSource == DamageSource.DROWN) {
            if (!player.level.isClientSide)
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, DROWN_EFFECT_DURATION));
        }
    }

    @Override
    public void onArmorTick(ItemStack artifact, Level level, Player player) {
        this.onHeld(level, player, artifact);
    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            artifact.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));

            if (!player.level.isClientSide)
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, USE_EFFECT_DURATION));

            ArtifactUtils.setAbilityCooldown(artifact, this, 400);
            return true;
        }
        return false;
    }

    @Override
    public void onHeld(Level level, Player player, ItemStack artifact) {
        if (!player.level.isClientSide)
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, PASSIVE_EFFECT_DURATION));
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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        if (isArmor)
            return random.nextInt(2) == 0 ? TriggerType.ARMOR_TICK : TriggerType.USER_DAMAGED;

        return switch (random.nextInt(3)) {
            default -> TriggerType.USE;
            case 1 -> TriggerType.HELD;
            case 2 -> TriggerType.USER_ATTACKING;
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
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        TriggerType type = ArtifactUtils.getTriggerFromStack(artifact, this);

        if (type == null) return null;

        return switch (type) {
            default -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.water_breathing.description.held", PASSIVE_EFFECT_DURATION / 20);
            case USER_DAMAGED -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.water_breathing.description.user_damaged", DROWN_EFFECT_DURATION / 20);
            case ARMOR_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.water_breathing.description.armor_tick", PASSIVE_EFFECT_DURATION / 20);
            case USE -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.water_breathing.description.use", (USE_EFFECT_DURATION / 20) / 60);
            case USER_ATTACKING -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.water_breathing.description.user_attacking", ATTACK_EFFECT_DURATION / 20);
        };
    }
}
