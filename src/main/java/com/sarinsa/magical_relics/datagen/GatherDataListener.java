package com.sarinsa.magical_relics.datagen;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRConfiguredFeatures;
import com.sarinsa.magical_relics.common.core.registry.MRDamageTypes;
import com.sarinsa.magical_relics.datagen.loot.MRLootProvider;
import com.sarinsa.magical_relics.datagen.model.MRBlockStateProvider;
import com.sarinsa.magical_relics.datagen.model.MRItemModelProvider;
import com.sarinsa.magical_relics.datagen.recipe.MRRecipeProvider;
import com.sarinsa.magical_relics.datagen.tag.MRBiomeTagProvider;
import com.sarinsa.magical_relics.datagen.tag.MRBlockTagProvider;
import com.sarinsa.magical_relics.datagen.tag.MRItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = MagicalRelics.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GatherDataListener {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, MRDamageTypes::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, MRConfiguredFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MRConfiguredFeatures::bootstrapPlaced);


    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new MRBlockStateProvider(generator, fileHelper));
        generator.addProvider(event.includeClient(), new MRItemModelProvider(generator, fileHelper));

        MRBlockTagProvider blockTagProvider = new MRBlockTagProvider(generator, lookupProvider, fileHelper);

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(generator.getPackOutput(), lookupProvider, BUILDER, Set.of(MagicalRelics.MODID)));
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new MRItemTagProvider(generator, lookupProvider, blockTagProvider.contentsGetter(), fileHelper));
        generator.addProvider(event.includeServer(), new MRBiomeTagProvider(generator, lookupProvider, fileHelper));
        generator.addProvider(event.includeServer(), new MRLootProvider(generator));
        generator.addProvider(event.includeServer(), new MRRecipeProvider(generator));
    }
}
