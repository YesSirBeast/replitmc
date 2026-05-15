package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.Entity;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bedrock entities can step up 0.5625 blocks (half-slab) without jumping,
 * vs Java's 0.6 step height default.  Adjusts {@code getStepHeight()} to
 * match Bedrock behaviour when connected to a Bedrock server.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.movement.MixinEntity}.</p>
 */
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "getStepHeight", at = @At("RETURN"), cancellable = true)
    private void viafabricplus$bedrockStepHeight(
            final CallbackInfoReturnable<Float> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            cir.setReturnValue(0.5625F);
        }
    }
}
