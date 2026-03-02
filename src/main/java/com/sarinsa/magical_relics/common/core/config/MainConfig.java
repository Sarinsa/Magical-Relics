package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.worldgen.processor.DisplayPedestalProcessor;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.InjectionWrapperField;
import fathertoast.crust.api.config.common.field.RegistryEntryListField;
import fathertoast.crust.api.config.common.value.RegistryEntryList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class MainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final AntiBuilder ANTI_BUILDER;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        GENERAL = new General( this );
        ANTI_BUILDER = new AntiBuilder( this );
    }
    
    public static class General extends AbstractConfigCategory<MainConfig> {
        
        public final InjectionWrapperField<RegistryEntryListField<BaseArtifactAbility<?>>> unobtainableAbilities;
        
        public final InjectionWrapperField<RegistryEntryListField<Item>> wizardFavoriteBlacklist;
        
        
        General( MainConfig parent ) {
            super( parent, "general",
                    "Options to customize settings that apply to the mod as a whole." );
            
            unobtainableAbilities = SPEC.define( new InjectionWrapperField<>( new RegistryEntryListField<>( "unobtainable_abilities",
                    createDefaultUnobtainableAbilities(),
                    "A list of artifact abilities that are blacklisted and cannot be obtained without using commands." ),
                    ( field ) -> ArtifactUtils.refreshObtainableAbilities( field.getEntries() ) ) );
            
            SPEC.newLine();
            
            wizardFavoriteBlacklist = SPEC.define( new InjectionWrapperField<>( new RegistryEntryListField<>( "wizards_favorite_blacklist",
                    createDefaultWizFavoriteBlacklist(),
                    "A list of items that should not be findable in \"Wizard's Favorite\" display "
                            + "pedestals in wizard tower structures." ),
                    ( field ) -> DisplayPedestalProcessor.refreshWizFavorites( field.getEntries() ) ) );
        }
        
        private RegistryEntryList<BaseArtifactAbility<?>> createDefaultUnobtainableAbilities() {
            return new RegistryEntryList<>( MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get() );
        }
        
        private RegistryEntryList<Item> createDefaultWizFavoriteBlacklist() {
            return new RegistryEntryList<>( ForgeRegistries.ITEMS,
                    Items.BEDROCK,
                    Items.STRUCTURE_BLOCK,
                    Items.JIGSAW,
                    Items.STRUCTURE_VOID,
                    Items.BARRIER,
                    Items.AIR,
                    Items.DEBUG_STICK
            );
        }
    }
    
    public static class AntiBuilder extends AbstractConfigCategory<MainConfig> {
        
        public final BooleanField antiBuilderBlocksBuilding;
        
        public AntiBuilder( MainConfig parent ) {
            super( parent, "anti_builder",
                    "Contains options related to the Anti-Builder / Alteration Negator" );
            
            antiBuilderBlocksBuilding = SPEC.define( new BooleanField( "blocks_building", false,
                    "If enabled, anti-builders will prevent most world interactions within their effective area.",
                    "This includes breaking blocks, placing blocks, explosions, mob griefing etc.",
                    "If this is disabled, the anti-builder will instead punish players with negative potion effects " +
                            "instead of just straight up disallowing the interaction." ) );
        }
    }
}