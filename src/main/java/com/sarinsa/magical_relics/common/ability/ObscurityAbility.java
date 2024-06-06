package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
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
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObscurityAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("obscurity", "obscure"),
            createPrefix("obscurity", "hidden")
    };

    private static final String[] SUFFIXES = {
            createSuffix("obscurity", "obscurity"),
            createSuffix("obscurity", "cloaking"),
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USER_DAMAGED, TriggerType.USER_ATTACKING, TriggerType.USE
    );

    private static final int EFFECT_DURATION = 610;

    private static ForgeConfigSpec.IntValue cooldown;


    public ObscurityAbility() {

    }


    @AbilityConfig(abilityId = "magical_relics:obscurity")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        cooldown = configBuilder.comment("How many ticks of cooldown to put this ability on when it has been used")
                .defineInRange("cooldown", EFFECT_DURATION, 5, 100000);
    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            artifact.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

            if (!player.level().isClientSide)
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION));

            ArtifactUtils.setAbilityCooldown(artifact, this, cooldown.get());
            return true;
        }
        return false;
    }

    @Override
    public void onUserDamaged(Level level, Player player, DamageSource damageSource, ItemStack artifact) {
        artifact.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));
        if (!player.level().isClientSide)
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION));
    }

    @Override
    public void onDamageMob(ItemStack artifact, Player player, LivingEntity attackedMob) {
        artifact.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));
        if (!player.level().isClientSide)
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION));
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
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Nullable
    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor, boolean isCurio) {
        if (isArmor)
            return TriggerType.USER_DAMAGED;

        return random.nextInt(2) == 0
                ? TriggerType.USE
                : TriggerType.USER_ATTACKING;
    }

    @NotNull
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return ArtifactCategory.ALL;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        TriggerType type = ArtifactUtils.getTriggerFromStack(artifact, this);

        if (type == null) return null;

        return switch (type) {
            default -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.obscurity.description.use", EFFECT_DURATION / 20);
            case USER_DAMAGED -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.obscurity.description.user_damaged", EFFECT_DURATION / 20);
            case USER_ATTACKING -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.obscurity.description.user_attacking", EFFECT_DURATION / 20);
        };
    }
}
