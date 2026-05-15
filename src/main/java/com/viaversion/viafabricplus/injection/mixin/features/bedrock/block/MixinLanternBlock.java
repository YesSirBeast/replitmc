package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bedrock lantern has a larger hitbox than Java (full 6×9×6 px vs Java's 6×7×6).
 * Restores Bedrock outline shape when connected to a Bedrock server.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinLanternBlock}.</p>
 */
@Mixin(LanternBlock.class)
public abstract class MixinLanternBlock {

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
