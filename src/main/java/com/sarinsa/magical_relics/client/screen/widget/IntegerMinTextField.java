package com.sarinsa.magical_relics.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public class IntegerMinTextField extends IntegerTextField {
    
    private final IntegerTextField maxField;
    
    public IntegerMinTextField( IntegerTextField maxField, Font fontRenderer, Integer defaultValue, Integer minValue, Integer maxValue, int x, int y, int width, int height, @Nullable MutableComponent descriptor, @Nullable Tooltip tooltip ) {
        super( fontRenderer, defaultValue, minValue, maxValue, x, y, width, height, descriptor, tooltip );
        this.maxField = maxField;
    }
    
    @Override
    public boolean checkIsValidValue( String value ) {
        try {
            int intValue = Integer.parseInt( value );
            
            return intValue >= minValue && intValue <= maxValue && intValue < maxField.getCurrentValue();
        }
        catch( Exception ignored ) {
            return false;
        }
    }
}
