package com.sarinsa.magical_relics.common.core.registry;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.worldgen.processor.CustomMossifierProcessor;
import com.sarinsa.magical_relics.common.worldgen.processor.DisplayPedestalProcessor;
import com.sarinsa.magical_relics.common.worldgen.structure.SmallWizardTowerStructure;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MRStructureProcessors {

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registry.STRUCTURE_PROCESSOR_REGISTRY, MagicalRelics.MODID);


    public static final RegistryObject<StructureProcessorType<DisplayPedestalProcessor>> DISPLAY_PEDESTAL = PROCESSORS.register("display_pedestal", () -> type(DisplayPedestalProcessor.CODEC));
    public static final RegistryObject<StructureProcessorType<CustomMossifierProcessor>> CUSTOM_MOSSIFIER = PROCESSORS.register("custom_mossifier", () -> type(CustomMossifierProcessor.CODEC));


    private static <T extends StructureProcessor> StructureProcessorType<T> type(Codec<T> codec) {
        return () -> codec;
    }
}
