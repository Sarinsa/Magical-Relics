package com.sarinsa.magical_relics.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sarinsa.magical_relics.common.ability.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.command.argument.AbilityArgument;
import com.sarinsa.magical_relics.common.command.argument.ArtifactCategoryArgument;
import com.sarinsa.magical_relics.common.command.argument.TriggerTypeArgument;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.item.ArtifactArmorItem;
import com.sarinsa.magical_relics.common.item.ArtifactItem;
import com.sarinsa.magical_relics.common.item.ItemArtifact;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.References;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MRBaseCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("magicalrelics")
                .then(ArtifactBaseCommand.register())
                .then(AbilityBaseCommand.register()));
    }


    /**
     * Base command for applying artifact abilities to an artifact.
     */
    private static class AbilityBaseCommand {

        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("ability")
                    .requires((source) -> source.hasPermission(3))
                    .then(cmdApply())
                    .then(cmdRemove());
        }

        private static ArgumentBuilder<CommandSourceStack, ?> cmdApply() {
            return Commands.literal("apply")
                    .then(Commands.argument("ability", AbilityArgument.ability())
                            .then(Commands.argument("trigger_type", TriggerTypeArgument.triggerType())
                                    .executes((context) -> applyAbility(context.getSource(), AbilityArgument.getAbility(context, "ability"), TriggerTypeArgument.getTriggerType(context, "trigger_type")))));
        }

        private static ArgumentBuilder<CommandSourceStack, ?> cmdRemove() {
            return Commands.literal("remove")
                    .then(Commands.argument("ability", AbilityArgument.ability())
                            .executes((context) -> removeAbility(context.getSource(), AbilityArgument.getAbility(context, "ability"))));
        }

        //TODO - Instead of having 2 ugly blocks of code in two different classes that almost does the same thing,
        //       lets consider making a more generalized method in ArtifactUtils that handles both random AND manual
        //       artifact creation, cause we all know I will forget and everything will explode at some point.
        private static int applyAbility(CommandSourceStack source, BaseArtifactAbility ability, TriggerType triggerType) {
            if (source.getPlayer() == null) {
                source.sendFailure(Component.translatable(References.PLAYER_ONLY_CMD));
                return 0;
            }
            if (!ability.supportedTriggers().contains(triggerType)) {
                source.sendFailure(Component.translatable(References.ABILITY_APPLY_ERROR_3));
                return 0;
            }
            ServerPlayer player = source.getPlayer();
            RandomSource random = source.getLevel().getRandom();
            ItemStack itemStack = player.getItemBySlot(EquipmentSlot.MAINHAND);

            if (!(itemStack.getItem() instanceof ItemArtifact)) {
                source.sendFailure(Component.translatable(References.ABILITY_APPLY_ERROR_2));
                return 0;
            }
            Map<BaseArtifactAbility, TriggerType> currentAbilities = ArtifactUtils.getAllAbilities(itemStack);

            // Make sure necessary NBT keys exist on the ItemStack
            CompoundTag stackTag = itemStack.getOrCreateTag();

            if (!stackTag.contains(ArtifactUtils.MOD_DATA_KEY, Tag.TAG_COMPOUND))
                stackTag.put(ArtifactUtils.MOD_DATA_KEY, new CompoundTag());

            CompoundTag modDataTag = stackTag.getCompound(ArtifactUtils.MOD_DATA_KEY);

            if (!modDataTag.contains(ArtifactUtils.ABILITY_KEY, Tag.TAG_LIST))
                modDataTag.put(ArtifactUtils.ABILITY_KEY, new ListTag());

            if (!modDataTag.contains(ArtifactUtils.ATTRIBUTE_MODS_KEY, Tag.TAG_LIST))
                modDataTag.put(ArtifactUtils.ATTRIBUTE_MODS_KEY, new ListTag());

            if (currentAbilities.containsKey(ability)) {
                source.sendFailure(Component.translatable(References.ABILITY_APPLY_ERROR_0));
                return 0;
            }
            if (currentAbilities.containsValue(triggerType) && !triggerType.canStack()) {
                source.sendFailure(Component.translatable(References.ABILITY_APPLY_ERROR_1));
                return 0;
            }
            String id = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(ability).toString();

            // Success, probably
            CompoundTag abilityData = new CompoundTag();
            abilityData.putString("AbilityId", id);
            abilityData.putString("TriggerType", triggerType.getName());
            modDataTag.getList(ArtifactUtils.ABILITY_KEY, Tag.TAG_COMPOUND).add(abilityData);
            ability.onAbilityAttached(itemStack, random);

            // Save any ability attribute modifiers to NBT
            AttributeBoost boost = ability.getAttributeWithBoost();

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
                modDataTag.getList(ArtifactUtils.ATTRIBUTE_MODS_KEY, Tag.TAG_COMPOUND).add(attributeMod);
            }
            List<BaseArtifactAbility> allAbilities = new ArrayList<>(currentAbilities.keySet());
            allAbilities.add(ability);

            BaseArtifactAbility firstAbility = allAbilities.get(random.nextInt(allAbilities.size()));
            BaseArtifactAbility secondAbility = allAbilities.get(random.nextInt(allAbilities.size()));

            modDataTag.putString(ArtifactUtils.PREFIX_KEY, firstAbility.getPrefixes()[random.nextInt(firstAbility.getPrefixes().length)]);
            modDataTag.putString(ArtifactUtils.SUFFIX_KEY, secondAbility.getSuffixes()[random.nextInt(secondAbility.getSuffixes().length)]);
            return 1;
        }

        private static int removeAbility(CommandSourceStack source, BaseArtifactAbility ability) {
            if (source.getPlayer() == null) {
                source.sendFailure(Component.translatable(References.PLAYER_ONLY_CMD));
                return 0;
            }
            String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey(ability).toString();
            ServerPlayer player = source.getPlayer();

            if (ArtifactUtils.removeAbility(player.getItemBySlot(EquipmentSlot.MAINHAND), ability)) {
                source.sendSuccess(() -> Component.translatable(References.ABILITY_REMOVE_CMD, abilityId), false);
                return 1;
            }
            source.sendFailure(Component.translatable(References.ABILITY_REMOVE_ERROR_0, abilityId));
            return 0;
        }
    }

    /**
     * Base command for creating an artifact.
     */
    private static class ArtifactBaseCommand {

        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("artifact")
                    .requires((source) -> source.hasPermission(3))
                    .then(cmdCreate());
        }

        private static ArgumentBuilder<CommandSourceStack, ?> cmdCreate() {
            return Commands.literal("create")
                    .then(Commands.argument("category", ArtifactCategoryArgument.artifactCategory())
                            .then(Commands.argument("variant", IntegerArgumentType.integer(1, 100)).executes((context) -> createArtifact(context.getSource(), ArtifactCategoryArgument.getCategory(context, "category"), IntegerArgumentType.getInteger(context, "variant")))));
        }

        private static int createArtifact(CommandSourceStack source, ArtifactCategory category, int variant) {
            if (source.getPlayer() == null) {
                source.sendFailure(Component.translatable(References.PLAYER_ONLY_CMD));
                return 0;
            }
            if (variant > category.getVariations()) {
                source.sendFailure(Component.translatable(References.ARTIFACT_CREATE_ERROR_0, category.getVariations()));
                return 0;
            }
            RandomSource random = source.getLevel().getRandom();
            ServerPlayer player = source.getPlayer();
            List<RegistryObject<? extends Item>> artifactsOfCategory = MRItems.ARTIFACTS_BY_CATEGORY.get(category);
            Item artifactItem = artifactsOfCategory.get(random.nextInt(artifactsOfCategory.size())).get();
            ItemStack artifact = ArtifactUtils.createBlankArtifact(artifactItem, variant, source.getLevel().random);
            ArtifactUtils.applyMandatoryAttributeMods(artifact, category, random);

            CompoundTag modDataTag = artifact.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);
            modDataTag.putString(ArtifactUtils.PREFIX_KEY, References.MUNDANE_ABILITY_PREFIX);

            boolean wasAdded = player.addItem(artifact);

            if (!wasAdded) {
                ItemEntity itemEntity = player.drop(artifact, false);

                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setThrower(player.getUUID());
                }
            }
            source.sendSuccess(() -> Component.translatable(References.ARTIFACT_CREATE_CMD, category.getName()), false);
            return 1;
        }
    }
}
