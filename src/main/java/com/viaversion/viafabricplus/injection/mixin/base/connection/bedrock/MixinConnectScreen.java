package com.viaversion.viafabricplus.injection.mixin.base.connection.bedrock;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks into {@link ConnectScreen} to detect Bedrock/NetherNet connection attempts.
 *
 * <p><b>Full Bedrock connection flow:</b></p>
 * <ol>
 *   <li>{@code ConnectScreen.connect(...)} fires — this mixin's injection point.</li>
 *   <li>{@link MixinServerAddress} detects {@code nethernet:<id>} strings in
 *       {@link ServerAddress#parse(String)} and stores the original address on the
 *       {@link com.viaversion.viafabricplus.injection.access.base.bedrock.IServerAddress}
 *       interface.</li>
 *   <li>{@link MixinServerNameResolver} intercepts the resolved
 *       {@link java.net.InetSocketAddress} and replaces it with a
 *       {@link com.viaversion.viafabricplus.util.bedrock.NetherNetInetSocketAddress}
 *       so the pipeline knows which transport to use.</li>
 *   <li>{@link MixinClientConnection} detects the address type and routes to either
 *       the RakNet (UDP) or NetherNet (WebRTC) bootstrap with the full ViaBedrock
 *       0.0.27 codec chain.</li>
 * </ol>
 *
 * <p>This is the <em>outermost</em> hook — it fires before any thread is spawned,
 * making it the right place for pre-connection setup (Xbox token pre-fetch,
 * UI indicators, etc.) in future iterations.</p>
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code core.connection.bedrock.MixinConnectScreen_1}
 * → 4.0.5-BACKPORT.  Yarn 1.21.4 mappings.</p>
 */
@Mixin(ConnectScreen.class)
public abstract class MixinConnectScreen {

    /**
     * Fires at the start of the public static connection factory.
     *
     * <p>Yarn 1.21.4 signature:
     * {@code public static void connect(Screen, MinecraftClient, ServerAddress,
     * ServerInfo, boolean, CookieStorage)}</p>
     *
     * <p>No-op for now — all routing is handled downstream by
     * {@link MixinServerNameResolver} and {@link MixinClientConnection}.
     * This hook is the stable entry point for future NetherNet pre-connection
     * work (Xbox XSTS token pre-fetch, loading indicators, etc.).</p>
     */
    @Inject(
        method = "connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;ZLnet/minecraft/client/network/CookieStorage;)V",
        at = @At("HEAD")
    )
    private static void viafabricplus$onBedrockConnect(
            final Screen parent,
            final MinecraftClient client,
            final ServerAddress serverAddress,
            final ServerInfo serverInfo,
            final boolean quickPlay,
            final CookieStorage cookieStorage,
            final CallbackInfo ci) {

        // No-op — address routing is handled by MixinServerNameResolver
        // and MixinClientConnection further downstream in the call chain.
    }
}
