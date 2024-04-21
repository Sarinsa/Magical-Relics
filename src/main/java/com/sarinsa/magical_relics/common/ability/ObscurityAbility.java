package com.sarinsa.magical_relics.common.ability;

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

    private static final int EFFECT_DURATION = 610;


    public ObscurityAbility() {

    }


    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            artifact.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));

            if (!player.level.isClientSide)
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION));

            ArtifactUtils.setAbilityCooldown(artifact, this, 400);
            return true;
        }
        return false;
    }

    @Override
    public void onUserDamaged(Level level, Player player, DamageSource damageSource, ItemStack artifact) {
        artifact.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));
        if (!player.level.isClientSide)
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION));
    }

    @Override
    public void onDamageMob(ItemStack artifact, Player player, LivingEntity attackedMob) {
        artifact.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));
        if (!player.level.isClientSide)
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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        if (isArmor)
            return TriggerType.USER_DAMAGED;

        return random.nextInt(2) == 0
                ? TriggerType.USE
                : TriggerType.USER_ATTACKING;
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
