package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.block.DisplayPedestalBlock;
import com.sarinsa.magical_relics.common.block.IllusionaryBlock;
import com.sarinsa.magical_relics.common.block.SpikeTrapBlock;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MRBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicalRelics.MODID);


    public static final RegistryObject<SpikeTrapBlock> SPIKE_TRAP = register("spike_trap", CreativeModeTab.TAB_BUILDING_BLOCKS, SpikeTrapBlock::new);
    public static final RegistryObject<IllusionaryBlock> ILLUSIONARY_BLOCK = register("illusionary_block", CreativeModeTab.TAB_BUILDING_BLOCKS, IllusionaryBlock::new);
    public static final RegistryObject<DisplayPedestalBlock> DISPLAY_PEDESTAL = register("display_pedestal", CreativeModeTab.TAB_DECORATIONS, DisplayPedestalBlock::new);



    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, CreativeModeTab tab, Supplier<T> block) {
        RegistryObject<T> blockRegObj = BLOCKS.register(name, block);
        MRItems.ITEMS.register(name, () -> new BlockItem(blockRegObj.get(), new Item.Properties().tab(tab)));
        return blockRegObj;
    }
}
