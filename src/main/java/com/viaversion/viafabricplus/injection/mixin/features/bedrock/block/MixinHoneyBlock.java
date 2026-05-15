package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * On Bedrock, honey blocks do not slow horizontal movement when walked on
 * from the side, only when slid down vertically.  The movement mixin handles
 * the slide; this block mixin disables the Java-side slow effect.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinHoneyBlock}.</p>
 */
@Mixin(HoneyBlock.class)
public abstract class MixinHoneyBlock {

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$skipHorizontalSlow(
            final BlockState state,
            final World world,
            final BlockPos pos,
            final Entity entity,
            final CallbackInfo ci) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            ci.cancel();
        }
    }
}
