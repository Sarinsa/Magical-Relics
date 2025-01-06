package com.sarinsa.magical_relics.common.core.config;

import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.worldgen.processor.DisplayPedestalProcessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ConfigReloadListener {

    @SubscribeEvent
    public void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getFileName().equals("magical_relics/general.toml")) {
            // Wiz' favorite item thingy
            DisplayPedestalProcessor.WIZARD_FAVORITES.clear();
            DisplayPedestalProcessor.WIZARD_FAVORITES.addAll(ForgeRegistries.ITEMS.getValues());

            for (String s : MRGeneralConfig.CONFIG.wizardFavoriteBlacklist.get()) {
                ResourceLocation id = ResourceLocation.tryParse(s);

                if (ForgeRegistries.ITEMS.containsKey(id)) {
                    DisplayPedestalProcessor.WIZARD_FAVORITES.remove(ForgeRegistries.ITEMS.getValue(id));
                }
            }

            // Refresh obtainable abilities
            ArtifactUtils.refreshObtainableAbilities();
        }
    }

    /** <b>NOTE TO SELF:</b> event is not thread-safe */
    @SubscribeEvent
    public void onConfigReload(ModConfigEvent.Reloading event) {

    }
}
