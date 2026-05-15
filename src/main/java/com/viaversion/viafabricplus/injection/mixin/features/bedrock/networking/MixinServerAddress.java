package com.viaversion.viafabricplus.injection.mixin.features.bedrock.networking;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.util.bedrock.NetherNetJsonRpcAddress;
import net.minecraft.client.network.ServerAddress;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces the default Bedrock port (19132) when the player does not supply
 * an explicit port and the protocol is Bedrock — regardless of whether the
 * address is a classic RakNet host or a NetherNet id.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.networking.MixinServerNameResolver}.</p>
 */
@Mixin(ServerAddress.class)
public abstract class MixinServerAddress {

    @Inject(method = "getPort", at = @At("RETURN"), cancellable = true)
    private void viafabricplus$defaultBedrockPort(
            final CallbackInfoReturnable<Integer> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest
                && cir.getReturnValue() == 25565) {
            // No explicit port given — default to 19132 for Bedrock
            cir.setReturnValue(19132);
        }
    }
}
