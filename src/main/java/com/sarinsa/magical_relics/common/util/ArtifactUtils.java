package com.sarinsa.magical_relics.common.util;

import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArtifactUtils {

    private static final String ABILITY_KEY = "MRArtifactAbilities";


    public static ItemStack generateRandomArtifact(RandomSource random) {
        ItemStack artifactStack = new ItemStack(Items.WOODEN_PICKAXE);

        tryApplyAbility(artifactStack, MRArtifactAbilities.BAKER.get());

        return artifactStack;
    }

    /**
     * Attempts to apply the given artifact ability to an ItemStack.
     * <br><br>
     * @param itemStack The ItemStack to put the ability on.
     * @param toApply The artifact ability instance to apply to the ItemStack.
     * <br><br>
     * @return True if the ability was applied successfully, false if not.
     */
    public static boolean tryApplyAbility(ItemStack itemStack, BaseArtifactAbility toApply) {
        Map<Enchantment, Integer> enchantments = itemStack.getAllEnchantments();

        // Check for incompatible enchantments
        if (!toApply.incompatibleEnchantments().isEmpty()) {
            for (Supplier<Enchantment> illegalEnchantments : toApply.incompatibleEnchantments()) {
                if (enchantments.containsKey(illegalEnchantments.get()))
                    return false;
            }
        }
        List<BaseArtifactAbility> allAbilities = getAllAbilities(itemStack);

        // Abort if the item already has the ability
        if (allAbilities.contains(toApply)) return false;

        // Check if the item already has an ability with a non-stackable trigger type
        for (BaseArtifactAbility ability : allAbilities) {
            if (ability.getTriggerType() == toApply.getTriggerType() && !toApply.getTriggerType().canStack())
                return false;
        }
        CompoundTag compoundTag = itemStack.getOrCreateTag();

        if (!compoundTag.contains(ABILITY_KEY, Tag.TAG_LIST))
            compoundTag.put(ABILITY_KEY, new ListTag());

        ResourceLocation abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(toApply);

        // Make sure the ability actually exists in the registry before applying
        if (abilityId == null) {
            MagicalRelics.LOG.warn("Attempted applying an ability with no ID to an artifact");
            MagicalRelics.LOG.warn("Problematic ability: " + toApply);
            return false;
        }

        // Success, probably
        compoundTag.getList(ABILITY_KEY, Tag.TAG_STRING).add(StringTag.valueOf(abilityId.toString()));
        return true;
    }

    @Nullable
    public static BaseArtifactAbility getFirstAbility(BaseArtifactAbility.TriggerType type, ItemStack itemStack) {
        List<BaseArtifactAbility> abilities = getAllAbilities(itemStack);

        if (abilities.isEmpty()) return null;

        for (BaseArtifactAbility ability : abilities) {
            if (ability.getTriggerType() == type)
                return ability;
        }
        return null;
    }

    @Nonnull
    public static List<BaseArtifactAbility> getAllAbilities(ItemStack itemStack) {
        List<BaseArtifactAbility> abilities = new ArrayList<>();
        CompoundTag compoundTag = itemStack.getOrCreateTag();

        if (!compoundTag.contains(ABILITY_KEY, Tag.TAG_LIST)) return abilities;

        ListTag abilitiesTag = compoundTag.getList(ABILITY_KEY, ListTag.TAG_STRING);

        for (int i = 0; i < abilitiesTag.size(); i++) {
            ResourceLocation abilityId = ResourceLocation.tryParse(abilitiesTag.getString(i));

            if (MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().containsKey(abilityId)) {
                abilities.add(MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getValue(abilityId));
            }
        }
        return abilities;
    }
}
