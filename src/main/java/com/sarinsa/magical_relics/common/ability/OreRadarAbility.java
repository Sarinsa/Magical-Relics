package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class OreRadarAbility extends BaseArtifactAbility {

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(ArtifactCategory.HELMET);
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(TriggerType.ARMOR_TICK);


    public OreRadarAbility(String abilityName) {
    }


    @Override
    public String[] getPrefixes() {
        return new String[0];
    }

    @Override
    public String[] getSuffixes() {
        return new String[0];
    }


    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return isArmor ? TriggerType.ARMOR_TICK : null;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.ore_radar.description");
    }
}
