package com.sarinsa.magical_relics.common.artifact;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class EmptyAbility extends BaseArtifactAbility {

    private static final String[] NO_PREFIXES = {""};
    private static final String[] NO_SUFFIXES = {""};
    private static final List<ArtifactCategory> TYPES = ImmutableList.copyOf(ArtifactCategory.values());

    public EmptyAbility() {
    }


    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return TriggerType.HELD;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return null;
    }

    @Override
    public String[] getPrefixes() {
        return NO_PREFIXES;
    }

    @Override
    public String[] getSuffixes() {
        return NO_SUFFIXES;
    }
}
