package net.jaams.weaponry.mixins.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.util.ModTraits;

/**
 * Keeps {@code LivingMotions.BLOCK_SHIELD} as the composite motion during the
 * isEnd branch of {@code ClientAnimator#tick}. {@code updateMotion} recomputes
 * {@code currentCompositeMotion} for the held item on every isEnd tick, and for
 * use items whose {@code UseAnim} is not {@code BLOCK} (e.g. whirling strike
 * with {@code UseAnim.NONE}) it falls back to the base living motion, silently
 * overriding the value set by {@link EpicFightGuardStanceBlockMixin}. Re-asserting
 * the guard motion here keeps the pose stable in both branches of
 * {@code ClientAnimator#tick}.
 */
@Mixin(value = AbstractClientPlayerPatch.class, remap = false)
public abstract class EpicFightGuardMotionMixin {

    @Inject(method = "updateMotion", at = @At("TAIL"), remap = false)
    private void jaams$guardPoseDuringUse(boolean tick, CallbackInfo ci) {
        LivingEntityPatch<?> patch = (LivingEntityPatch<?>) (Object) this;
        if (!patch.getEntityState().updateLivingMotion()) {
            return;
        }
        LivingEntity original = patch.getOriginal();
        if (original instanceof Player player && player.isUsingItem()) {
            ItemStack useItem = player.getUseItem();
            if (ModTraits.isGuardPoseItem(useItem)) {
                patch.currentCompositeMotion = LivingMotions.BLOCK_SHIELD;
            }
        }
    }
}
