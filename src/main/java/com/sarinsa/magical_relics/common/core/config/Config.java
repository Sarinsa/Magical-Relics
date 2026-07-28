package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Field;

/**
 * Used as the sole hub for all config access from outside the config package.
 * <br><br>
 * Contains references to the main configs in this mod, which in turn provide direct 'getter' access to each
 * configurable value.<br><br>
 * <strong>Ability configs</strong> are handled a bit differently. An ability's config can be accessed via the ability object's
 * {@link BaseArtifactAbility#getConfig()} method.
 */
public class Config {
    
    public static MainConfig MAIN;
    public static WorldgenConfig WORLDGEN;
    
    
    public static void initialize() {
        final ConfigManager cfgManager = ConfigManager.create( "MagicalRelics", MagicalRelics.MODID );
        
        MAIN = new MainConfig( cfgManager, "_main" );
        MAIN.SPEC.initialize();
        WORLDGEN = new WorldgenConfig( cfgManager, "worldgen" );
        WORLDGEN.SPEC.initialize();
        
        initAbilityConfigs( cfgManager );
    }
    
    /**
     * Loops through the ability Forge registry and constructs
     * each ability's config via {@link BaseArtifactAbility#createConfig(ConfigManager, ResourceLocation)}.
     */
    private static void initAbilityConfigs( ConfigManager cfgManager ) {
        final IForgeRegistry<BaseArtifactAbility<?>> registry = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get();
        
        if( registry == null )
            throw new IllegalStateException( "Artifact Abilities registry has not yet been constructed!" );
        
        for( BaseArtifactAbility<?> ability : registry ) {
            ResourceLocation id = registry.getKey( ability );
            if( id == null ) {
                MagicalRelics.LOG.warn( "Attempted to load ability config for an unregistered ability! Ability: {}", ability );
                continue;
            }
            try {
                AbilityConfig cfg = ability.createConfig( cfgManager, id );
                cfg.SPEC.initialize();
                
                Field cfgField = BaseArtifactAbility.class.getDeclaredField( "config" );
                
                if( cfgField.trySetAccessible() ) {
                    cfgField.set( ability, cfg );
                }
                else {
                    MagicalRelics.LOG.error( "Can not access config field reflectively, very strange!" );
                }
            }
            catch( Exception e ) {
                MagicalRelics.LOG.error( "Failed to initialize ability config for ability with ID \"{}\"!", id );
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }
}