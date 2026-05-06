package com.sarinsa.magical_relics.datagen.loot;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.loot.glm.AddArtifactModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class MRLootModProvider extends GlobalLootModifierProvider {
    
    public MRLootModProvider( DataGenerator generator ) {
        super( generator.getPackOutput(), MagicalRelics.MODID );
    }
    
    @Override
    protected void start() {
        add( "simple_dungeon_modifier", new AddArtifactModifier(
                new LootItemCondition[] {},
                0.5F,
                2,
                1,
                ResourceLocation.withDefaultNamespace( "chests/simple_dungeon" )
        ) );
        
        add( "desert_pyramid_modifier", new AddArtifactModifier(
                new LootItemCondition[] {},
                0.3F,
                1,
                1,
                ResourceLocation.withDefaultNamespace( "chests/desert_pyramid" )
        ) );
        
        add( "jungle_temple_modifier", new AddArtifactModifier(
                new LootItemCondition[] {},
                0.5F,
                2,
                2,
                ResourceLocation.withDefaultNamespace( "chests/jungle_temple" )
        ) );
        
        add( "wizard_tower_chest_modifier", new AddArtifactModifier(
                new LootItemCondition[] {},
                0.8F,
                3,
                1,
                MagicalRelics.rl( "chests/wizard_tower_artifact" )
        ) );
        
        add( "wizard_tower_dispenser_modifier", new AddArtifactModifier(
                new LootItemCondition[] {},
                0.7F,
                2,
                1,
                MagicalRelics.rl( "chests/wizard_tower_dispenser_artifact" )
        ) );
    }
}
