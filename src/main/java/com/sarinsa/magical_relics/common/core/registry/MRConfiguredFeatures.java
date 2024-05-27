package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;

import java.util.List;

public class MRConfiguredFeatures {


    public static final DeferredRegister<ConfiguredFeature<?, ?>> CF_REGISTRY = DeferredRegister.create(Registries.CONFIGURED_FEATURE, MagicalRelics.MODID);
    public static final DeferredRegister<PlacedFeature> P_REGISTRY = DeferredRegister.create(Registries.PLACED_FEATURE, MagicalRelics.MODID);


    public static final ResourceKey<ConfiguredFeature<?, ?>> QUICKSAND_LAKE = configuredKey("quicksand_lake");

    public static final ResourceKey<PlacedFeature> PLACED_QUICKSAND_LAKE_SURFACE = placedKey("quicksand_lake_surface");




    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, MRConfiguredFeatures.QUICKSAND_LAKE, new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(BlockStateProvider.simple(MRBlocks.QUICKSAND.get().defaultBlockState()), BlockStateProvider.simple(Blocks.DIRT.defaultBlockState()))));
    }


    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> getter = context.lookup(Registries.CONFIGURED_FEATURE);

        final Holder<ConfiguredFeature<?, ?>> QUICKSAND_LAKE = getter.getOrThrow(MRConfiguredFeatures.QUICKSAND_LAKE);

        register(context, MRConfiguredFeatures.PLACED_QUICKSAND_LAKE_SURFACE, QUICKSAND_LAKE, List.of(RarityFilter.onAverageOnceEvery(100), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
    }





    protected static void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature) {
        context.register(confFeatureKey, configuredFeature);
    }

    protected static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers) {
        register(context, placedFeatureKey, configuredFeature, List.of(modifiers));
    }

    protected static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers) {
        context.register(placedFeatureKey, new PlacedFeature(configuredFeature, modifiers));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, MagicalRelics.resLoc(name));
    }

    public static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, MagicalRelics.resLoc(name));
    }
}
