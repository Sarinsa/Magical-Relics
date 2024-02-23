package com.sarinsa.magical_relics.common.artifact;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

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

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.TRINKET, ArtifactCategory.STAFF, ArtifactCategory.WAND, ArtifactCategory.FIGURINE, ArtifactCategory.RING, ArtifactCategory.WAND, ArtifactCategory.HELMET
    );


    public IlluminationAbility() {

    }

    @Override
    public void onArmorTick(ItemStack artifact, Level level, Player player) {
        BlockPos pos = player.blockPosition();

        boolean shouldIlluminate = level.getBrightness(LightLayer.BLOCK, pos) < 3;

        if (shouldIlluminate) {
            if (level.getFluidState(pos).isEmpty() && level.getBlockState(pos).getMaterial().isReplaceable()) {
                level.setBlock(pos, MRBlocks.ILLUMINATION_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
            }
            else {
                if (level.getFluidState(pos.above()).isEmpty() && level.getBlockState(pos.above()).getMaterial().isReplaceable()) {
                    level.setBlock(pos.above(), MRBlocks.ILLUMINATION_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    @Override
    public void onHeld(Level level, Player player, ItemStack artifact) {
        this.onArmorTick(artifact, level, player);
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
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.HELD;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        if (ArtifactUtils.getTriggerFromStack(artifact, this) == TriggerType.ARMOR_TICK) {
            return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.armor_tick");
        }
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.illumination.description.held");
    }
}
