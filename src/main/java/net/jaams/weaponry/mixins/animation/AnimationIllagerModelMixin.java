package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.monster.AbstractIllager;

import net.minecraft.client.model.IllagerModel;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.AnimationAPI.MobAnimationState;
import net.jaams.weaponry.animation.AnimationHelper;

@Mixin(value = IllagerModel.class, priority = 3000)
public abstract class AnimationIllagerModelMixin<T extends AbstractIllager> {

    @ModifyVariable(method = "setupAnim(Lnet/minecraft/world/entity/monster/AbstractIllager;FFFFF)V", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private boolean jaams$modifyUseCrossedArms(boolean useCrossedArms, T entity, float limbSwing,
            float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        
        if (!AnimationHelper.getCurrentAnimationName(entity).isEmpty()) {
            return false;
        }
        
        
        MobAnimationState state = AnimationAPI.mob_active_animations.get(entity.getId());
        if (state != null && state.blendOutTicks > 0) {
            return false;
        }
        return useCrossedArms;
    }
}
