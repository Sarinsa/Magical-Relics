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
import com.sarinsa.magical_relics.common.util.TranslationUtils;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TriggerTypeArgument implements ArgumentType<TriggerType> {
    
    private static final DynamicCommandExceptionType ERROR_INVALID_TRIGGER = new DynamicCommandExceptionType( ( o ) -> Component.translatable( TranslationUtils.ERROR_INVALID_TRIGGER, o ) );
    private static final Collection<String> EXAMPLES = Arrays.asList( "use", "inventory_tick", "" );
    
    
    private TriggerTypeArgument() { }
    
    
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
    public <S> CompletableFuture<Suggestions> listSuggestions( CommandContext<S> context, SuggestionsBuilder builder ) {
        final BaseArtifactAbility<?> abilityArg = context.getArgument( "ability", BaseArtifactAbility.class );
        TriggerType[] triggerTypes = TriggerType.values();
        
        // If there is an ability in the given command context,
        // assume we only want to list compatible trigger types.
        if( abilityArg != null ) {
            triggerTypes = abilityArg.supportedTriggers().toArray( new TriggerType[0] );
        }
        
        // Full list without hint
        if( builder.getRemaining().isEmpty() ) {
            for( TriggerType triggerType : triggerTypes ) {
                builder.suggest( triggerType.getSerializedName() );
            }
            return builder.buildFuture();
        }
        
        // Partial list from hint
        for( TriggerType triggerType : triggerTypes ) {
            if( triggerType.getSerializedName().contains( builder.getRemaining() ) ) {
                builder.suggest( triggerType.getSerializedName() );
            }
        }
        return builder.buildFuture();
    }
    
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
