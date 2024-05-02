package com.sarinsa.magical_relics.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.util.References;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TriggerTypeArgument implements ArgumentType<TriggerType> {

    private static final DynamicCommandExceptionType ERROR_INVALID_TRIGGER = new DynamicCommandExceptionType((o) -> Component.translatable(References.ERROR_INVALID_TRIGGER, o));
    private static final Collection<String> EXAMPLES = Arrays.asList("use", "inventory_tick", "");


    public TriggerTypeArgument() {

    }

    public static TriggerTypeArgument triggerType() {
        return new TriggerTypeArgument();
    }

    @Override
    public TriggerType parse(StringReader stringReader) throws CommandSyntaxException {
        String s = stringReader.readUnquotedString();
        TriggerType triggerType = TriggerType.getFromName(s);

        if (triggerType == null) throw ERROR_INVALID_TRIGGER.create(s);

        return triggerType;
    }

    public static <S> TriggerType getTriggerType(CommandContext<S> context, String s) {
        return context.getArgument(s, TriggerType.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            for (TriggerType triggerType : TriggerType.values()) {
                suggestionsBuilder.suggest(triggerType.getName());
            }
            return suggestionsBuilder.buildFuture();
        }

        for (TriggerType triggerType : TriggerType.values()) {

            if (triggerType.getName().contains(suggestionsBuilder.getRemaining())) {
                suggestionsBuilder.suggest(triggerType.getName());
            }
        }
        return suggestionsBuilder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
