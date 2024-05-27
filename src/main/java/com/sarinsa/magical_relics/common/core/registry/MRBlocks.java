package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.block.*;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class MRBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicalRelics.MODID);


    public static final Map<RegistryObject<WallPressurePlateBlock>, Block> WALL_PRESSURE_PLATES = new HashMap<>();
    public static final Map<RegistryObject<CrumblingBlock>, Block> CRUMBLING_BLOCKS = new HashMap<>();

    /** Used for data generation when looking up what tags a block should be in. */
    public static final Map<RegistryObject<? extends Block>, TagKey<Block>[]> BLOCK_TAGS = new HashMap<>();


    public static final RegistryObject<Block> SOLID_AIR = registerNoItem("solid_air", () -> new SolidAirBlock(BlockBehaviour.Properties.of().noLootTable().noOcclusion()));
    public static final RegistryObject<Block> ILLUMINATION_BLOCK = registerNoItem("illumination_block", IlluminationBlock::new);

    public static final RegistryObject<SpikeTrapBlock> SPIKE_TRAP = register("spike_trap", MRCreativeTabs.MOD_TAB.getKey(), SpikeTrapBlock::new, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<QuicksandBlock> QUICKSAND = register("quicksand", MRCreativeTabs.MOD_TAB.getKey(), QuicksandBlock::new, BlockTags.MINEABLE_WITH_SHOVEL);
    public static final RegistryObject<CamoDispenserBlock> CAMO_DISPENSER = register("camo_dispenser", MRCreativeTabs.MOD_TAB.getKey(), CamoDispenserBlock::new, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<CamoTripwireHookBlock> CAMO_TRIPWIRE_HOOK = register("camo_tripwire_hook", MRCreativeTabs.MOD_TAB.getKey(), CamoTripwireHookBlock::new);
    public static final RegistryObject<TripWireBlock> THICK_TRIPWIRE = registerNoItem("thick_tripwire", () -> new ThickTripwireBlock(CAMO_TRIPWIRE_HOOK.get(), BlockBehaviour.Properties.copy(Blocks.TRIPWIRE)));
    public static final RegistryObject<IllusionaryBlock> ILLUSIONARY_BLOCK = register("illusionary_block", MRCreativeTabs.MOD_TAB.getKey(), () -> new IllusionaryBlock(BlockBehaviour.Properties.of().strength(0.8F).noLootTable().noOcclusion().isViewBlocking((state, level, pos) -> false)));
    public static final RegistryObject<DisplayPedestalBlock> DISPLAY_PEDESTAL = register("display_pedestal", MRCreativeTabs.MOD_TAB.getKey(), DisplayPedestalBlock::new, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<AntiBuilderBlock> ANTI_BUILDER = register("anti_builder", MRCreativeTabs.MOD_TAB.getKey(), AntiBuilderBlock::new);

    public static final RegistryObject<CrumblingBlock> CRUMBLING_COBBLESTONE = crumblingBlock("crumbling_cobblestone", Blocks.COBBLESTONE, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_MOSSY_COBBLESTONE = crumblingBlock("crumbling_mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_STONE = crumblingBlock("crumbling_stone", Blocks.STONE, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_STONE_BRICKS = crumblingBlock("crumbling_stone_bricks", Blocks.STONE_BRICKS, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<CrumblingBlock> CRUMBLING_MOSSY_STONE_BRICKS = crumblingBlock("crumbling_mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS, BlockTags.MINEABLE_WITH_PICKAXE);

    public static final RegistryObject<WallPressurePlateBlock> COBBLE_WALL_PRESSURE_PLATE = wallPressurePlate("cobblestone_wall_pressure_plate", Blocks.COBBLESTONE, BlockSetType.STONE, true, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<WallPressurePlateBlock> STONE_WALL_PRESSURE_PLATE = wallPressurePlate("stone_wall_pressure_plate", Blocks.STONE, BlockSetType.STONE, true, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<WallPressurePlateBlock> STONE_BRICKS_WALL_PRESSURE_PLATE = wallPressurePlate("stone_bricks_wall_pressure_plate", Blocks.STONE_BRICKS, BlockSetType.STONE, true, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<WallPressurePlateBlock> OBSIDIAN_WALL_PRESSURE_PLATE = wallPressurePlate("obsidian_wall_pressure_plate", Blocks.OBSIDIAN, BlockSetType.STONE, true, BlockTags.MINEABLE_WITH_PICKAXE);
    public static final RegistryObject<WallPressurePlateBlock> OAK_WALL_PRESSURE_PLATE = wallPressurePlate("oak_wall_pressure_plate", Blocks.OAK_PLANKS, BlockSetType.OAK, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> BIRCH_WALL_PRESSURE_PLATE = wallPressurePlate("birch_wall_pressure_plate", Blocks.BIRCH_PLANKS, BlockSetType.BIRCH, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> SPRUCE_WALL_PRESSURE_PLATE = wallPressurePlate("spruce_wall_pressure_plate", Blocks.SPRUCE_PLANKS, BlockSetType.SPRUCE, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> JUNGLE_WALL_PRESSURE_PLATE = wallPressurePlate("jungle_wall_pressure_plate", Blocks.JUNGLE_PLANKS, BlockSetType.JUNGLE, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> ACACIA_WALL_PRESSURE_PLATE = wallPressurePlate("acacia_wall_pressure_plate", Blocks.ACACIA_PLANKS, BlockSetType.ACACIA, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> DARK_OAK_WALL_PRESSURE_PLATE = wallPressurePlate("dark_oak_wall_pressure_plate", Blocks.DARK_OAK_PLANKS, BlockSetType.DARK_OAK, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> MANGROVE_WALL_PRESSURE_PLATE = wallPressurePlate("mangrove_wall_pressure_plate", Blocks.MANGROVE_PLANKS, BlockSetType.MANGROVE, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> WARPED_WALL_PRESSURE_PLATE = wallPressurePlate("warped_wall_pressure_plate", Blocks.WARPED_PLANKS, BlockSetType.WARPED, false, BlockTags.MINEABLE_WITH_AXE);
    public static final RegistryObject<WallPressurePlateBlock> CRIMSON_WALL_PRESSURE_PLATE = wallPressurePlate("crimson_wall_pressure_plate", Blocks.CRIMSON_PLANKS, BlockSetType.CRIMSON, false, BlockTags.MINEABLE_WITH_AXE);



    private static RegistryObject<WallPressurePlateBlock> wallPressurePlate(String name, Block parent, BlockSetType blockSetType, boolean needsTools, TagKey<Block>... tags) {
        RegistryObject<WallPressurePlateBlock> regObj = BLOCKS.register(name, () -> {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.copy(parent);
            if (needsTools) properties = properties.requiresCorrectToolForDrops();

            return new WallPressurePlateBlock(properties, blockSetType);
        });
        MRItems.register(name, () -> new BlockItem(regObj.get(), new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
        WALL_PRESSURE_PLATES.put(regObj, parent);

        if (tags != null && tags.length > 0) {
            BLOCK_TAGS.put(regObj, tags);
        }
        return regObj;
    }

    private static RegistryObject<CrumblingBlock> crumblingBlock(String name, Block parent, TagKey<Block>... tags) {
        RegistryObject<CrumblingBlock> regObj = BLOCKS.register(name, () -> new CrumblingBlock(BlockBehaviour.Properties.copy(parent).noLootTable()));
        MRItems.register(name, () -> new BlockItem(regObj.get(), new Item.Properties()), MRCreativeTabs.MOD_TAB.getKey());
        CRUMBLING_BLOCKS.put(regObj, parent);

        if (tags != null && tags.length > 0) {
            BLOCK_TAGS.put(regObj, tags);
        }
        return regObj;
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block, TagKey<Block>... tags) {
        RegistryObject<T> regObj = BLOCKS.register(name, block);

        if (tags != null && tags.length > 0) {
            BLOCK_TAGS.put(regObj, tags);
        }
        return regObj;
    }

    private static <T extends Block> RegistryObject<T> register(String name, ResourceKey<CreativeModeTab> tab, Supplier<T> block, TagKey<Block>... tags) {
        RegistryObject<T> regObj = BLOCKS.register(name, block);
        MRItems.register(name, () -> new BlockItem(regObj.get(), new Item.Properties()), tab);

        if (tags != null && tags.length > 0) {
            BLOCK_TAGS.put(regObj, tags);
        }
        return regObj;
    }
}
