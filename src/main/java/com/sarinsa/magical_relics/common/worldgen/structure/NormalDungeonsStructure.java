package com.sarinsa.magical_relics.common.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sarinsa.magical_relics.common.core.registry.MRStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;


@SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
public class NormalDungeonsStructure extends Structure {
    
    public static final Codec<NormalDungeonsStructure> CODEC = RecordCodecBuilder.<NormalDungeonsStructure>mapCodec( instance ->
            instance.group( NormalDungeonsStructure.settingsCodec( instance ),
                    StructureTemplatePool.CODEC.fieldOf( "start_pool" ).forGetter( structure -> structure.startPool ),
                    ResourceLocation.CODEC.optionalFieldOf( "start_jigsaw_name" ).forGetter( structure -> structure.startJigsawName ),
                    Codec.intRange( 0, 30 ).fieldOf( "size" ).forGetter( structure -> structure.size ),
                    HeightProvider.CODEC.fieldOf( "start_height" ).forGetter( structure -> structure.startHeight ),
                    Heightmap.Types.CODEC.optionalFieldOf( "project_start_to_heightmap" ).forGetter( structure -> structure.projectStartToHeightmap ),
                    Codec.intRange( 1, 128 ).fieldOf( "max_distance_from_center" ).forGetter( structure -> structure.maxDistanceFromCenter ),
                    Codec.intRange( -20, 200 ).fieldOf( "max_y" ).forGetter( structure -> structure.maxY ),
                    Codec.BOOL.fieldOf( "can_generate_in_water" ).forGetter( structure -> structure.canGenerateInWater )
            ).apply( instance, NormalDungeonsStructure::new ) ).codec();
    
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final int maxY;
    private final boolean canGenerateInWater;
    
    
    public NormalDungeonsStructure( Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int size,
                                    HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter, int maxY, boolean canGenerateInWater ) {
        super( config );
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.maxY = maxY;
        this.canGenerateInWater = canGenerateInWater;
    }
    
    private static boolean extraSpawningChecks( Structure.GenerationContext context, int maxY, boolean canGenerateInWater ) {
        ChunkPos chunkpos = context.chunkPos();
        
        if( !canGenerateInWater ) {
            // Don't generate in water. Checks middle of chunk and all corners.
            BlockPos middle = chunkpos.getMiddleBlockPosition( 0 );
            
            if( isWaterAt( context, middle.getX(), middle.getY() ) ||
                    isWaterAt( context, chunkpos.getMinBlockX(), chunkpos.getMinBlockZ() ) ||
                    isWaterAt( context, chunkpos.getMinBlockX(), chunkpos.getMaxBlockZ() ) ||
                    isWaterAt( context, chunkpos.getMaxBlockX(), chunkpos.getMinBlockZ() ) ||
                    isWaterAt( context, chunkpos.getMaxBlockX(), chunkpos.getMaxBlockZ() ) ) {
                return false;
            }
        }
        
        // Altitude limitation
        return context.chunkGenerator().getFirstOccupiedHeight(
                chunkpos.getMinBlockX(),
                chunkpos.getMinBlockZ(),
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                context.heightAccessor(),
                context.randomState() ) <= maxY;
        
    }
    
    /**
     * Checks if the top block at the given X and Z coordinates is a fluid, using WORLD_SURFACE_WG heightmap.
     */
    private static boolean isWaterAt( Structure.GenerationContext context, int x, int z ) {
        int landHeight = context.chunkGenerator().getFirstOccupiedHeight( x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState() );
        NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn( x, z, context.heightAccessor(), context.randomState() );
        BlockState topBlock = columnOfBlocks.getBlock( landHeight );
        
        return !topBlock.getFluidState().isEmpty();
    }
    
    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint( Structure.GenerationContext context ) {
        if( !extraSpawningChecks( context, maxY, canGenerateInWater ) )
            return Optional.empty();
        
        int startY = startHeight.sample( context.random(), new WorldGenerationContext( context.chunkGenerator(), context.heightAccessor() ) );
        
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos( chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ() );
        
        return JigsawPlacement.addPieces(
                context,
                startPool,
                startJigsawName,
                size,
                blockPos,
                false,
                projectStartToHeightmap,
                maxDistanceFromCenter );
    }
    
    @Override
    public StructureType<?> type() {
        return MRStructureTypes.NORMAL_DUNGEON.get();
    }
}
