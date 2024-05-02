package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.event.MREventListener;
import com.sarinsa.magical_relics.common.item.ItemArtifact;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.commands.arguments.item.ItemArgument;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RepairOthersAbility extends BaseArtifactAbility {


    private static final String[] PREFIXES = {
            createPrefix("repair_others", "repairing"),
            createPrefix("repair_others", "recharging")
    };

    private static final String[] SUFFIXES = {
            createSuffix("repair_others", "renewal")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.HELD, TriggerType.USE
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.WAND, ArtifactCategory.BELT, ArtifactCategory.STAFF, ArtifactCategory.TRINKET, ArtifactCategory.CHESTPLATE, ArtifactCategory.HELMET
    );


    public RepairOthersAbility() {

    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            ItemStack stackToRepair = ItemStack.EMPTY;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack checkedStack = player.getInventory().getItem(i);

                if (!(checkedStack.getItem() instanceof ItemArtifact) && checkedStack.getDamageValue() > 0) {
                    stackToRepair = player.getInventory().getItem(i);
                    break;
                }
            }

            if (!stackToRepair.isEmpty()) {
                stackToRepair.hurt(-1, level.random, player instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                artifact.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                ArtifactUtils.setAbilityCooldown(artifact, this, 20);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onArmorTick(ItemStack artifact, Level level, Player player) {
        this.onHeld(level, player, artifact);
    }

    @Override
    public void onHeld(Level level, Player player, ItemStack artifact) {
        if (MREventListener.getRepairTick() % 200 == 0) {
            ItemStack stackToRepair = ItemStack.EMPTY;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack checkedStack = player.getInventory().getItem(i);

                if (!(checkedStack.getItem() instanceof ItemArtifact) && checkedStack.getDamageValue() > 0) {
                    stackToRepair = player.getInventory().getItem(i);
                    break;
                }
            }

            if (!stackToRepair.isEmpty()) {
                stackToRepair.hurt(-1, level.random, player instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                artifact.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
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
        if(isArmor) {
            return TriggerType.ARMOR_TICK;
        }
        return random.nextInt(2) == 0 ? TriggerType.USE : TriggerType.HELD;
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

        if (triggerType == null) return null;

        return switch (triggerType) {
            default -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.repair_others.description.armor_tick");
            case USE -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.repair_others.description.use");
            case HELD -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.repair_others.description.held");
        };
    }
}
