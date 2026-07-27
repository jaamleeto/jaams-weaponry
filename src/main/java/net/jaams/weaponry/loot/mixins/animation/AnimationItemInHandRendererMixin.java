package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.util.ModAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(ItemInHandRenderer.class)
public abstract class AnimationItemInHandRendererMixin {
    private String master = null;
    private Minecraft mc = Minecraft.getInstance();
    private EntityRenderDispatcher dispatcher = null;

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void renderHandsWithItems(float f, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
            LocalPlayer localPlayer, int i, CallbackInfo ci) {
        if (master == null) {
            if (!AnimationAPI.animations.isEmpty())
                master = "jaams_weaponry";
            else
                return;
        }
        if (!master.equals("jaams_weaponry"))
            return;
        if (ModAnimations.isLocalPlayerInFirstPerson(localPlayer)) {
            if (dispatcher == null)
                dispatcher = mc.getEntityRenderDispatcher();
            String currentAnim = ModAnimations.getCurrentAnimationName(localPlayer);
            if (!currentAnim.isEmpty()
                    && (!ModAnimations.isFirstPersonAnimation(localPlayer)
                            || ModAnimations.isAnimationReset(localPlayer))) {
                PlayerModel model = ((PlayerRenderer) dispatcher.getRenderer(localPlayer)).getModel();
                model.setupAnim(localPlayer, 0, 0, localPlayer.tickCount + f, 0, 0);
            }
            if (ModAnimations.isFirstPersonAnimation(localPlayer)) {
                ci.cancel();
            }
        }
    }
}
