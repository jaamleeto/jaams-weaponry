package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.animation.AnimationAccessor;
import net.jaams.weaponry.animation.AnimationHelper;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;


@Mixin(LivingEntityRenderer.class)
public abstract class AnimationMobRendererMixin {

    @Shadow
    @Final
    protected EntityModel<?> model;

    @Unique
    private String master;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void jaams$afterSetupAnim(LivingEntity entity, float entityYaw, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (master == null) {
            if (!AnimationAPI.animations.isEmpty())
                master = "jaams_weaponry";
            else
                return;
        }
        if (!master.equals("jaams_weaponry"))
            return;

        
        if (entity instanceof Player)
            return;

        PlayerAnimation animation = AnimationHelper.getActiveAnimation(entity);
        if (animation == null)
            return;

        
        
        
        AnimationHelper.advanceAnimation(entity);

        
        if (!AnimationHelper.hasActiveAnimation(entity)) {
            resetAnimationBones(animation);
            return;
        }

        
        float smoothProgress = AnimationHelper.getSmoothProgressForMob(entity, animation);
        float blendFactor = AnimationHelper.getMobBlendFactor(entity);
        applyAnimationToModel(entity, animation, smoothProgress, blendFactor, partialTick);
    }

    @Unique
    private void resetAnimationBones(PlayerAnimation animation) {
        for (String boneName : animation.bones.keySet()) {
            ModelPart part = null;
            if (model instanceof HumanoidModel<?> hm) {
                @SuppressWarnings("unchecked")
                AnimationAccessor acc = new AnimationAccessor.Humanoid((HumanoidModel<LivingEntity>) (Object) hm);
                part = acc.get(boneName);
            } else if (model instanceof HierarchicalModel<?> hm) {
                AnimationAccessor acc = new AnimationAccessor.Hierarchical(hm);
                part = acc.get(boneName);
            }
            if (part != null) {
                var init = part.getInitialPose();
                part.x = init.x;
                part.y = init.y;
                part.z = init.z;
                part.xScale = 1.0F;
                part.yScale = 1.0F;
                part.zScale = 1.0F;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Unique
    private void applyAnimationToModel(LivingEntity entity, PlayerAnimation animation, float progress,
            float blendFactor, float partialTick) {
        if (model instanceof HumanoidModel<?> humanoidModel) {
            applyBoneTransforms(entity, animation, progress,
                    new AnimationAccessor.Humanoid((HumanoidModel<LivingEntity>) (Object) humanoidModel), blendFactor,
                    partialTick);
        } else if (model instanceof HierarchicalModel<?> hierarchicalModel) {
            applyBoneTransforms(entity, animation, progress,
                    new AnimationAccessor.Hierarchical(hierarchicalModel), blendFactor, partialTick);
        }
    }

    @Unique
    private float getMobVanillaBlendFactor(LivingEntity entity, PlayerAnimation animation,
            String boneName, float partialTick) {
        float result = 0.0F;

        
        if (!animation.ignoreSwing && model instanceof HumanoidModel<?> hm) {
            boolean isArm = boneName.equals("right_arm") || boneName.equals("left_arm");
            if (isArm) {
                int id = entity.getId();
                float attackTime = hm.attackTime;
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
                if (blend > 0f) {
                    result = Math.max(result, Mth.clamp(blend, 0f, 1f));
                }
            }
        }

        
        if (boneName.equals("right_leg") || boneName.equals("left_leg")) {
            float limbSwingAmount = entity.walkAnimation.speed(partialTick);
            if (limbSwingAmount > 0.01F) {
                result = Math.max(result, Mth.clamp(limbSwingAmount / 0.3F, 0.0F, 1.0F));
            }
        }

        
        if (!animation.ignoreItemPoses && model instanceof HumanoidModel<?> hm) {
            if (boneName.equals("right_arm") || boneName.equals("left_arm")) {
                HumanoidModel.ArmPose armPose = boneName.equals("right_arm")
                        ? hm.rightArmPose
                        : hm.leftArmPose;
                if (armPose != HumanoidModel.ArmPose.EMPTY && armPose != HumanoidModel.ArmPose.ITEM) {
                    result = Math.max(result, 1.0F);
                } else if (armPose == HumanoidModel.ArmPose.ITEM) {
                    
                    
                    
                    
                    
                    
                }
            }
        }

        return result;
    }

    @Unique
    private void applyBoneTransforms(LivingEntity entity, PlayerAnimation animation,
            float progress, AnimationAccessor bones, float blendFactor, float partialTick) {

        for (Map.Entry<String, AnimationAPI.PlayerBone> entry : animation.bones.entrySet()) {
            String boneName = entry.getKey();
            AnimationAPI.PlayerBone bone = entry.getValue();

            ModelPart modelPart = bones.get(boneName);
            if (modelPart == null)
                continue;

            
            float vanillaBlend = getMobVanillaBlendFactor(entity, animation, boneName, partialTick);
            
            
            float effectiveBlend = Math.max(vanillaBlend, blendFactor);

            if (effectiveBlend >= 1.0F && blendFactor <= 0.0F)
                continue;

            
            float origXRot = modelPart.xRot;
            float origYRot = modelPart.yRot;
            float origZRot = modelPart.zRot;
            float origX = modelPart.x;
            float origY = modelPart.y;
            float origZ = modelPart.z;
            float origXScale = modelPart.xScale;
            float origYScale = modelPart.yScale;
            float origZScale = modelPart.zScale;

            Vec3 rotation = AnimationAPI.PlayerBone.interpolate(bone.rotations, progress, entity);
            if (rotation != null) {
                modelPart.xRot = (float) Math.toRadians(rotation.x);
                modelPart.yRot = (float) Math.toRadians(rotation.y);
                modelPart.zRot = (float) Math.toRadians(rotation.z);
            }

            Vec3 position = AnimationAPI.PlayerBone.interpolate(bone.positions, progress, entity);
            if (position != null) {
                bones.resetPosition(modelPart, boneName);
                modelPart.x += (float) position.x;
                modelPart.y -= (float) position.y;
                modelPart.z += (float) position.z;
            }

            Vec3 scale = AnimationAPI.PlayerBone.interpolate(bone.scales, progress, entity);
            if (scale != null) {
                modelPart.xScale = (float) scale.x;
                modelPart.yScale = (float) scale.y;
                modelPart.zScale = (float) scale.z;
            }

            
            if (effectiveBlend > 0.0F) {
                if (rotation != null) {
                    modelPart.xRot = origXRot * effectiveBlend + modelPart.xRot * (1.0F - effectiveBlend);
                    modelPart.yRot = origYRot * effectiveBlend + modelPart.yRot * (1.0F - effectiveBlend);
                    modelPart.zRot = origZRot * effectiveBlend + modelPart.zRot * (1.0F - effectiveBlend);
                }
                if (position != null) {
                    modelPart.x = origX * effectiveBlend + modelPart.x * (1.0F - effectiveBlend);
                    modelPart.y = origY * effectiveBlend + modelPart.y * (1.0F - effectiveBlend);
                    modelPart.z = origZ * effectiveBlend + modelPart.z * (1.0F - effectiveBlend);
                }
                if (scale != null) {
                    modelPart.xScale = origXScale * effectiveBlend + modelPart.xScale * (1.0F - effectiveBlend);
                    modelPart.yScale = origYScale * effectiveBlend + modelPart.yScale * (1.0F - effectiveBlend);
                    modelPart.zScale = origZScale * effectiveBlend + modelPart.zScale * (1.0F - effectiveBlend);
                }
            }
        }
    }

}
