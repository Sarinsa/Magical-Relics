package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Used as the sole hub for all config access from outside the config package.
 * <br><br>
 * Contains references to the main configs in this mod, which in turn provide direct 'getter' access to each
 * configurable value.<br><br>
 * <strong>Ability configs</strong> are handled a bit differently. An ability's config can be accessed via the ability object's
 * {@link BaseArtifactAbility#getConfig()} method.
 */
public class Config {
    
    private static final ConfigManager MANAGER = ConfigManager.create( "MagicalRelics", MagicalRelics.MODID );
    
    public static MainConfig MAIN;
    
    
    public static void initialize() {
        MAIN = new MainConfig( MANAGER, "_main" );
        MAIN.SPEC.initialize();
        
        initAbilityConfigs();
    }
    
    /**
     * Loops through the ability Forge registry and constructs
     * each ability's config via {@link BaseArtifactAbility#createConfig(ConfigManager, ResourceLocation)}.
     */
    private static void initAbilityConfigs() {
        final IForgeRegistry<BaseArtifactAbility<?>> registry = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get();
        
        if( registry == null )
            throw new IllegalStateException( "Artifact Abilities registry has not yet been constructed!" );
        
        for( BaseArtifactAbility<?> ability : registry ) {
            ResourceLocation id = registry.getKey( ability );
            
            try {
                AbilityConfig cfg = ability.createConfig( MANAGER, id );
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
    
    /**
     * Called from {@link MagicalRelics#MagicalRelics(FMLJavaModLoadingContext)} to load this class.
     * Actual config initialization is queued for after Forge registries have been loaded.
     */
    public static void init() {
        DeferredWorkQueue.lookup( Optional.of( ModLoadingStage.COMMON_SETUP ) ).ifPresent(
                ( workQueue ) -> workQueue.enqueueWork( ModList.get().getModContainerById( MagicalRelics.MODID ).orElseThrow(),
                        Config::initialize )
        );
    }
}