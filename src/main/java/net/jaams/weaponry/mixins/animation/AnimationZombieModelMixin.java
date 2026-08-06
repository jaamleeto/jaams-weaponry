package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.monster.Monster;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.animation.AnimationAPI.MobAnimationState;
import net.jaams.weaponry.animation.AnimationHelper;

import java.util.Map;

@Mixin(AbstractZombieModel.class)
public abstract class AnimationZombieModelMixin {

    @Inject(method = "setupAnim", at = @At(value = "TAIL"))
    public void jaams$onSetupAnimTail(Monster entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity == null)
            return;

        
        
        
        MobAnimationState state = AnimationAPI.mob_active_animations.get(entity.getId());
        if (state == null || state.blendOutTicks > 0)
            return;

        PlayerAnimation animation = AnimationHelper.getActiveAnimation(entity);
        if (animation == null)
            return;

        float progress = AnimationHelper.getAnimationProgress(entity);
        if (progress < 0)
            return;

        AbstractZombieModel<?> model = (AbstractZombieModel<?>) (Object) this;

        for (Map.Entry<String, AnimationAPI.PlayerBone> entry : animation.bones.entrySet()) {
            String boneName = entry.getKey();
            if (!boneName.equals("right_arm") && !boneName.equals("left_arm"))
                continue;

            AnimationAPI.PlayerBone bone = entry.getValue();
            ModelPart modelPart = boneName.equals("right_arm") ? model.rightArm : model.leftArm;

            if (bone.rotations != null && !bone.rotations.isEmpty()) {
                Vec3 rotation = AnimationAPI.PlayerBone.interpolate(bone.rotations, progress, entity);
                if (rotation != null) {
                    modelPart.xRot = (float) Math.toRadians(rotation.x);
                    modelPart.yRot = (float) Math.toRadians(rotation.y);
                    modelPart.zRot = (float) Math.toRadians(rotation.z);
                }
            }

            if (bone.positions != null && !bone.positions.isEmpty()) {
                Vec3 position = AnimationAPI.PlayerBone.interpolate(bone.positions, progress, entity);
                if (position != null) {
                    modelPart.x = (float) position.x;
                    modelPart.y = (float) position.y;
                    modelPart.z = (float) position.z;
                }
            }

            if (bone.scales != null && !bone.scales.isEmpty()) {
                Vec3 scale = AnimationAPI.PlayerBone.interpolate(bone.scales, progress, entity);
                if (scale != null) {
                    modelPart.xScale = (float) scale.x;
                    modelPart.yScale = (float) scale.y;
                    modelPart.zScale = (float) scale.z;
                }
            }
        }
    }
}
