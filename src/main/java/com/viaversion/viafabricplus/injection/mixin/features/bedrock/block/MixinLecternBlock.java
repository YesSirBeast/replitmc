package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
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
 * Bedrock lectern opens the book directly; Java shows a "take book" button.
 * When connected to Bedrock skip the Java take-book UI.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinLecternBlock}.</p>
 */
@Mixin(LecternBlock.class)
public abstract class MixinLecternBlock {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$bedrockLecternUse(
            final BlockState state,
            final World world,
            final BlockPos pos,
            final PlayerEntity player,
            final BlockHitResult hit,
            final CallbackInfoReturnable<ActionResult> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // Allow interaction packet to go to server without client-side book check
        }
    }
}
