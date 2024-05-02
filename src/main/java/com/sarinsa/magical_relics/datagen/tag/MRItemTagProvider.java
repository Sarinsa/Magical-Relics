package com.sarinsa.magical_relics.datagen.tag;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.tag.MRItemTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MRItemTagProvider extends ItemTagsProvider {

    public MRItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper fileHelper) {
        super(generator, blockTagsProvider, MagicalRelics.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        TagAppender<Item> ARTIFACTS_TAG = tag(MRItemTags.ARTIFACTS);

        for (List<RegistryObject<? extends Item>> artifactList : MRItems.ARTIFACTS_BY_CATEGORY.values()) {
            for (RegistryObject<? extends Item> regObj : artifactList) {
                ARTIFACTS_TAG.add(regObj.get());
            }
        }
    }
}
