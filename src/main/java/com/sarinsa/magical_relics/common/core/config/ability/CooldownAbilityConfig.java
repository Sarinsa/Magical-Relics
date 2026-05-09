package com.sarinsa.magical_relics.common.core.config.ability;

import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.LongField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

public class CooldownAbilityConfig extends AbilityConfig {
    
    public final Cooldown COOLDOWN;
    
    public CooldownAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                  Rarity rarity, int cooldown ) {
        super( cfgManager, abilityId, rarity );
        
        COOLDOWN = new Cooldown( this, cooldown );
    }
    
    public CooldownAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                  int cooldown ) {
        this( cfgManager, abilityId, ArtifactUtils.RARITY_MUNDANE, cooldown );
    }
    
    public static class Cooldown extends AbstractConfigCategory<AbilityConfig> {
        
        public final LongField cooldown;
        
        public Cooldown( AbilityConfig parent, int cooldwn ) {
            super( parent, "cooldown", "Settings for this ability's cooldown." );
            
            cooldown = SPEC.define( new LongField( "cooldown", cooldwn, LongField.Range.NON_NEGATIVE,
                    "The number of ticks to put this ability on cooldown for after use." ) );
        }
    }
}
