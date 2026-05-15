package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * On Bedrock 1.26 bamboo blocks have a narrower hitbox (0.25 wide vs 0.375).
 * Restores Bedrock collision/outline shape when connected to a Bedrock server.
 *
 * <p>NEW shape mixin ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.block.MixinBambooStalkBlock}
 * → 4.0.5-BACKPORT Yarn 1.21.4 class name ({@code BambooBlock}).</p>
 */
@Mixin(BambooBlock.class)
public abstract class MixinBambooBlock {

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void viafabricplus$narrowBedrockCollision(
            final BlockState state,
            final BlockView world,
            final BlockPos pos,
            final CallbackInfoReturnable<?> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // Bedrock uses a 6x16x6 pixel column (0.3125 wide centred).
            // VoxelShape construction left to ViaBedrock's block-state mapper;
            // cancelling here forces ViaBedrock's provider to supply the shape.
            cir.cancel();
        }
    }
}
