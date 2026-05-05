package com.sarinsa.magical_relics.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TranslationUtil {
    
    // MISC
    public static final String INFINITY_CHAR = "∞";
    
    
    // STATIC CHAT COMPONENTS
    public static final MutableComponent ANTI_BUILDER_BLOCK_MESSAGE = Component.translatable( "magical_relics.anti_builder.blocked_message" );
    
    public static final MutableComponent ANTI_BUILDER_CORNER_1 = Component.translatable( "magical_relics.anti_builder.screen.corner_1" );
    public static final MutableComponent ANTI_BUILDER_CORNER_2 = Component.translatable( "magical_relics.anti_builder.screen.corner_2" );
    
    public static final MutableComponent PEDESTAL_LOCKED = Component.translatable( "magical_relics.display_pedestal.locked_message" );
    
    // TRANSLATION KEYS
    public static final String ARTIFACT_CREATE_CMD = "magical_relics.command.artifact.create.message";
    public static final String ARTIFACT_CREATE_ERROR_0 = "magical_relics.command.artifact.create.error.invalid_variation";
    public static final String ABILITY_APPLY_ERROR_0 = "magical_relics.command.ability.apply.error.already_exists";
    public static final String ABILITY_APPLY_ERROR_1 = "magical_relics.command.ability.apply.error.trigger_occupied";
    public static final String ABILITY_APPLY_ERROR_2 = "magical_relics.command.ability.apply.error.invalid_item";
    public static final String ABILITY_APPLY_ERROR_3 = "magical_relics.command.ability.apply.error.unsupported_trigger";
    public static final String ABILITY_REMOVE_CMD = "magical_relics.command.ability.remove.message";
    public static final String ABILITY_REMOVE_ERROR_0 = "magical_relics.command.ability.remove.error";
    public static final String PLAYER_ONLY_CMD = "magical_relics.command.failure.player_only";
    public static final String ERROR_INVALID_CATEGORY = "magical_relics.command.argument.artifact_category.error.invalid_category";
    public static final String ERROR_INVALID_ABILITY = "magical_relics.command.argument.artifact_ability.error.invalid_ability";
    public static final String ERROR_INVALID_TRIGGER = "magical_relics.command.argument.trigger_type.error.invalid_trigger";
    
    public static final String MUNDANE_ABILITY_PREFIX = "magical_relics.ability.mundane_prefix";
    
    public static final String PEDESTAL_LOCKED_TOOLTIP = "magical_relics.display_pedestal.locked_tooltip";
    
    public static final String FORMAT_SINGULAR_SECOND = "magical_relics.format.singular_second";
    public static final String FORMAT_SECONDS = "magical_relics.format.seconds";
    public static final String FORMAT_SINGULAR_MINUTE = "magical_relics.format.singular_minute";
    public static final String FORMAT_MINUTES = "magical_relics.format.minutes";
    public static final String FORMAT_SINGULAR_HOUR = "magical_relics.format.singular_hour";
    public static final String FORMAT_HOURS = "magical_relics.format.hours";
    public static final String FORMAT_AND = "magical_relics.format.and";
    
    
    /** @return The given effect multiplier as a translated potion level String. */
    public static String potionLevel( int effectMult ) {
        return Component.translatable( "enchantment.level." + (effectMult + 1) ).getString();
    }
    
    /**
     * @return A translatable component with the given amount of ticks formatted as<br>
     * <strong>"{} hours, {} minutes and {} seconds".</strong> Any unit that ends up being 0 (unless it is the only unit) is skipped.
     * <br><br>
     * If the ticks parameter is negative, all calculations are skipped and a component with
     * the format <strong>"{INFINITY_CHAR} seconds"</strong> is returned.
     */
    public static Component ticksToHMS( long ticks ) {
        if( ticks <= -1 ) {
            return Component.translatable( FORMAT_SECONDS, INFINITY_CHAR );
        }
        else if( ticks < 20 ) {
            return Component.translatable( FORMAT_SECONDS, 0 );
        }
        final int hours = (int) (ticks / 72000);
        final int minutes = (int) ((ticks % 72000) / 1200);
        final int seconds = (int) (((ticks % 72000) % 1200) / 20);
        
        final Component[] subComponents = new Component[3];
        
        if( hours > 0 )
            subComponents[0] = Component.translatable( hours == 1 ? FORMAT_SINGULAR_HOUR : FORMAT_HOURS, hours );
        if( minutes > 0 )
            subComponents[1] = Component.translatable( minutes == 1 ? FORMAT_SINGULAR_MINUTE : FORMAT_MINUTES, minutes );
        if( seconds > 0 )
            subComponents[2] = Component.translatable( seconds == 1 ? FORMAT_SINGULAR_SECOND : FORMAT_SECONDS, seconds );
        
        final MutableComponent component = Component.empty();
        int addedComponents = 0;
        
        for( int i = 0; i < subComponents.length; i++ ) {
            if( subComponents[i] == null ) continue;
            
            if( addedComponents > 0 ) {
                if( i == subComponents.length - 1 )
                    component.append( " " + Component.translatable( FORMAT_AND ).getString() + " " );
                else
                    component.append( ", " );
            }
            component.append( subComponents[i] );
            
            ++addedComponents;
        }
        return component;
    }
}
