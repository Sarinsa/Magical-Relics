package com.sarinsa.magical_relics.common.core.config.sync;

import fathertoast.crust.api.config.common.field.AbstractConfigField;

import java.util.function.Supplier;

public class SyncedProperty<V, F extends AbstractConfigField> {
    
    private final Supplier<F> fieldProvider;
    
    private final byte propertyId;
    private V currentValue;
    
    protected SyncedProperty( Supplier<F> fieldProvider, byte propertyId ) {
        this.fieldProvider = fieldProvider;
        this.propertyId = propertyId;
        currentValue = null;
    }
    
    /**
     * @return This synced property's field, if it exists
     * AND its underlying spec has been initialized.
     */
    public F getField() {
        F field = fieldProvider.get();
        
        if( field == null )
            throw new IllegalStateException( "Field provider for SyncedProperty returned null; it must have been accessed too early!" );
        
        if( !field.getSpec().isInitialized() )
            throw new IllegalStateException( "Field provider for SyncedProperty returned a field that was not initialized!" );
        
        return field;
    }
    
    /** @return The ID of this synced property. Used when sending packets. */
    public byte getPropertyId() {
        return propertyId;
    }
    
    /** @return The cached value. */
    public V getValue() {
        if( currentValue == null )
            throw new IllegalStateException( "Cannot get value of SyncedProperty because it is null. This should not happen!" );
        return currentValue;
    }
    
    /**
     * Sets the current value of this synced property.
     * This should only ever be called on the client,
     * via sync packets.
     */
    public void setValue( V value ) {
        this.currentValue = value;
    }
}
