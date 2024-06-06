package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SelfRepairAbility extends BaseArtifactAbility {


    private static final String[] PREFIXES = {
            createPrefix("self_repair", "repairing"),
            createPrefix("self_repair", "recharging")
    };

    private static final String[] SUFFIXES = {
            createSuffix("self_repair", "renewal")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.HELD, TriggerType.INVENTORY_TICK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.RING,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.TRINKET,
            ArtifactCategory.LEGGINGS,
            ArtifactCategory.BOOTS,
            ArtifactCategory.DAGGER,
            ArtifactCategory.SWORD,
            ArtifactCategory.AXE
    );

    private static ForgeConfigSpec.IntValue cooldown;


    public SelfRepairAbility() {

    }


    @AbilityConfig(abilityId = "magical_relics:self_repair")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        cooldown = configBuilder.comment("How many ticks of cooldown must pass before the next time this ability can restore a point of durability")
                .defineInRange("cooldown", 120, 5, 100000);
    }

    @Override
    public void onInventoryTick(ItemStack artifact, Level level, Entity entity, int slot, boolean isSelectedItem) {
        handleRepair(artifact, level, entity);
    }

    @Override
    public void onArmorTick(ItemStack artifact, Level level, Player player, EquipmentSlot slot) {
        handleRepair(artifact, level, player);
    }

    @Override
    public void onHeld(Level level, Player player, ItemStack artifact, EquipmentSlot slot) {
        handleRepair(artifact, level, player);
    }

    private void handleRepair(ItemStack artifact, Level level, Entity entity) {
        if (artifact.getDamageValue() > 0) {
            if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
                artifact.hurt(-1, level.random, entity instanceof ServerPlayer serverPlayer ? serverPlayer : null);

                ArtifactUtils.setAbilityCooldown(artifact, this, cooldown.get());
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
        if(isArmor) {
            return random.nextInt(2) == 0 ? TriggerType.ARMOR_TICK : TriggerType.HELD;
        }
        return random.nextInt(2) == 0 ? TriggerType.INVENTORY_TICK : TriggerType.HELD;
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
    public boolean showCooldownSymbol() {
        return false;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        TriggerType triggerType = ArtifactUtils.getTriggerFromStack(artifact, this);

        if (triggerType == null) return null;

        return switch (triggerType) {
            default -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.self_repair.description.armor_tick");
            case INVENTORY_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.self_repair.description.inventory_tick");
            case HELD -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.self_repair.description.held");
        };
    }
}
