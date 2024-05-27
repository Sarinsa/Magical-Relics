package com.sarinsa.magical_relics.datagen.loot;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MRLootProvider extends LootTableProvider {

    public MRLootProvider(DataGenerator dataGenerator) {
        super(dataGenerator.getPackOutput(), null, List.of(
                new SubProviderEntry(() -> new MRBlockLoot(Set.of(), FeatureFlags.VANILLA_SET), LootContextParamSets.BLOCK)
        ));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext context) {
        // Not validating
    }
}
