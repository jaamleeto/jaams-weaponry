package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

import net.jaams.weaponry.util.ModAnimations;

import java.util.Random;

@Mixin(Camera.class)
public abstract class AnimationCameraMixin {

    private static final Random SHAKE_RNG = new Random();

    @Shadow
    protected abstract void setRotation(float yRot, float xRot, float roll);

    @Inject(method = "setup", at = @At("RETURN"))
    private void onSetup(BlockGetter level, Entity entity, boolean detached, boolean thirdPerson, float partialTick, CallbackInfo ci) {
        float intensity = ModAnimations.cameraShakeIntensity;
        if (intensity <= 0f)
            return;

        Camera self = (Camera) (Object) this;

        float offsetX = (SHAKE_RNG.nextFloat() - 0.5f) * 2.0f * intensity;
        float offsetY = (SHAKE_RNG.nextFloat() - 0.5f) * 2.0f * intensity;

        // Re-apply the rotation through setRotation so the cached rotation
        // quaternion (and forwards/up/left vectors) are updated too; the render
        // only uses camera.rotation(), so patching xRot/yRot alone is invisible.
        this.setRotation(self.getYRot() + offsetY, self.getXRot() + offsetX, self.getRoll());
    }
}
