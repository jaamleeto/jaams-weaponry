package net.jaams.weaponry.compat;

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
 * Shared animation-bridging logic used by the Epic Fight compat mixins.
 *
 * <p>Epic Fight replaces the vanilla {@code LivingEntityRenderer}/{@code HumanoidModel}
 * pipeline with its own skinned mesh driven by {@code PatchedEntityRenderer} (third person)
 * and {@code FirstPersonRenderer} (first person). Both build a {@link Pose} and either pass it
 * to {@link Armature#setPose} or convert it directly via
 * {@link Armature#getPoseAsTransformMatrix}. This class pushes the mod's per-bone transforms
 * into the pose, so the mod's bedrock-style animations are visible while Epic Fight is in
 * charge.
 *
 * <p>Bone names from the animation files ({@code torso}, {@code head}, {@code right_arm} ...)
 * are mapped onto Epic Fight's humanoid armature joints. Epic Fight's armature frame is related
 * to the vanilla {@code ModelPart} frame by a 180° rotation about Y (a point {@code (x, y, z)}
 * in vanilla frame sits at {@code (-x, y, -z)} in the armature frame, i.e. both frames face the
 * same physical direction but differ in handedness). Rotations use the same composition as
 * vanilla ({@code R = Rx * Ry * Rz}, i.e. {@code Quaternionf.rotationXYZ}) and are transformed
 * into joint-local space via bind-chain conjugation ({@code chain⁻¹·R·chain}), which naturally
 * handles the frame conversion and the per-joint bind rotations (including the {@code Thigh_*}
 * joints' {@code Rx(180°)} bind). Positions are relative offsets converted from ModelPart pixels
 * (16 per block) into Epic Fight mesh units with the same {@code (-x, y, -z)} frame mapping.
 */
public final class EpicFightAnimationPose {

    private EpicFightAnimationPose() {
    }

    /**
     * Applies the player's active Jaam's animation (plus combinable overlays) to the pose
     * prepared by Epic Fight's animator.
     *
     * @param isFirstPerson whether the pose is being built for a first-person view (Epic Fight's
     *                      {@code FirstPersonRenderer}). When true, animations flagged with
     *                      {@code skipInFirstPerson} are skipped.
     */
    public static void applyPlayerPose(Armature armature, Pose pose, Player player, float partialTicks,
            boolean isFirstPerson) {
        if (armature == null || pose == null || player == null)
            return;
        AnimationTickResult result = ModAnimations.advanceAnimation(player, player.tickCount + partialTicks);
        if (!result.active || result.animation == null)
            return;
        PlayerAnimation animation = result.animation;
        if (animation.skipInFirstPerson && isFirstPerson)
            return;

        float globalBlend = ModAnimations.getAnimationBlendFactor(player);
        float animationProgress = result.progress;

        for (Map.Entry<String, PlayerBone> entry : animation.bones.entrySet()) {
            String jointName = toJointName(entry.getKey());
            if (jointName == null)
                continue;

            float vanillaBlend = getVanillaBlend(player, animation, entry.getKey(), partialTicks);
            float effectiveBlend = Math.max(vanillaBlend, globalBlend);
            if (effectiveBlend >= 1.0F && globalBlend <= 0.0F)
                continue;

            putBlendedTransform(armature, pose, jointName, entry.getValue(), animationProgress, player,
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
                String jointName = toJointName(boneEntry.getKey());
                if (jointName == null)
                    continue;

                float vanillaBlend = getVanillaBlend(player, combinableAnim, boneEntry.getKey(), partialTicks);
                if (vanillaBlend >= 1.0F)
                    continue;

                putBlendedTransform(armature, pose, jointName, boneEntry.getValue(), combinableProgress, player,
                        vanillaBlend);
            }
        }
    }

    /** Applies a mob's active Jaam's animation to the pose prepared by Epic Fight's animator. */
    public static void applyMobPose(Armature armature, Pose pose, LivingEntity entity, float partialTicks) {
        if (armature == null || pose == null || entity == null)
            return;
        PlayerAnimation animation = AnimationHelper.getActiveAnimation(entity);
        if (animation == null)
            return;

        AnimationHelper.advanceAnimation(entity);
        if (!AnimationHelper.hasActiveAnimation(entity))
            return;

        float progress = AnimationHelper.getSmoothProgressForMob(entity, animation);
        float blendFactor = AnimationHelper.getMobBlendFactor(entity);

        for (Map.Entry<String, PlayerBone> entry : animation.bones.entrySet()) {
            String jointName = toJointName(entry.getKey());
            if (jointName == null)
                continue;

            float vanillaBlend = getMobVanillaBlend(entity, animation, entry.getKey(), partialTicks);
            float effectiveBlend = Math.max(vanillaBlend, blendFactor);
            if (effectiveBlend >= 1.0F && blendFactor <= 0.0F)
                continue;

            putBlendedTransform(armature, pose, jointName, entry.getValue(), progress, entity, effectiveBlend);
        }
    }

    /**
     * Evaluates the bone's keyframes at {@code progress}, converts the values into Epic Fight
     * joint space and blends them against the pose already prepared by Epic Fight's animator.
     */
    private static void putBlendedTransform(Armature armature, Pose pose, String jointName, PlayerBone bone,
            float progress, LivingEntity entity, float effectiveBlend) {
        JointTransform animationTransform = buildJointTransform(armature, jointName, bone, progress, entity);
        JointTransform baseTransform = pose.orElseEmpty(jointName);
        JointTransform blended = JointTransform.interpolate(animationTransform, baseTransform,
                Mth.clamp(effectiveBlend, 0.0F, 1.0F));
        pose.putJointData(jointName, blended);
    }

    /**
     * Builds the joint transform from the bone's keyframes.
     *
     * <p>The mod's rotations are expressed in the vanilla {@code ModelPart} frame, where up is
     * {@code +Y} and forward is {@code +Z}. The vanilla {@code Rx·Ry·Rz} composition is
     * transformed into joint-local space via bind-chain conjugation
     * ({@code chain⁻¹·R(x,y,z)·chain}), which handles the armature frame conversion and
     * per-joint bind rotations. At rest this yields the vanilla standing pose.
     *
     * <p>Positions are relative offsets converted from ModelPart pixels (16 per block) into
     * Epic Fight mesh units with the same {@code (-x, y, -z)} frame mapping.
     */
    private static JointTransform buildJointTransform(Armature armature, String jointName, PlayerBone bone,
            float progress, LivingEntity entity) {
        Vec3f translation = new Vec3f(0.0F, 0.0F, 0.0F);
        Quaternionf rotation = new Quaternionf();
        Vec3f scale = new Vec3f(1.0F, 1.0F, 1.0F);

        Quaternionf chain = computeBindChain(armature, jointName);
        Quaternionf chainInv = new Quaternionf(chain).conjugate();

        Vec3 rot = PlayerBone.interpolate(bone.rotations, progress, entity);
        if (rot != null) {
            float xRad = (float) Math.toRadians(rot.x);
            float yRad = (float) Math.toRadians(rot.y);
            float zRad = (float) Math.toRadians(rot.z);
            float injectedX = isTorsoJoint(jointName) ? xRad
                    : isLegJoint(jointName) ? xRad : -xRad;
            float injectedZ = isTorsoJoint(jointName) ? zRad : -zRad;
            new Quaternionf(chainInv)
                    .mul(new Quaternionf().rotationXYZ(injectedX, yRad, injectedZ))
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
    private static Quaternionf computeBindChain(Armature armature, String jointName) {
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

    private static float getVanillaBlend(Player player, PlayerAnimation animation, String boneName,
            float partialTicks) {
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

    private static float getMobVanillaBlend(LivingEntity entity, PlayerAnimation animation, String boneName,
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

    private static boolean isTorsoJoint(String jointName) {
        return jointName.equals("Torso");
    }

    private static boolean isLegJoint(String jointName) {
        return jointName.equals("Thigh_R") || jointName.equals("Thigh_L");
    }

    private static String toJointName(String boneName) {
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
