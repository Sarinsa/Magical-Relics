package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StunAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("stun", "slowing"),
            createPrefix("stun", "immobilizing")
    };

    private static final String[] SUFFIXES = {
            createSuffix("stun", "trapping"),
            createSuffix("stun", "halting"),
    };

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.FIGURINE,
            ArtifactCategory.TRINKET
    );

    private static final int EFFECT_DURATION = 100;


    public StunAbility() {

    }


    @Override
    public boolean onDropped(Level level, ItemEntity itemEntity, Player player) {
        if (!ArtifactUtils.isAbilityOnCooldown(itemEntity.getItem(), this)) {
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20.0D, 10.0D, 20.0F));
            nearbyEntities.remove(player);

            for (LivingEntity entity : nearbyEntities) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION, 1));
            }
            ArtifactUtils.setAbilityCooldown(itemEntity.getItem(), this, 500);
        }
        return false;
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
        return isArmor ? null : TriggerType.DROPPED;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.stun.description", EFFECT_DURATION / 20);
    }
}
