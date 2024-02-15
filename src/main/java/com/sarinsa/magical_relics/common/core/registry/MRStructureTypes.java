package com.sarinsa.magical_relics.common.core.registry;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.worldgen.structure.SmallWizardTowerStructure;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MRStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, MagicalRelics.MODID);


    public static final RegistryObject<StructureType<SmallWizardTowerStructure>> SMALL_WIZARD_TOWER = STRUCTURES.register("small_wizard_tower", () -> type(SmallWizardTowerStructure.CODEC));


    private static <T extends Structure> StructureType<T> type(Codec<T> codec) {
        return () -> codec;
    }
}
