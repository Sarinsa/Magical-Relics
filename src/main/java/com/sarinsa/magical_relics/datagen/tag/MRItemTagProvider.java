package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.tag.MRItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MRItemTagProvider extends ItemTagsProvider {
    
    public MRItemTagProvider( DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper fileHelper ) {
        super( generator.getPackOutput(), lookupProvider, blockTagsProvider, MagicalRelics.MODID, fileHelper );
    }
    
    protected void addTags( HolderLookup.Provider provider ) {
        IntrinsicTagAppender<Item> artifactsTag = tag( MRItemTags.ARTIFACTS );
        IntrinsicTagAppender<Item> artifactCuriosTag = tag( MRItemTags.ARTIFACT_CURIOS );
        IntrinsicTagAppender<Item> trimmableArmorTag = tag( ItemTags.TRIMMABLE_ARMOR );
        
        MRItems.ARTIFACTS_BY_CATEGORY.forEach( ( category, items ) ->
                items.stream().map( ( regObj ) -> regObj.get().artifactAsItem() ).forEach( artifactsTag::add ) );
        
        MRItems.DIAMOND_ARTIFACT_ARMOR.forEach( ( slot, item ) -> trimmableArmorTag.add( item.get().artifactAsItem() ) );
        MRItems.IRON_ARTIFACT_ARMOR.forEach( ( slot, item ) -> trimmableArmorTag.add( item.get().artifactAsItem() ) );
        MRItems.GOLD_ARTIFACT_ARMOR.forEach( ( slot, item ) -> trimmableArmorTag.add( item.get().artifactAsItem() ) );
        MRItems.LEATHER_ARTIFACT_ARMOR.forEach( ( slot, item ) -> trimmableArmorTag.add( item.get().artifactAsItem() ) );
        
        MRItems.RINGS.dataStructure().forEach( ( regObj ) -> artifactCuriosTag.add( regObj.get().artifactAsItem() ) );
        MRItems.AMULETS.dataStructure().forEach( ( regObj ) -> artifactCuriosTag.add( regObj.get().artifactAsItem() ) );
        MRItems.BELTS.dataStructure().forEach( ( regObj ) -> artifactCuriosTag.add( regObj.get().artifactAsItem() ) );
    }
}
