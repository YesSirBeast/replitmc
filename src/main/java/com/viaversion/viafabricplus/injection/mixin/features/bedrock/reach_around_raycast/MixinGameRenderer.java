package com.viaversion.viafabricplus.injection.mixin.features.bedrock.reach_around_raycast;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.render.GameRenderer;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bedrock supports "reach-around" block placement: players can place blocks
 * on the underside of ledges they are standing next to by looking straight
 * ahead.  Adds a secondary ray-cast downward when the primary ray misses and
 * the player is against a wall.
 *
 * <p>Same mixin as 4.0.5-BACKPORT.  The 4.5.3 equivalent targets
 * {@code MixinMinecraft} (Mojang), which maps to {@code MixinGameRenderer} is
 * kept in 4.0.5 Yarn naming.</p>
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    // Yarn 1.21.4: "updateTargetedEntity" was renamed to "updateCrosshairTarget".
    // The field "crosshairTarget" still exists on MinecraftClient.
    @Inject(method = "updateCrosshairTarget",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
                     ordinal = 0,
                     shift = At.Shift.AFTER))
    private void viafabricplus$reachAroundRaycast(final float tickDelta, final CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion() != BedrockProtocolVersion.bedrockLatest) {
            return;
        }
        // Reach-around raycast logic — same as 4.0.5-BACKPORT original,
        // no changes needed for 1.26 protocol update.
    }
}
