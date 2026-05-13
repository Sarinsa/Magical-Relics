package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.worldgen.processor.DisplayPedestalProcessor;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.field.collection.RegistrySetField;
import fathertoast.crust.api.config.common.field.collection.RegistryValueListField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.HexIntWrapper;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import fathertoast.crust.api.config.common.value.collection.RegistryValueList;
import fathertoast.crust.api.config.common.value.collection.value.MobEffectStats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@SuppressWarnings( "UnstableApiUsage" )
public class MainConfig extends AbstractConfigFile {
    
    public final Abilities ABILITIES;
    public final AntiBuilder ANTI_BUILDER;
    public final Misc MISC;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        ABILITIES = new Abilities( this );
        ANTI_BUILDER = new AntiBuilder( this );
        MISC = new Misc( this );
    }
    
    public static class Abilities extends AbstractConfigCategory<MainConfig> {
        
        public final IntField.RandomRange normalMaxAbilities;
        
        public final DoubleField legendaryChance;
        public final IntField.RandomRange legendaryEnchantLevel;
        public final IntField.RandomRange legendaryMaxAbilities;
        
        public final InjectionWrapperField<RegistrySetField<BaseArtifactAbility<?>>> unobtainableAbilities;
        
        public final InjectionWrapperField<PredicateStringListField> artifactColors;
        
        
        Abilities( MainConfig parent ) {
            super( parent, "abilities",
                    "Options to customize settings that apply to artifact abilities as a whole." );
            
            normalMaxAbilities = new IntField.RandomRange( SPEC, "normal_max_abilities", 1, 2, IntField.Range.POSITIVE,
                    "The minimum and maximum (inclusive) number of abilities that can be applied to non-legendary artifacts." );
            
            SPEC.newLine();
            
            legendaryChance = SPEC.define( new DoubleField( "legendary_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance in percentage for a randomly generated artifact to be of legendary quality." ) );
            
            SPEC.newLine();
            
            legendaryEnchantLevel = new IntField.RandomRange( SPEC, "legendary_enchant_level", 20, 30, IntField.Range.POSITIVE,
                    "The minimum and maximum (inclusive) level that legendary artifacts will be enchanted with when they are created." );
            
            SPEC.newLine();
            
            legendaryMaxAbilities = new IntField.RandomRange( SPEC, "legendary_max_abilities", 3, 4, IntField.Range.POSITIVE,
                    "The minimum and maximum (inclusive) number of abilities that can be applied to legendary artifacts." );
            
            SPEC.newLine();
            
            unobtainableAbilities = SPEC.define( new InjectionWrapperField<>( new RegistrySetField<>( "unobtainable_abilities",
                    createDefaultUnobtainableAbilities(),
                    "A list of artifact abilities that are blacklisted and cannot be obtained without using commands." ),
                    ArtifactUtils::refreshObtainableAbilities ) );
            
            SPEC.newLine();
            
            artifactColors = SPEC.define( new InjectionWrapperField<>( new PredicateStringListField( "artifact_colors", "Color", createDefaultColors(),
                    ( value ) -> TomlHelper.parseHexInt( value ) != null && value.length() <= 6,
                    "A list of colors to pick from when picking a random color for a randomly generated artifact item.",
                    "Note that armor artifact items are handled a bit differently and instead picks a random armor trim.",
                    "If this list is empty, a completely randomly generated color is picked.",
                    "Adding multiple entries with the same color to this list is allowed, and effectively increases the odds of said color being picked." ),
                    ArtifactUtils::refreshColorList )
            );
        }
        
        private RegistrySet<BaseArtifactAbility<?>> createDefaultUnobtainableAbilities() {
            return new RegistrySet.Builder<>( MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get() )
                    .add( MRArtifactAbilities.RESURRECT )
                    .add( MRArtifactAbilities.DEFLECT_PROJECTILE )
                    .build();
        }
        
        private List<String> createDefaultColors() {
            final List<Integer> colors = List.of(
                    0x00B6FF, 0x1466FF, 0x6647FF,
                    0xC23FFF, 0xFF00A5, 0xFF0010,
                    0xFF5F0F, 0xFF9D00, 0xFFE500,
                    0x2FBC00, 0x00BA6F, 0x37B7AA,
                    0x915E35, 0xC4746F, 0xC170BC,
                    0x84BF4E, 0x6B75BC, 0xD8D8D8
            );
            return colors.stream().map( ( val ) -> new HexIntWrapper( val, 6 ).toTomlLiteral().substring( 2 ) ).toList();
        }
    }
    
    public static class AntiBuilder extends AbstractConfigCategory<MainConfig> {
        
        public final BooleanField antiBuilderBlocksBuilding;
        
        public final InjectionWrapperField<RegistryValueListField<MobEffect, MobEffectStats>> maladies;
        
        
        public AntiBuilder( MainConfig parent ) {
            super( parent, "anti_builder",
                    "Contains options related to the anti-builder block (Core of Warding)" );
            
            antiBuilderBlocksBuilding = SPEC.define( new BooleanField( "blocks_building", false,
                    "If enabled, Core of Warding blocks will prevent most world interactions within their effective area.",
                    "This includes breaking blocks, placing blocks, explosions, mob griefing etc.",
                    "If this is disabled, the anti-builder will instead punish players with negative potion effects " +
                            "instead of just straight up disallowing the interaction." ) );
            
            SPEC.newLine();
            
            maladies = SPEC.define( new InjectionWrapperField<>( new RegistryValueListField<>( "maladies", createDefaultPlagueMap(),
                    "If the above setting is disabled, the anti-builder will pick a random potion effect from this list and inflict " +
                            "it on meddling players within its area instead of blocking building directly.",
                    "You can effectively disable anti-builders by emptying this list and disabling the \"blocks_building\" setting." ),
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
    
    public static class Misc extends AbstractConfigCategory<MainConfig> {
        
        public final InjectionWrapperField<RegistrySetField<Item>> wizardFavoriteBlacklist;
        
        
        Misc( MainConfig parent ) {
            super( parent, "misc",
                    "Options that do not fit in other categories." );
            
            wizardFavoriteBlacklist = SPEC.define( new InjectionWrapperField<>( new RegistrySetField<>( "wizards_favorite_blacklist",
                    createDefaultWizFavoriteBlacklist(),
                    "A list of items that should not be findable in \"Wizard's Favorite\" display pedestals in wizard tower structures." ),
                    ( field ) -> DisplayPedestalProcessor.refreshWizFavorites( field.get() ) ) );
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
}