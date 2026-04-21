package com.sarinsa.magical_relics.common.network;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.network.message.C2SSaveAntiBuilderData;
import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.network.message.S2COpenAntiBuilderScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {
    
    public static final ResourceLocation CHANNEL_NAME = MagicalRelics.rl( "channel" );
    public static final ResourceLocation EVENT_CHANNEL_NAME = MagicalRelics.rl( "network_events" );
    
    private static final String PROTOCOL_NAME = "MAGICAL_RELICS";
    /**
     * The network channel our mod will be
     * using when sending messages.
     */
    public static final SimpleChannel CHANNEL = createChannel();
    public static final EventNetworkChannel EVENT_CHANNEL = createEventChannel();
    
    private static int messageIndex;
    private static boolean registered = false;
    
    private static SimpleChannel createChannel() {
        return NetworkRegistry.ChannelBuilder
                .named( CHANNEL_NAME )
                .serverAcceptedVersions( PROTOCOL_NAME::equals )
                .clientAcceptedVersions( PROTOCOL_NAME::equals )
                .networkProtocolVersion( () -> PROTOCOL_NAME )
                .simpleChannel();
    }
    
    private static EventNetworkChannel createEventChannel() {
        return NetworkRegistry.ChannelBuilder
                .named( EVENT_CHANNEL_NAME )
                .serverAcceptedVersions( PROTOCOL_NAME::equals )
                .clientAcceptedVersions( PROTOCOL_NAME::equals )
                .networkProtocolVersion( () -> PROTOCOL_NAME )
                .eventNetworkChannel();
    }
    
    public static void register() {
        if( registered )
            throw new IllegalStateException( "Network setup already complete!" );
        
        // Server -> Client
        registerMessage( S2CJukeboxAbility.class, S2CJukeboxAbility::encode, S2CJukeboxAbility::decode, S2CJukeboxAbility::handle );
        registerMessage( S2COpenAntiBuilderScreen.class, S2COpenAntiBuilderScreen::encode, S2COpenAntiBuilderScreen::decode, S2COpenAntiBuilderScreen::handle );
        
        // Client -> Server
        registerMessage( C2SSaveAntiBuilderData.class, C2SSaveAntiBuilderData::encode, C2SSaveAntiBuilderData::decode, C2SSaveAntiBuilderData::handle );
        
        registered = true;
    }
    
    private static <MSG> void registerMessage( Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer ) {
        CHANNEL.registerMessage( messageIndex++, messageType, encoder, decoder, messageConsumer, Optional.empty() );
    }
    
    /**
     * Sends the specified message to the given ServerPlayer's client.
     *
     * @param message The message to send to the client.
     * @param player  The player client that should receive this message.
     * @param <MSG>   Packet type.
     */
    public static <MSG> void sendToClient( MSG message, ServerPlayer player ) {
        CHANNEL.sendTo( message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT );
    }
}
