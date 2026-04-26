package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.worldgen.processor.DisplayPedestalProcessor;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.InjectionWrapperField;
import fathertoast.crust.api.config.common.field.collection.RegistrySetField;
import fathertoast.crust.api.config.common.field.collection.RegistryValueListField;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import fathertoast.crust.api.config.common.value.collection.RegistryValueList;
import fathertoast.crust.api.config.common.value.collection.value.MobEffectStats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings( "UnstableApiUsage" )
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
        
        public final InjectionWrapperField<RegistrySetField<BaseArtifactAbility<?>>> unobtainableAbilities;
        
        public final InjectionWrapperField<RegistrySetField<Item>> wizardFavoriteBlacklist;
        
        
        General( MainConfig parent ) {
            super( parent, "general",
                    "Options to customize settings that apply to the mod as a whole." );
            
            unobtainableAbilities = SPEC.define( new InjectionWrapperField<>( new RegistrySetField<>( "unobtainable_abilities",
                    createDefaultUnobtainableAbilities(),
                    "A list of artifact abilities that are blacklisted and cannot be obtained without using commands." ),
                    ( field ) -> ArtifactUtils.refreshObtainableAbilities( field.get() ) ) );
            
            SPEC.newLine();
            
            wizardFavoriteBlacklist = SPEC.define( new InjectionWrapperField<>( new RegistrySetField<>( "wizards_favorite_blacklist",
                    createDefaultWizFavoriteBlacklist(),
                    "A list of items that should not be findable in \"Wizard's Favorite\" display pedestals in wizard tower structures." ),
                    ( field ) -> DisplayPedestalProcessor.refreshWizFavorites( field.get() ) ) );
        }
        
        private RegistrySet<BaseArtifactAbility<?>> createDefaultUnobtainableAbilities() {
            return new RegistrySet.Builder<>( MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get() )
                    .build();
        }
        
        private RegistrySet<Item> createDefaultWizFavoriteBlacklist() {
            return new RegistrySet.Builder<>( ForgeRegistries.ITEMS )
                    .add( Items.BEDROCK )
                    .add( Items.STRUCTURE_BLOCK )
                    .add( Items.JIGSAW )
                    .add( Items.STRUCTURE_VOID )
                    .add( Items.BARRIER )
                    .add( Items.AIR )
                    .add( Items.DEBUG_STICK )
                    .build();
        }
    }
    
    public static class AntiBuilder extends AbstractConfigCategory<MainConfig> {
        
        public final BooleanField antiBuilderBlocksBuilding;
        
        public final InjectionWrapperField<RegistryValueListField<MobEffect, MobEffectStats>> maladies;
        
        
        public AntiBuilder( MainConfig parent ) {
            super( parent, "anti_builder",
                    "Contains options related to the Anti-Builder / Alteration Negator" );
            
            antiBuilderBlocksBuilding = SPEC.define( new BooleanField( "blocks_building", false,
                    "If enabled, anti-builders will prevent most world interactions within their effective area.",
                    "This includes breaking blocks, placing blocks, explosions, mob griefing etc.",
                    "If this is disabled, the anti-builder will instead punish players with negative potion effects " +
                            "instead of just straight up disallowing the interaction." ) );
            
            SPEC.newLine();
            
            maladies = SPEC.define( new InjectionWrapperField<>( new RegistryValueListField<>( "maladies", createDefaultPlagueMap(),
                    "If the above setting is disabled, the anti-builder will pick a random potion effect from this list and inflict " +
                            "it on meddling players within its area instead of blocking building directly." ),
                    ( field ) -> AntiBuilderBlockEntity.refreshMaladiesList( field.get() ) ) );
        }
        
        private RegistryValueList<MobEffect, MobEffectStats> createDefaultPlagueMap() {
            return new RegistryValueList.Builder<>( ForgeRegistries.MOB_EFFECTS, MobEffectStats.CODEC )
                    .put( MobEffects.WITHER, new MobEffectStats( 80, 1 ) )
                    .put( MobEffects.POISON, new MobEffectStats( 80, 1 ) )
                    .put( MobEffects.HUNGER, new MobEffectStats( 240, 2 ) )
                    .put( MobEffects.MOVEMENT_SLOWDOWN, new MobEffectStats( 160, 2 ) )
                    .put( MobEffects.DIG_SLOWDOWN, new MobEffectStats( 160, 2 ) )
                    .put( MobEffects.UNLUCK, new MobEffectStats( 400, 3 ) )
                    .build();
        }
    }
}