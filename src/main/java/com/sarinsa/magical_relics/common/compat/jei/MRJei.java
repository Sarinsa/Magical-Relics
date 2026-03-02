package com.sarinsa.magical_relics.common.compat.jei;

import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.item.IArtifactItem;
import fathertoast.crust.api.lib.NBTHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.sarinsa.magical_relics.common.util.ArtifactUtils.TAG_MOD_DATA;
import static com.sarinsa.magical_relics.common.util.ArtifactUtils.TAG_VARIANT;

@JeiPlugin
public class MRJei implements IModPlugin {
    
    private static final ResourceLocation ID = MagicalRelics.rl( "magical_relics_jei" );
    
    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
    
    @Override
    public void registerRecipes( @Nonnull IRecipeRegistration registration ) {
        // Anvil repair for artifact items
        artifactAnvilRecipes( registration );
    }
    
    private void artifactAnvilRecipes( IRecipeRegistration registration ) {
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();
        
        for( List<RegistryObject<? extends Item>> artifactSet : MRItems.ARTIFACTS_BY_CATEGORY.values() ) {
            for( RegistryObject<? extends Item> regObj : artifactSet ) {
                Item item = regObj.get();
                
                if( item instanceof TieredItem tieredItem ) {
                    List<ItemStack> inputs = new ArrayList<>();
                    List<ItemStack> outputs = new ArrayList<>();
                    ArtifactCategory category = ((IArtifactItem) item).getCategory();
                    
                    for( int i = 0; i < category.getVariations(); i++ ) {
                        ItemStack inputStack = new ItemStack( item );
                        
                        CompoundTag inputModData = NBTHelper.getOrCreateCompound( inputStack.getOrCreateTag(), TAG_MOD_DATA );
                        inputModData.putInt( TAG_VARIANT, i );
                        inputStack.setDamageValue( item.getMaxDamage( inputStack ) );
                        inputs.add( inputStack );
                        
                        ItemStack outputStack = new ItemStack( item );
                        
                        CompoundTag outputModData = NBTHelper.getOrCreateCompound( outputStack.getOrCreateTag(), TAG_MOD_DATA );
                        outputModData.putInt( TAG_VARIANT, i );
                        outputStack.setDamageValue( item.getMaxDamage( outputStack ) - item.getMaxDamage( outputStack ) / 4 );
                        outputs.add( outputStack );
                    }
                    Ingredient ingredient = tieredItem.getTier().getRepairIngredient();
                    recipes.add( registration.getVanillaRecipeFactory().createAnvilRecipe( inputs, List.of( ingredient.getItems() ), outputs ) );
                }
                else if( item instanceof ArmorItem armorItem ) {
                    Ingredient ingredient = armorItem.getMaterial().getRepairIngredient();
                    ItemStack input = new ItemStack( item );
                    input.setDamageValue( item.getMaxDamage( input ) );
                    ItemStack result = new ItemStack( item );
                    result.setDamageValue( item.getMaxDamage( result ) - item.getMaxDamage( result ) / 4 );
                    
                    recipes.add( registration.getVanillaRecipeFactory().createAnvilRecipe( input, List.of( ingredient.getItems() ), List.of( result ) ) );
                }
            }
        }
        registration.addRecipes( RecipeTypes.ANVIL, recipes );
    }
}
