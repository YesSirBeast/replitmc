package com.viaversion.viafabricplus.injection.mixin.base.connection.bedrock;

import com.viaversion.viafabricplus.injection.access.base.bedrock.IEventLoopGroupHolder;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.netty.ViaFabricPlusVLPipeline;
import com.viaversion.viafabricplus.util.bedrock.NetherNetInetSocketAddress;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import dev.kastle.netty.channel.nethernet.NetherNetClientChannel;
import dev.kastle.netty.channel.nethernet.config.NetherChannelOption;
import dev.kastle.netty.channel.nethernet.config.NetherNetAddress;
import dev.kastle.netty.channel.nethernet.signaling.NetherNetXboxRpcSignaling;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import net.minecraft.network.ClientConnection;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hooks into {@link ClientConnection} to redirect Bedrock connections away from
 * the normal Minecraft TCP bootstrap and into either a RakNet (UDP) or NetherNet
 * (WebRTC) bootstrap, with the full ViaBedrock 0.0.27 codec chain installed.
 *
 * <h3>Pipeline layout (both transports)</h3>
 * <pre>
 *   [RakNet/NetherNet channel]
 *     → viabedrock-frame-encapsulation  (RakMessage ↔ ByteBuf)
 *     → viabedrock-batch-length         (Bedrock batch framing)
 *     → viabedrock-packet-encapsulation (PacketCodec 0.0.27)
 *     → viabedrock-disconnect-handler
 *     → via-codec                       (ViaVersion protocol translation)
 *     → "packet-handler"                (ClientConnection — Minecraft side)
 * </pre>
 *
 * <h3>Address routing</h3>
 * <ul>
 *   <li>Regular {@link InetSocketAddress} → RakNet UDP bootstrap</li>
 *   <li>{@link NetherNetInetSocketAddress} → NetherNet WebRTC bootstrap</li>
 * </ul>
 *
 * <p>Lifecycle cleanup: when {@code disconnect} is called the UDP/WebRTC
 * {@link EventLoopGroup} is shut down gracefully so no threads are leaked
 * between sessions.</p>
 *
 * <p>Ported from ViaFabricPlus 4.5.3 {@code core.connection.bedrock.MixinConnection}
 * → 4.0.5-BACKPORT package layout.  Yarn 1.21.4 mappings.</p>
 */
