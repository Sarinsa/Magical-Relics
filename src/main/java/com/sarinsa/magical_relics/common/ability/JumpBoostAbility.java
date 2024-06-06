package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class JumpBoostAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("jump_boost", "jumpy"),
            createPrefix("jump_boost", "bouncing")
    };

    private static final String[] SUFFIXES = {
            createSuffix("jump_boost", "leaping"),
            createSuffix("jump_boost", "lightness")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.USE, TriggerType.USER_ATTACKING, TriggerType.INVENTORY_TICK, TriggerType.CURIO_TICK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.RING, ArtifactCategory.BELT, ArtifactCategory.LEGGINGS, ArtifactCategory.STAFF, ArtifactCategory.TRINKET
    );

    private static final int USE_EFFECT_DURATION = 900;
    private static final int ATTACK_EFFECT_DURATION = 125;
    private static final int PASSIVE_EFFECT_DURATION = 310;

    private static ForgeConfigSpec.IntValue maxAmplifier;
    private static ForgeConfigSpec.IntValue cooldown;


    public JumpBoostAbility() {

    }


    @AbilityConfig(abilityId = "magical_relics:jump_boost")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        maxAmplifier = configBuilder.comment("The maximum possible potion amplifier that can be applied to this ability.")
                .defineInRange("maxAmplifier", 2, 0, 10);

        cooldown = configBuilder.comment("How many ticks of cooldown to put this ability on when it has been used")
                .defineInRange("cooldown", USE_EFFECT_DURATION, 20, 100000);
    }

    @Override
    public void onAbilityAttached(ItemStack artifact, RandomSource randomSource) {
        CompoundTag modDataTag = artifact.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);
        int multiplier = randomSource.nextInt(maxAmplifier.get() + 1);

        CompoundTag abilityDataTag = new CompoundTag();
        abilityDataTag.putInt("EffectMultiplier", multiplier);

        modDataTag.put("JumpBoostAbilityData", abilityDataTag);
    }

    private int getEffectMultiplier(ItemStack artifact) {
        CompoundTag modDataTag = artifact.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);
        CompoundTag abilityDataTag = modDataTag.getCompound("JumpBoostAbilityData");

        if (abilityDataTag.contains("EffectMultiplier", Tag.TAG_INT)) {
            return Math.max(0, abilityDataTag.getInt("EffectMultiplier"));
        }
        return 0;
    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            artifact.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

            if (!player.level().isClientSide)
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, USE_EFFECT_DURATION, getEffectMultiplier(artifact)));

            ArtifactUtils.setAbilityCooldown(artifact, this, cooldown.get());
            return true;
        }
        return false;
    }

    @Override
    public void onDamageMob(ItemStack artifact, Player player, LivingEntity attackedMob) {
        if (!player.level().isClientSide)
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, ATTACK_EFFECT_DURATION, getEffectMultiplier(artifact)));
    }

    @Override
    public void onInventoryTick(ItemStack artifact, Level level, Entity entity, int slot, boolean isSelectedItem) {
        if (!level.isClientSide) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.JUMP, PASSIVE_EFFECT_DURATION, getEffectMultiplier(artifact)));
            }
        }
    }

    @Override
    public void onCurioTick(ItemStack artifact, Level level, Player player, SlotContext slotContext) {
        onInventoryTick(artifact, level, player, 0, false);
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

    @Nullable
    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor, boolean isCurio) {
        if (isArmor) return TriggerType.ARMOR_TICK;

        if (isCurio) return TriggerType.CURIO_TICK;

        return switch (random.nextInt(3)) {
            default -> TriggerType.USE;
            case 1 -> TriggerType.USER_ATTACKING;
            case 2 -> TriggerType.INVENTORY_TICK;
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
        TriggerType triggerType = ArtifactUtils.getTriggerFromStack(artifact, this);
        Component potionLevel = Component.translatable("enchantment.level." + (getEffectMultiplier(artifact) + 1));

        if (triggerType == null) return null;

        return switch (triggerType) {
            default -> null;
            case USE -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.jump_boost.description.use", USE_EFFECT_DURATION / 20, potionLevel.getString());
            case USER_ATTACKING -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.jump_boost.description.user_attacking", ATTACK_EFFECT_DURATION / 20, potionLevel.getString());
            case INVENTORY_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.jump_boost.description.inventory_tick", PASSIVE_EFFECT_DURATION / 20, potionLevel.getString());
            case CURIO_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.jump_boost.description.curio", USE_EFFECT_DURATION / 20, potionLevel.getString());
        };
    }
}
