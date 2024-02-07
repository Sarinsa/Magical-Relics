package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class IllusionaryBlockEntity extends BaseCamoBlockEntity {

    public IllusionaryBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.ILLUSIONARY_BLOCK.get(), pos, state);
    }
}
