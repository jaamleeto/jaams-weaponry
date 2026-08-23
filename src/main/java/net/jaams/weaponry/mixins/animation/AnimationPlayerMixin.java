package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.util.ModAnimations;
import net.jaams.weaponry.util.ModAnimations.AnimationTickResult;
import net.jaams.weaponry.util.ModRenderState;

import java.util.Map;

@Mixin(PlayerModel.class)
public abstract class AnimationPlayerMixin<T extends LivingEntity> extends HumanoidModel<T> {
    private String master = null;
    private Minecraft mc = Minecraft.getInstance();

    public AnimationPlayerMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setupAnim", at = @At(value = "HEAD"))
    public void setupPivot(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch, CallbackInfo ci) {
        if (master == null) {
            if (!AnimationAPI.animations.isEmpty())
                master = "jaams_weaponry";
            else
                return;
        }
        if (!master.equals("jaams_weaponry"))
            return;
        PlayerModel<T> model = (PlayerModel<T>) (Object) this;
        resetModelPose(model);
    }

    @Inject(method = "setupAnim", at = @At(value = "TAIL"))
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch, CallbackInfo ci) {
        if (ageInTicks <= 0)
            return;
        if (master == null || !master.equals("jaams_weaponry")) {
            if (!AnimationAPI.animations.isEmpty())
                AnimationAPI.animations.clear();
            return;
        }
        PlayerModel<T> model = (PlayerModel<T>) (Object) this;
        if (!(entityIn instanceof Player player))
            return;

        boolean isFirstPerson = ModAnimations.shouldRenderInFirstPerson(player);

        
        
        
        
        float attackTime = model.attackTime;
        Float currentBlend = AnimationAPI.playerSwingBlend.get(player);
        float smoothBlend = currentBlend != null ? currentBlend : 0f;
        if (attackTime > 0f) {
            AnimationAPI.playerSwingBlend.put(player, attackTime);
            if (player.swingingArm != null) {
                HumanoidArm mainArm = player.getMainArm();
                HumanoidArm attackingArm = player.swingingArm == InteractionHand.MAIN_HAND
                        ? mainArm
                        : mainArm.getOpposite();
                AnimationAPI.playerSwingArm.put(player, attackingArm);
            }
        } else if (smoothBlend > 0.01f) {
            smoothBlend *= 0.65f;
            AnimationAPI.playerSwingBlend.put(player, smoothBlend);
        } else {
            AnimationAPI.playerSwingBlend.remove(player);
            AnimationAPI.playerSwingArm.remove(player);
        }

        
        AnimationTickResult result = ModAnimations.advanceAnimation(player, ageInTicks);
        boolean hasMainAnim = result.active && result.animation != null;

        
        if (hasMainAnim) {
            PlayerAnimation animation = result.animation;
            float animationProgress = result.progress;

            
            
            boolean skipTransforms = animation.skipInFirstPerson && isFirstPerson;

            if (!skipTransforms) {
                float globalBlend = ModAnimations.getAnimationBlendFactor(player);

                for (Map.Entry<String, AnimationAPI.PlayerBone> entry : animation.bones.entrySet()) {
                    String boneName = entry.getKey();
                    AnimationAPI.PlayerBone bone = entry.getValue();
                    ModelPart modelPart = getModelPart(model, boneName);
                    if (modelPart == null)
                        continue;

                    float vanillaBlend = getVanillaBlendFactor(entityIn, model, boneName, animation,
                            limbSwingAmount);

                    if (vanillaBlend >= 1.0F && globalBlend <= 0.0F)
                        continue;

                    float headLookXRot = model.head.xRot;
                    float headLookYRot = model.head.yRot;

                    float origXRot = modelPart.xRot;
                    float origYRot = modelPart.yRot;
                    float origZRot = modelPart.zRot;
                    float origX = modelPart.x;
                    float origY = modelPart.y;
                    float origZ = modelPart.z;
                    float origXScale = modelPart.xScale;
                    float origYScale = modelPart.yScale;
                    float origZScale = modelPart.zScale;

                    Vec3 rotation = AnimationAPI.PlayerBone.interpolate(bone.rotations, animationProgress, player);
                    if (rotation != null) {
                        modelPart.xRot = (float) Math.toRadians(rotation.x);
                        modelPart.yRot = (float) Math.toRadians(rotation.y);
                        modelPart.zRot = (float) Math.toRadians(rotation.z);

                        if (!isFirstPerson && animation.headRot) {
                            modelPart.xRot += headLookXRot;
                            modelPart.yRot += headLookYRot;
                        }
                    }

                    Vec3 position = AnimationAPI.PlayerBone.interpolate(bone.positions, animationProgress, player);
                    if (position != null) {
                        resetBonePosition(modelPart, boneName);
                        modelPart.x += (float) position.x;
                        modelPart.y -= (float) position.y;
                        modelPart.z += (float) position.z;
                    }

                    Vec3 scale = AnimationAPI.PlayerBone.interpolate(bone.scales, animationProgress, player);
                    if (scale != null) {
                        modelPart.xScale = (float) scale.x;
                        modelPart.yScale = (float) scale.y;
                        modelPart.zScale = (float) scale.z;
                    }

                    float effectiveBlend = Math.max(vanillaBlend, globalBlend);
                    if (effectiveBlend > 0.0F) {
                        modelPart.xRot = origXRot * effectiveBlend + modelPart.xRot * (1.0F - effectiveBlend);
                        modelPart.yRot = origYRot * effectiveBlend + modelPart.yRot * (1.0F - effectiveBlend);
                        modelPart.zRot = origZRot * effectiveBlend + modelPart.zRot * (1.0F - effectiveBlend);
                        modelPart.x = origX * effectiveBlend + modelPart.x * (1.0F - effectiveBlend);
                        modelPart.y = origY * effectiveBlend + modelPart.y * (1.0F - effectiveBlend);
                        modelPart.z = origZ * effectiveBlend + modelPart.z * (1.0F - effectiveBlend);
                        modelPart.xScale = origXScale * effectiveBlend + modelPart.xScale * (1.0F - effectiveBlend);
                        modelPart.yScale = origYScale * effectiveBlend + modelPart.yScale * (1.0F - effectiveBlend);
                        modelPart.zScale = origZScale * effectiveBlend + modelPart.zScale * (1.0F - effectiveBlend);
                    }
                }

                
                if (isFirstPerson) {
                    if (!animation.bones.containsKey("right_arm")) {
                        model.rightArm.xRot = 0;
                        model.rightArm.yRot = 0;
                        model.rightArm.zRot = 0;
                    }
                    if (!animation.bones.containsKey("left_arm")) {
                        model.leftArm.xRot = 0;
                        model.leftArm.yRot = 0;
                        model.leftArm.zRot = 0;
                    }
                }
            }
        }

        
        
        
        Map<String, Float> combinableData = ModAnimations.getCombinableRenderData(player);
        for (Map.Entry<String, Float> combinableEntry : combinableData.entrySet()) {
            String combinableName = combinableEntry.getKey();
            float combinableProgress = combinableEntry.getValue();
            PlayerAnimation combinableAnim = ModAnimations.getAnimation(combinableName);
            if (combinableAnim == null)
                continue;

            if (combinableAnim.skipInFirstPerson && isFirstPerson)
                continue;

            for (Map.Entry<String, AnimationAPI.PlayerBone> boneEntry : combinableAnim.bones.entrySet()) {
                String boneName = boneEntry.getKey();
                AnimationAPI.PlayerBone bone = boneEntry.getValue();
                ModelPart modelPart = getModelPart(model, boneName);
                if (modelPart == null)
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

                
                Vec3 rotation = AnimationAPI.PlayerBone.interpolate(bone.rotations, combinableProgress, player);
                if (rotation != null) {
                    modelPart.xRot = (float) Math.toRadians(rotation.x);
                    modelPart.yRot = (float) Math.toRadians(rotation.y);
                    modelPart.zRot = (float) Math.toRadians(rotation.z);

                    
                    
                    if (!isFirstPerson && (combinableAnim.headRot || boneName.equals("head"))) {
                        modelPart.xRot += model.head.xRot;
                        modelPart.yRot += model.head.yRot;
                    }
                }

                
                Vec3 position = AnimationAPI.PlayerBone.interpolate(bone.positions, combinableProgress, player);
                if (position != null) {
                    resetBonePosition(modelPart, boneName);
                    modelPart.x += (float) position.x;
                    modelPart.y -= (float) position.y;
                    modelPart.z += (float) position.z;
                }

                
                Vec3 scale = AnimationAPI.PlayerBone.interpolate(bone.scales, combinableProgress, player);
                if (scale != null) {
                    modelPart.xScale = (float) scale.x;
                    modelPart.yScale = (float) scale.y;
                    modelPart.zScale = (float) scale.z;
                }

                
                float vanillaBlend = getVanillaBlendFactor(entityIn, model, boneName,
                        combinableAnim, limbSwingAmount);
                if (vanillaBlend > 0.0F) {
                    modelPart.xRot = origXRot * vanillaBlend + modelPart.xRot * (1.0F - vanillaBlend);
                    modelPart.yRot = origYRot * vanillaBlend + modelPart.yRot * (1.0F - vanillaBlend);
                    modelPart.zRot = origZRot * vanillaBlend + modelPart.zRot * (1.0F - vanillaBlend);
                    modelPart.x = origX * vanillaBlend + modelPart.x * (1.0F - vanillaBlend);
                    modelPart.y = origY * vanillaBlend + modelPart.y * (1.0F - vanillaBlend);
                    modelPart.z = origZ * vanillaBlend + modelPart.z * (1.0F - vanillaBlend);
                    modelPart.xScale = origXScale * vanillaBlend + modelPart.xScale * (1.0F - vanillaBlend);
                    modelPart.yScale = origYScale * vanillaBlend + modelPart.yScale * (1.0F - vanillaBlend);
                    modelPart.zScale = origZScale * vanillaBlend + modelPart.zScale * (1.0F - vanillaBlend);
                }
            }
        }

        
        
        
        
        
        boolean disableCrouching = false;

        if (hasMainAnim) {
            PlayerAnimation anim = result.animation;
            boolean skipFP = anim.skipInFirstPerson && isFirstPerson;
            if (!skipFP
                    && (anim.ignoreCrouching
                            || anim.bones.containsKey("torso")
                            || anim.bones.containsKey("right_leg")
                            || anim.bones.containsKey("left_leg"))) {
                disableCrouching = true;
            }
        }

        if (!disableCrouching) {
            for (Map.Entry<String, Float> entry : combinableData.entrySet()) {
                PlayerAnimation combAnim = ModAnimations.getAnimation(entry.getKey());
                if (combAnim != null && !(combAnim.skipInFirstPerson && isFirstPerson)) {
                    if (combAnim.bones.containsKey("torso")
                            || combAnim.bones.containsKey("right_leg")
                            || combAnim.bones.containsKey("left_leg")) {
                        disableCrouching = true;
                        break;
                    }
                }
            }
        }

        if (disableCrouching) {
            model.crouching = false;
        }

        
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.jacket.copyFrom(model.body);
        model.hat.copyFrom(model.head);
    }

    private float getVanillaBlendFactor(T entity, PlayerModel<T> model, String boneName,
            PlayerAnimation animation, float limbSwingAmount) {
        float result = 0.0F;

        
        
        if (!animation.ignoreSwing && (boneName.equals("right_arm") || boneName.equals("left_arm"))) {
            
            HumanoidArm currentSwingArm = null;
            if (entity instanceof Player player && player.swingingArm != null) {
                HumanoidArm mainArm = player.getMainArm();
                currentSwingArm = player.swingingArm == InteractionHand.MAIN_HAND
                        ? mainArm
                        : mainArm.getOpposite();
            } else if (entity instanceof Player player) {
                
                
                currentSwingArm = AnimationAPI.playerSwingArm.get(player);
            }

            boolean isAttackingArm = currentSwingArm == null
                    || boneName.equals(currentSwingArm == HumanoidArm.RIGHT ? "right_arm" : "left_arm");

            if (isAttackingArm) {
                float threshold = 0.3F;
                float attackBlend = model.attackTime > 0.0F
                        ? Mth.clamp(model.attackTime / threshold, 0.0F, 1.0F)
                        : 0.0F;

                
                float smoothBlend = 0.0F;
                if (entity instanceof Player player) {
                    Float sb = AnimationAPI.playerSwingBlend.get(player);
                    if (sb != null) {
                        smoothBlend = sb;
                    }
                }

                float blend = Math.max(attackBlend, smoothBlend);
                if (blend > 0.0F) {
                    result = Math.max(result, Mth.clamp(blend, 0.0F, 1.0F));
                }
            }
        }

        
        
        if (boneName.equals("right_leg") || boneName.equals("left_leg")) {
            if (limbSwingAmount > 0.01F) {
                result = Math.max(result, Mth.clamp(limbSwingAmount / 0.3F, 0.0F, 1.0F));
            }
        }

        
        
        if (!animation.ignoreItemPoses && (boneName.equals("right_arm") || boneName.equals("left_arm"))) {
            HumanoidModel.ArmPose armPose = boneName.equals("right_arm") ? model.rightArmPose
                    : model.leftArmPose;
            if (armPose != HumanoidModel.ArmPose.EMPTY && armPose != HumanoidModel.ArmPose.ITEM) {
                result = Math.max(result, 1.0F);
            } else if (armPose == HumanoidModel.ArmPose.ITEM) {
                
                
                
                
                
                
                
            }
        }

        return result;
    }

    private void resetModelPose(PlayerModel<T> model) {
        model.leftLeg.setPos(1.9F, 12.0F, 0.0F);
        model.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
        model.head.setPos(0.0F, 0.0F, 0.0F);
        
        model.rightArm.z = 0.0F;
        model.rightArm.x = -5.0F;
        model.rightArm.y = 2.0F;
        model.leftArm.z = 0.0F;
        model.leftArm.x = 5.0F;
        model.leftArm.y = 2.0F;
        
        model.rightArm.xRot = 0.0F;
        model.rightArm.yRot = 0.0F;
        model.rightArm.zRot = 0.0F;
        model.leftArm.xRot = 0.0F;
        model.leftArm.yRot = 0.0F;
        model.leftArm.zRot = 0.0F;
        model.body.xRot = 0.0F;
        model.rightLeg.z = 0.1F;
        model.leftLeg.z = 0.1F;
        model.rightLeg.y = 12.0F;
        model.leftLeg.y = 12.0F;
        model.head.y = 0.0F;
        model.head.zRot = 0f;
        model.body.y = 0.0F;
        model.body.x = 0f;
        model.body.z = 0f;
        model.body.yRot = 0;
        model.body.zRot = 0;
        model.head.xScale = ModelPart.DEFAULT_SCALE;
        model.head.yScale = ModelPart.DEFAULT_SCALE;
        model.head.zScale = ModelPart.DEFAULT_SCALE;
        model.body.xScale = ModelPart.DEFAULT_SCALE;
        model.body.yScale = ModelPart.DEFAULT_SCALE;
        model.body.zScale = ModelPart.DEFAULT_SCALE;
        model.rightArm.xScale = ModelPart.DEFAULT_SCALE;
        model.rightArm.yScale = ModelPart.DEFAULT_SCALE;
        model.rightArm.zScale = ModelPart.DEFAULT_SCALE;
        model.leftArm.xScale = ModelPart.DEFAULT_SCALE;
        model.leftArm.yScale = ModelPart.DEFAULT_SCALE;
        model.leftArm.zScale = ModelPart.DEFAULT_SCALE;
        model.rightLeg.xScale = ModelPart.DEFAULT_SCALE;
        model.rightLeg.yScale = ModelPart.DEFAULT_SCALE;
        model.rightLeg.zScale = ModelPart.DEFAULT_SCALE;
        model.leftLeg.xScale = ModelPart.DEFAULT_SCALE;
        model.leftLeg.yScale = ModelPart.DEFAULT_SCALE;
        model.leftLeg.zScale = ModelPart.DEFAULT_SCALE;
    }

    private void resetBonePosition(ModelPart modelPart, String boneName) {
        switch (boneName) {
            case "torso":
                modelPart.x = 0f;
                modelPart.y = 0f;
                modelPart.z = 0f;
                break;
            case "head":
                modelPart.setPos(0.0F, 0.0F, 0.0F);
                break;
            case "right_arm":
                modelPart.x = -5.0F;
                modelPart.y = 2.0F;
                modelPart.z = 0.0F;
                break;
            case "left_arm":
                modelPart.x = 5.0F;
                modelPart.y = 2.0F;
                modelPart.z = 0.0F;
                break;
            case "right_leg":
                modelPart.x = -1.9F;
                modelPart.y = 12.0F;
                modelPart.z = 0.1F;
                break;
            case "left_leg":
                modelPart.x = 1.9F;
                modelPart.y = 12.0F;
                modelPart.z = 0.1F;
                break;
        }
    }

    private ModelPart getModelPart(PlayerModel<T> model, String boneName) {
        switch (boneName) {
            case "torso":
                return model.body;
            case "head":
                return model.head;
            case "right_arm":
                return model.rightArm;
            case "left_arm":
                return model.leftArm;
            case "right_leg":
                return model.rightLeg;
            case "left_leg":
                return model.leftLeg;
            default:
                return null;
        }
    }
}
