package com.sarinsa.magical_relics.common.core.registry;

import com.mojang.serialization.Codec;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.worldgen.structure.NormalDungeonsStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MRStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, MagicalRelics.MODID);


    public static final RegistryObject<StructureType<NormalDungeonsStructure>> NORMAL_DUNGEON = STRUCTURES.register("normal_dungeon", () -> type(NormalDungeonsStructure.CODEC));


    private static <T extends Structure> StructureType<T> type(Codec<T> codec) {
        return () -> codec;
    }
}
