package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;

import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.util.ModAnimations;


import java.util.List;
import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(LivingEntityRenderer.class)
public abstract class AnimationLivingEntityRendererMixin {
    private String master = null;
    private final Minecraft mc = Minecraft.getInstance();

    @Shadow
    @Final
    protected List<Object> layers;

    private List<Object> jaams_savedLayers = null;
    private int jaams_filterDepth = 0;

    private void jaams_restoreLayers() {
        if (jaams_savedLayers != null) {
            this.layers.clear();
            this.layers.addAll(jaams_savedLayers);
            jaams_savedLayers = null;
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void jaams_beforeRender(LivingEntity entity, float f, float g, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (master == null) {
            if (!AnimationAPI.animations.isEmpty())
                master = "jaams_weaponry";
            else
                return;
        }
        if (!master.equals("jaams_weaponry"))
            return;

        boolean needsFiltering = false;
        Player player = null;

        if (entity instanceof Player p && mc.options.getCameraType().isFirstPerson()) {
            player = p;
            if (ModAnimations.isLocalPlayerInFirstPerson(player)) {
                if (ModAnimations.isFirstPersonAnimation(player)) {
                    needsFiltering = true;
                } else if (ModAnimations.hasAnimationNullRender(player)) {
                    needsFiltering = true;
                }
            }
        }

        if (!needsFiltering) {
            if (jaams_filterDepth > 0) {
                return;
            }
            jaams_restoreLayers();
            return;
        }

        if (jaams_filterDepth > 0) {
            jaams_filterDepth++;
            return;
        }

        if (ModAnimations.isFirstPersonAnimation(player)) {
            ModAnimations.setAnimationNullRender(player, 4);
            jaams_savedLayers = new ArrayList<>(this.layers);
            jaams_filterDepth = 1;
            this.layers.clear();
            this.layers.addAll(jaams_savedLayers.stream()
                    .filter(layer -> layer instanceof PlayerItemInHandLayer)
                    .toList());
        } else if (ModAnimations.hasAnimationNullRender(player)) {
            int val = ModAnimations.getAnimationNullRender(player);
            if (val <= 0) {
                ModAnimations.removeAnimationNullRender(player);
                jaams_restoreLayers();
            } else {
                ModAnimations.setAnimationNullRender(player, val - 1);
                jaams_savedLayers = new ArrayList<>(this.layers);
                jaams_filterDepth = 1;
                this.layers.clear();
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    private void jaams_afterRender(LivingEntity entity, float f, float g, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (jaams_filterDepth > 0) {
            jaams_filterDepth--;
            if (jaams_filterDepth == 0) {
                jaams_restoreLayers();
            }
        } else {
            jaams_restoreLayers();
        }
    }
}
