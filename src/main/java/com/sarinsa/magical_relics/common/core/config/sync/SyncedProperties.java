package com.sarinsa.magical_relics.common.core.config.sync;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.network.PacketHandler;
import com.sarinsa.magical_relics.common.network.message.S2CSimpleCfgDoubleSync;
import com.sarinsa.magical_relics.common.network.message.S2CSimpleCfgIntSync;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Sends misc config data from the server to the client on login
 * and when the server's configs change.
 */
@Mod.EventBusSubscriber( modid = MagicalRelics.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class SyncedProperties {
    
    /** A map of all synced properties and their IDs. */
    private static final Map<Byte, SyncedProperty<?, ?>> SYNC_PROPERTIES = new HashMap<>();
    
    /** The next available byte ID that can be taken by a {@link SyncedProperty}. */
    private static byte nextId = (byte) 0;
    
    /** The current server instance. */
    private static MinecraftServer currentServer;
    
    
    //-----------------------------------------------------------------------------
    //                               PROPERTIES
    //-----------------------------------------------------------------------------
    
    /** @see com.sarinsa.magical_relics.common.ability.SailorAbility.SailorAbilityConfig.Sailor#speedMultiplier */
    public static SyncedProperty<Double, DoubleField> SAILOR_ABILITY_SPEED_MULT = registerProperty(
            () -> MRArtifactAbilities.SAILOR.get().getConfig().SAILOR.speedMultiplier.field() );
    
    /** @see com.sarinsa.magical_relics.common.ability.OreRadarAbility.OreRadarAbilityConfig.OreRadar#radius */
    public static SyncedProperty<Integer, IntField> ORE_RADAR_RADIUS = registerProperty(
            () -> MRArtifactAbilities.ORE_RADAR.get().getConfig().ORE_RADAR.radius.field() );
    
    
    //-----------------------------------------------------------------------------
    //                             EVENT LISTENER
    //-----------------------------------------------------------------------------
    
    /** Called when the server is starting up. */
    @SubscribeEvent
    public static void onServerStarting( final ServerStartingEvent event ) {
        currentServer = event.getServer();
    }
    
    /** Called when the server has fully shut down. */
    @SubscribeEvent
    public static void onServerStopped( final ServerStoppedEvent event ) {
        currentServer = null;
    }
    
    /**
     * Called when a player logs in on the server.
     * Here we send the info we need to sync to the client.
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn( final PlayerEvent.PlayerLoggedInEvent event ) {
        final ServerPlayer player = (ServerPlayer) event.getEntity();
        
        for( SyncedProperty<?, ?> syncedProperty : SYNC_PROPERTIES.values() ) {
            sendSyncProperty( player, syncedProperty );
        }
    }
    
    
    //-----------------------------------------------------------------------------
    //                              CONVENIENCE
    //-----------------------------------------------------------------------------
    
    /**
     * Sends the given property's config field's value on the server to all
     * connected clients. If this is somehow called before {@link SyncedProperties#currentServer}
     * has been populated, an NPE will be thrown.
     */
    public static void broadcastSyncProperty( SyncedProperty<?, ?> property ) {
        Objects.requireNonNull( property );
        // noinspection resource
        MinecraftServer server = DistExecutor.unsafeRunForDist(
                () -> ServerGetterHelper::getServerOnClient,
                () -> () -> currentServer
        );
        // If there currently is no server, be it local or dedicated, abort
        if( server == null ) return;
        
        try {
            final List<ServerPlayer> players = currentServer.getPlayerList().getPlayers();
            final AbstractConfigField field = property.getField();
            final Object value = field.getValue();
            final byte valueId = property.getPropertyId();
            
            if( value instanceof Double d ) {
                final S2CSimpleCfgDoubleSync packet = new S2CSimpleCfgDoubleSync( d, valueId );
                for( ServerPlayer player : players ) {
                    PacketHandler.sendToClient( packet, player );
                }
            }
            else if( value instanceof Integer i ) {
                final S2CSimpleCfgIntSync packet = new S2CSimpleCfgIntSync( i, valueId );
                for( ServerPlayer player : players ) {
                    PacketHandler.sendToClient( packet, player );
                }
            }
            else {
                final Class<?> clazz = value == null ? null : value.getClass();
                throw new IllegalArgumentException( "Unsupported value type: " + (clazz == null ? "null" : clazz.getName()) );
            }
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    /**
     * Called from {@link SyncedProperties#onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent)} to
     * send the given property's config field's value on the server to the client.
     */
    private static void sendSyncProperty( ServerPlayer player, SyncedProperty<?, ?> property ) {
        Objects.requireNonNull( property );
        
        try {
            final AbstractConfigField field = property.getField();
            final Object value = field.getValue();
            final byte propertyId = property.getPropertyId();
            
            if( value instanceof Double d ) {
                PacketHandler.sendToClient( new S2CSimpleCfgDoubleSync( d, propertyId ), player );
            }
            else if( value instanceof Integer i ) {
                PacketHandler.sendToClient( new S2CSimpleCfgIntSync( i, propertyId ), player );
                
            }
            else {
                final Class<?> clazz = value == null ? null : value.getClass();
                throw new IllegalArgumentException( "Unsupported value type: " + (clazz == null ? "null" : clazz.getName()) );
            }
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    @Nullable
    public static SyncedProperty<?, ?> getFromId( byte id ) {
        return SYNC_PROPERTIES.get( id );
    }
    
    //-----------------------------------------------------------------------------
    //                              REGISTRATION
    //-----------------------------------------------------------------------------
    
    /** Called from {@link MagicalRelics#MagicalRelics(FMLJavaModLoadingContext)} to load this class. */
    public static void init() { }
    
    /**
     * Creates a new SyncedProperty instance with the given field provider and registers
     * it to the map of sync properties with the next available ID.
     */
    private static <V, F extends AbstractConfigField> SyncedProperty<V, F> registerProperty( Supplier<F> fieldProvider ) {
        Objects.requireNonNull( fieldProvider );
        final byte id = nextId();
        final SyncedProperty<V, F> property = new SyncedProperty<>( fieldProvider, id );
        
        SYNC_PROPERTIES.put( id, property );
        
        return property;
    }
    
    /** @return The current value of {@link SyncedProperties#nextId} and increments it. */
    static byte nextId() {
        return nextId++;
    }
    
    // Utility/registry class, no instantiation
    private SyncedProperties() { }
}
