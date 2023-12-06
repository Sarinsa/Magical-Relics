package com.sarinsa.magical_relics.datagen;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.datagen.model.MRBlockStateProvider;
import com.sarinsa.magical_relics.datagen.model.MRItemModelProvider;
import com.sarinsa.magical_relics.datagen.recipe.MRRecipeProvider;
import com.sarinsa.magical_relics.datagen.tag.MRBlockTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MagicalRelics.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GatherDataListener {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new MRBlockStateProvider(generator, fileHelper));
        generator.addProvider(event.includeClient(), new MRItemModelProvider(generator, fileHelper));

        generator.addProvider(event.includeServer(), new MRBlockTagProvider(generator, fileHelper));
        generator.addProvider(event.includeServer(), new MRRecipeProvider(generator));
    }
}
