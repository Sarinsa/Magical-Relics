package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CamoTripwireBlockEntity extends BaseCamoBlockEntity {

    public CamoTripwireBlockEntity(BlockPos pos, BlockState state) {
        super(MRBlockEntities.CAMO_TRIPWIRE_HOOK.get(), pos, state);
    }
}
