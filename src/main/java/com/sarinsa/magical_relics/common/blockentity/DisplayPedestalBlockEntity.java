package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayPedestalBlockEntity extends BlockEntity {

    public DisplayPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.DISPLAY_PEDESTAL.get(), pos, state);
    }
}
