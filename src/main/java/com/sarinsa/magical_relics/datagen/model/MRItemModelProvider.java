package com.sarinsa.magical_relics.datagen.model;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.core.registry.util.ArtifactSet;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class MRItemModelProvider extends ItemModelProvider {

    public MRItemModelProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, MagicalRelics.MODID, fileHelper);
    }

    @Override
    protected void registerModels() {
        artifactSet(MRItems.AMULETS);
        artifactSet(MRItems.BELTS);
        artifactSet(MRItems.DAGGERS);
        artifactSet(MRItems.SWORDS);
        artifactSet(MRItems.FIGURINES);
        artifactSet(MRItems.RINGS);
        artifactSet(MRItems.TRINKETS);
        artifactSet(MRItems.WANDS);
        artifactSet(MRItems.STAFFS);
    }

    public void artifactSet(ArtifactSet<List<RegistryObject<Item>>> artifactSet) {
        for (RegistryObject<Item> regObj : artifactSet.getDataStructure()) {
            ResourceLocation itemId = regObj.getId();
            String artifactType = artifactSet.getType();

            ItemModelBuilder builder = getBuilder(itemId.toString())
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", modArtifactTexture(artifactType, artifactType + "1"));

            for (int i = 1; i < artifactSet.getVariations(); i++) {
                ItemModelBuilder subModelBuilder = getBuilder(MagicalRelics.MODID + ":" + artifactType + i)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", modArtifactTexture(artifactType, artifactType + i))
                        .texture("layer1", modArtifactTexture(artifactType, artifactType + i + "_overlay"));

                builder.override()
                        .predicate(MagicalRelics.resLoc(artifactType + "_variant"), i)
                        .model(new ModelFile.UncheckedModelFile(subModelBuilder.getUncheckedLocation()));
            }
        }
    }

    private static ResourceLocation modTexture(String name) {
        return MagicalRelics.resLoc("item/" + name);
    }

    private static ResourceLocation modArtifactTexture(String type, String name) {
        return MagicalRelics.resLoc("item/" + type + "/" + name);
    }
}
