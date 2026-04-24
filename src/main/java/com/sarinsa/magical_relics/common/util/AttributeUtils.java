package com.sarinsa.magical_relics.common.util;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.UUID;

public class AttributeUtils {
    
    /**
     * Attempts to read an AttributeModifier from the given CompoundTag just like
     * {@link AttributeModifier#load(CompoundTag)}, except modifier UUID is manually
     * replaced with specific UUID objects when needed.<br><br>
     * <p>
     * This is because some places in vanilla, UUID objects are compared as "normal" objects and not UUIDs.
     */
    public static AttributeModifier loadUUIDSensitive( CompoundTag compoundTag ) {
        try {
            UUID uuid = findAndReplace( compoundTag.getUUID( "UUID" ) );
            AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue( compoundTag.getInt( "Operation" ) );
            
            return new AttributeModifier( uuid, compoundTag.getString( "Name" ), compoundTag.getDouble( "Amount" ), operation );
        }
        catch( Exception exception ) {
            MagicalRelics.LOG.warn( "Unable to create attribute: {}", exception.getMessage() );
            return null;
        }
    }
    
    private static UUID findAndReplace( UUID uuid ) {
        if( uuid.equals( Item.BASE_ATTACK_DAMAGE_UUID ) ) {
            return Item.BASE_ATTACK_DAMAGE_UUID;
        }
        else if( uuid.equals( Item.BASE_ATTACK_SPEED_UUID ) ) {
            return Item.BASE_ATTACK_SPEED_UUID;
        }
        return uuid;
    }
}
