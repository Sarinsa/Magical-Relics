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

public class DisplayPedestalBlock extends Block implements EntityBlock {

    public DisplayPedestalBlock() {
        super(BlockBehaviour.Properties.of(Material.GLASS)
                .strength(1.0F, 0.5F)
                .sound(SoundType.GLASS));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
