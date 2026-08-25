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
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

/**
 * Same as {@link GeoEntityRendererAnimationMixin} but for GeckoLib "replaced entity" renderers
 * (vanilla mobs swapped to a GeckoLib model). The real mob is exposed via the {@code currentEntity}
 * field rather than the {@code animatable} parameter (which is the replacement {@link GeoAnimatable}).
 */
@Mixin(GeoReplacedEntityRenderer.class)
public abstract class GeoReplacedEntityRendererAnimationMixin {
    @Shadow(remap = false)
    protected GeoModel<?> model;

    @Shadow(remap = false)
    protected Entity currentEntity;

    @Inject(method = "actuallyRender",
            remap = false,
            require = 0,
            at = @At(value = "INVOKE",
                    target = "Lsoftware/bernie/geckolib/model/GeoModel;handleAnimations(Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;JLsoftware/bernie/geckolib/core/animation/AnimationState;)V",
                    shift = At.Shift.AFTER))
    private void jaams$applyAfterGeckolib(PoseStack poseStack, GeoAnimatable animatable, BakedGeoModel bakedModel,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
            float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha,
            CallbackInfo ci) {
        if (isReRender) {
            return;
        }
        Entity entity = this.currentEntity;
        if (!(entity instanceof LivingEntity living) || entity instanceof Player) {
            return;
        }
        MobAnimationApplier.applyActiveAnimation(living, (Object) this.model, partialTick);
    }
}
