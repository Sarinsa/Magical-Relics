package com.sarinsa.magical_relics.common.core;

import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.util.MRDamageSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MagicalRelics.MODID)
public class MagicalRelics {

    public static final String MODID = "magical_relics";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public MagicalRelics() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::onCommonSetup);

        MRDamageSources.init();

        MRBlocks.BLOCKS.register(modBus);
        MRItems.ITEMS.register(modBus);
        MRBlockEntities.BLOCK_ENTITIES.register(modBus);
        MRArtifactAbilities.ARTIFACT_ABILITIES.register(modBus);
    }


    public void onCommonSetup(FMLCommonSetupEvent event) {

    }

    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
