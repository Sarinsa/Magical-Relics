package com.sarinsa.magical_relics.common.item;

import com.sarinsa.magical_relics.common.core.registry.MRItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum ArtifactItemTiers implements Tier {
    
    WOOD( "wood", 0, 59, 2.0F, 0.0F, 15,
            () -> Ingredient.of( MRItems.WOOD_MANAESSENCE.get() ) ),
    STONE( "stone", 1, 131, 4.0F, 1.0F, 5,
            () -> Ingredient.of( MRItems.STONE_MANAESSENCE.get() ) ),
    IRON( "iron", 2, 250, 6.0F, 2.0F, 14,
            () -> Ingredient.of( MRItems.IRON_MANAESSENCE.get() ) ),
    DIAMOND( "diamond", 3, 1561, 8.0F, 3.0F, 10,
            () -> Ingredient.of( MRItems.DIAMOND_MANAESSENCE.get() ) ),
    GOLD( "gold", 0, 32, 12.0F, 0.0F, 22,
            () -> Ingredient.of( MRItems.GOLD_MANAESSENCE.get() ) );
    
    private final String materialName;
    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;
    
    ArtifactItemTiers( String materialName, int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient ) {
        this.materialName = materialName;
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }
    
    public String getMaterialName() {
        return materialName;
    }
    
    @Override
    public int getUses() {
        return this.uses;
    }
    
    @Override
    public float getSpeed() {
        return this.speed;
    }
    
    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }
    
    @Override
    public int getLevel() {
        return this.level;
    }
    
    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
    
    @Nullable
    public TagKey<Block> getTag() {
        return switch( this ) {
            case WOOD -> Tags.Blocks.NEEDS_WOOD_TOOL;
            case GOLD -> Tags.Blocks.NEEDS_GOLD_TOOL;
            case STONE -> BlockTags.NEEDS_STONE_TOOL;
            case IRON -> BlockTags.NEEDS_IRON_TOOL;
            case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
        };
    }
}
