package com.sarinsa.magical_relics.common.network.work;

import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.network.message.C2SSaveALTNEGData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MagicalRelics.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerWork {

    private static MinecraftServer server;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }


    public static void handleSaveALTNEGData(C2SSaveALTNEGData message) {
        if (server == null) return;

        ServerPlayer player = server.getPlayerList().getPlayer(message.playerUUID);

        if (player == null) return;

        ServerLevel level = player.getLevel();

        BlockEntity blockEntity = level.getBlockEntity(message.blockEntityPos);

        if (blockEntity instanceof AntiBuilderBlockEntity antiBuilder) {
            antiBuilder.setEffectiveArea(new AABB(message.blockEntityPos).inflate(message.xSize, message.ySize, message.zSize));
        }
    }
}
