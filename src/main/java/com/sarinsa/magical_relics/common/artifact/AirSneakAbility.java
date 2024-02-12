package com.sarinsa.magical_relics.common.artifact;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

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

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.TRINKET, ArtifactCategory.FIGURINE, ArtifactCategory.STAFF, ArtifactCategory.RING, ArtifactCategory.WAND, ArtifactCategory.BOOTS
    );


    public AirSneakAbility() {
    }


    @Override
    public void onHeld(Level level, Player player, ItemStack heldArtifact) {
        BlockPos belowPos = player.blockPosition().below().immutable();

        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                if (level.getBlockState(belowPos).isAir() && !level.getBlockState(belowPos).is(MRBlocks.SOLID_AIR.get())) {
                    level.setBlock(belowPos, MRBlocks.SOLID_AIR.get().defaultBlockState(), Block.UPDATE_ALL);
                    level.scheduleTick(belowPos, MRBlocks.SOLID_AIR.get(), 20);
                }
            }
            else {
                if (level.getBlockState(belowPos).is(MRBlocks.SOLID_AIR.get()))
                    level.removeBlock(belowPos, false);
            }
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        onHeld(level, player, stack);
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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.air_sneak.description");
    }
}
