package com.sarinsa.magical_relics.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.command.argument.ArtifactCategoryArgument;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.References;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/**
 * Base command for creating an artifact.
 */
public class ArtifactBaseCommand {

    protected static ArgumentBuilder<CommandSourceStack, ?> register() {
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