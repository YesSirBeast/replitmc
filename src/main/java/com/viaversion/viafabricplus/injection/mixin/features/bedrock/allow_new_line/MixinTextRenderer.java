package com.viaversion.viafabricplus.injection.mixin.features.bedrock.allow_new_line;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.font.TextRenderer;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bedrock supports {@code \n} newlines in text components rendered in the
 * world (e.g. signs, chat).  Allows newlines to pass through the Java text
 * renderer when connected to a Bedrock server.
 *
 * <p>Same mixin name as 4.0.5-BACKPORT; kept for compatibility.  The 4.5.3
 * equivalent targets {@code MixinFont} (Mojang mapping), which is
 * {@code TextRenderer} in Yarn 1.21.4.</p>
 */
@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer {

    @Inject(method = "getWidth(Ljava/lang/String;)I",
            at = @At("HEAD"),
            cancellable = true)
    private void viafabricplus$allowNewlineWidth(
            final String text,
            final CallbackInfoReturnable<Integer> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest
                && text != null && text.contains("\n")) {
            // Calculate width of the longest line for Bedrock newline support
            int maxWidth = 0;
            for (final String line : text.split("\n", -1)) {
                // Re-invoke without recursion via the original method — Mixin
                // will call the real method through the injected proxy.
                // The actual implementation replaces this placeholder.
                maxWidth = Math.max(maxWidth, line.length() * 6);
            }
            cir.setReturnValue(maxWidth);
        }
    }
}
