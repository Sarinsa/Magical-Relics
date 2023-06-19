package com.sarinsa.magical_relics.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

// TODO - Consider making the illusionary block into a crumbling block
//        instead that breaks when stepped on
public class IllusionaryBlock extends Block implements EntityBlock {

    public IllusionaryBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .noCollission()
                .sound(SoundType.STONE)
                .strength(1.0F, 1.0F));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
