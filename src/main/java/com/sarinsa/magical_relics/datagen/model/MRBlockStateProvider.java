package com.sarinsa.magical_relics.datagen.model;

import com.sarinsa.magical_relics.common.block.WallPressurePlateBlock;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class MRBlockStateProvider extends BlockStateProvider {

    public MRBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MagicalRelics.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        MRBlocks.WALL_PRESSURE_PLATES.forEach(this::wallPressurePlate);
    }

    /**
     * @param regObj The block registry object
     * @param textureBlock The block to use as "texture parent"
     */
    private void wallPressurePlate(RegistryObject<WallPressurePlateBlock> regObj, Block textureBlock) {
        getVariantBuilder(regObj.get()).forAllStates((state) -> {
            boolean powered = state.getValue(WallPressurePlateBlock.POWERED);
            Direction direction = state.getValue(WallPressurePlateBlock.FACING);
            ResourceLocation parentModel = resLoc("block/wall_pressure_plate");
            ResourceLocation parentModelActive = resLoc("block/wall_pressure_plate_active");

            return ConfiguredModel.builder()
                    .modelFile(models().withExistingParent(powered ? name(regObj.get()) + "_active" : name(regObj.get()), powered ? parentModelActive : parentModel)
                            .texture("texture", blockTexture(textureBlock)))
                    .rotationY((int) direction.toYRot())
                    .build();
        });
        itemModels().withExistingParent(name(regObj.get()), resLoc("block/" + name(regObj.get())));
    }

    protected String name(Block block) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
    }

    private static ResourceLocation resLoc(String path) {
        return MagicalRelics.resLoc(path);
    }

    public static ResourceLocation texture(String textureName) {
        return resLoc("block/" + textureName);
    }

    public static ResourceLocation itemTexture(String textureName) {
        return resLoc("item/" + textureName);
    }

    public static ResourceLocation blockTextureWith(Block block, String suffix) {
        ResourceLocation name = key(block);
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + "_" + suffix);
    }

    public static ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
}
