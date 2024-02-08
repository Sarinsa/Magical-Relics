package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventListener {


    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public void onBlockOutlineRender(RenderHighlightEvent.Block event) {
        // Don't render block outline for solid air
        if (Minecraft.getInstance().level.getBlockState(event.getTarget().getBlockPos()).is(MRBlocks.SOLID_AIR.get()))
            event.setCanceled(true);
    }
}
