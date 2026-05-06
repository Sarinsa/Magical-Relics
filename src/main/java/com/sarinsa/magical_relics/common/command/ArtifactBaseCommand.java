package com.sarinsa.magical_relics.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.command.argument.ArtifactCategoryArgument;
import com.sarinsa.magical_relics.common.command.argument.ArtifactVariantArgument;
import com.sarinsa.magical_relics.common.item.IArtifactItem;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.TranslationUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Base command for creating an artifact.
 */
public class ArtifactBaseCommand {
    
    protected static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal( "artifact" )
                .requires( ( source ) -> source.hasPermission( 3 ) )
                .then( cmdCreate() );
    }
    
    private static ArgumentBuilder<CommandSourceStack, ?> cmdCreate() {
        return Commands.literal( "create" )
                .then( Commands.argument( "category", ArtifactCategoryArgument.artifactCategory() )
                        .then( Commands.argument( "variant", ArtifactVariantArgument.artifactVariant() )
                                .executes( ( context ) -> createArtifact(
                                        context.getSource(),
                                        ArtifactCategoryArgument.getCategory( context, "category" ),
                                        ArtifactVariantArgument.getVariant( context, "variant" ) ) ) ) );
    }
    
    private static int createArtifact( CommandSourceStack source, ArtifactCategory category, int variant ) {
        final RandomSource random = source.getLevel().getRandom();
        final ServerPlayer player = source.getPlayer();
        boolean randomAbilities = false;
        
        if( player == null ) {
            source.sendFailure( Component.translatable( TranslationUtils.PLAYER_ONLY_CMD ) );
            return 0;
        }
        if( variant > category.getVariations() ) {
            source.sendFailure( Component.translatable( TranslationUtils.ARTIFACT_CREATE_ERROR_0, category.getVariations() ) );
            return 0;
        }
        if( variant == -1 ) {
            variant = random.nextInt( category.getVariations() + 1 );
            randomAbilities = true;
        }
        final List<IArtifactItem> artifactsOfCategory = ArtifactUtils.getArtifactsOfCategory( category );
        final IArtifactItem artifactItem = artifactsOfCategory.get( random.nextInt( artifactsOfCategory.size() ) );
        final ItemStack artifact = ArtifactUtils.createBlankArtifact( artifactItem, variant, source.getLevel().random );
        
        ArtifactUtils.applyMandatoryAttributeMods( artifact, category, random );
        
        if( randomAbilities ) {
            BaseArtifactAbility<?>[] appliedAbilities = ArtifactUtils.applyAbilities( artifact, random, random.nextFloat() < 0.1F, ArtifactUtils.getAbilitiesForCategory( category ) );
            ArtifactUtils.setPrefixAndSuffix( artifact, random, appliedAbilities );
        }
        else {
            final CompoundTag modDataTag = artifact.getOrCreateTag().getCompound( ArtifactUtils.TAG_MOD_DATA );
            modDataTag.putString( ArtifactUtils.TAG_PREFIX, TranslationUtils.MUNDANE_ABILITY_PREFIX );
        }
        
        if( !player.addItem( artifact ) ) {
            final ItemEntity itemEntity = player.drop( artifact, false );
            
            if( itemEntity != null ) {
                itemEntity.setNoPickUpDelay();
                itemEntity.setThrower( player.getUUID() );
            }
        }
        source.sendSuccess( () -> Component.translatable( TranslationUtils.ARTIFACT_CREATE_CMD, category.getName() ), false );
        return 1;
    }
}