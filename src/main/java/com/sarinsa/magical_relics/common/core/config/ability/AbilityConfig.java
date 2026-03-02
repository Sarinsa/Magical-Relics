package com.sarinsa.magical_relics.common.core.config.ability;

import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.EnumField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

import java.util.Objects;

public class AbilityConfig extends AbstractConfigFile {
    
    public General GENERAL;
    
    public AbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId, Rarity rarity ) {
        super( cfgManager, getCfgName( Objects.requireNonNull( abilityId ) ),
                "This config contains options for the " + abilityId + " ability." );
        
        GENERAL = new General( this, rarity );
    }
    
    public AbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        this( cfgManager, abilityId, ArtifactUtils.RARITY_MUNDANE );
    }
    
    private static String getCfgName( ResourceLocation abilityId ) {
        return abilityId.getNamespace() + "_abilities/" + abilityId.getPath();
    }
    
    public static class General extends AbstractConfigCategory<AbilityConfig> {
        
        public EnumField<Rarity> rarity;
        
        public General( AbilityConfig parent, Rarity rarty ) {
            super( parent, "general", "General settings for this ability." );
            
            rarity = SPEC.define( new EnumField<>( "rarity", rarty,
                    "The rarity of this ability. Primarily only determines the color of the ability's description." ) );
        }
    }
}
