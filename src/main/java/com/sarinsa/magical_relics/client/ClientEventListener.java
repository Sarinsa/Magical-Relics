package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.ability.OreRadarAbility;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.core.registry.MRParticles;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ClientEventListener {

    private int timeNextOrePing = 120;


    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public void onBlockOutlineRender(RenderHighlightEvent.Block event) {
        // Don't render block outline for solid air
        if (Minecraft.getInstance().level.getBlockState(event.getTarget().getBlockPos()).is(MRBlocks.SOLID_AIR.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderFog(ViewportEvent.RenderFog event) {

    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.START) {
            if (--timeNextOrePing <= 0) {
                Player player = event.player;
                Level level = player.level();
                BlockPos playerPos = player.blockPosition();

                // Make sure the player has the ore radar ability
                if (ArtifactUtils.hasAbility(player.getItemBySlot(EquipmentSlot.HEAD), MRArtifactAbilities.ORE_RADAR.get())) {
                    final int scanRange = OreRadarAbility.scanRange.get();

                    for (BlockPos pos : BlockPos.betweenClosed(
                            playerPos.offset(scanRange, scanRange, scanRange),
                            playerPos.offset(-scanRange, -scanRange, -scanRange))) {
                        if (level.hasChunkAt(pos) && level.getBlockState(pos).is(Tags.Blocks.ORES)) {
                            level.addParticle(MRParticles.ORE_PING.get(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0F, 0.0F, 0.0F);
                        }
                    }
                }
                timeNextOrePing = 120;
            }
        }
    }
}
