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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArtifactUtils {

    public static final String MOD_DATA_KEY = "MagicalRelicsData";
    public static final String ABILITY_KEY = "MRArtifactAbilities";
    public static final String VARIANT_KEY = "MRArtifactVariant";
    public static final String ITEM_COLOR = "MRItemColor";

    /** Possible overlay colors for artifact items. */
    private static final int[] ARTIFACT_COLORS = {
            0x00B6FF,
            0x1466FF,
            0x6647FF,
            0xC23FFF,
            0xFF00A5,
            0xFF0010,
            0xFF5F0F,
            0xFF9D00,
            0xFFE500,
            0x2FBC00,
            0x00BA6F,
            0x37B7AA,
            0x915E35,
            0xC4746F,
            0xC170BC,
            0x84BF4E,
            0x6B75BC,
            0xD8D8D8
    };


    public static ItemStack generateRandomArtifact(RandomSource random) {
        ArtifactSet<List<RegistryObject<Item>>> artifactSet = MRItems.ALL_ARTIFACTS.get(random.nextInt(MRItems.ALL_ARTIFACTS.size()));
        Item artifactItem = artifactSet.dataStructure().get(random.nextInt(artifactSet.dataStructure().size())).get();
        ItemStack artifactStack = new ItemStack(artifactItem);

        CompoundTag tag = artifactStack.getOrCreateTag();
        CompoundTag modDataTag = new CompoundTag();

        modDataTag.putInt(VARIANT_KEY, random.nextInt(artifactSet.variants()));
        modDataTag.putInt(ITEM_COLOR, ARTIFACT_COLORS[random.nextInt(ARTIFACT_COLORS.length)]);
        tag.put(MOD_DATA_KEY, modDataTag);

        tryApplyAbilities(artifactStack, MRArtifactAbilities.BAKER.get());

        return artifactStack;
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
     */
    public static void tryApplyAbilities(ItemStack itemStack, BaseArtifactAbility... toApply) {
        List<BaseArtifactAbility> allAbilities = getAllAbilities(itemStack);

        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (!stackTag.contains(MOD_DATA_KEY, Tag.TAG_COMPOUND))
            stackTag.put(MOD_DATA_KEY, new CompoundTag());

        CompoundTag modDataTag = stackTag.getCompound(MOD_DATA_KEY);

        if (!modDataTag.contains(ABILITY_KEY, Tag.TAG_LIST))
            modDataTag.put(ABILITY_KEY, new ListTag());


        for (BaseArtifactAbility nextToApply : toApply) {
            // Abort if the item already has the ability
            if (allAbilities.contains(nextToApply)) continue;

            // Check if the item already has an ability with a non-stackable trigger type
            for (BaseArtifactAbility ability : allAbilities) {
                if (ability.getTriggerType() == nextToApply.getTriggerType() && !nextToApply.getTriggerType().canStack())
                    return;
            }
            ResourceLocation abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(nextToApply);

            // Make sure the ability actually exists in the registry before applying
            if (abilityId == null) {
                MagicalRelics.LOG.warn("Attempted applying an ability with no ID to an artifact");
                MagicalRelics.LOG.warn("Problematic ability: " + nextToApply);
                continue;
            }

            // Success, probably
            modDataTag.getCompound(MOD_DATA_KEY).getList(ABILITY_KEY, Tag.TAG_STRING).add(StringTag.valueOf(abilityId.toString()));
        }
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
