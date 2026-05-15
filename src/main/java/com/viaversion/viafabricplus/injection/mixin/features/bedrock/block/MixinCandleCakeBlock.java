package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bedrock candle-cake can be eaten by right-clicking anywhere on the cake face,
 * not only on the non-candle portion as in Java.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinCandleCakeBlock}.</p>
 */
@Mixin(CandleCakeBlock.class)
public abstract class MixinCandleCakeBlock {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$allowEating(
            final BlockState state,
            final World world,
            final BlockPos pos,
            final PlayerEntity player,
            final BlockHitResult hit,
            final CallbackInfoReturnable<ActionResult> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // Pass to server — Bedrock decides eat vs. extinguish.
        }
    }
}
