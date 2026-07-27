package net.jaams.weaponry.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.monster.Vindicator;

import net.jaams.weaponry.animation.AnimationHelper;

@Mixin(targets = "net.minecraft.client.renderer.entity.VindicatorRenderer$1")
public abstract class AnimationVindicatorRendererMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Vindicator;isAggressive()Z"))
    private boolean jaams$redirectIsAggressive(Vindicator entity) {
        return entity.isAggressive() || AnimationHelper.hasActiveAnimation(entity);
    }
}
