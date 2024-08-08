package com.sarinsa.magical_relics.datagen.loot;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.loot.glm.AddArtifactModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class MRLootModProvider extends GlobalLootModifierProvider {

    public MRLootModProvider(DataGenerator generator) {
        super(generator.getPackOutput(), MagicalRelics.MODID);
    }

    @Override
    protected void start() {
        add("simple_dungeon_modifier", new AddArtifactModifier(
                new LootItemCondition[]{},
                0.5F,
                0.15F,
                2,
                1,
                new ResourceLocation("chests/simple_dungeon")
        ));

        add("desert_pyramid_modifier", new AddArtifactModifier(
                new LootItemCondition[]{},
                0.3F,
                0.4F,
                1,
                1,
                new ResourceLocation("chests/desert_pyramid")
        ));

        add("jungle_temple_modifier", new AddArtifactModifier(
                new LootItemCondition[]{},
                0.5F,
                0.2F,
                2,
                2,
                new ResourceLocation("chests/jungle_temple")
        ));
    }
}
