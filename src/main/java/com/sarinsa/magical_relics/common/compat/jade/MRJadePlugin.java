package com.sarinsa.magical_relics.common.compat.jade;

import com.sarinsa.magical_relics.common.blockentity.CamoBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.*;
import snownee.jade.api.config.IWailaConfig;

@WailaPlugin
public class MRJadePlugin implements IWailaPlugin {

    private static final ResourceLocation displayCamosId = MagicalRelics.resLoc("display_camos");


    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(displayCamosId, true);
        registration.markAsClientFeature(displayCamosId);

        // Solid air and illumination block should be hidden
        registration.hideTarget(MRBlocks.ILLUMINATION_BLOCK.get());
        registration.hideTarget(MRBlocks.SOLID_AIR.get());

        // Make camo blocks appear as what they are disguised as
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                if (blockAccessor.getBlockEntity() instanceof CamoBlockEntity camoBlockEntity) {
                    BlockState camoState = camoBlockEntity.getCamoState();
                    if (IWailaConfig.get().getPlugin().get(displayCamosId) && camoState != null && camoState != CamoBlockEntity.defaultCamoState.get()) {
                        return registration.blockAccessor().from(blockAccessor)
                                .blockEntity(() -> null)
                                .blockState(camoBlockEntity.getCamoState())
                                .build();
                    }
                }
            }
            return accessor;
        });
    }
}
