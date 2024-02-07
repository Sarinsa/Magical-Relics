package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.client.renderer.block.CamoTrapRenderer;
import com.sarinsa.magical_relics.client.renderer.block.DisplayPedestalRenderer;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.core.registry.util.ArtifactSet;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.RenderShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = MagicalRelics.MODID)
public class ClientRegister {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventListener());

        ItemBlockRenderTypes.setRenderLayer(MRBlocks.THICK_TRIPWIRE.get(), RenderType.tripwire());

        ItemModelProps.register();
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MRBlockEntities.DISPLAY_PEDESTAL.get(), DisplayPedestalRenderer::new);
        event.registerBlockEntityRenderer(MRBlockEntities.CAMO_DISPENSER.get(), CamoTrapRenderer::new);
        event.registerBlockEntityRenderer(MRBlockEntities.CAMO_TRIPWIRE_HOOK.get(), CamoTrapRenderer::new);
        event.registerBlockEntityRenderer(MRBlockEntities.ILLUSIONARY_BLOCK.get(), CamoTrapRenderer::new);
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        // All artifact items (including armor)
        for (ArtifactSet<List<RegistryObject<Item>>> artifactSet : MRItems.ALL_ARTIFACTS) {
            for (RegistryObject<Item> regObj : artifactSet.dataStructure()) {
                event.register((itemStack, index) -> {
                    if (index > 0) {
                        CompoundTag stackTag = itemStack.getOrCreateTag();

                        if (stackTag.contains(ArtifactUtils.MOD_DATA_KEY, Tag.TAG_COMPOUND) && stackTag.getCompound(ArtifactUtils.MOD_DATA_KEY).contains(ArtifactUtils.ITEM_COLOR_KEY)) {
                            return stackTag.getCompound(ArtifactUtils.MOD_DATA_KEY).getInt(ArtifactUtils.ITEM_COLOR_KEY);
                        }
                    }
                    return -1;
                }, regObj.get());
            }
        }
    }
}
