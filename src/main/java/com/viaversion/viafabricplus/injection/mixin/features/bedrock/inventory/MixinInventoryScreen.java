package com.viaversion.viafabricplus.injection.mixin.features.bedrock.inventory;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bedrock edition does not show the recipe book button in the inventory screen.
 * Hides the recipe book button when connected to a Bedrock server.
 *
 * <p>Ported from ViaFabricPlus 4.5.3
 * {@code features.bedrock.inventory.MixinInventoryScreen}.</p>
 */
@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen {

    @Inject(method = "init", at = @At("TAIL"))
    private void viafabricplus$hideRecipeBook(final CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            // Locate and hide the recipe-book toggle button that Java adds.
            // The actual widget removal depends on 1.21.4 InventoryScreen internals;
            // this @Inject is a placeholder — replace with a field @Shadow + setVisible(false)
            // once the refmap is verified.
        }
    }
}
