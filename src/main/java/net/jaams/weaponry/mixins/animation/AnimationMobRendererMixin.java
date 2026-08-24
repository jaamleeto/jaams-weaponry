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
import net.jaams.weaponry.compat.CitadelCompat;

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
            Object part = null;
            if (model instanceof HumanoidModel<?> hm) {
                @SuppressWarnings("unchecked")
                AnimationAccessor acc = new AnimationAccessor.Humanoid((HumanoidModel<LivingEntity>) (Object) hm);
                part = acc.get(boneName);
            } else if (model instanceof HierarchicalModel<?> hm) {
                AnimationAccessor acc = new AnimationAccessor.Hierarchical(hm);
                part = acc.get(boneName);
            } else if (CitadelCompat.isBasicEntityModel(model)) {
                part = CitadelCompat.getBone(model, boneName);
            }
            if (part != null) {
                CitadelCompat.resetPosition(part);
                CitadelCompat.setXScale(part, 1.0F);
                CitadelCompat.setYScale(part, 1.0F);
                CitadelCompat.setZScale(part, 1.0F);
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
        } else if (CitadelCompat.isBasicEntityModel(model)) {
            applyBoneTransforms(entity, animation, progress,
                    new CitadelAccessor(model), blendFactor, partialTick);
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

            Object boneObj = bones.get(boneName);
            if (boneObj == null)
                continue;

            
            float vanillaBlend = getMobVanillaBlendFactor(entity, animation, boneName, partialTick);
            
            
            float effectiveBlend = Math.max(vanillaBlend, blendFactor);

            if (effectiveBlend >= 1.0F && blendFactor <= 0.0F)
                continue;

            CitadelCompat.captureInitialPose(boneObj);

            float origXRot = CitadelCompat.getXRot(boneObj);
            float origYRot = CitadelCompat.getYRot(boneObj);
            float origZRot = CitadelCompat.getZRot(boneObj);
            float origX = CitadelCompat.getX(boneObj);
            float origY = CitadelCompat.getY(boneObj);
            float origZ = CitadelCompat.getZ(boneObj);
            float origXScale = CitadelCompat.getXScale(boneObj);
            float origYScale = CitadelCompat.getYScale(boneObj);
            float origZScale = CitadelCompat.getZScale(boneObj);

            Vec3 rotation = AnimationAPI.PlayerBone.interpolate(bone.rotations, progress, entity);
            if (rotation != null) {
                CitadelCompat.setXRot(boneObj, (float) Math.toRadians(rotation.x));
                CitadelCompat.setYRot(boneObj, (float) Math.toRadians(rotation.y));
                CitadelCompat.setZRot(boneObj, (float) Math.toRadians(rotation.z));
            }

            Vec3 position = AnimationAPI.PlayerBone.interpolate(bone.positions, progress, entity);
            if (position != null) {
                bones.resetPosition(boneObj, boneName);
                CitadelCompat.setX(boneObj, CitadelCompat.getX(boneObj) + (float) position.x);
                CitadelCompat.setY(boneObj, CitadelCompat.getY(boneObj) - (float) position.y);
                CitadelCompat.setZ(boneObj, CitadelCompat.getZ(boneObj) + (float) position.z);
            }

            Vec3 scale = AnimationAPI.PlayerBone.interpolate(bone.scales, progress, entity);
            if (scale != null) {
                CitadelCompat.setXScale(boneObj, (float) scale.x);
                CitadelCompat.setYScale(boneObj, (float) scale.y);
                CitadelCompat.setZScale(boneObj, (float) scale.z);
            }

            
            if (effectiveBlend > 0.0F) {
                if (rotation != null) {
                    CitadelCompat.setXRot(boneObj, origXRot * effectiveBlend + CitadelCompat.getXRot(boneObj) * (1.0F - effectiveBlend));
                    CitadelCompat.setYRot(boneObj, origYRot * effectiveBlend + CitadelCompat.getYRot(boneObj) * (1.0F - effectiveBlend));
                    CitadelCompat.setZRot(boneObj, origZRot * effectiveBlend + CitadelCompat.getZRot(boneObj) * (1.0F - effectiveBlend));
                }
                if (position != null) {
                    CitadelCompat.setX(boneObj, origX * effectiveBlend + CitadelCompat.getX(boneObj) * (1.0F - effectiveBlend));
                    CitadelCompat.setY(boneObj, origY * effectiveBlend + CitadelCompat.getY(boneObj) * (1.0F - effectiveBlend));
                    CitadelCompat.setZ(boneObj, origZ * effectiveBlend + CitadelCompat.getZ(boneObj) * (1.0F - effectiveBlend));
                }
                if (scale != null) {
                    CitadelCompat.setXScale(boneObj, origXScale * effectiveBlend + CitadelCompat.getXScale(boneObj) * (1.0F - effectiveBlend));
                    CitadelCompat.setYScale(boneObj, origYScale * effectiveBlend + CitadelCompat.getYScale(boneObj) * (1.0F - effectiveBlend));
                    CitadelCompat.setZScale(boneObj, origZScale * effectiveBlend + CitadelCompat.getZScale(boneObj) * (1.0F - effectiveBlend));
                }
            }
        }
    }

    private static final class CitadelAccessor implements AnimationAccessor {
        private final Object model;

        CitadelAccessor(Object model) {
            this.model = model;
        }

        @Override
        public Object get(String boneName) {
            return CitadelCompat.getBone(model, boneName);
        }

        @Override
        public void resetPosition(Object part, String boneName) {
            CitadelCompat.resetPosition(part);
        }
    }

}
