package com.sarinsa.magical_relics.common.loot.glm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sarinsa.magical_relics.common.core.config.Config;
import com.sarinsa.magical_relics.common.core.registry.MRGlobalLootMods;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class AddArtifactModifier extends LootModifier {
    
    public static final Supplier<Codec<AddArtifactModifier>> CODEC = () -> RecordCodecBuilder.create( inst -> LootModifier.codecStart( inst )
            .and( inst.group(
                            Codec.FLOAT.fieldOf( "addChance" )
                                    .forGetter( m -> m.addChance ),
                            Codec.INT.fieldOf( "maxCount" )
                                    .forGetter( m -> m.maxArtifacts ),
                            Codec.INT.fieldOf( "minCount" )
                                    .forGetter( m -> m.minArtifacts ),
                            ResourceLocation.CODEC
                                    .fieldOf( "lootTable" )
                                    .forGetter( m -> m.targetLootTable )
                    )
            )
            .apply( inst, AddArtifactModifier::new )
    );
    
    public final float addChance;
    public final int maxArtifacts;
    public final int minArtifacts;
    public final ResourceLocation targetLootTable;
    
    
    public AddArtifactModifier( LootItemCondition[] conditions, float addChance, int maxArtifacts, int minArtifacts, ResourceLocation targetLootTable ) {
        super( conditions );
        
        if( minArtifacts == 0 || minArtifacts > maxArtifacts ) {
            throw new IllegalArgumentException( "Tried constructing AddArtifactModifier with invalid minCount value. Must be greater than 0 and less than maxCount!" );
        }
        
        if( addChance < 0.0F || addChance > 1.0F ) {
            throw new IllegalArgumentException( "Tried constructing AddArtifactModifier with invalid addChance value. Must be greater than 0.0 and not above 1.0" );
        }
        this.addChance = addChance;
        this.maxArtifacts = maxArtifacts;
        this.minArtifacts = minArtifacts;
        this.targetLootTable = targetLootTable;
    }
    
    @Override
    @Nonnull
    protected ObjectArrayList<ItemStack> doApply( ObjectArrayList<ItemStack> generatedLoot, LootContext context ) {
        if( context.getQueriedLootTableId().equals( targetLootTable ) ) {
            RandomSource random = context.getRandom();
            
            if( random.nextFloat() <= addChance ) {
                int totalArtifacts = maxArtifacts == minArtifacts
                        ? minArtifacts
                        : (minArtifacts + (random.nextInt( 1 + maxArtifacts - minArtifacts )));
                
                for( int i = 0; i < totalArtifacts; i++ ) {
                    ItemStack artifact = ArtifactUtils.generateRandomArtifact( context.getLevel(), random, Config.MAIN.ABILITIES.legendaryChance.rollChance( random ) );
                    generatedLoot.add( artifact );
                }
            }
        }
        return generatedLoot;
    }
    
    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return MRGlobalLootMods.ADD_ARTIFACT.get();
    }
}
