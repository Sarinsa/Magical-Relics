package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class MRConfiguredFeatures {


    public static final DeferredRegister<ConfiguredFeature<?, ?>> CF_REGISTRY = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, MagicalRelics.MODID);
    public static final DeferredRegister<PlacedFeature> P_REGISTRY = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, MagicalRelics.MODID);


    //----------------------------- CONFIGURED ----------------------------------
    public static final RegistryObject<ConfiguredFeature<LakeFeature.Configuration, ?>> QUICKSAND_LAKE = register("quicksand_lake", () -> Feature.LAKE, () -> new LakeFeature.Configuration(BlockStateProvider.simple(MRBlocks.QUICKSAND.get().defaultBlockState()), BlockStateProvider.simple(Blocks.DIRT.defaultBlockState())));

    //------------------------------- PLACED ------------------------------------
    public static final RegistryObject<PlacedFeature> QUICKSAND_LAKE_SURFACE = registerPlaced("quicksand_lake_surface", QUICKSAND_LAKE, () -> List.of(RarityFilter.onAverageOnceEvery(100), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));



    private static <FC extends FeatureConfiguration, F extends Feature<FC>> RegistryObject<ConfiguredFeature<FC, ?>> register(String name, Supplier<F> feature, Supplier<FC> config) {
        return CF_REGISTRY.register(name, () -> new ConfiguredFeature<>(feature.get(), config.get()));
    }

    private static RegistryObject<PlacedFeature> registerPlaced(String name, RegistryObject<? extends ConfiguredFeature<?, ?>> feature, Supplier<List<PlacementModifier>> modifiers) {
        return P_REGISTRY.register(name, () -> new PlacedFeature(Holder.hackyErase(feature.getHolder().orElseThrow()), modifiers.get()));
    }

}