@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements IEventLoopGroupHolder {

    @Unique
    private EventLoopGroup viafabricplus$eventLoopGroup;

    // ── IEventLoopGroupHolder ──────────────────────────────────────────────────

    @Override
    public EventLoopGroup viafabricplus$getEventLoopGroup() {
        return viafabricplus$eventLoopGroup;
    }

    @Override
    public void viafabricplus$setEventLoopGroup(final EventLoopGroup group) {
        this.viafabricplus$eventLoopGroup = group;
    }

    // ── Disconnect cleanup ─────────────────────────────────────────────────────

    /**
     * Shuts down the dedicated UDP/WebRTC event loop group on disconnect so that
     * RakNet/NetherNet threads do not linger between sessions.
     */
    @Inject(
        method = "disconnect(Lnet/minecraft/network/DisconnectionInfo;)V",
        at = @At("HEAD")
    )
    private void viafabricplus$shutdownUdpGroup(final CallbackInfo ci) {
        if (viafabricplus$eventLoopGroup != null) {
            viafabricplus$eventLoopGroup.shutdownGracefully();
            viafabricplus$eventLoopGroup = null;
        }
    }

    // ── Pipeline injection ─────────────────────────────────────────────────────

    /**
     * Intercepts the static TCP bootstrap factory.  When targeting a Bedrock server
     * the normal TCP connect is cancelled and replaced with either a RakNet or
     * NetherNet bootstrap depending on the address type.
     *
     * <p>Method target (Yarn 1.21.4):
     * {@code public static ChannelFuture connect(InetSocketAddress, boolean, ClientConnection)}</p>
     */
    @Inject(
        method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void viafabricplus$injectBedrockPipeline(
            final InetSocketAddress address,
            final boolean useEpoll,
            final ClientConnection connection,
            final CallbackInfoReturnable<ChannelFuture> cir) {

        if (ProtocolTranslator.getTargetVersion() != BedrockProtocolVersion.bedrockLatest) {
            return;
        }

        if (address instanceof NetherNetInetSocketAddress) {
            viafabricplus$setupNetherNetPipeline(
                    (NetherNetInetSocketAddress) address, connection, cir);
        } else {
            viafabricplus$setupRakNetPipeline(address, connection, cir);
        }
    }

    // ── RakNet (UDP) bootstrap ─────────────────────────────────────────────────

    /**
     * Creates a RakNet UDP {@link Bootstrap} for standard Bedrock server addresses
     * and installs the ViaBedrock codec chain via {@link ViaFabricPlusVLPipeline}.
     *
     * <p>RakNet options:</p>
     * <ul>
     *   <li>Protocol version 11 — required by Bedrock 1.20+</li>
     *   <li>Random GUID per session — required by the RakNet handshake</li>
     *   <li>10 s connect timeout / 30 s session timeout</li>
     * </ul>
     */
    @Unique
    private static void viafabricplus$setupRakNetPipeline(
            final InetSocketAddress address,
            final ClientConnection connection,
            final CallbackInfoReturnable<ChannelFuture> cir) {

        final NioEventLoopGroup group = new NioEventLoopGroup(2);
        ((IEventLoopGroupHolder) connection).viafabricplus$setEventLoopGroup(group);

        final Bootstrap bootstrap = new Bootstrap()
            .group(group)
            .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
            .option(RakChannelOption.RAK_PROTOCOL_VERSION, 11)
            .option(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong())
            .option(RakChannelOption.RAK_CONNECT_TIMEOUT, 10_000L)
            .option(RakChannelOption.RAK_SESSION_TIMEOUT, 30_000L)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel ch) {
                    final UserConnection userConnection =
                            new UserConnectionImpl(ch, false);
                    ch.pipeline().addLast(new ViaFabricPlusVLPipeline(userConnection));
                    ch.pipeline().addLast("packet-handler", connection);
                }
            });

        cir.setReturnValue(bootstrap.connect(address));
    }

    // ── NetherNet (WebRTC) bootstrap ───────────────────────────────────────────

    /**
     * Creates a NetherNet WebRTC {@link Bootstrap} for {@code nethernet:<id>}
     * server addresses and installs the ViaBedrock codec chain.
     *
     * <p>The NetherNet signaling layer uses Xbox Live authentication via
     * {@link NetherNetXboxRpcSignaling}.  The Xbox token is obtained from the
     * current Minecraft session; the local network-ID is a random UUID.</p>
     *
     * <p>The bootstrap uses {@link NetherNetClientChannel} directly (without
     * a native {@code PeerConnectionFactory}) so that no native WebRTC
     * binaries are required at compile time.</p>
     */
    @Unique
    private static void viafabricplus$setupNetherNetPipeline(
            final NetherNetInetSocketAddress netherNetAddress,
            final ClientConnection connection,
            final CallbackInfoReturnable<ChannelFuture> cir) {

        final NioEventLoopGroup group = new NioEventLoopGroup(2);
        ((IEventLoopGroupHolder) connection).viafabricplus$setEventLoopGroup(group);

        // Acquire the Xbox auth token from the active Minecraft session.
        // In Yarn 1.21.4, MinecraftClient.getInstance().getSession().getAccessToken()
        // returns the bearer token that Xbox Live signaling expects.
        final String xboxToken    = viafabricplus$getXboxToken();
        final String localNetId   = UUID.randomUUID().toString();

        // NetherNetXboxRpcSignaling(String xboxToken, String localNetworkId)
        final NetherNetXboxRpcSignaling signaling =
                new NetherNetXboxRpcSignaling(xboxToken, localNetId);

        final Bootstrap bootstrap = new Bootstrap()
            .group(group)
            // Use the single-arg constructor that does not require a native
            // PeerConnectionFactory; WebRTC peer connections are handled
            // internally by the NetherNet library.
            .channelFactory(() -> new NetherNetClientChannel(signaling))
            .option(NetherChannelOption.NETHER_CLIENT_HANDSHAKE_TIMEOUT_MS, 30_000)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel ch) {
                    final UserConnection userConnection =
                            new UserConnectionImpl(ch, false);
                    ch.pipeline().addLast(new ViaFabricPlusVLPipeline(userConnection));
                    ch.pipeline().addLast("packet-handler", connection);
                }
            });

        // Connect using a NetherNetAddress carrying the 64-bit connection ID
        // that was extracted from the "nethernet:<id>" address string.
        final NetherNetAddress connectAddress =
                new NetherNetAddress(netherNetAddress.getConnectionId());
        cir.setReturnValue(bootstrap.connect(connectAddress));
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Retrieves the Xbox Live access token from the active Minecraft session.
     * Returns an empty string if the session is unavailable (e.g. offline mode);
     * the NetherNet signaling layer will fail gracefully in that case.
     */
    @Unique
    private static String viafabricplus$getXboxToken() {
        try {
            // Yarn 1.21.4: net.minecraft.client.MinecraftClient
            //              .getInstance().getSession().getAccessToken()
            final Class<?> mcClass =
                    Class.forName("net.minecraft.client.MinecraftClient");
            final Object mc = mcClass.getMethod("getInstance").invoke(null);
            final Object session = mcClass.getMethod("getSession").invoke(mc);
            final Object token = session.getClass()
                    .getMethod("getAccessToken").invoke(session);
            return token != null ? token.toString() : "";
        } catch (final Exception e) {
            return "";
        }
    }
}
