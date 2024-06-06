package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.item.ArtifactItem;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
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
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

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

    private static ForgeConfigSpec.IntValue cooldown;


    public ResurrectAbility() {

    }


    @AbilityConfig(abilityId = "magical_relics:resurrect")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        cooldown = configBuilder.comment("How many ticks of cooldown to put this ability on when it has been activated")
                .defineInRange("cooldown", 6000, 5, 100000);
    }

    @Override
    public void onDeath(Level level, Player player, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext, ItemStack artifact, LivingDeathEvent event) {
        // Don't do stuff if the event is already canceled
        if (event.isCanceled()) return;

        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            ArtifactUtils.setAbilityCooldown(artifact, this, cooldown.get());

            if (slotContext != null) {
                artifact.hurtAndBreak(artifact.getMaxDamage() / 4, player, (p) -> CuriosApi.broadcastCurioBreakEvent(slotContext));
            }
            else if (slot != null) {
                artifact.hurtAndBreak(artifact.getMaxDamage() / 4, player, (p) -> p.broadcastBreakEvent(slot));
            }

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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor, boolean isCurio) {
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
