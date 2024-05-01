package com.sarinsa.magical_relics.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.util.References;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ArtifactCategoryArgument implements ArgumentType<ArtifactCategory> {

    private static final DynamicCommandExceptionType ERROR_INVALID_CATEGORY = new DynamicCommandExceptionType((o) -> Component.translatable(References.ERROR_INVALID_CATEGORY, o));
    private static final Collection<String> EXAMPLES = Arrays.asList("trinket", "sword", "staff");


    public ArtifactCategoryArgument() {

    }

    public static ArtifactCategoryArgument artifactCategory() {
        return new ArtifactCategoryArgument();
    }

    @Override
    public ArtifactCategory parse(StringReader stringReader) throws CommandSyntaxException {
        String s = stringReader.readUnquotedString();
        ArtifactCategory category = ArtifactCategory.getFromName(s);

        if (category == null) throw ERROR_INVALID_CATEGORY.create(s);

        return category;
    }

    public static <S> ArtifactCategory getCategory(CommandContext<S> context, String s) {
        return context.getArgument(s, ArtifactCategory.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            for (ArtifactCategory category : ArtifactCategory.values()) {
                suggestionsBuilder.suggest(category.getName());
            }
            return suggestionsBuilder.buildFuture();
        }

        for (ArtifactCategory category : ArtifactCategory.values()) {

            if (category.getName().contains(suggestionsBuilder.getRemaining())) {
                suggestionsBuilder.suggest(category.getName());
            }
        }
        return suggestionsBuilder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
