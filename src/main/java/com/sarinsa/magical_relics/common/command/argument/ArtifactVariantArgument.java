package com.sarinsa.magical_relics.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ArtifactVariantArgument implements ArgumentType<Integer> {
    
    private static final Collection<String> EXAMPLES = Arrays.asList( "0", "1", "13", "99" );
    private static final String RANDOM_VAR = "random";
    
    private static final int MIN = 1;
    private static final int MAX = 100;
    
    
    private ArtifactVariantArgument() { }
    
    
    public static ArtifactVariantArgument artifactVariant() {
        return new ArtifactVariantArgument();
    }
    
    public static int getVariant( final CommandContext<?> context, final String name ) {
        return context.getArgument( name, int.class );
    }
    
    @Override
    public Integer parse( StringReader reader ) throws CommandSyntaxException {
        if( !Character.isDigit( reader.peek() ) ) {
            if( reader.readUnquotedString().equals( "random" ) ) {
                return -1;
            }
        }
        final int start = reader.getCursor();
        final int result = reader.readInt();
        
        if( result < MIN ) {
            reader.setCursor( start );
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext( reader, result, MIN );
        }
        if( result > MAX ) {
            reader.setCursor( start );
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext( reader, result, MAX );
        }
        return result;
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions( CommandContext<S> context, SuggestionsBuilder builder ) {
        final ArtifactCategory categoryArg = context.getArgument( "category", ArtifactCategory.class );
        
        // Only suggest if a category argument is present.
        if( categoryArg != null ) {
            // Full list without hint
            if( builder.getRemaining().isEmpty() ) {
                builder.suggest( RANDOM_VAR );
                
                for( int variant = 1; variant < categoryArg.getVariations() + 1; variant++ ) {
                    builder.suggest( variant );
                }
                return builder.buildFuture();
            }
            
            // Suggest random string if partially present
            if( RANDOM_VAR.contains( builder.getRemaining() ) ) {
                builder.suggest( RANDOM_VAR );
            }
        }
        return builder.buildFuture();
    }
    
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
