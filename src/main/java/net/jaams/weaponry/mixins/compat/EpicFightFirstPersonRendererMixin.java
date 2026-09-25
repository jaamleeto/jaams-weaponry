package net.jaams.weaponry.mixins.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.jaams.weaponry.compat.EpicFightCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.FirstPersonRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

/**
 * Bridges the mod's Epic Fight compat procedural poses (gun aiming) into Epic Fight's
 * first-person renderer.
 *
 * <p>Unlike the third-person path ({@code PatchedEntityRenderer#setArmaturePose}), Epic Fight's
 * {@link FirstPersonRenderer} never calls {@link Armature#setPose}. It builds a {@link Pose}
 * directly and converts it to world matrices via {@link Armature#getPoseAsTransformMatrix}. This
 * mixin intercepts that conversion and pushes the mod's procedural per-bone transforms into the
 * pose first, so the gun aiming animation is visible on the first-person Epic Fight model.
 *
 * <p>The pose logic lives in {@link EpicFightCompat} and no longer depends on the base
 * Animation API.
 */
@Mixin(value = FirstPersonRenderer.class, remap = false)
public abstract class EpicFightFirstPersonRendererMixin {

    @Unique
    private LocalPlayerPatch jaams$renderingPatch;

    @Unique
    private float jaams$renderingPartialTicks;

    @Inject(method = "render(Lnet/minecraft/client/player/LocalPlayer;Lyesman/epicfight/client/world/capabilites/entitypatch/player/LocalPlayerPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void jaams$captureRenderContext(LocalPlayer localPlayer, LocalPlayerPatch entitypatch,
            LivingEntityRenderer<?, ?> renderer, MultiBufferSource bufferSource, PoseStack poseStack, int packedLight,
            float partialTicks, CallbackInfo ci) {
        if (localPlayer != Minecraft.getInstance().player
                || !Minecraft.getInstance().options.getCameraType().isFirstPerson())
            ci.cancel();
        this.jaams$renderingPatch = entitypatch;
        this.jaams$renderingPartialTicks = partialTicks;
    }

    @Redirect(method = "render(Lnet/minecraft/client/player/LocalPlayer;Lyesman/epicfight/client/world/capabilites/entitypatch/player/LocalPlayerPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/model/Armature;getPoseAsTransformMatrix(Lyesman/epicfight/api/animation/Pose;Z)[Lyesman/epicfight/api/utils/math/OpenMatrix4f;"), remap = false)
    private OpenMatrix4f[] jaams$redirectGetPoseAsTransformMatrix(Armature armature, Pose pose, boolean arg) {
        LocalPlayerPatch entitypatch = this.jaams$renderingPatch;
        if (entitypatch != null) {
            Entity entity = entitypatch.getOriginal();
            if (entity instanceof Player player) {
                EpicFightCompat.applyProceduralPoses(armature, pose, player, this.jaams$renderingPartialTicks);
            }
        }
        return armature.getPoseAsTransformMatrix(pose, arg);
    }
}
