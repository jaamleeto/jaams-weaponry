package net.jaams.weaponry.mixins.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

import net.jaams.weaponry.util.ModAnimations;
import net.jaams.weaponry.util.ModUtils;

import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

/**
 * When Epic Fight's "vanilla model" config is off, {@code AbstractClientPlayerPatch.overrideRender()}
 * returns {@code true} even outside of combat, causing Epic Fight's patched renderer to cancel the
 * vanilla player render.  This prevents the animation API's custom first-person body from activating.
 *
 * <p>This mixin intercepts {@code overrideRender()} and returns {@code false} when the local player
 * is in first-person camera, is not in battle mode, and has an active first-person animation.  This
 * lets the vanilla {@code PlayerRenderer} handle the local player so the animation API's
 * first-person system can animate it — identical to what happens in battle mode when Epic Fight's
 * animated first-person config is disabled.
 */
@Mixin(value = AbstractClientPlayerPatch.class, remap = false)
public abstract class EpicFightAbstractClientPlayerPatchMixin {

    @Inject(method = "overrideRender", at = @At("HEAD"), cancellable = true)
    private void jaams$allowAnimatedFirstPerson(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = ((EntityPatch) (Object) this).getOriginal();
        if (entity instanceof LocalPlayer localPlayer) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.options.getCameraType().isFirstPerson()
                    && !ModUtils.isEntityInBattleMode(localPlayer)
                    && ModAnimations.isFirstPersonAnimation(localPlayer)) {
                cir.setReturnValue(false);
            }
        }
    }
}
