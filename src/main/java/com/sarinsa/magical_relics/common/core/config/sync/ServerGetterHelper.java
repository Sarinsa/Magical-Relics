package com.sarinsa.magical_relics.common.core.config.sync;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

public class ServerGetterHelper {
    
    @Nullable
    static MinecraftServer getServerOnClient() {
        if( FMLEnvironment.dist == Dist.CLIENT ) {
            ServerData currentServer = Minecraft.getInstance().getCurrentServer();
            
            if( currentServer != null || !Minecraft.getInstance().hasSingleplayerServer() )
                return null;
            
            return Minecraft.getInstance().getSingleplayerServer();
        }
        return null;
    }
}
