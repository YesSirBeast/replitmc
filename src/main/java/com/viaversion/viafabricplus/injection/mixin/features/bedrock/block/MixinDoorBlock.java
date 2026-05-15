package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
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
 * Bedrock 1.26 changed door interaction: iron doors can be opened by hand
 * when survival mode is connected to a Bedrock server.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinDoorBlock}.</p>
 */
@Mixin(DoorBlock.class)
public abstract class MixinDoorBlock {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$allowIronDoorInteraction(
            final BlockState state,
            final World world,
            final BlockPos pos,
            final PlayerEntity player,
            final BlockHitResult hit,
            final CallbackInfoReturnable<ActionResult> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // On Bedrock, doors always respond to use — let the server decide.
            // We return PASS to avoid client-side cancellation.
        }
    }
}
