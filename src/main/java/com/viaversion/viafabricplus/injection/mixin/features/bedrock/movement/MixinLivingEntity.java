package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.LivingEntity;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * On Bedrock, sneaking entities are not subject to the Java slow-falling
 * honey-block drag when touching the side.  Paired with MixinHoneyBlock.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.movement.MixinLivingEntity}.</p>
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "isClimbing", at = @At("RETURN"), cancellable = true)
    private void viafabricplus$noHoneyClimb(final CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // Bedrock does not have honey-block wall climbing
            cir.setReturnValue(false);
        }
    }
}
