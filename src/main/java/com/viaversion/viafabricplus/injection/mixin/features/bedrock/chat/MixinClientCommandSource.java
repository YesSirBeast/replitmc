package com.viaversion.viafabricplus.injection.mixin.features.bedrock.chat;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.network.ClientCommandSource;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;

/**
 * Removes fake/dummy player name suggestions added by ViaBedrock's entity
 * tracker when connected to a Bedrock server.  On Bedrock, the server sends
 * real player names differently; the dummy entries appear in the tab-complete
 * list and confuse users.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.chat.MixinClientSuggestionProvider}.</p>
 */
@Mixin(ClientCommandSource.class)
public abstract class MixinClientCommandSource {

    @Inject(method = "getPlayerNames", at = @At("RETURN"), cancellable = true)
    private void viafabricplus$filterDummyPlayers(
            final CallbackInfoReturnable<Collection<String>> cir) {

        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            final Collection<String> names = cir.getReturnValue();
            if (names != null) {
                names.removeIf(name -> name.startsWith("~dummy~"));
            }
        }
    }
}
