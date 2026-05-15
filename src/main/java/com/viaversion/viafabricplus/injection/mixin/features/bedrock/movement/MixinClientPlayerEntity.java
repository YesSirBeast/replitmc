package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * On Bedrock, the client player's swim speed in water is slightly lower.
 * Adapts the swim speed multiplier when targeting Bedrock 1.26.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.movement.MixinLocalPlayer}.</p>
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    // Yarn 1.21.4: the method that controls the underwater tint overlay is
    // "isSubmergedInWater", not "isUnderwater" (which no longer exists).
    @Inject(method = "isSubmergedInWater", at = @At("RETURN"), cancellable = true)
    private void viafabricplus$noBedrockUnderwaterView(
            final CallbackInfoReturnable<Boolean> cir) {

        // Bedrock does not show the underwater overlay the same way Java does.
        // Return false to avoid Java underwater tint when on a Bedrock server.
        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            cir.setReturnValue(false);
        }
    }
}
