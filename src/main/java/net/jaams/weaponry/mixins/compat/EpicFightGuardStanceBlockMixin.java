package net.jaams.weaponry.mixins.compat;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.util.ModTraits;

/**
 * Forces a blocking pose on the player model whenever a weapon that makes the
 * player use it is held (right-click held): guard-stance, parry-guard and the
 * charge-release/charge-finishing traits (whirling strike, slash/piercing
 * assault, shock impact, quick swap, throwable).
 *
 * <p>Epic Fight only registers {@code BLOCK_SHIELD -> BIPED_BLOCK} into the
 * composite living motion map for items whose capability reports it through
 * {@code CapabilityItem#getLivingMotionModifier} (normally shields). Weapons
 * without such a capability leave {@code ClientAnimator#compositeLivingAnimations}
 * empty for {@code BLOCK_SHIELD}, so {@code ClientAnimator#tick} never plays the
 * block pose. Injecting at the head of {@code ClientAnimator#tick} (which runs
 * every client tick and reads {@code LivingEntityPatch#currentCompositeMotion})
 * both registers the missing mapping and drives the block pose.
 *
 * <p>Guard-stance weapons (swords) and the charge-release traits use the Epic
 * Fight guard-skill pose ({@code Animations.SWORD_GUARD}, the same one
 * registered for the Guard skill via {@code LivingMotions.BLOCK}), while
 * parry-guard weapons (gauntlet) keep the shield block pose
 * ({@code Animations.BIPED_BLOCK}). Both go through the {@code BLOCK_SHIELD}
 * motion key, which {@code updateMotion} already assigns for
 * {@code UseAnim.BLOCK} items, keeping the two branches of
 * {@code ClientAnimator#tick} consistent.
 */
@Mixin(value = ClientAnimator.class, remap = false)
public abstract class EpicFightGuardStanceBlockMixin {

    @Shadow
    public abstract LivingEntityPatch<?> getOwner();

    @Shadow
    @Final
    private Map<LivingMotion, AssetAccessor<? extends StaticAnimation>> compositeLivingAnimations;

    @Unique
    private AssetAccessor<? extends StaticAnimation> jaams$guardBlockAnimation(ItemStack useItem) {
        if (ModTraits.isParryGuardItem(useItem)) {
            return Animations.BIPED_BLOCK;
        }
        return Animations.SWORD_GUARD;
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void jaams$guardStanceShieldBlockPose(CallbackInfo ci) {
        LivingEntity original = this.getOwner().getOriginal();
        if (original instanceof Player player && player.isUsingItem()) {
            ItemStack useItem = player.getUseItem();
            if (ModTraits.isGuardPoseItem(useItem)) {
                this.compositeLivingAnimations.put(LivingMotions.BLOCK_SHIELD, this.jaams$guardBlockAnimation(useItem));
                this.getOwner().currentCompositeMotion = LivingMotions.BLOCK_SHIELD;
            }
        }
    }
}