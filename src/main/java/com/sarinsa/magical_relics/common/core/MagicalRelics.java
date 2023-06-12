package com.sarinsa.magical_relics.common.core;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MagicalRelics.MODID)
public class MagicalRelics {

    public static final String MODID = "magical_relics";


    public MagicalRelics() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::onCommonSetup);
    }


    public void onCommonSetup(FMLCommonSetupEvent event) {

    }
}
