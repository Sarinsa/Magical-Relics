package com.sarinsa.magical_relics.common.core;

import com.sarinsa.magical_relics.common.core.registry.*;
import com.sarinsa.magical_relics.common.event.MREventListener;
import com.sarinsa.magical_relics.common.tag.MRBlockTags;
import com.sarinsa.magical_relics.common.util.MRDamageSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MagicalRelics.MODID)
public class MagicalRelics {

    // TODO LIST
    //
    // - Make separate models for Thick Tripwire (consider a slightly different THICCER texture as well?)
    //
    // - Make a lock function and key for the Display Pedestal, preventing looting unless a key is found first
    //
    // - Improve cleanup of solid air blocks placed by air sneak ability
    //
    // - Look around for a way to allow attribute mods on ItemStacks to be applied
    //   regardless of what equipment slot the item is in
    //


    public static final String MODID = "magical_relics";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public MagicalRelics() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(new MREventListener());

        MRDamageSources.init();
        MRBlockTags.init();

        MRBlocks.BLOCKS.register(modBus);
        MRItems.ITEMS.register(modBus);
        MRBlockEntities.BLOCK_ENTITIES.register(modBus);
        MRArtifactAbilities.ARTIFACT_ABILITIES.register(modBus);
        MRConfiguredFeatures.CF_REGISTRY.register(modBus);
        MRConfiguredFeatures.P_REGISTRY.register(modBus);
    }


    public void onCommonSetup(FMLCommonSetupEvent event) {

    }

    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
