package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.block.*;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MRBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicalRelics.MODID);


    public static final Map<RegistryObject<WallPressurePlateBlock>, Block> WALL_PRESSURE_PLATES = new HashMap<>();
    public static final Map<RegistryObject<CrumblingBlock>, Block> CRUMBLING_BLOCKS = new HashMap<>();


    public static final RegistryObject<Block> SOLID_AIR = registerNoItem("solid_air", () -> new SolidAirBlock(BlockBehaviour.Properties.of(Material.AIR).noLootTable().air().noOcclusion()));
    public static final RegistryObject<TripWireBlock> THICK_TRIPWIRE = register("thick_tripwire", MRItems.MRCreativeTab.MOD_TAB, () -> new ThickTripwireBlock(MRBlocks.CAMO_TRIPWIRE_HOOK.get(), BlockBehaviour.Properties.copy(Blocks.TRIPWIRE)));

    public static final RegistryObject<SpikeTrapBlock> SPIKE_TRAP = register("spike_trap", MRItems.MRCreativeTab.MOD_TAB, SpikeTrapBlock::new);
    public static final RegistryObject<QuicksandBlock> QUICKSAND = register("quicksand", MRItems.MRCreativeTab.MOD_TAB, QuicksandBlock::new);
    public static final RegistryObject<CamoDispenserBlock> CAMO_DISPENSER = register("camo_dispenser", MRItems.MRCreativeTab.MOD_TAB, CamoDispenserBlock::new);
    public static final RegistryObject<CamoTripwireHookBlock> CAMO_TRIPWIRE_HOOK = register("camo_tripwire_hook", MRItems.MRCreativeTab.MOD_TAB, CamoTripwireHookBlock::new);
    public static final RegistryObject<DisplayPedestalBlock> DISPLAY_PEDESTAL = register("display_pedestal", MRItems.MRCreativeTab.MOD_TAB, DisplayPedestalBlock::new);
    public static final RegistryObject<AntiBuilderBlock> ANTI_BUILDER = register("anti_builder", MRItems.MRCreativeTab.MOD_TAB, AntiBuilderBlock::new);

    public static final RegistryObject<CrumblingBlock> CRUMBLING_COBBLESTONE = crumblingBlock("crumbling_cobblestone", Blocks.COBBLESTONE);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_MOSSY_COBBLESTONE = crumblingBlock("crumbling_mossy_cobblestone", Blocks.MOSSY_COBBLESTONE);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_STONE = crumblingBlock("crumbling_stone", Blocks.STONE);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_STONE_BRICKS = crumblingBlock("crumbling_stone_bricks", Blocks.STONE_BRICKS);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_MOSSY_STONE_BRICKS = crumblingBlock("crumbling_mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS);

    public static final RegistryObject<WallPressurePlateBlock> COBBLE_WALL_PRESSURE_PLATE = wallPressurePlate("cobblestone_wall_pressure_plate", Blocks.COBBLESTONE);
    public static final RegistryObject<WallPressurePlateBlock> STONE_WALL_PRESSURE_PLATE = wallPressurePlate("stone_wall_pressure_plate", Blocks.STONE);
    public static final RegistryObject<WallPressurePlateBlock> STONE_BRICKS_WALL_PRESSURE_PLATE = wallPressurePlate("stone_bricks_wall_pressure_plate", Blocks.STONE_BRICKS);
    public static final RegistryObject<WallPressurePlateBlock> OBSIDIAN_WALL_PRESSURE_PLATE = wallPressurePlate("obsidian_wall_pressure_plate", Blocks.OBSIDIAN);
    public static final RegistryObject<WallPressurePlateBlock> OAK_WALL_PRESSURE_PLATE = wallPressurePlate("oak_wall_pressure_plate", Blocks.OAK_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> BIRCH_WALL_PRESSURE_PLATE = wallPressurePlate("birch_wall_pressure_plate", Blocks.BIRCH_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> SPRUCE_WALL_PRESSURE_PLATE = wallPressurePlate("spruce_wall_pressure_plate", Blocks.SPRUCE_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> JUNGLE_WALL_PRESSURE_PLATE = wallPressurePlate("jungle_wall_pressure_plate", Blocks.JUNGLE_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> ACACIA_WALL_PRESSURE_PLATE = wallPressurePlate("acacia_wall_pressure_plate", Blocks.ACACIA_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> DARK_OAK_WALL_PRESSURE_PLATE = wallPressurePlate("dark_oak_wall_pressure_plate", Blocks.DARK_OAK_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> MANGROVE_WALL_PRESSURE_PLATE = wallPressurePlate("mangrove_wall_pressure_plate", Blocks.MANGROVE_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> WARPED_WALL_PRESSURE_PLATE = wallPressurePlate("warped_wall_pressure_plate", Blocks.WARPED_PLANKS);
    public static final RegistryObject<WallPressurePlateBlock> CRIMSON_WALL_PRESSURE_PLATE = wallPressurePlate("crimson_wall_pressure_plate", Blocks.CRIMSON_PLANKS);




    private static RegistryObject<WallPressurePlateBlock> wallPressurePlate(String name, Block parent) {
        RegistryObject<WallPressurePlateBlock> regObj = BLOCKS.register(name, () -> new WallPressurePlateBlock(BlockBehaviour.Properties.copy(parent)));
        MRItems.ITEMS.register(name, () -> new BlockItem(regObj.get(), new Item.Properties().tab(MRItems.MRCreativeTab.MOD_TAB)));
        WALL_PRESSURE_PLATES.put(regObj, parent);
        return regObj;
    }

    private static RegistryObject<CrumblingBlock> crumblingBlock(String name, Block parent) {
        RegistryObject<CrumblingBlock> regObj = BLOCKS.register(name, () -> new CrumblingBlock(BlockBehaviour.Properties.copy(parent)));
        MRItems.ITEMS.register(name, () -> new BlockItem(regObj.get(), new Item.Properties().tab(MRItems.MRCreativeTab.MOD_TAB)));
        CRUMBLING_BLOCKS.put(regObj, parent);
        return regObj;
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, CreativeModeTab tab, Supplier<T> block) {
        RegistryObject<T> blockRegObj = BLOCKS.register(name, block);
        MRItems.ITEMS.register(name, () -> new BlockItem(blockRegObj.get(), new Item.Properties().tab(tab)));
        return blockRegObj;
    }
}
