package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.Minecraft;

import net.jaams.weaponry.animation.AnimationAPI;


@Mixin(EntityRenderer.class)
public abstract class AnimationEntityCullingMixin<T extends Entity> {
    private String master = null;
    private final Minecraft mc = Minecraft.getInstance();

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void jaams$preventCulling(T entity, Frustum frustum, double x, double y, double z,
            CallbackInfoReturnable<Boolean> cir) {
        if (master == null) {
            if (!AnimationAPI.animations.isEmpty())
                master = "jaams_weaponry";
            else
                return;
        }
        if (!master.equals("jaams_weaponry"))
            return;
        if (entity instanceof Player plr && plr != mc.player
                && AnimationAPI.active_animations.get(plr) != null) {
            cir.setReturnValue(true);
        }
    }
}
