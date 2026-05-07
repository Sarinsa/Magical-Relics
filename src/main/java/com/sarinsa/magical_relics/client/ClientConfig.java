package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.util.MarkedMobEffectInstance;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;

public class ClientConfig extends AbstractConfigFile {
    
    public final Misc MISC;
    
    
    /** Builds the config spec that should be used for this config. */
    public ClientConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains various client-sided options."
        );
        MISC = new Misc( this );
    }
    
    
    public static class Misc extends AbstractConfigCategory<ClientConfig> {
        
        
        public final BooleanField noEffectIconFlicker;
        
        
        Misc( ClientConfig parent ) {
            super( parent, "misc",
                    "Settings that don't fit in other categories." );
            
            noEffectIconFlicker = SPEC.define( new BooleanField( "no_effect_icon_flicker", true,
                    "If enabled, Magical Relics will try and stop the \"pulsating\" visual effect on potion effect icons in the GUI that " +
                            "happen when a potion effect is about to run out.",
                    "This only applies to effects with " + MarkedMobEffectInstance.TICK_THRESHOLD + " ticks or less of duration." ) );
        }
    }
}
