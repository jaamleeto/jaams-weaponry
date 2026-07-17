package net.jaams.weaponry.mixins.client;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;

import org.joml.Matrix4f;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.Font;

@Mixin(value = ClientTextTooltip.class, priority = 1100)
public abstract class ClientTextTooltipMixin implements ClientTooltipComponent {
    @Shadow
    private FormattedCharSequence text;

    @Inject(method = "getHeight()I", at = @At("HEAD"), cancellable = true)
    private void jaam$modifyHeight(CallbackInfoReturnable<Integer> cir) {
        boolean hasCustomFontChar = false;
        boolean[] result = new boolean[] { false };
        this.text.accept((index, style, codePoint) -> {
            if (codePoint == '\uFFF0') {
                result[0] = true;
                return false;
            }
            return true;
        });
        hasCustomFontChar = result[0];
        if (hasCustomFontChar) {
            cir.setReturnValue(13);
        }
    }

    @Inject(method = "renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", at = @At("HEAD"), cancellable = true)
    private void jaam$onRenderText(Font font, int x, int y, Matrix4f matrix,
            MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        boolean[] hasCustomFontChar = new boolean[] { false };
        this.text.accept((index, style, codePoint) -> {
            if (codePoint == '\uFFF0') {
                hasCustomFontChar[0] = true;
                return false;
            }
            return true;
        });
        float adjustedY = hasCustomFontChar[0] ? (float) y + 2.0F : (float) y;
        font.drawInBatch(this.text, (float) x, adjustedY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0,
                15728880);
        ci.cancel();
    }

    @Unique
    private String jaam$getTextContent(FormattedCharSequence sequence) {
        StringBuilder builder = new StringBuilder();
        sequence.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });
        return builder.toString();
    }
}
