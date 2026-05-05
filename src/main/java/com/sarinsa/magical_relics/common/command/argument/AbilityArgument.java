package com.sarinsa.magical_relics.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.TranslationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class AbilityArgument implements ArgumentType<BaseArtifactAbility<?>> {
    
    private static final DynamicCommandExceptionType ERROR_INVALID_ABILITY = new DynamicCommandExceptionType( ( o ) -> Component.translatable( TranslationUtils.ERROR_INVALID_ABILITY, o ) );
    private static final Collection<String> EXAMPLES = Arrays.asList( "magical_relics:jump_boost", "glow_vision", "jei:recipe_smuggler" );
    
    
    private AbilityArgument() { }
    
    
    public static AbilityArgument ability() {
        return new AbilityArgument();
    }
    
    @Override
    public BaseArtifactAbility<?> parse( StringReader stringReader ) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read( stringReader );
        
        if( !MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().containsKey( id ) )
            throw ERROR_INVALID_ABILITY.create( id );
        
        return MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getValue( id );
    }
    
    public static <S> BaseArtifactAbility<?> getAbility( CommandContext<S> context, String s ) {
        return context.getArgument( s, BaseArtifactAbility.class );
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions( CommandContext<S> context, SuggestionsBuilder builder ) {
        // Full list without hint
        if( builder.getRemaining().isEmpty() ) {
            for( ResourceLocation id : MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKeys() ) {
                String s = id.toString();
                builder.suggest( s );
            }
            return builder.buildFuture();
        }
        
        // Partial list from hint
        for( ResourceLocation id : MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKeys() ) {
            String s = id.toString();
            
            if( s.contains( builder.getRemaining() ) ) {
                builder.suggest( s );
            }
        }
        return builder.buildFuture();
    }
    
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
