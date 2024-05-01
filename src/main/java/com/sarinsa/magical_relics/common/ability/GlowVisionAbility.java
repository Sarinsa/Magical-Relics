package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlowVisionAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("glow_vision", "revealing"),
            createPrefix("glow_vision", "glowing"),
            createPrefix("glow_vision", "seekers")
    };

    private static final String[] SUFFIXES = {
            createSuffix("glow_vision", "silhouettes"),
            createSuffix("glow_vision", "spotting"),
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.TRINKET, ArtifactCategory.STAFF, ArtifactCategory.WAND, ArtifactCategory.DAGGER, ArtifactCategory.SWORD
    );


    public GlowVisionAbility() {

    }


    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(30, 30, 30));

            if (!nearbyEntities.isEmpty()) {
                nearbyEntities.remove(player);

                for (LivingEntity livingEntity : nearbyEntities) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 180));
                }
                level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0F, 0.9F + (level.random.nextFloat() / 3));

                if (!player.isCreative()) {
                    artifact.hurtAndBreak(3, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
            ArtifactUtils.setAbilityCooldown(artifact, this, 400);
            return true;
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
        return isArmor ? null : TriggerType.USE;
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
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.glow_vision.description");
    }
}
