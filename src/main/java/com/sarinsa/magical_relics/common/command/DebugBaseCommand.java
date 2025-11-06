package com.sarinsa.magical_relics.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sarinsa.magical_relics.common.ability.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class DebugBaseCommand {
    
    protected static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal( "debug" )
                .requires( ( source ) -> source.hasPermission( 3 ) )
                .then( cmdAbilityCategoryRatios() );
    }
    
    private static ArgumentBuilder<CommandSourceStack, ?> cmdAbilityCategoryRatios() {
        return Commands.literal( "ca_ratios" ).executes( ( context ) -> displayCARatios( context.getSource() ) );
    }
    
    private static int displayCARatios( CommandSourceStack source ) {
        Map<ArtifactCategory, Integer> usagesPerCategory = new HashMap<>();
        
        for( ArtifactCategory category : ArtifactCategory.values() ) {
            usagesPerCategory.put( category, 0 );
        }
        
        for( BaseArtifactAbility ability : MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get() ) {
            for( ArtifactCategory category : ability.getCompatibleTypes() ) {
                usagesPerCategory.put( category, usagesPerCategory.get( category ) + 1 );
            }
        }
        
        source.sendSystemMessage( Component.literal( "-----------------------------" ) );
        source.sendSystemMessage( Component.literal( "Applicable abilities per artifact category:" ).withStyle( ChatFormatting.GRAY ) );
        
        usagesPerCategory.forEach( ( category, count ) -> {
            source.sendSystemMessage( Component.literal( category.getName() + " - " + count ) );
        } );
        source.sendSystemMessage( Component.literal( "-----------------------------" ) );
        
        return 1;
    }
}
