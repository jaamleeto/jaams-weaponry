package net.jaams.weaponry.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.jaams.weaponry.client.ClientAnimationUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {
    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"))
    private void jaams_setPanelFlag(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
            CallbackInfo ci) {
        ClientAnimationUtils.isRenderingInventoryPanel = true;
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("RETURN"))
    private void jaams_resetPanelFlag(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
            CallbackInfo ci) {
        ClientAnimationUtils.isRenderingInventoryPanel = false;
    }
}
