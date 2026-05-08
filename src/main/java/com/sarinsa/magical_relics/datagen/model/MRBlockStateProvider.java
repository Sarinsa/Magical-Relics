package com.sarinsa.magical_relics.datagen.model;

import com.sarinsa.magical_relics.common.block.CrumblingBlock;
import com.sarinsa.magical_relics.common.block.WallPressurePlateBlock;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class MRBlockStateProvider extends BlockStateProvider {
    
    public MRBlockStateProvider( DataGenerator gen, ExistingFileHelper exFileHelper ) {
        super( gen.getPackOutput(), MagicalRelics.MODID, exFileHelper );
    }
    
    @Override
    protected void registerStatesAndModels() {
        MRBlocks.WALL_PRESSURE_PLATES.forEach( this::wallPressurePlate );
        MRBlocks.CRUMBLING_BLOCKS.forEach( this::crumblingBlock );
        
        tripwire( MRBlocks.THICK_TRIPWIRE );
    }
    
    /**
     * @param regObj       The block registry object
     * @param textureBlock The block to use as "texture parent"
     */
    private void wallPressurePlate( RegistryObject<WallPressurePlateBlock> regObj, Block textureBlock ) {
        getVariantBuilder( regObj.get() ).forAllStates( ( state ) -> {
            boolean powered = state.getValue( WallPressurePlateBlock.POWERED );
            Direction direction = state.getValue( WallPressurePlateBlock.FACING );
            ResourceLocation parentModel = resLoc( "block/template/wall_pressure_plate" );
            ResourceLocation parentModelActive = resLoc( "block/template/wall_pressure_plate_active" );
            
            return ConfiguredModel.builder()
                    .modelFile( models().withExistingParent( powered ? name( regObj.get() ) + "_active" : name( regObj.get() ), powered ? parentModelActive : parentModel )
                            .texture( "texture", blockTexture( textureBlock ) ) )
                    .rotationY( (int) direction.toYRot() )
                    .build();
        } );
        itemModels().withExistingParent( name( regObj.get() ), resLoc( "block/" + name( regObj.get() ) ) );
    }
    
    private void crumblingBlock( RegistryObject<CrumblingBlock> regObj, Block textureBlock ) {
        ModelFile crumbleModel = models().withExistingParent( name( regObj.get() ) + "_crumbling", resLoc( "block/template/layer_cube" ) )
                .texture( "texture", blockTexture( textureBlock ) )
                .texture( "crumble", mcLoc( "block/destroy_stage_4" ) )
                .renderType( "cutout" );
        ModelFile baseModel = models().withExistingParent( name( regObj.get() ), mcLoc( "block/cube_all" ) )
                .texture( "all", blockTexture( textureBlock ) );
        
        
        getVariantBuilder( regObj.get() ).forAllStates( ( state ) -> {
            boolean crumbling = state.getValue( CrumblingBlock.CRUMBLING );
            
            if( crumbling ) {
                return ConfiguredModel.builder()
                        .modelFile( crumbleModel )
                        .build();
            }
            else {
                return ConfiguredModel.builder()
                        .modelFile( baseModel )
                        .build();
            }
        } );
        simpleBlockItem( regObj.get(), baseModel );
    }
    
    private void tripwire( RegistryObject<TripWireBlock> regObj ) {
        final TripWireBlock block = regObj.get();
        final ResourceLocation texture = blockTexture( block );
        final String blockName = name( regObj.get() );
        
        final ModelFile defaultModel = models()
                .withExistingParent( blockName + "_default", resLoc( "block/template/base_default_tripwire" ) )
                .texture( "texture", texture )
                .texture( "particle", texture )
                .renderType( "tripwire" );
        
        final ModelFile normalModel = models()
                .withExistingParent( blockName, resLoc( "block/template/base_tripwire" ) )
                .texture( "texture", texture )
                .texture( "particle", texture )
                .renderType( "tripwire" );
        
        final ModelFile attachedModel = models()
                .withExistingParent( blockName + "_attached", resLoc( "block/template/base_tripwire_attached" ) )
                .texture( "texture", texture )
                .texture( "particle", texture )
                .renderType( "tripwire" );
        
        final MultiPartBlockStateBuilder builder = getMultipartBuilder( block );
        final MultiPartBlockStateBuilder.PartBuilder defaultPart = builder.part().modelFile( defaultModel ).addModel();
        
        CrossCollisionBlock.PROPERTY_BY_DIRECTION.forEach( ( dir, property ) -> {
            if( dir.getAxis().isHorizontal() ) {
                builder.part().modelFile( normalModel ).rotationY( (((int) dir.toYRot()) + 180) % 360 ).uvLock( false ).addModel()
                        .condition( property, true ).condition( TripWireBlock.ATTACHED, false );
                
                builder.part().modelFile( attachedModel ).rotationY( (((int) dir.toYRot()) + 180) % 360 ).uvLock( false ).addModel()
                        .condition( property, true ).condition( TripWireBlock.ATTACHED, true );
                
                defaultPart.condition( property, false );
            }
        } );
    }
    
    protected String name( Block block ) {
        return Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( block ) ).getPath();
    }
    
    private static ResourceLocation resLoc( String path ) {
        return MagicalRelics.rl( path );
    }
    
    public static ResourceLocation texture( String textureName ) {
        return resLoc( "block/" + textureName );
    }
    
    public static ResourceLocation itemTexture( String textureName ) {
        return resLoc( "item/" + textureName );
    }
    
    public static ResourceLocation blockTextureWith( Block block, String suffix ) {
        ResourceLocation name = key( block );
        return ResourceLocation.fromNamespaceAndPath( name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + "_" + suffix );
    }
    
    public static ResourceLocation key( Block block ) {
        return ForgeRegistries.BLOCKS.getKey( block );
    }
}
