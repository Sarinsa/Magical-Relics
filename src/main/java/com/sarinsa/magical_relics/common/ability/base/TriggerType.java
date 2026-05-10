package com.sarinsa.magical_relics.common.ability.base;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

/**
 * Represents the type of trigger that activates an artifact ability.
 */
public enum TriggerType implements StringRepresentable {
    USE( "use", false ),                      // Activates when the player right-clicks with the artifact
    HELD( "held", false ),                    // Activates every tick while the artifact is held in main hand or off-hand
    CURIO_TICK( "curio", false ),             // Activates every tick when the artifact is equipped as a curio
    USER_DAMAGED( "user_damaged", true ),     // Activates when the player takes damage
    USER_ATTACKING( "user_attacking", true ), // Activates when the player deals damage to a mob
    DROPPED( "dropped", false ),              // Activates when the artifact is thrown out of the player's inventory
    ARMOR_TICK( "armor_tick", true ),         // Activates when the artifact is equipped as armor, every tick
    INVENTORY_TICK( "inventory_tick", true ), // Activates every tick, regardless of where in the inventory the artifact is
    ON_DEATH( "on_death", true );             // Activates when the player dies. Only tracked for item stacks in vanilla equipment slots (held, offhand or armor).
    
    TriggerType( String name, boolean canStack ) {
        this.name = name;
        this.canStack = canStack;
    }
    
    /** The trigger type's name. */
    final String name;
    /**
     * Whether more than one ability with this
     * trigger type can exist on the same artifact.
     */
    final boolean canStack;
    
    @Override
    public String getSerializedName() {
        return name;
    }
    
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    public boolean canStack() {
        return canStack;
    }
    
    /**
     * @return The {@link TriggerType} whose name matches
     * the specified name string. Returns null if no match is found.
     */
    @Nullable
    public static TriggerType getFromName( String name ) {
        for( TriggerType triggerType : values() ) {
            if( triggerType.getSerializedName().equals( name ) )
                return triggerType;
        }
        return null;
    }
}