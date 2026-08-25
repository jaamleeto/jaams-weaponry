package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.MobAnimationApplier;
import net.jaams.weaponry.compat.GeckoLibCompat;

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

        Object modelObj = this.model;
        // GeckoLib re-applies its own bone transforms during render, so applying here (after
        // setupAnim) would be overwritten. The GeckoLib render hook applies it instead.
        if (GeckoLibCompat.isGeoModel(modelObj))
            return;

        MobAnimationApplier.applyActiveAnimation(entity, modelObj, partialTick);
    }


}
