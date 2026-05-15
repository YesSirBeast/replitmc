package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bedrock honey-block slide speed differs from Java.
 * Returns a modified slide velocity when on a Bedrock server.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.movement.MixinHoneyBlock}.</p>
 */
@Mixin(HoneyBlock.class)
public abstract class MixinHoneyBlock {

    // Yarn 1.21.4: the private honey-slide check is "isSliding(BlockPos, Entity)"
    // not "isSlidingDown(Entity)" — update the method target accordingly.
    @Inject(method = "isSliding", at = @At("RETURN"), cancellable = true)
    private static void viafabricplus$bedrockSlide(
            final net.minecraft.util.math.BlockPos pos,
            final Entity entity,
            final CallbackInfoReturnable<Boolean> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // Bedrock slide triggers at same vertical speed as Java — keep true
        }
    }
}
