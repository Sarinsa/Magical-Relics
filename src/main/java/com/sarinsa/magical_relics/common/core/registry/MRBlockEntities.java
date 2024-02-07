package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.blockentity.*;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MagicalRelics.MODID);




    public static final RegistryObject<BlockEntityType<AntiBuilderBlockEntity>> ANTI_BUILDER =
            register("anti_builder", () -> BlockEntityType.Builder.of(AntiBuilderBlockEntity::new, MRBlocks.ANTI_BUILDER.get()));

    public static final RegistryObject<BlockEntityType<DisplayPedestalBlockEntity>> DISPLAY_PEDESTAL =
            register("display_pedestal", () -> BlockEntityType.Builder.of(DisplayPedestalBlockEntity::new, MRBlocks.DISPLAY_PEDESTAL.get()));

    public static final RegistryObject<BlockEntityType<CamoDispenserBlockEntity>> CAMO_DISPENSER =
            register("camo_dispenser", () -> BlockEntityType.Builder.of(CamoDispenserBlockEntity::new, MRBlocks.CAMO_DISPENSER.get()));

    public static final RegistryObject<BlockEntityType<CamoTripwireBlockEntity>> CAMO_TRIPWIRE_HOOK =
            register("camo_tripwire_hook", () -> BlockEntityType.Builder.of(CamoTripwireBlockEntity::new, MRBlocks.CAMO_TRIPWIRE_HOOK.get()));

    public static final RegistryObject<BlockEntityType<IllusionaryBlockEntity>> ILLUSIONARY_BLOCK =
            register("illusionary_block", () -> BlockEntityType.Builder.of(IllusionaryBlockEntity::new, MRBlocks.ILLUSIONARY_BLOCK.get()));



    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType.Builder<T>> builder) {
        return BLOCK_ENTITIES.register(name, () -> builder.get().build(null));
    }
}
