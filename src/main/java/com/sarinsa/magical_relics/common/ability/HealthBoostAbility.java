package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.UUID;

public class HealthBoostAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("health_boost", "hardy"),
            createPrefix("health_boost", "bulky"),
            createPrefix("health_boost", "sturdy")
    };

    private static final String[] SUFFIXES = {
            createSuffix("health_boost", "toughness"),
            createSuffix("health_boost", "vitality"),
            createSuffix("health_boost", "health")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK, TriggerType.INVENTORY_TICK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.RING, ArtifactCategory.BELT, ArtifactCategory.CHESTPLATE, ArtifactCategory.LEGGINGS, ArtifactCategory.HELMET
    );

    private static final AttributeBoost HEALTH_BOOST = new AttributeBoost(
            () -> Attributes.MAX_HEALTH,
            "MRHealthBoost",
            AttributeModifier.Operation.ADDITION,
            (random) -> 1.0D + random.nextInt(HealthBoostAbility.maxBoost.get()),
            AttributeBoost.ActiveType.EQUIPPED
    );

    private static ForgeConfigSpec.IntValue maxBoost;



    public HealthBoostAbility() {
    }


    @AbilityConfig(abilityId = "magical_relics:health_boost")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        maxBoost = configBuilder.comment("The maximum amount of extra health the boost of this ability can grant. When this ability is applied to an artifact, a random value between 1 and maxBoost is picked.")
                .defineInRange("maxBoost", 3, 1, 10);
    }

    @Override
    public AttributeBoost getAttributeWithBoost() {
        return HEALTH_BOOST;
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
        return isArmor ? TriggerType.ARMOR_TICK : TriggerType.INVENTORY_TICK;
    }

    @Override
    public void onUnequipped(SlotContext slotContext, ItemStack artifact) {
        if (slotContext.entity() instanceof Player player) {
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
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
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.health_boost.description");
    }
}
