package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpiderAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("spider", "arachnid"),
            createPrefix("spider", "climber")
    };

    private static final String[] SUFFIXES = {
            createSuffix("spider", "bouldering"),
            createSuffix("spider", "scrabbling")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.LEGGINGS,
            ArtifactCategory.BOOTS
    );



    public SpiderAbility() {

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
        return isArmor ? TriggerType.ARMOR_TICK : null;
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
    public MutableComponent getAbilityDescription(TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        if (type == null) return null;

        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.spider.description.armor_tick");
    }
}
