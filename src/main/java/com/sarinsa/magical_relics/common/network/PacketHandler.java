package com.sarinsa.magical_relics.common.network;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.network.message.C2SSaveALTNEGData;
import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.network.message.S2COpenBEScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

    private static final String PROTOCOL_NAME = "MAGICAL_RELICS";
    /** The network channel our mod will be
     *  using when sending messages. */
    public static final SimpleChannel CHANNEL = createChannel();

    private static int messageIndex;
    private static boolean registered = false;

    private static SimpleChannel createChannel() {
        return NetworkRegistry.ChannelBuilder
                .named(MagicalRelics.resLoc("channel"))
                .serverAcceptedVersions(PROTOCOL_NAME::equals)
                .clientAcceptedVersions(PROTOCOL_NAME::equals)
                .networkProtocolVersion(() -> PROTOCOL_NAME)
                .simpleChannel();
    }

    public static void registerMessages() {
        if (registered) throw new IllegalStateException("Network messages already registered. This should not happen!");

        // Server -> Client
        registerMessage(S2CJukeboxAbility.class, S2CJukeboxAbility::encode, S2CJukeboxAbility::decode, S2CJukeboxAbility::handle);
        registerMessage(S2COpenBEScreen.class, S2COpenBEScreen::encode, S2COpenBEScreen::decode, S2COpenBEScreen::handle);

        // Client -> Server
        registerMessage(C2SSaveALTNEGData.class, C2SSaveALTNEGData::encode, C2SSaveALTNEGData::decode, C2SSaveALTNEGData::handle);

        registered = true;
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        CHANNEL.registerMessage(messageIndex++, messageType, encoder, decoder, messageConsumer, Optional.empty());
    }

    /**
     * Sends the specified message to the given ServerPlayer's client.
     *
     * @param message The message to send to the client.
     * @param player The player client that should receive this message.
     * @param <MSG> Packet type.
     */
    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        CHANNEL.sendTo(message, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
