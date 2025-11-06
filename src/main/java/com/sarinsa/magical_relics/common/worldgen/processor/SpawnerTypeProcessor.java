package com.sarinsa.magical_relics.common.worldgen.processor;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Looks for spawner blocks and picks a new random mob type for each spawner.
 * Possible types depend on processor config.
 */
public class SpawnerTypeProcessor extends StructureProcessor {
    
    public static final Codec<SpawnerTypeProcessor> CODEC = Codec.list( ResourceLocation.CODEC ).fieldOf( "possible_types" )
            .xmap( SpawnerTypeProcessor::new, ( processor ) -> processor.possibleTypes )
            .codec();
    
    private final List<ResourceLocation> possibleTypes;
    private final List<EntityType<?>> entityTypes = new ArrayList<>();
    
    
    public SpawnerTypeProcessor( List<ResourceLocation> possibleTypes ) {
        this.possibleTypes = possibleTypes;
        
        for( ResourceLocation id : possibleTypes ) {
            if( ForgeRegistries.ENTITY_TYPES.containsKey( id ) ) {
                entityTypes.add( ForgeRegistries.ENTITY_TYPES.getValue( id ) );
            }
            else {
                MagicalRelics.LOG.warn( "Invalid entity type ID found in SpawnerTypeProcessor: '{}'. Does not exist in the registry!", id.toString() );
            }
        }
    }
    
    
    @Nullable
    @SuppressWarnings( "ConstantConditions" )
    public StructureTemplate.StructureBlockInfo process( LevelReader level, BlockPos pos, BlockPos p_74142_, StructureTemplate.StructureBlockInfo info, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings structureSettings, @Nullable StructureTemplate template ) {
        RandomSource random = structureSettings.getRandom( blockInfo.pos() );
        BlockState state = blockInfo.state();
        BlockPos blockpos = blockInfo.pos();
        
        boolean isSpawner = state.is( Blocks.SPAWNER );
        CompoundTag tag = blockInfo.nbt();
        
        if( isSpawner ) {
            if( tag == null ) tag = new CompoundTag();
            EntityType<?> entityType = entityTypes.get( random.nextInt( entityTypes.size() ) );
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey( entityType );
            CompoundTag spawnTag = new CompoundTag();
            spawnTag.putString( "id", id.toString() );
            
            Optional<SpawnData.CustomSpawnRules> spawnRules = Optional.empty();
            
            // TODO - Make config list of entity types that should ignore light value
            if( entityType == EntityType.SLIME ) {
                spawnRules = Optional.of( new SpawnData.CustomSpawnRules( new InclusiveRange<>( 0, 15 ), new InclusiveRange<>( 0, 15 ) ) );
            }
            tag.put( "SpawnData", SpawnData.CODEC.encodeStart( NbtOps.INSTANCE, new SpawnData( spawnTag, spawnRules ) ).result().orElseThrow( ()
                    -> new IllegalStateException( "Invalid SpawnData" ) ) );
        }
        return isSpawner ? new StructureTemplate.StructureBlockInfo( blockpos, state, tag ) : blockInfo;
    }
    
    @Override
    protected StructureProcessorType<?> getType() {
        return MRStructureProcessors.SPAWNER_TYPE.get();
    }
}
