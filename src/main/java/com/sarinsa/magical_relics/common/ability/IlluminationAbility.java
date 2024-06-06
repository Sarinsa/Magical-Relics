package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class IlluminationAbility extends BaseArtifactAbility {


    private static final String[] PREFIXES = {
            createPrefix("illumination", "illuminating"),
            createPrefix("illumination", "bright"),
            createPrefix("illumination", "shining")
    };

    private static final String[] SUFFIXES = {
            createSuffix("illumination", "light"),
            createSuffix("illumination", "sun")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.HELD, TriggerType.CURIO_TICK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.TRINKET, ArtifactCategory.STAFF, ArtifactCategory.WAND, ArtifactCategory.FIGURINE, ArtifactCategory.RING, ArtifactCategory.WAND, ArtifactCategory.HELMET
    );


    public IlluminationAbility() {

    }

    @Override
    public void onArmorTick(ItemStack artifact, Level level, Player player, EquipmentSlot slot) {
        if (!level.isClientSide) {
            BlockPos pos = player.blockPosition();

            boolean shouldIlluminate = level.getBrightness(LightLayer.BLOCK, pos) < 3;

            if (shouldIlluminate) {
                if (isPosForIllumination(level, pos)) {
                    level.setBlock(pos, MRBlocks.ILLUMINATION_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                }
                else {
                    if (isPosForIllumination(level, pos.above()) && !level.getBlockState(pos).is(MRBlocks.ILLUMINATION_BLOCK.get())) {
                        level.setBlock(pos.above(), MRBlocks.ILLUMINATION_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private boolean isPosForIllumination(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getFluidState().isEmpty() && state.is(BlockTags.REPLACEABLE) && !state.is(MRBlocks.ILLUMINATION_BLOCK.get());
    }

    @Override
    public void onCurioTick(ItemStack artifact, Level level, Player player, SlotContext slotContext) {
        onArmorTick(artifact, level, player, null);
    }

    @Override
    public void onHeld(Level level, Player player, ItemStack artifact, EquipmentSlot slot) {
        onArmorTick(artifact, level, player, slot);
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
        if (isCurio) return TriggerType.CURIO_TICK;

        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
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
            default -> null;
            case ARMOR_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.armor_tick");
            case HELD -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.held");
            case CURIO_TICK -> Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.curio");
        };
    }
}
