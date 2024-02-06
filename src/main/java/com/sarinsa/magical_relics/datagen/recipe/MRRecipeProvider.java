package com.sarinsa.magical_relics.datagen.recipe;

import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MRRecipeProvider extends RecipeProvider {

    public MRRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        manaessenceRecipe(MRItems.WOOD_MANAESSENCE, ItemTags.LOGS_THAT_BURN, consumer);
        manaessenceRecipe(MRItems.STONE_MANAESSENCE, Tags.Items.COBBLESTONE, consumer);
        manaessenceRecipe(MRItems.LEATHER_MANAESSENCE, Tags.Items.LEATHER, consumer);
        manaessenceRecipe(MRItems.IRON_MANAESSENCE, Items.IRON_INGOT, consumer);
        manaessenceRecipe(MRItems.GOLD_MANAESSENCE, Items.GOLD_INGOT, consumer);
        manaessenceRecipe(MRItems.DIAMOND_MANAESSENCE, Items.DIAMOND, consumer);

        simpleShapeless(Items.STRING, 2, MRBlocks.THICK_TRIPWIRE.get(), consumer);
    }

    private void manaessenceRecipe(Supplier<Item> manaessenceItem, ItemLike keyIngredient, Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(manaessenceItem.get(), 2)
                .requires(Tags.Items.NUGGETS_GOLD)
                .requires(keyIngredient)
                .requires(MRItems.RAW_MANAESSENCE.get())
                .unlockedBy("has_raw_manaessence", has(MRItems.RAW_MANAESSENCE.get()))
                .save(consumer);
    }

    private void manaessenceRecipe(Supplier<Item> manaessenceItem, TagKey<Item> keyIngredient, Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(manaessenceItem.get(), 2)
                .requires(Tags.Items.NUGGETS_GOLD)
                .requires(keyIngredient)
                .requires(MRItems.RAW_MANAESSENCE.get())
                .unlockedBy("has_raw_manaessence", has(MRItems.RAW_MANAESSENCE.get()))
                .save(consumer);
    }

    private void simpleShapeless(Item result, int amount, ItemLike ingredient, Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(result, amount)
                .requires(ingredient)
                .unlockedBy("has_" + ForgeRegistries.ITEMS.getKey(ingredient.asItem()).getPath(), has(ingredient))
                .save(consumer);
    }
}
