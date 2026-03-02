package com.sarinsa.magical_relics.datagen.model;

import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class MRItemModelProvider extends ItemModelProvider {
    
    public MRItemModelProvider( DataGenerator generator, ExistingFileHelper fileHelper ) {
        super( generator.getPackOutput(), MagicalRelics.MODID, fileHelper );
    }
    
    @Override
    protected void registerModels() {
        MRItems.ARTIFACTS_BY_CATEGORY.forEach( this::artifactSet );
    }
    
    private void artifactSet( ArtifactCategory category, List<RegistryObject<? extends Item>> artifactSet ) {
        for( RegistryObject<? extends Item> regObj : artifactSet ) {
            // TODO - skip armor for now. We don't have variants, just trims
            if( regObj.get() instanceof ArmorItem ) continue;
            
            ResourceLocation itemId = regObj.getId();
            String categoryName = category.getName();
            String parentModel = "item/generated";
            
            if( category == ArtifactCategory.SWORD || category == ArtifactCategory.DAGGER
                    || category == ArtifactCategory.STAFF || category == ArtifactCategory.WAND || category == ArtifactCategory.AXE )
                parentModel = "item/handheld";
            
            ItemModelBuilder builder = getBuilder( itemId.toString() )
                    .parent( new ModelFile.UncheckedModelFile( parentModel ) )
                    .texture( "layer0", modArtifactTexture( categoryName, categoryName + "1" ) );
            
            for( int i = 1; i < category.getVariations() + 1; ++i ) {
                ItemModelBuilder subModelBuilder = getBuilder( MagicalRelics.MODID + ":" + categoryName + i )
                        .parent( new ModelFile.UncheckedModelFile( parentModel ) )
                        .texture( "layer0", modArtifactTexture( categoryName, categoryName + i ) )
                        .texture( "layer1", modArtifactTexture( categoryName, categoryName + i + "_overlay" ) );
                
                builder.override()
                        .predicate( MagicalRelics.rl( categoryName + "_variant" ), i )
                        .model( new ModelFile.UncheckedModelFile( subModelBuilder.getUncheckedLocation() ) );
            }
        }
    }
    
    private static ResourceLocation modTexture( String name ) {
        return MagicalRelics.rl( "item/" + name );
    }
    
    private static ResourceLocation modArtifactTexture( String type, String name ) {
        return MagicalRelics.rl( "item/" + type + "/" + name );
    }
}
