package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bedrock sea pickle hitbox is taller (7 px vs 6 px in Java).
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinSeaPickleBlock}.</p>
 */
@Mixin(SeaPickleBlock.class)
public abstract class MixinSeaPickleBlock {

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$bedrockOutline(
            final BlockState state,
            final BlockView world,
            final BlockPos pos,
            final CallbackInfoReturnable<?> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            cir.cancel();
        }
    }
}
