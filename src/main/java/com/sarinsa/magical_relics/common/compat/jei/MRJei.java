package com.sarinsa.magical_relics.common.compat.jei;

import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.item.ArtifactItem;
import com.sarinsa.magical_relics.common.item.ItemArtifact;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryObject;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sarinsa.magical_relics.common.util.ArtifactUtils.MOD_DATA_KEY;
import static com.sarinsa.magical_relics.common.util.ArtifactUtils.VARIANT_KEY;

@JeiPlugin
public class MRJei implements IModPlugin {

    private static final ResourceLocation ID = MagicalRelics.resLoc("magical_relics_jei");


    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        // Anvil repair for artifact items
        artifactAnvilRecipes(registration);
    }

    private void artifactAnvilRecipes(IRecipeRegistration registration) {
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();

        for (List<RegistryObject<? extends Item>> artifactSet : MRItems.ARTIFACTS_BY_CATEGORY.values()) {
            for (RegistryObject<? extends Item> regObj : artifactSet) {
                Item item = regObj.get();

                if (item instanceof TieredItem tieredItem) {
                    List<ItemStack> inputs = new ArrayList<>();
                    List<ItemStack> outputs = new ArrayList<>();

                    ArtifactCategory category = ((ItemArtifact) item).getCategory();

                    for (int i = 0; i < category.getVariations(); i++) {
                        ItemStack input = new ItemStack(item);

                        CompoundTag inputStackTag = input.getOrCreateTag();
                        CompoundTag inputModDataTag = new CompoundTag();
                        inputModDataTag.putInt(VARIANT_KEY, i);
                        inputStackTag.put(MOD_DATA_KEY, inputModDataTag);
                        input.setDamageValue(item.getMaxDamage(input));
                        inputs.add(input);

                        ItemStack output = new ItemStack(item);

                        CompoundTag outputStackTag = output.getOrCreateTag();
                        CompoundTag outputModDataTag = new CompoundTag();
                        outputModDataTag.putInt(VARIANT_KEY, i);
                        outputStackTag.put(MOD_DATA_KEY, outputModDataTag);
                        output.setDamageValue(item.getMaxDamage(output) -  item.getMaxDamage(output) / 4);
                        outputs.add(output);
                    }
                    Ingredient ingredient = tieredItem.getTier().getRepairIngredient();

                    IJeiAnvilRecipe recipe = registration.getVanillaRecipeFactory().createAnvilRecipe(inputs, List.of(ingredient.getItems()), outputs);

                    if (recipe != null)
                        recipes.add(recipe);
                }
                else if (item instanceof ArmorItem armorItem) {
                    Ingredient ingredient = armorItem.getMaterial().getRepairIngredient();
                    ItemStack input = new ItemStack(item);
                    input.setDamageValue(item.getMaxDamage(input));
                    ItemStack result = new ItemStack(item);
                    result.setDamageValue(item.getMaxDamage(result) -  item.getMaxDamage(result) / 4);

                    IJeiAnvilRecipe recipe = registration.getVanillaRecipeFactory().createAnvilRecipe(input, List.of(ingredient.getItems()), List.of(result));

                    if (recipe != null)
                        recipes.add(recipe);
                }
            }
        }
        registration.addRecipes(RecipeTypes.ANVIL, recipes);
    }
}
