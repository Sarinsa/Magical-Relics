package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.item.ArtifactItem;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResurrectAbility extends BaseArtifactAbility {


    private static final String[] PREFIXES = {
            createPrefix("resurrect", "regenerative"),
            createPrefix("resurrect", "invigorating")
    };

    private static final String[] SUFFIXES = {
            createSuffix("resurrect", "resurrection"),
            createSuffix("resurrect", "lazarus")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ON_DEATH
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.HELMET, ArtifactCategory.CHESTPLATE, ArtifactCategory.BELT, ArtifactCategory.FIGURINE
    );

    public ResurrectAbility() {

    }


    @Override
    public void onDeath(Level level, Player player, EquipmentSlot slot, ItemStack artifact, LivingDeathEvent event) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            ArtifactUtils.setAbilityCooldown(artifact, this, 6000);

            artifact.hurtAndBreak(artifact.getMaxDamage() / 5, player, (p) -> p.broadcastBreakEvent(slot));

            event.setCanceled(true);
            player.setHealth(Math.min(10.0F, player.getMaxHealth()));

            if (!level.isClientSide) {
                level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
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
        return TriggerType.ON_DEATH;
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
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.resurrect.description");
    }
}
