package com.sarinsa.magical_relics.common.compat.jade;

import com.sarinsa.magical_relics.common.blockentity.CamoBlockEntity;
import com.sarinsa.magical_relics.common.blockentity.DisplayPedestalBlockEntity;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.*;

@WailaPlugin
public class MRJadePlugin implements IWailaPlugin {


    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Solid air and illumination block should be hidden
        registration.hideTarget(MRBlocks.ILLUMINATION_BLOCK.get());
        registration.hideTarget(MRBlocks.SOLID_AIR.get());

        // Make camo blocks appear as what they are disguised as
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                if (blockAccessor.getLevel().getExistingBlockEntity(blockAccessor.getPosition()) instanceof CamoBlockEntity camoBlockEntity) {
                    BlockState camoState = camoBlockEntity.getCamoState();
                    if (camoState != null && camoState != CamoBlockEntity.defaultCamoState.get()) {
                        return registration.blockAccessor().from(blockAccessor)
                                .blockState(camoBlockEntity.getCamoState())
                                .build();
                    }
                }
            }
            return accessor;
        });
    }
}
