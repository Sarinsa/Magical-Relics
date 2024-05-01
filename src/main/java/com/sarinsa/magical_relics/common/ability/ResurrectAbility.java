package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResurrectAbility extends BaseArtifactAbility {


    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ON_DEATH
    );

    public ResurrectAbility() {

    }


    @Override
    public String[] getPrefixes() {
        return new String[0];
    }

    @Override
    public String[] getSuffixes() {
        return new String[0];
    }

    @Nullable
    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return null;
    }

    @NotNull
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return null;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return null;
    }
}
