package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nonnull;
import java.util.List;


public class AirSneakAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("air_sneak", "floaty"),
            createPrefix("air_sneak", "light"),
            createPrefix("air_sneak", "airy")
    };

    private static final String[] SUFFIXES = {
            createSuffix("air_sneak", "hermes"),
            createSuffix("air_sneak", "flight")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.HELD, TriggerType.CURIO_TICK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.TRINKET, ArtifactCategory.FIGURINE, ArtifactCategory.STAFF, ArtifactCategory.RING, ArtifactCategory.WAND, ArtifactCategory.BOOTS
    );


    public AirSneakAbility() {
    }


    @Override
    public void onHeld(Level level, Player player, ItemStack heldArtifact, EquipmentSlot slot) {
        airSneak(heldArtifact, level, player, slot, null);
    }

    @Override
    public void onCurioTick(ItemStack artifact, Level level, Player player, SlotContext slotContext) {
        airSneak(artifact, level, player, null, slotContext);
    }

    private void airSneak(ItemStack artifact, Level level, Player player, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext) {
        BlockPos belowPos = player.blockPosition().below().immutable();

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                if (level.getBlockState(belowPos).isAir() && !level.getBlockState(belowPos).is(MRBlocks.SOLID_AIR.get())) {
                    level.setBlock(belowPos, MRBlocks.SOLID_AIR.get().defaultBlockState(), Block.UPDATE_ALL);
                    level.scheduleTick(belowPos, MRBlocks.SOLID_AIR.get(), 20);

                    if (slotContext != null) {
                        artifact.hurtAndBreak(1, player, (p) -> CuriosApi.broadcastCurioBreakEvent(slotContext));
                    }
                    else if (slot != null) {
                        artifact.hurtAndBreak(1, player, (p) -> player.broadcastBreakEvent(slot));
                    }
                }
            }
            else {
                if (level.getBlockState(belowPos).is(MRBlocks.SOLID_AIR.get()))
                    level.removeBlock(belowPos, false);
            }
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player, EquipmentSlot slot) {
        onHeld(level, player, stack, slot);
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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor, boolean isCurio) {
        if (isCurio) {
            return random.nextBoolean() ? TriggerType.HELD : TriggerType.CURIO_TICK;
        }
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
    }

    @Override
    @Nonnull
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
            default -> null;
            case HELD -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.air_sneak.description.held");
            case CURIO_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.air_sneak.description.curio");
            case ARMOR_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.air_sneak.description.armor_tick");
        };
    }
}
