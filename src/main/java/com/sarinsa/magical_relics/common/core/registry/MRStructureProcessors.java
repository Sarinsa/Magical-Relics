package com.sarinsa.magical_relics.common.core.registry;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.worldgen.processor.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MRStructureProcessors {
    
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create( Registries.STRUCTURE_PROCESSOR, MagicalRelics.MODID );
    
    
    public static final RegistryObject<StructureProcessorType<DisplayPedestalProcessor>> DISPLAY_PEDESTAL = PROCESSORS.register( "display_pedestal", () -> type( DisplayPedestalProcessor.CODEC ) );
    public static final RegistryObject<StructureProcessorType<CustomAgingProcessor>> CUSTOM_MOSSIFIER = PROCESSORS.register( "custom_aging", () -> type( CustomAgingProcessor.CODEC ) );
    public static final RegistryObject<StructureProcessorType<SpawnerTypeProcessor>> SPAWNER_TYPE = PROCESSORS.register( "spawner_type", () -> type( SpawnerTypeProcessor.CODEC ) );
    public static final RegistryObject<StructureProcessorType<PotPlantProcessor>> POT_PLANT = PROCESSORS.register( "pot_plant", () -> type( PotPlantProcessor.CODEC ) );
    public static final RegistryObject<StructureProcessorType<ChiseledBookshelfProcessor>> CHISELED_BOOKSHELF = PROCESSORS.register( "chiseled_bookshelf", () -> type( ChiseledBookshelfProcessor.CODEC ) );
    public static final RegistryObject<StructureProcessorType<NoWaterloggingProcessor>> NO_WATERLOGGING = PROCESSORS.register( "no_waterlogging", () -> type( NoWaterloggingProcessor.CODEC ) );
    
    
    private static <T extends StructureProcessor> StructureProcessorType<T> type( Codec<T> codec ) {
        return () -> codec;
    }
}
