package com.sarinsa.magical_relics.common.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.sarinsa.magical_relics.common.ability.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.core.registry.util.ArtifactSet;
import com.sarinsa.magical_relics.common.item.ItemArtifact;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ArtifactUtils {

    public static final Rarity COMMON_ABILITY = Rarity.create(MagicalRelics.resLoc("common_ability").toString(), ChatFormatting.GRAY);
    public static final Rarity MAGICAL = Rarity.create(MagicalRelics.resLoc("magical").toString(), ChatFormatting.GREEN);


    /** NBT keys for mod data storage. */
    public static final String MOD_DATA_KEY = "MagicalRelicsData";
    public static final String ABILITY_KEY = "MRArtifactAbilities";
    public static final String VARIANT_KEY = "MRArtifactVariant";
    public static final String ITEM_COLOR_KEY = "MRItemColor";
    public static final String ATTRIBUTE_MODS_KEY = "MRAttributeModifiers";
    public static final String ABILITY_COOLDOWNS_KEY = "MRAbilityCooldowns";
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

    public static ItemStack createBlankArtifact(Item artifactItem, int variant, RandomSource randomSource) {
        ItemStack artifactStack = new ItemStack(artifactItem);

        // Create necessary tags needed later
        CompoundTag tag = artifactStack.getOrCreateTag();
        CompoundTag modDataTag = new CompoundTag();

        modDataTag.putInt(VARIANT_KEY, variant);
        modDataTag.putInt(ITEM_COLOR_KEY, ARTIFACT_COLORS[randomSource.nextInt(ARTIFACT_COLORS.length)]);
        modDataTag.put(ABILITY_COOLDOWNS_KEY, new CompoundTag());
        modDataTag.putString(PREFIX_KEY, "");
        modDataTag.putString(SUFFIX_KEY, "");
        tag.put(MOD_DATA_KEY, modDataTag);

        return artifactStack;
    }
    /**
     * Generates an artifact with randomized abilities, type and overlay color. Neat!
     * <br><br>
     * @return The randomly generated artifact ItemStack.
     */
    public static ItemStack generateRandomArtifact(RandomSource random, boolean legendary) {
        ArtifactSet<List<RegistryObject<Item>>> artifactSet = MRItems.ALL_ARTIFACTS.get(random.nextInt(MRItems.ALL_ARTIFACTS.size()));
        Item artifactItem = artifactSet.dataStructure().get(random.nextInt(artifactSet.dataStructure().size())).get();
        ItemStack artifactStack = createBlankArtifact(artifactItem, random.nextInt(artifactSet.variants()), random);

        List<BaseArtifactAbility> allAbilities = Lists.newArrayList(MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getValues());
        // Filter out abilities that are not applicable to the Artifact's category.
        allAbilities.removeIf((ability) -> !ability.getCompatibleTypes().contains(((ItemArtifact) artifactItem).getType()));
        // Make sure we don't try to apply the empty ability
        allAbilities.remove(MRArtifactAbilities.EMPTY.get());

        BaseArtifactAbility[] abilitiesToApply;
        BaseArtifactAbility[] appliedAbilities = {};
        // We might get unlucky RNG here and there,
        // so try 10 times before giving up (10 times should be sufficient)
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(allAbilities);

            final int maxAbilities = legendary
                    ? Math.min(4, allAbilities.size())
                    : Math.min(1 + (random.nextInt(3) == 0 ? random.nextInt(3) : 0), allAbilities.size());
            abilitiesToApply = new BaseArtifactAbility[maxAbilities];

            for (int j = 0; j < maxAbilities; j++)
                abilitiesToApply[j] = allAbilities.get(j);

            appliedAbilities = tryApplyAbilities(artifactStack, random, abilitiesToApply);

            if (appliedAbilities.length > 0)
                break;
        }
        // Try to apply enchantments if this is a legendary artifact
        if (legendary) {
            EnchantmentHelper.enchantItem(random, artifactStack, 20 + random.nextInt(11), false);
        }
        // Apply some stock attribute mods for daggers and swords and whatnot
        applyMandatoryAttributeMods(artifactStack, ((ItemArtifact) artifactItem).getType(), random);

        // Create a custom display name for the ItemStack, picking random
        // prefixes and suffixes from successfully applied abilities
        if (appliedAbilities.length > 0) {
            CompoundTag tag = artifactStack.getOrCreateTag();
            CompoundTag modDataTag = tag.getCompound(MOD_DATA_KEY);

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
    public static Component getItemDisplayName(ItemStack itemStack) {
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

    /**
     * Applies "mandatory" attribute modifiers to artifacts of
     * a certain artifact category.
     */
    @SuppressWarnings("ConstantConditions")
    public static void applyMandatoryAttributeMods(ItemStack itemStack, ArtifactCategory category, RandomSource random) {
        CompoundTag modDataTag = itemStack.getOrCreateTag().getCompound(MOD_DATA_KEY);

        if (category == ArtifactCategory.SWORD || category == ArtifactCategory.DAGGER) {
            String attackDmgId = ForgeRegistries.ATTRIBUTES.getKey(Attributes.ATTACK_DAMAGE).toString();
            String attackSpeedId = ForgeRegistries.ATTRIBUTES.getKey(Attributes.ATTACK_SPEED).toString();

            // Attack damage
            CompoundTag attackDmgMod = new CompoundTag();
            attackDmgMod.putString("AttributeId", attackDmgId);
            attackDmgMod.put("AttributeMod", new AttributeModifier(
                    UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"),
                    "Weapon modifier",
                    (double) ((TieredItem) itemStack.getItem()).getTier().getAttackDamageBonus() + 3.0D + (double) (random.nextInt(3)),
                    AttributeModifier.Operation.ADDITION
            ).save());

            // Attack damage
            CompoundTag attackSpeed = new CompoundTag();
            attackSpeed.putString("AttributeId", attackSpeedId);
            attackSpeed.put("AttributeMod", new AttributeModifier(
                    UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"),
                    "Weapon modifier",
                    category == ArtifactCategory.DAGGER ? -1.8D  : -2.4D,
                    AttributeModifier.Operation.ADDITION
            ).save());

            modDataTag.getList(ATTRIBUTE_MODS_KEY, Tag.TAG_COMPOUND).add(attackDmgMod);
            modDataTag.getList(ATTRIBUTE_MODS_KEY, Tag.TAG_COMPOUND).add(attackSpeed);
        }
    }

    @Nullable
    public static Multimap<Attribute, AttributeModifier> getAttributeMods(ItemStack itemStack) {
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (stackTag.contains(MOD_DATA_KEY, Tag.TAG_COMPOUND) && stackTag.getCompound(MOD_DATA_KEY).contains(ATTRIBUTE_MODS_KEY, Tag.TAG_LIST)) {
            ListTag attributeModsTag = stackTag.getCompound(MOD_DATA_KEY).getList(ATTRIBUTE_MODS_KEY, Tag.TAG_COMPOUND);
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

            for (int i = 0; i < attributeModsTag.size(); i++) {
                CompoundTag attributeTag = attributeModsTag.getCompound(i);

                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(attributeTag.getString("AttributeId")));
                AttributeModifier modifier = AttributeModifier.load(attributeTag.getCompound("AttributeMod"));

                if (attribute != null && modifier != null)
                    builder.put(attribute, modifier);
            }
            return builder.build();
        }
        return null;
    }

    public static int getVariant(ItemStack itemStack) {
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (!stackTag.contains(MOD_DATA_KEY, Tag.TAG_COMPOUND) || !stackTag.getCompound(MOD_DATA_KEY).contains(VARIANT_KEY, Tag.TAG_INT))
            return 1;

        return stackTag.getCompound(MOD_DATA_KEY).getInt(VARIANT_KEY);
    }

    /**
     * Attempts to apply the given artifact abilities to an ItemStack.
     * <br><br>
     * @param itemStack The ItemStack to put the abilities on.
     * @param toApply The artifact ability instances to apply to the ItemStack.
     * <br><br>
     * @return An array containing the abilities that were successfully applied.
     */
    @SuppressWarnings("ConstantConditions")
    public static BaseArtifactAbility[] tryApplyAbilities(ItemStack itemStack, RandomSource random, BaseArtifactAbility... toApply) {
        if (toApply.length <= 0)
            return new BaseArtifactAbility[0];

        Map<BaseArtifactAbility, BaseArtifactAbility.TriggerType> currentAbilities = getAllAbilities(itemStack);

        // Make sure necessary NBT keys exist on the ItemStack
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (!stackTag.contains(MOD_DATA_KEY, Tag.TAG_COMPOUND))
            stackTag.put(MOD_DATA_KEY, new CompoundTag());

        CompoundTag modDataTag = stackTag.getCompound(MOD_DATA_KEY);

        if (!modDataTag.contains(ABILITY_KEY, Tag.TAG_LIST))
            modDataTag.put(ABILITY_KEY, new ListTag());

        if (!modDataTag.contains(ATTRIBUTE_MODS_KEY, Tag.TAG_LIST))
            modDataTag.put(ATTRIBUTE_MODS_KEY, new ListTag());

        List<BaseArtifactAbility> successfullyApplied = new ArrayList<>();
        List<BaseArtifactAbility.TriggerType> occupiedTriggers = new ArrayList<>();

        for (BaseArtifactAbility nextToApply : toApply) {
            // Skip if the item already has the ability
            if (currentAbilities.containsKey(nextToApply)) continue;

            // TODO - Don't forget about changing this once armor is incorporated into this whole thingamajig
            BaseArtifactAbility.TriggerType randomTrigger = nextToApply.getRandomTrigger(random, false);

            // No suitable trigger found, skip to next ability
            if (randomTrigger == null) continue;
            // Continue if we already applied an ability with this trigger type, and it is not stackable
            if (occupiedTriggers.contains(randomTrigger) && !randomTrigger.canStack()) continue;

            ResourceLocation abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(nextToApply);

            // Make sure the ability actually exists in the registry before applying
            if (abilityId == null) {
                MagicalRelics.LOG.warn("Attempted applying an ability with no ID to an artifact");
                MagicalRelics.LOG.warn("Problematic ability: " + nextToApply);
                continue;
            }

            // Success, probably
            CompoundTag abilityData = new CompoundTag();
            abilityData.putString("AbilityId", abilityId.toString());
            abilityData.putString("TriggerType", randomTrigger.getName());
            modDataTag.getList(ABILITY_KEY, Tag.TAG_COMPOUND).add(abilityData);
            nextToApply.onAbilityAttached(itemStack, random);
            successfullyApplied.add(nextToApply);
            occupiedTriggers.add(randomTrigger);

            // Save any ability attribute modifiers to NBT
            AttributeBoost boost = nextToApply.getAttributeWithBoost();

            if (boost != null) {
                String attributeId = ForgeRegistries.ATTRIBUTES.getKey(boost.attribute().get()).toString();
                CompoundTag attributeMod = new CompoundTag();

                attributeMod.putString("AttributeId", attributeId);
                attributeMod.put("AttributeMod", new AttributeModifier(
                        boost.modifierUUID(),
                        boost.name(),
                        boost.valueProvider().getRangedValue(random),
                        boost.operation()
                ).save());
                modDataTag.getList(ATTRIBUTE_MODS_KEY, Tag.TAG_COMPOUND).add(attributeMod);
            }
        }
        return successfullyApplied.toArray(new BaseArtifactAbility[0]);
    }

    /**
     * Attempts to remove the specified ability from the artifact item.
     * @return True if nothing went horribly wrong.
     */
    public static boolean removeAbility(@Nonnull ItemStack artifact, @Nonnull BaseArtifactAbility ability) {
        try {
            CompoundTag stackTag = artifact.getOrCreateTag();
            CompoundTag modDataTag = stackTag.getCompound(MOD_DATA_KEY);
            ListTag abilityListTag = modDataTag.getList(ABILITY_KEY, Tag.TAG_COMPOUND);

            String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(ability).toString();

            abilityListTag.removeIf((tag) -> ((CompoundTag) tag).getString("AbilityId").equals(abilityId));

            ListTag attributeBoostListTag = modDataTag.getList(ATTRIBUTE_MODS_KEY, Tag.TAG_COMPOUND);

            attributeBoostListTag.removeIf((tag) -> {
                CompoundTag attributeModTag = ((CompoundTag) tag).getCompound("AttributeMod");
                return attributeModTag.getString("Name").equals(ability.getAttributeWithBoost().name());
            });
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @return The TriggerType associated with the given ability, if present in the ItemStack's NBT.
     *         return null otherwise.
     */
    @Nullable
    public static BaseArtifactAbility.TriggerType getTriggerFromStack(ItemStack artifact, BaseArtifactAbility ability) {
        Map<BaseArtifactAbility, BaseArtifactAbility.TriggerType> allAbilities = getAllAbilities(artifact);

        for (BaseArtifactAbility abilityToCheck : allAbilities.keySet()) {
            if (abilityToCheck == ability)
                return allAbilities.get(abilityToCheck);
        }
        return null;
    }

    /**
     * @return All abilities on the artifact item stack with the given TriggerType
     */
    @Nullable
    public static BaseArtifactAbility getAbilityWithTrigger(BaseArtifactAbility.TriggerType type, ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;

        Map<BaseArtifactAbility, BaseArtifactAbility.TriggerType> abilities = getAllAbilities(itemStack);

        if (abilities.isEmpty()) return null;

        for (BaseArtifactAbility ability : abilities.keySet()) {
            if (abilities.get(ability) == type)
                return ability;
        }
        return null;
    }

    public static boolean hasAbility(ItemStack itemStack, BaseArtifactAbility ability) {
        Map<BaseArtifactAbility, BaseArtifactAbility.TriggerType> abilities = getAllAbilities(itemStack);
        if (abilities.isEmpty()) return false;
        return abilities.containsKey(ability);
    }

    /**
     * @return A List of all artifact abilities the given ItemStack has. Will never
     *         return null, but will return an empty List if no abilities are found.
     */
    @Nonnull
    public static Map<BaseArtifactAbility, BaseArtifactAbility.TriggerType> getAllAbilities(ItemStack itemStack) {
        Map<BaseArtifactAbility, BaseArtifactAbility.TriggerType> abilities = new HashMap<>();
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if (!stackTag.contains(MOD_DATA_KEY) || !stackTag.getCompound(MOD_DATA_KEY).contains(ABILITY_KEY)) return abilities;

        ListTag abilitiesTag = stackTag.getCompound(MOD_DATA_KEY).getList(ABILITY_KEY, ListTag.TAG_COMPOUND);

        for (int i = 0; i < abilitiesTag.size(); i++) {
            ResourceLocation abilityId = ResourceLocation.tryParse(abilitiesTag.getCompound(i).getString("AbilityId"));
            BaseArtifactAbility.TriggerType triggerType = BaseArtifactAbility.TriggerType.getFromName(abilitiesTag.getCompound(i).getString("TriggerType"));

            if (MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().containsKey(abilityId)) {
                abilities.put(MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getValue(abilityId), triggerType);
            }
        }
        return abilities;
    }

    /**
     * Adds the description of every ability on an artifact item stack to its tooltip.
     * Called from {@link com.sarinsa.magical_relics.common.item.ArtifactItem#appendHoverText(ItemStack, Level, List, TooltipFlag)} and
     * {@link com.sarinsa.magical_relics.common.item.ArtifactArmorItem#appendHoverText(ItemStack, Level, List, TooltipFlag)}
     */
    public static void addDescriptionsToTooltip(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        Map<BaseArtifactAbility, BaseArtifactAbility.TriggerType> abilities = ArtifactUtils.getAllAbilities(itemStack);
        
        if (!abilities.isEmpty()) {
            components.add(Component.literal(" "));
            
            for (BaseArtifactAbility ability : abilities.keySet()) {
                MutableComponent description = ability.getAbilityDescription(itemStack, level, flag);

                if (description != null) {
                    description.setStyle(ability.getRarity().getStyleModifier().apply(description.getStyle()));
                    components.add(description);
                }
            }
            components.add(Component.literal(" "));
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void setAbilityCooldown(ItemStack itemStack, BaseArtifactAbility ability, int cooldown) {
        CompoundTag modData = itemStack.getOrCreateTag().getCompound(MOD_DATA_KEY);

        if (modData.contains(ABILITY_COOLDOWNS_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag cooldownsTag = modData.getCompound(ABILITY_COOLDOWNS_KEY);
            String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(ability).toString();

            if (!cooldownsTag.contains(abilityId)) {
                cooldownsTag.putInt(abilityId, cooldown);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isAbilityOnCooldown(ItemStack itemStack, BaseArtifactAbility ability) {
        CompoundTag cooldownData = itemStack.getOrCreateTag().getCompound(MOD_DATA_KEY).getCompound(ABILITY_COOLDOWNS_KEY);
        String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(ability).toString();

        return cooldownData.contains(abilityId);
    }

    /**
     * Decrements all ability cooldowns on the ItemStack by the given number.
     */
    public static void tickAbilityCooldowns(Player player, int decrement) {
        for (ItemStack itemStack : player.getInventory().items) {
            CompoundTag cooldownData = itemStack.getOrCreateTag().getCompound(MOD_DATA_KEY).getCompound(ABILITY_COOLDOWNS_KEY);

            for (String key : cooldownData.getAllKeys()) {
                cooldownData.putInt(key, cooldownData.getInt(key) - decrement);
            }
            cooldownData.getAllKeys().removeIf(key -> cooldownData.getInt(key) <= 0);
        }
    }
}
