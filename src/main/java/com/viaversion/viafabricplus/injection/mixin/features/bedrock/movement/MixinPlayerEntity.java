package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bedrock players can sprint while sneaking (Bedrock 1.19.10+).
 * Restores this behaviour when connected to a Bedrock 1.26 server.
 *
 * <p>In Yarn 1.21.4, {@code canSprint()} is a private method on
 * {@link ClientPlayerEntity} (not on the abstract {@code PlayerEntity}),
 * so the mixin target is the concrete client-side player class.</p>
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.movement.MixinPlayer}.</p>
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinPlayerEntity {

    @Inject(method = "canSprint", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$sprintWhileSneaking(
            final CallbackInfoReturnable<Boolean> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            cir.setReturnValue(true);
        }
    }
}
