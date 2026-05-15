package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
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
 * Bedrock iron trapdoors can be toggled by hand (no redstone required).
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinTrapDoorBlock}.</p>
 */
@Mixin(TrapdoorBlock.class)
public abstract class MixinTrapdoorBlock {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$allowIronTrapdoor(
            final BlockState state,
            final World world,
            final BlockPos pos,
            final PlayerEntity player,
            final BlockHitResult hit,
            final CallbackInfoReturnable<ActionResult> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // Let server decide — Bedrock allows toggling iron trapdoors by hand
        }
    }
}
