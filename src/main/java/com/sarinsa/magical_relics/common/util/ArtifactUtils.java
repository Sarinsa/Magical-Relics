package com.sarinsa.magical_relics.common.util;

import com.sarinsa.magical_relics.common.artifact.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.core.registry.util.ArtifactSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArtifactUtils {

    /** NBT keys for mod data storage. */
    public static final String MOD_DATA_KEY = "MagicalRelicsData";
    public static final String ABILITY_KEY = "MRArtifactAbilities";
    public static final String VARIANT_KEY = "MRArtifactVariant";
    public static final String ITEM_COLOR_KEY = "MRItemColor";
    public static final String PREFIX_KEY = "MRNamePrefix";
    public static final String SUFFIX_KEY = "MRNameSuffix";

    /** Possible overlay colors for artifact items. */
    private static final int[] ARTIFACT_COLORS = {
            0x00B6FF, 0x1466FF, 0x6647FF,
            0xC23FFF, 0xFF00A5, 0xFF0010,
            0xFF5F0F, 0xFF9D00, 0xFFE500,
            0x2FBC00, 0x00BA6F, 0x37B7AA,
            0x915E35, 0xC4746F, 0xC170BC,
            0x84BF4E, 0x6B75BC, 0xD8D8D8
    };


    /**
     * Generates an artifact with randomized abilities, type and overlay color. Neat!
     * <br><br>
     * @return The randomly generated artifact ItemStack.
     */
    public static ItemStack generateRandomArtifact(RandomSource random) {
        ArtifactSet<List<RegistryObject<Item>>> artifactSet = MRItems.ALL_ARTIFACTS.get(random.nextInt(MRItems.ALL_ARTIFACTS.size()));
        Item artifactItem = artifactSet.dataStructure().get(random.nextInt(artifactSet.dataStructure().size())).get();
        ItemStack artifactStack = new ItemStack(artifactItem);

        CompoundTag tag = artifactStack.getOrCreateTag();
        CompoundTag modDataTag = new CompoundTag();

        modDataTag.putInt(VARIANT_KEY, random.nextInt(artifactSet.variants()));
        modDataTag.putInt(ITEM_COLOR_KEY, ARTIFACT_COLORS[random.nextInt(ARTIFACT_COLORS.length)]);
        modDataTag.putString(PREFIX_KEY, "");
        modDataTag.putString(SUFFIX_KEY, "");
        tag.put(MOD_DATA_KEY, modDataTag);

        BaseArtifactAbility[] appliedAbilities = tryApplyAbilities(artifactStack, MRArtifactAbilities.BAKER.get(), MRArtifactAbilities.CASHOUT.get());

        if (appliedAbilities.length > 0) {
            modDataTag.putString(PREFIX_KEY, appliedAbilities[0].getPrefixes()[random.nextInt(appliedAbilities[0].getPrefixes().length)]);

            if (appliedAbilities.length > 1) {
                modDataTag.putString(SUFFIX_KEY, appliedAbilities[1].getSuffixes()[random.nextInt(appliedAbilities[1].getSuffixes().length)]);
            }
            else {
                modDataTag.putString(SUFFIX_KEY, appliedAbilities[0].getSuffixes()[random.nextInt(appliedAbilities[0].getSuffixes().length)]);
            }
        }
        return artifactStack;
    }

    /**
     * @return The altered display name for the given artifact item.
     */
    @Nullable
    public static Component getItemName(ItemStack itemStack) {
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (stackTag.contains(MOD_DATA_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag modDataTag = stackTag.getCompound(MOD_DATA_KEY);

            if (modDataTag.contains(PREFIX_KEY, Tag.TAG_STRING) && modDataTag.contains(SUFFIX_KEY, Tag.TAG_STRING)) {
                return Component.literal(
                        Component.translatable(modDataTag.getString(PREFIX_KEY)).getString() + " "
                                + Component.translatable(itemStack.getItem().getDescriptionId(itemStack)).getString() + " "
                                + Component.translatable(modDataTag.getString(SUFFIX_KEY)).getString()
                );
            }
        }
        return null;
    }

    public static int getVariant(ItemStack itemStack) {
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (!stackTag.contains(MOD_DATA_KEY) || !stackTag.getCompound(MOD_DATA_KEY).contains(VARIANT_KEY, Tag.TAG_INT))
            return 1;

        return stackTag.getCompound(MOD_DATA_KEY).getInt(VARIANT_KEY);
    }

    /**
     * Attempts to apply the given artifact ability to an ItemStack.
     * <br><br>
     * @param itemStack The ItemStack to put the ability on.
     * @param toApply The artifact ability instance to apply to the ItemStack.
     * <br><br>
     * @return An array containing the abilities that were successfully applied.
     */
    public static BaseArtifactAbility[] tryApplyAbilities(ItemStack itemStack, BaseArtifactAbility... toApply) {
        List<BaseArtifactAbility> allAbilities = getAllAbilities(itemStack);

        // Make sure necessary NBT tags exist on the ItemStack
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (!stackTag.contains(MOD_DATA_KEY, Tag.TAG_COMPOUND))
            stackTag.put(MOD_DATA_KEY, new CompoundTag());

        CompoundTag modDataTag = stackTag.getCompound(MOD_DATA_KEY);

        if (!modDataTag.contains(ABILITY_KEY, Tag.TAG_LIST))
            modDataTag.put(ABILITY_KEY, new ListTag());

        List<BaseArtifactAbility> successfullyApplied = new ArrayList<>();

        for (BaseArtifactAbility nextToApply : toApply) {
            // Skip if the item already has the ability
            if (allAbilities.contains(nextToApply)) continue;

            // Check if the item already has an ability with a non-stackable trigger type
            boolean goToNext = false;
            for (BaseArtifactAbility ability : allAbilities) {
                if (ability.getTriggerType() == nextToApply.getTriggerType() && !nextToApply.getTriggerType().canStack())
                    goToNext = true;
            }
            if (goToNext) continue;

            ResourceLocation abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(nextToApply);

            // Make sure the ability actually exists in the registry before applying
            if (abilityId == null) {
                MagicalRelics.LOG.warn("Attempted applying an ability with no ID to an artifact");
                MagicalRelics.LOG.warn("Problematic ability: " + nextToApply);
                continue;
            }

            // Success, probably
            modDataTag.getList(ABILITY_KEY, Tag.TAG_STRING).add(StringTag.valueOf(abilityId.toString()));
            successfullyApplied.add(nextToApply);
        }
        return successfullyApplied.toArray(new BaseArtifactAbility[0]);
    }

    /**
     * @return The first artifact ability on an ItemStack, if any, with the given TriggerType.
     */
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

    /**
     * @return A List of all artifact abilities the given ItemStack has. Will never
     *         return null, but will return an empty List if no abilities are found.
     */
    @Nonnull
    public static List<BaseArtifactAbility> getAllAbilities(ItemStack itemStack) {
        List<BaseArtifactAbility> abilities = new ArrayList<>();
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (!stackTag.contains(MOD_DATA_KEY) || !stackTag.getCompound(MOD_DATA_KEY).contains(ABILITY_KEY)) return abilities;

        ListTag abilitiesTag = stackTag.getCompound(MOD_DATA_KEY).getList(ABILITY_KEY, ListTag.TAG_STRING);

        for (int i = 0; i < abilitiesTag.size(); i++) {
            ResourceLocation abilityId = ResourceLocation.tryParse(abilitiesTag.getString(i));

            if (MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().containsKey(abilityId)) {
                abilities.add(MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getValue(abilityId));
            }
        }
        return abilities;
    }

    /**
     * @return A List containing all the artifact ability descriptions from
     *         the artifact abilities the given ItemStack has.
     *         <br><br>
     *         Primarily used for item tooltip.
     */
    @Nonnull
    public static List<Component> getDescriptions(ItemStack itemStack) {
        List<BaseArtifactAbility> abilities = ArtifactUtils.getAllAbilities(itemStack);
        List<Component> components = new ArrayList<>();

        if (!abilities.isEmpty()) {
            for (BaseArtifactAbility ability : abilities) {
                Component description = ability.getAbilityDescription();

                if (description != null)
                    components.add(description);
            }
        }
        return components;
    }
}
