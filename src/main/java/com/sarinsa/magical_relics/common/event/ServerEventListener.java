package com.sarinsa.magical_relics.common.event;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.lib.EnvironmentHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ServerEventListener {
    
    // private static MinecraftServer serverInstance;
    
    private static int nextCooldownTick = 0;
    private static final int maxCooldownTick = 10;
    
    private static int nextRepairTick;
    private static final int maxRepairTick = 10000;
    
    private static final Deque<ServerPlayer> aggroClearingList = new ConcurrentLinkedDeque<>();
    
    
    /*
    @SubscribeEvent
    public void onServerStarted( ServerStartedEvent event ) {
        serverInstance = event.getServer();
    }
     */
    
    /*
    @SubscribeEvent
    public void onServerStopped( ServerStoppedEvent event ) {
        serverInstance = null;
    }
     */
    
    @SubscribeEvent
    public void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END ) {
            if( ++nextRepairTick >= maxRepairTick )
                nextRepairTick = 0;
            
            if( ++nextCooldownTick >= maxCooldownTick ) {
                nextCooldownTick = 0;
                
                // Tick ability cooldowns
                for( ServerPlayer player : event.getServer().getPlayerList().getPlayers() ) {
                    try {
                        ArtifactUtils.tickAbilityCooldowns( player, maxCooldownTick );
                    }
                    catch( Exception e ) {
                        MagicalRelics.LOG.error( "Failed to tick ability cooldowns for player {}!", player.getName() );
                        // noinspection CallToPrintStackTrace
                        e.printStackTrace();
                    }
                }
                
                // Go through the aggro clearing list
                if( MRArtifactAbilities.OBSCURITY.get().getConfig().OBSCURITY.resetAggroRange.get() > 0.0 ) {
                    aggroClearingList.removeIf( ( player ) -> {
                        if( player.isAlive() ) {
                            final Level level = player.level();
                            
                            if( EnvironmentHelper.isLoaded( level, player.blockPosition() ) ) {
                                for( PathfinderMob pathfinderMob : level.getEntitiesOfClass( PathfinderMob.class, player.getBoundingBox().inflate( 30.0D, 30.0D, 30.0D ) ) ) {
                                    if( pathfinderMob.getTarget() == player || pathfinderMob.getLastHurtByMob() == player ) {
                                        pathfinderMob.setTarget( null );
                                        pathfinderMob.setLastHurtByMob( null );
                                    }
                                }
                            }
                        }
                        return true;
                    } );
                }
            }
        }
    }
    
    /** @return The current value of the repair-tick timer. */
    public static int getRepairTick() {
        return nextRepairTick;
    }
    
    /**
     * Puts the given player at the bottom of the deaggro deque.
     * This method is thread-safe.
     */
    public static void queuePlayerForDeaggro( @Nonnull ServerPlayer player ) {
        Objects.requireNonNull( player );
        
        if( !aggroClearingList.contains( player ) )
            aggroClearingList.add( player );
    }
}
