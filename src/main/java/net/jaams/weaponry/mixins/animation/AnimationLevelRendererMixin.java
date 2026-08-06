package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import org.joml.Matrix4f;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.util.ModAnimations;

import net.jaams.weaponry.mixins.access.CameraAccessor;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(LevelRenderer.class)
public abstract class AnimationLevelRendererMixin {
    private String master = null;
    private Minecraft mc = Minecraft.getInstance();

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z"))
    private void fakeThirdPersonMode(net.minecraft.client.DeltaTracker deltaTracker,
            boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture,
            Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (master == null) {
            if (!AnimationAPI.animations.isEmpty())
                master = "jaams_weaponry";
            else
                return;
        }
        if (!master.equals("jaams_weaponry")) {
            return;
        }
        if (camera.getEntity() instanceof Player player
                && ModAnimations.shouldRenderInFirstPerson(player)) {
            ((CameraAccessor) camera).setDetached(true);
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z", shift = At.Shift.AFTER))
    private void resetThirdPerson(net.minecraft.client.DeltaTracker deltaTracker,
            boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture,
            Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (master == null) {
            if (!AnimationAPI.animations.isEmpty())
                master = "jaams_weaponry";
            else
                return;
        }
        if (!master.equals("jaams_weaponry")) {
            return;
        }
        ((CameraAccessor) camera).setDetached(false);
    }
}
