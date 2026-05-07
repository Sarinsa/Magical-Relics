package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.collection.RegistrySetField;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.text.WordUtils;

@SuppressWarnings( "UnstableApiUsage" )
public class WorldgenConfig extends AbstractConfigFile {
    
    public final StructureProcessorProperties STRUCTURE_PROCESSORS;
    
    
    /** Builds the config spec that should be used for this config. */
    WorldgenConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options related to world generation added by Magical Relics."
        );
        
        STRUCTURE_PROCESSORS = new StructureProcessorProperties( this );
    }
    
    public static class StructureProcessorProperties extends AbstractConfigCategory<WorldgenConfig> {
        
        // Spawner Type props
        public final RegistrySetField<EntityType<?>> lightIgnoringEntities;
        
        
        StructureProcessorProperties( WorldgenConfig parent ) {
            super( parent, "structure_processors",
                    "Options to customize various properties for structure processor types added by Magical Relics." );
            
            processorTitle( MRStructureProcessors.SPAWNER_TYPE );
            
            // TODO - Update description once this is functional
            lightIgnoringEntities = SPEC.define( new RegistrySetField<>( "light_ignoring_entities",
                    createDefaultLightIgnoringEntities(),
                    "(NIY) A set of entity types that should ignore light values when spawning from a spawner modified by this processor." ) );
        }
        
        private RegistrySet<EntityType<?>> createDefaultLightIgnoringEntities() {
            return new RegistrySet.Builder<>( ForgeRegistries.ENTITY_TYPES )
                    .add( EntityType.SLIME )
                    .build();
        }
        
        @SuppressWarnings( { "ConstantConditions", "deprecation" } )
        private <T extends StructureProcessor> void processorTitle( RegistryObject<StructureProcessorType<T>> regObj ) {
            final String formattedId = WordUtils.capitalizeFully( regObj.getId().getPath().replaceAll( "_", " " ) );
            SPEC.titledComment( WordUtils.capitalizeFully( formattedId ) + " Processor" );
            SPEC.newLine();
        }
    }
}
