package com.sarinsa.magical_relics.common.network.work;

import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.network.message.C2SSaveAntiBuilderData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = MagicalRelics.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public class ServerWork {
    
    private static MinecraftServer server;
    
    @SubscribeEvent
    public static void onServerStarting( ServerStartingEvent event ) {
        server = event.getServer();
    }
    
    
    public static void handleSaveALTNEGData( C2SSaveAntiBuilderData message ) {
        if( server == null ) return;
        
        if( message.bbCoordinates.length != 6 ) {
            MagicalRelics.LOG.warn( "Received anti-builder 'update bounds' packet with invalid coordinates array length. Expected 6, got {}", message.bbCoordinates.length );
            return;
        }
        
        ServerPlayer player = server.getPlayerList().getPlayer( message.playerUUID );
        
        if( player == null ) return;
        
        ServerLevel level = player.serverLevel();
        
        BlockEntity blockEntity = level.getBlockEntity( message.blockEntityPos );
        
        if( blockEntity instanceof AntiBuilderBlockEntity antiBuilder ) {
            antiBuilder.recalculateEffectiveArea( message.bbCoordinates );
        }
    }
}
