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
        MRItems.ALL_ARTIFACTS.forEach(this::artifactSet);
    }

    private void artifactSet(ArtifactSet<List<RegistryObject<Item>>> artifactSet) {
        for (RegistryObject<Item> regObj : artifactSet.dataStructure()) {
            ResourceLocation itemId = regObj.getId();
            String artifactType = artifactSet.type();

            ItemModelBuilder builder = getBuilder(itemId.toString())
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", modArtifactTexture(artifactType, artifactType + "1"));

            for (int i = 1; i < artifactSet.variants() + 1; ++i) {
                System.out.println(artifactType);
                System.out.println(i);
                System.out.println("--------------------------------------");
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
