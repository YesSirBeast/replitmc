package com.viaversion.viafabricplus.injection.mixin.base.connection.bedrock;

import com.viaversion.viafabricplus.injection.access.base.bedrock.IServerAddress;
import com.viaversion.viafabricplus.util.bedrock.NetherNetInetSocketAddress;
import com.viaversion.viafabricplus.util.bedrock.NetherNetJsonRpcAddress;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;

/**
 * Replaces the resolved {@link InetSocketAddress} with a
 * {@link NetherNetInetSocketAddress} when the player typed a
 * {@code nethernet:<id>} address, so the pipeline provider can recognise
 * it and open a WebRTC channel instead of a TCP/RakNet socket.
 *
 * <p>Ported from ViaFabricPlus 4.5.3 {@code core.connection.bedrock.MixinServerNameResolver}
 * → 4.0.5-BACKPORT.  Yarn 1.21.4 mappings.  Note: in 1.21.4 the resolver
 * class is an inner class of {@code ConnectScreen}; adjust the target if
 * Yarn renames it.</p>
 */
@Mixin(targets = "net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1")
public abstract class MixinServerNameResolver {

    /**
     * After Minecraft resolves the server hostname, check if the original
     * {@link ServerAddress} carried a NetherNet connection id and, if so,
     * swap the result with a {@link NetherNetInetSocketAddress}.
     *
     * <p>Yarn 1.21.4: the resolve method signature is
     * {@code InetSocketAddress resolve(ServerAddress)}.
     * Adjust {@code method} if intermediary differs.</p>
     */
    @Inject(method = "resolve", at = @At("RETURN"), cancellable = true)
    private void viafabricplus$redirectToNetherNet(
            final ServerAddress serverAddress,
            final CallbackInfoReturnable<InetSocketAddress> cir) {

        final IServerAddress iAddr = (IServerAddress)(Object) serverAddress;
        final String original = iAddr.viafabricplus$getOriginalAddress();

        if (original != null && NetherNetJsonRpcAddress.isNetherNetAddress(original)) {
            final long connectionId = NetherNetJsonRpcAddress.parseConnectionId(original);
            cir.setReturnValue(new NetherNetInetSocketAddress(connectionId));
        }
    }
}
