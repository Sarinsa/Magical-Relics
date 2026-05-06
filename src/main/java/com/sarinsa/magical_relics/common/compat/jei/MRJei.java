package com.sarinsa.magical_relics.common.compat.jei;

import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.item.IArtifactItem;
import fathertoast.crust.api.lib.NBTHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
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
@SuppressWarnings( "unused" )
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
        IIngredientHelper<ItemStack> ingredientHelper = registration.getIngredientManager().getIngredientHelper( VanillaTypes.ITEM_STACK );
        final List<IJeiAnvilRecipe> recipes = new ArrayList<>();
        
        for( List<RegistryObject<? extends IArtifactItem>> artifactSet : MRItems.ARTIFACTS_BY_CATEGORY.values() ) {
            for( RegistryObject<? extends IArtifactItem> regObj : artifactSet ) {
                Item item = regObj.get().artifactAsItem();
                
                if( item instanceof TieredItem tieredItem ) {
                    final List<ItemStack> inputs = new ArrayList<>();
                    final List<ItemStack> outputs = new ArrayList<>();
                    final ArtifactCategory category = ((IArtifactItem) item).getCategory();
                    
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
                    String ingredientId = ingredientHelper.getUniqueId( inputs.get( 0 ), UidContext.Recipe );
                    
                    recipes.add( registration.getVanillaRecipeFactory().createAnvilRecipe( inputs, List.of( ingredient.getItems() ), outputs,
                            MagicalRelics.rl( "self_repair." + toValidPath( ingredientId ) ) ) );
                }
                else if( item instanceof ArmorItem armorItem ) {
                    Ingredient ingredient = armorItem.getMaterial().getRepairIngredient();
                    ItemStack inputStack = new ItemStack( item );
                    inputStack.setDamageValue( item.getMaxDamage( inputStack ) );
                    ItemStack outputStack = new ItemStack( item );
                    outputStack.setDamageValue( item.getMaxDamage( outputStack ) - item.getMaxDamage( outputStack ) / 4 );
                    
                    String ingredientId = ingredientHelper.getUniqueId( inputStack, UidContext.Recipe );
                    recipes.add( registration.getVanillaRecipeFactory().createAnvilRecipe( inputStack, List.of( ingredient.getItems() ), List.of( outputStack ),
                            MagicalRelics.rl( "self_repair." + toValidPath( ingredientId ) ) ) );
                }
            }
        }
        registration.addRecipes( RecipeTypes.ANVIL, recipes );
    }
    
    /**
     * Replaces all chars in the given String that aren't
     * allowed in ResourceLocation paths with "." and returns it.
     */
    private static String toValidPath( String value ) {
        char[] chars = value.toCharArray();
        
        for( int i = 0; i < chars.length; i++ ) {
            char c = chars[i];
            if( !ResourceLocation.isAllowedInResourceLocation( c ) || c == ':' ) {
                chars[i] = '.';
            }
        }
        return new String( chars );
    }
}
