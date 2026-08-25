package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.jaams.weaponry.animation.MobAnimationApplier;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Drives JAAMS animations on GeckoLib entity models.
 *
 * <p>GeckoLib applies its own bone transforms inside {@code GeoModel.handleAnimations}, which the
 * renderer invokes from {@code actuallyRender} (after {@code preRender}, before the bones are drawn).
 * By injecting immediately after that call, the JAAMS transforms become the final pose applied to
 * the {@link GeoModel}'s bones for this frame.</p>
 *
 * <p>{@code isReRender} passes (glow/translucent layers) are skipped so the animation does not
 * advance/progress twice per frame.</p>
 */
@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererAnimationMixin {
    @Shadow(remap = false)
    protected GeoModel<?> model;

    @Inject(method = "actuallyRender",
            remap = false,
            require = 0,
            at = @At(value = "INVOKE",
                    target = "Lsoftware/bernie/geckolib/model/GeoModel;handleAnimations(Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;JLsoftware/bernie/geckolib/core/animation/AnimationState;)V",
                    shift = At.Shift.AFTER))
    private void jaams$applyAfterGeckolib(PoseStack poseStack, Entity animatable, BakedGeoModel bakedModel,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
            float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha,
            CallbackInfo ci) {
        if (isReRender) {
            return;
        }
        if (!(animatable instanceof LivingEntity living) || animatable instanceof Player) {
            return;
        }
        MobAnimationApplier.applyActiveAnimation(living, (Object) this.model, partialTick);
    }
}
