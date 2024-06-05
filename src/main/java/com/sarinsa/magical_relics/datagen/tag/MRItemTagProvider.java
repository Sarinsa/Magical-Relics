package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.tag.MRItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MRItemTagProvider extends ItemTagsProvider {

    public MRItemTagProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper fileHelper) {
        super(generator.getPackOutput(), lookupProvider, blockTagsProvider, MagicalRelics.MODID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        IntrinsicTagAppender<Item> artifactsTag = tag(MRItemTags.ARTIFACTS);
        IntrinsicTagAppender<Item> artifactCuriosTag = tag(MRItemTags.ARTIFACT_CURIOS);

        for (List<RegistryObject<? extends Item>> artifactList : MRItems.ARTIFACTS_BY_CATEGORY.values()) {
            for (RegistryObject<? extends Item> regObj : artifactList) {
                artifactsTag.add(regObj.get());
            }
        }

        MRItems.RINGS.dataStructure().forEach((regObj) -> artifactCuriosTag.add(regObj.get()));
        MRItems.AMULETS.dataStructure().forEach((regObj) -> artifactCuriosTag.add(regObj.get()));
        MRItems.BELTS.dataStructure().forEach((regObj) -> artifactCuriosTag.add(regObj.get()));
    }
}
