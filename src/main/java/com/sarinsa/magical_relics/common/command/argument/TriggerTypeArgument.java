package com.sarinsa.magical_relics.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.util.TranslationUtil;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TriggerTypeArgument implements ArgumentType<TriggerType> {
    
    private static final DynamicCommandExceptionType ERROR_INVALID_TRIGGER = new DynamicCommandExceptionType( ( o ) -> Component.translatable( TranslationUtil.ERROR_INVALID_TRIGGER, o ) );
    private static final Collection<String> EXAMPLES = Arrays.asList( "use", "inventory_tick", "" );
    
    
    public TriggerTypeArgument() { }
    
    
    public static TriggerTypeArgument triggerType() {
        return new TriggerTypeArgument();
    }
    
    @Override
    public TriggerType parse( StringReader stringReader ) throws CommandSyntaxException {
        String s = stringReader.readUnquotedString();
        TriggerType triggerType = TriggerType.getFromName( s );
        
        if( triggerType == null ) throw ERROR_INVALID_TRIGGER.create( s );
        
        return triggerType;
    }
    
    public static <S> TriggerType getTriggerType( CommandContext<S> context, String s ) {
        return context.getArgument( s, TriggerType.class );
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions( CommandContext<S> context, SuggestionsBuilder suggestionsBuilder ) {
        final BaseArtifactAbility<?> abilityArg = context.getArgument( "ability", BaseArtifactAbility.class );
        TriggerType[] triggerTypes = TriggerType.values();
        
        // If there is an ability in the given command context,
        // assume we only want to list compatible trigger types.
        if( abilityArg != null ) {
            triggerTypes = abilityArg.supportedTriggers().toArray( new TriggerType[0] );
        }
        
        // Full list without hint
        if( suggestionsBuilder.getRemaining().isEmpty() ) {
            for( TriggerType triggerType : triggerTypes ) {
                suggestionsBuilder.suggest( triggerType.getName() );
            }
            return suggestionsBuilder.buildFuture();
        }
        
        // Partial list from hint
        for( TriggerType triggerType : triggerTypes ) {
            if( triggerType.getName().contains( suggestionsBuilder.getRemaining() ) ) {
                suggestionsBuilder.suggest( triggerType.getName() );
            }
        }
        return suggestionsBuilder.buildFuture();
    }
    
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
