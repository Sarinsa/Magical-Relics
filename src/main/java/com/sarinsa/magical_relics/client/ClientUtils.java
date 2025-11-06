package com.sarinsa.magical_relics.client;

import com.sarinsa.magical_relics.common.block.QuicksandBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientUtils {
    
    public static boolean isQuicksandViewBlocking() {
        Player player = Minecraft.getInstance().player;
        
        if( player == null ) return false;
        
        return QuicksandBlock.areEyesInQuicksand( player );
    }
}
