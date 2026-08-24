package net.jaams.weaponry.mixins.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.jaams.weaponry.compat.EpicFightAnimationPose;
import net.jaams.weaponry.client.ClientAnimationUtils;
import net.jaams.weaponry.util.ModAnimations;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * Bridges the mod's animation system into Epic Fight's third-person skeleton renderer.
 *
 * <p>Epic Fight replaces the vanilla {@code LivingEntityRenderer}/{@code HumanoidModel}
 * pipeline with its own skinned mesh driven by {@code PatchedEntityRenderer}. Every patched
 * renderer builds a {@link Pose} and hands it to {@link Armature#setPose}. This mixin
 * intercepts that call (before it happens) and pushes the mod's per-bone transforms into the
 * pose, so the mod's bedrock-style animations are visible while Epic Fight is in charge.
 *
 * <p>The per-bone transform logic lives in {@link EpicFightAnimationPose}, shared with the
 * first-person bridge {@code EpicFightFirstPersonRendererMixin}.
 */
@Mixin(value = PatchedEntityRenderer.class, remap = false)
public abstract class EpicFightModelAnimationMixin {

    @Unique
    private LivingEntityPatch<?> jaams$renderingPatch;

    @Unique
    private float jaams$renderingPartialTicks;

    @Inject(method = "setArmaturePose", at = @At("HEAD"), remap = false)
    private void jaams$captureRenderContext(LivingEntityPatch<?> entitypatch, Armature armature, float partialTicks,
            CallbackInfo ci) {
        this.jaams$renderingPatch = entitypatch;
        this.jaams$renderingPartialTicks = partialTicks;
    }

    @Redirect(method = "setArmaturePose", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/model/Armature;setPose(Lyesman/epicfight/api/animation/Pose;)V"), remap = false)
    private void jaams$redirectArmatureSetPose(Armature armature, Pose pose) {
        LivingEntityPatch<?> entitypatch = this.jaams$renderingPatch;
        if (entitypatch != null) {
            jaams$applyAnimationPose(entitypatch, armature, this.jaams$renderingPartialTicks, pose);
        }
        armature.setPose(pose);
    }

    @Unique
    private void jaams$applyAnimationPose(LivingEntityPatch<?> entitypatch, Armature armature, float partialTicks,
            Pose pose) {
        if (entitypatch == null || pose == null)
            return;
        LivingEntity entity = entitypatch.getOriginal();
        if (entity == null)
            return;
        if (entity instanceof Player player) {
            EpicFightAnimationPose.applyPlayerPose(armature, pose, player, partialTicks,
                    ClientAnimationUtils.shouldRenderInFirstPerson(player));
            EpicFightAnimationPose.applyProceduralPoses(armature, pose, player, partialTicks);
        } else {
            EpicFightAnimationPose.applyMobPose(armature, pose, entity, partialTicks);
        }
    }
}
