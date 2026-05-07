package com.sarinsa.magical_relics.common.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.sarinsa.magical_relics.common.ability.base.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.command.argument.AbilityArgument;
import com.sarinsa.magical_relics.common.command.argument.TriggerTypeArgument;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.item.IArtifactItem;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.TranslationUtils;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base command for applying artifact abilities to an artifact.
 */
public class AbilityBaseCommand {
    
    protected static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal( "ability" )
                .requires( ( source ) -> source.hasPermission( 3 ) )
                .then( cmdApply() )
                .then( cmdRemove() );
    }
    
    private static ArgumentBuilder<CommandSourceStack, ?> cmdApply() {
        return Commands.literal( "apply" )
                .then( Commands.argument( "ability", AbilityArgument.ability() )
                        .then( Commands.argument( "trigger_type", TriggerTypeArgument.triggerType() )
                                .executes( ( context ) -> applyAbility( context.getSource(), AbilityArgument.getAbility( context, "ability" ), TriggerTypeArgument.getTriggerType( context, "trigger_type" ) ) ) ) );
    }
    
    private static ArgumentBuilder<CommandSourceStack, ?> cmdRemove() {
        return Commands.literal( "remove" )
                .then( Commands.argument( "ability", AbilityArgument.ability() )
                        .executes( ( context ) -> removeAbility( context.getSource(), AbilityArgument.getAbility( context, "ability" ) ) ) );
    }
    
    private static int applyAbility( CommandSourceStack source, BaseArtifactAbility<?> ability, TriggerType triggerType ) {
        if( source.getPlayer() == null ) {
            source.sendFailure( Component.translatable( TranslationUtils.PLAYER_ONLY_CMD ) );
            return 0;
        }
        if( !ability.supportedTriggers().contains( triggerType ) ) {
            source.sendFailure( Component.translatable( TranslationUtils.ABILITY_APPLY_ERROR_3 ) );
            return 0;
        }
        final ServerPlayer player = source.getPlayer();
        final RandomSource random = source.getLevel().getRandom();
        final ItemStack itemStack = player.getItemBySlot( EquipmentSlot.MAINHAND );
        
        if( !(itemStack.getItem() instanceof IArtifactItem) ) {
            source.sendFailure( Component.translatable( TranslationUtils.ABILITY_APPLY_ERROR_2 ) );
            return 0;
        }
        
        try {
            final Map<BaseArtifactAbility<?>, TriggerType> currentAbilities = ArtifactUtils.getAllAbilities( itemStack );
            
            if( currentAbilities.containsKey( ability ) ) {
                source.sendFailure( Component.translatable( TranslationUtils.ABILITY_APPLY_ERROR_0 ) );
                return 0;
            }
            if( currentAbilities.containsValue( triggerType ) && !triggerType.canStack() ) {
                source.sendFailure( Component.translatable( TranslationUtils.ABILITY_APPLY_ERROR_1 ) );
                return 0;
            }
            // noinspection ConstantConditions
            final String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( ability ).toString();
            final CompoundTag modDataTag = NBTHelper.getOrCreateCompound( itemStack.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
            
            // Success, probably
            final CompoundTag abilityData = new CompoundTag();
            abilityData.putString( "AbilityId", abilityId );
            abilityData.putString( "TriggerType", triggerType.getName() );
            modDataTag.getList( ArtifactUtils.TAG_ABILITY, Tag.TAG_COMPOUND ).add( abilityData );
            ability.onAbilityAttached( itemStack, random );
            
            // Save any ability attribute modifiers to NBT
            final AttributeBoost boost = ability.getAttributeWithBoost();
            
            if( boost != null ) {
                // noinspection ConstantConditions
                String attributeId = ForgeRegistries.ATTRIBUTES.getKey( boost.attribute().get() ).toString();
                CompoundTag attributeMod = new CompoundTag();
                
                attributeMod.putString( "AttributeId", attributeId );
                attributeMod.put( "AttributeMod", new AttributeModifier(
                        boost.name(),
                        boost.valueProvider().getRangedValue( random ),
                        boost.operation()
                ).save() );
                attributeMod.putString( "ActiveType", boost.activeType().getName() );
                modDataTag.getList( ArtifactUtils.TAG_ATTRIBUTE_MODS, Tag.TAG_COMPOUND ).add( attributeMod );
            }
            final List<BaseArtifactAbility<?>> allAbilities = new ArrayList<>( currentAbilities.keySet() );
            allAbilities.add( ability );
            
            final BaseArtifactAbility<?> firstAbility = allAbilities.get( random.nextInt( allAbilities.size() ) );
            final BaseArtifactAbility<?> secondAbility = allAbilities.get( random.nextInt( allAbilities.size() ) );
            
            modDataTag.putString( ArtifactUtils.TAG_PREFIX, firstAbility.getPrefixes()[random.nextInt( firstAbility.getPrefixes().length )] );
            modDataTag.putString( ArtifactUtils.TAG_SUFFIX, secondAbility.getSuffixes()[random.nextInt( secondAbility.getSuffixes().length )] );
        }
        catch( Exception e ) {
            source.sendFailure( Component.translatable( TranslationUtils.ABILITY_APPLY_ERROR_4 ) );
            return 0;
        }
        return 1;
    }
    
    private static int removeAbility( CommandSourceStack source, BaseArtifactAbility<?> ability ) {
        if( source.getPlayer() == null ) {
            source.sendFailure( Component.translatable( TranslationUtils.PLAYER_ONLY_CMD ) );
            return 0;
        }
        // noinspection ConstantConditions
        String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( ability ).toString();
        ServerPlayer player = source.getPlayer();
        
        if( ArtifactUtils.removeAbility( player.getItemBySlot( EquipmentSlot.MAINHAND ), ability ) ) {
            source.sendSuccess( () -> Component.translatable( TranslationUtils.ABILITY_REMOVE_CMD, abilityId ), false );
            return 1;
        }
        source.sendFailure( Component.translatable( TranslationUtils.ABILITY_REMOVE_ERROR_0, abilityId ) );
        return 0;
    }
}
