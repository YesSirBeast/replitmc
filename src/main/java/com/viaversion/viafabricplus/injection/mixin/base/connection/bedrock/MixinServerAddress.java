package com.viaversion.viafabricplus.injection.mixin.base.connection.bedrock;

import com.viaversion.viafabricplus.injection.access.base.bedrock.IServerAddress;
import com.viaversion.viafabricplus.util.bedrock.NetherNetJsonRpcAddress;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts {@link ServerAddress#parse(String)} to detect
 * {@code nethernet:<connectionId>} strings and:
 * <ol>
 *   <li>Saves the original address/port on the {@link IServerAddress} interface.</li>
 *   <li>Returns a synthetic {@code 0.0.0.0:19132} address so Minecraft's
 *       socket code doesn't choke on the non-standard format.</li>
 * </ol>
 *
 * <p>Ported from ViaFabricPlus 4.5.3 {@code core.connection.bedrock.MixinServerAddress}
 * → 4.0.5-BACKPORT.  Yarn 1.21.4 mappings.</p>
 */
@Mixin(ServerAddress.class)
public abstract class MixinServerAddress implements IServerAddress {

    @Unique
    private String viafabricplus$originalAddress;

    @Unique
    private int viafabricplus$originalPort;

    // ── IServerAddress ────────────────────────────────────────────────────────

    @Override
    public String viafabricplus$getOriginalAddress() {
        return viafabricplus$originalAddress;
    }

    @Override
    public int viafabricplus$getOriginalPort() {
        return viafabricplus$originalPort;
    }

    @Override
    public void viafabricplus$setOriginalAddress(final String address) {
        this.viafabricplus$originalAddress = address;
    }

    @Override
    public void viafabricplus$setOriginalPort(final int port) {
        this.viafabricplus$originalPort = port;
    }

    // ── Static parse hook ─────────────────────────────────────────────────────

    /**
     * Yarn 1.21.4: {@code ServerAddress.parse(String)} is a static factory.
     * We intercept it to handle NetherNet addresses before Minecraft tries to
     * resolve the hostname via DNS.
     */
    @Inject(method = "parse", at = @At("HEAD"), cancellable = true)
    private static void viafabricplus$interceptNetherNet(
            final String address,
            final CallbackInfoReturnable<ServerAddress> cir) {

        if (!NetherNetJsonRpcAddress.isNetherNetAddress(address)) {
            return;
        }

        // Return a dummy address that Minecraft's code can parse safely.
        // The real connection ID is stored on the result via the IServerAddress
        // interface and recovered by the pipeline provider.
        final ServerAddress result = ServerAddress.parse(
                NetherNetJsonRpcAddress.extractHost(address) + ":19132");

        ((IServerAddress)(Object) result).viafabricplus$setOriginalAddress(address);
        ((IServerAddress)(Object) result).viafabricplus$setOriginalPort(19132);

        cir.setReturnValue(result);
    }
}
