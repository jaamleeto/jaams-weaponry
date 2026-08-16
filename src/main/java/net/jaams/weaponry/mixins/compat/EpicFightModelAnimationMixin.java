package net.jaams.weaponry.mixins.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.joml.Quaternionf;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.animation.AnimationAPI.PlayerBone;
import net.jaams.weaponry.animation.AnimationHelper;
import net.jaams.weaponry.util.ModAnimations;
import net.jaams.weaponry.util.ModAnimations.AnimationTickResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bridges the mod's animation system into Epic Fight's skeleton renderer.
 *
 * <p>Epic Fight replaces the vanilla {@code LivingEntityRenderer}/{@code HumanoidModel}
 * pipeline with its own skinned mesh driven by {@code PatchedEntityRenderer}. Every patched
 * renderer builds a {@link Pose} and hands it to {@link Armature#setPose}. This mixin
 * intercepts that call (before it happens) and pushes the mod's per-bone transforms into the
 * pose, so the mod's bedrock-style animations are visible while Epic Fight is in charge.
 *
 * <p>Bone names from the animation files ({@code torso}, {@code head}, {@code right_arm} ...)
 * are mapped onto Epic Fight's humanoid armature joints. Epic Fight's armature frame is related
 * to the vanilla {@code ModelPart} frame by a 180° rotation about Y (a point {@code (x, y, z)}
 * in vanilla frame sits at {@code (-x, y, -z)} in the armature frame, i.e. both frames face the
 * same physical direction but differ in handedness). Rotations use the same composition as
 * vanilla ({@code R = Rx * Ry * Rz}, i.e. {@code Quaternionf.rotationXYZ}) with the frame
 * alignment applied by conjugation ({@code Ry(π)·R(x,y,z)·Ry(π) = R(-x, y, -z)}), premultiplied
 * by the inverse bind chain ({@code root → joint}). Whole-leg rotations are applied to the
 * {@code Thigh_*} joints so the full leg chain (thigh, shin, knee) moves together. Positions
 * are relative offsets converted from ModelPart pixels (16 per block) into Epic Fight mesh
 * units with the same {@code (-x, y, -z)} frame mapping.
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
            jaams$applyPlayerAnimation(player, armature, pose, partialTicks);
        } else {
            jaams$applyMobAnimation(entity, armature, pose, partialTicks);
        }
    }

    @Unique
    private void jaams$applyPlayerAnimation(Player player, Armature armature, Pose pose, float partialTicks) {
        boolean isFirstPerson = ModAnimations.shouldRenderInFirstPerson(player);
        AnimationTickResult result = ModAnimations.advanceAnimation(player, player.tickCount + partialTicks);
        if (!result.active || result.animation == null)
            return;
        PlayerAnimation animation = result.animation;
        if (animation.skipInFirstPerson && isFirstPerson)
            return;

        float globalBlend = ModAnimations.getAnimationBlendFactor(player);
        float animationProgress = result.progress;

        for (Map.Entry<String, PlayerBone> entry : animation.bones.entrySet()) {
            String jointName = jaams$toJointName(entry.getKey());
            if (jointName == null)
                continue;

            float vanillaBlend = jaams$getVanillaBlend(player, animation, entry.getKey(), partialTicks);
            float effectiveBlend = Math.max(vanillaBlend, globalBlend);
            if (effectiveBlend >= 1.0F && globalBlend <= 0.0F)
                continue;

            jaams$putBlendedTransform(armature, pose, jointName, entry.getValue(), animationProgress, player,
                    effectiveBlend);
        }

        Map<String, Float> combinableData = ModAnimations.getCombinableRenderData(player);
        for (Map.Entry<String, Float> combinableEntry : combinableData.entrySet()) {
            PlayerAnimation combinableAnim = ModAnimations.getAnimation(combinableEntry.getKey());
            if (combinableAnim == null)
                continue;
            if (combinableAnim.skipInFirstPerson && isFirstPerson)
                continue;

            float combinableProgress = combinableEntry.getValue();
            for (Map.Entry<String, PlayerBone> boneEntry : combinableAnim.bones.entrySet()) {
                String jointName = jaams$toJointName(boneEntry.getKey());
                if (jointName == null)
                    continue;

                float vanillaBlend = jaams$getVanillaBlend(player, combinableAnim, boneEntry.getKey(), partialTicks);
                if (vanillaBlend >= 1.0F)
                    continue;

                jaams$putBlendedTransform(armature, pose, jointName, boneEntry.getValue(), combinableProgress, player,
                        vanillaBlend);
            }
        }
    }

    @Unique
    private void jaams$applyMobAnimation(LivingEntity entity, Armature armature, Pose pose, float partialTicks) {
        PlayerAnimation animation = AnimationHelper.getActiveAnimation(entity);
        if (animation == null)
            return;

        AnimationHelper.advanceAnimation(entity);
        if (!AnimationHelper.hasActiveAnimation(entity))
            return;

        float progress = AnimationHelper.getSmoothProgressForMob(entity, animation);
        float blendFactor = AnimationHelper.getMobBlendFactor(entity);

        for (Map.Entry<String, PlayerBone> entry : animation.bones.entrySet()) {
            String jointName = jaams$toJointName(entry.getKey());
            if (jointName == null)
                continue;

            float vanillaBlend = jaams$getMobVanillaBlend(entity, animation, entry.getKey(), partialTicks);
            float effectiveBlend = Math.max(vanillaBlend, blendFactor);
            if (effectiveBlend >= 1.0F && blendFactor <= 0.0F)
                continue;

            jaams$putBlendedTransform(armature, pose, jointName, entry.getValue(), progress, entity, effectiveBlend);
        }
    }

    /**
     * Evaluates the bone's keyframes at {@code progress}, converts the values into Epic Fight
     * joint space and blends them against the pose already prepared by Epic Fight's animator.
     */
    @Unique
    private void jaams$putBlendedTransform(Armature armature, Pose pose, String jointName, PlayerBone bone, float progress,
            LivingEntity entity, float effectiveBlend) {
        JointTransform animationTransform = jaams$buildJointTransform(armature, jointName, bone, progress, entity);
        JointTransform baseTransform = pose.orElseEmpty(jointName);
        JointTransform blended = JointTransform.interpolate(animationTransform, baseTransform,
                Mth.clamp(effectiveBlend, 0.0F, 1.0F));
        pose.putJointData(jointName, blended);
    }

    /**
     * Builds the joint transform from the bone's keyframes.
     *
     * <p>The mod's rotations are expressed in the vanilla {@code ModelPart} frame, where up is
     * {@code +Y} and forward is {@code +Z}. Epic Fight's armature frame is related to that frame
     * by a 180° rotation about Y ({@code (x, y, z) ↦ (-x, y, -z)}), so the vanilla
     * {@code Rx·Ry·Rz} composition is mapped by conjugation
     * ({@code Ry(π)·R(x,y,z)·Ry(π) = R(-x, y, -z)}) and then premultiplied by the inverse of the
     * bind chain ({@code root → joint}), which brings the rotation into the joint's own local
     * frame. At rest this yields the vanilla standing pose.
     *
     * <p>Positions are relative offsets converted from ModelPart pixels (16 per block) into
     * Epic Fight mesh units with the same {@code (-x, y, -z)} frame mapping.
     */
    @Unique
    private JointTransform jaams$buildJointTransform(Armature armature, String jointName, PlayerBone bone, float progress,
            LivingEntity entity) {
        Vec3f translation = new Vec3f(0.0F, 0.0F, 0.0F);
        Quaternionf rotation = new Quaternionf();
        Vec3f scale = new Vec3f(1.0F, 1.0F, 1.0F);

        Quaternionf chain = jaams$computeBindChain(armature, jointName);
        Quaternionf chainInv = new Quaternionf(chain).conjugate();

        Vec3 rot = PlayerBone.interpolate(bone.rotations, progress, entity);
        if (rot != null) {
            float xRad = (float) Math.toRadians(rot.x);
            float yRad = (float) Math.toRadians(rot.y);
            float zRad = (float) Math.toRadians(rot.z);
            new Quaternionf(chainInv)
                    .mul(new Quaternionf().rotationXYZ(-xRad, yRad, -zRad))
                    .mul(chain, rotation);
        }

        Vec3 pos = PlayerBone.interpolate(bone.positions, progress, entity);
        if (pos != null) {
            translation.set((float) -pos.x / 16.0F, (float) pos.y / 16.0F, (float) -pos.z / 16.0F);
        }

        Vec3 sca = PlayerBone.interpolate(bone.scales, progress, entity);
        if (sca != null) {
            scale.set((float) sca.x, (float) sca.y, (float) sca.z);
        }

        return new JointTransform(translation, rotation, scale);
    }

    /**
     * Computes the bind rotation product along the path {@code root → joint}, i.e. the rotation
     * that takes the joint's local frame into the armature frame at rest. Returns the identity
     * quaternion when the armature has no such joint (for example non-humanoid armatures).
     */
    @Unique
    private Quaternionf jaams$computeBindChain(Armature armature, String jointName) {
        if (!armature.hasJoint(jointName)) {
            return new Quaternionf();
        }
        List<String> path = new ArrayList<>();
        armature.gatherAllJointsInPathToTerminal(jointName, path);
        Quaternionf chain = new Quaternionf();
        for (String name : path) {
            Joint joint = armature.searchJointByName(name);
            if (joint != null) {
                chain.mul(joint.getLocalTransform().toQuaternion());
            }
        }
        return chain;
    }

    @Unique
    private float jaams$getVanillaBlend(Player player, PlayerAnimation animation, String boneName, float partialTicks) {
        float result = 0.0F;

        if (!animation.ignoreSwing && (boneName.equals("right_arm") || boneName.equals("left_arm"))) {
            float attackTime = player.getAttackAnim(partialTicks);
            HumanoidArm attackingArm = null;
            if (player.swingingArm != null) {
                HumanoidArm mainArm = player.getMainArm();
                attackingArm = player.swingingArm == InteractionHand.MAIN_HAND
                        ? mainArm
                        : mainArm.getOpposite();
            }
            boolean isAttackingArm = attackingArm == null
                    || boneName.equals(attackingArm == HumanoidArm.RIGHT ? "right_arm" : "left_arm");
            if (isAttackingArm) {
                float attackBlend = attackTime > 0.0F
                        ? Mth.clamp(attackTime / 0.3F, 0.0F, 1.0F)
                        : 0.0F;
                if (attackBlend > 0.0F)
                    result = Math.max(result, attackBlend);
            }
        }

        if (boneName.equals("right_leg") || boneName.equals("left_leg")) {
            float limbSwingAmount = player.walkAnimation.speed(partialTicks);
            if (limbSwingAmount > 0.01F)
                result = Math.max(result, Mth.clamp(limbSwingAmount / 0.3F, 0.0F, 1.0F));
        }

        return result;
    }

    @Unique
    private float jaams$getMobVanillaBlend(LivingEntity entity, PlayerAnimation animation, String boneName,
            float partialTicks) {
        float result = 0.0F;

        if (!animation.ignoreSwing && (boneName.equals("right_arm") || boneName.equals("left_arm"))) {
            int id = entity.getId();
            float attackTime = entity.getAttackAnim(partialTicks);
            Float currentBlend = AnimationAPI.mobSwingBlend.get(id);
            float smoothBlend = currentBlend != null ? currentBlend : 0f;

            if (attackTime > 0f) {
                AnimationAPI.mobSwingBlend.put(id, attackTime);
            } else if (smoothBlend > 0.01f) {
                smoothBlend *= 0.65f;
                AnimationAPI.mobSwingBlend.put(id, smoothBlend);
            } else {
                AnimationAPI.mobSwingBlend.remove(id);
            }

            float attackBlend = attackTime > 0f
                    ? Mth.clamp(attackTime / 0.3f, 0f, 1f)
                    : 0f;
            float blend = Math.max(attackBlend, smoothBlend);
            if (blend > 0f)
                result = Math.max(result, Mth.clamp(blend, 0f, 1f));
        }

        if (boneName.equals("right_leg") || boneName.equals("left_leg")) {
            float limbSwingAmount = entity.walkAnimation.speed(partialTicks);
            if (limbSwingAmount > 0.01F)
                result = Math.max(result, Mth.clamp(limbSwingAmount / 0.3F, 0.0F, 1.0F));
        }

        return result;
    }

    @Unique
    private String jaams$toJointName(String boneName) {
        switch (boneName) {
            case "torso":
            case "body":
                return "Torso";
            case "head":
                return "Head";
            case "right_arm":
                return "Arm_R";
            case "left_arm":
                return "Arm_L";
            case "right_leg":
                return "Thigh_R";
            case "left_leg":
                return "Thigh_L";
            default:
                return null;
        }
    }
}
