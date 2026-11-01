package net.jaams.weaponry.mixins.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.handler.trait.GuardStanceHandler;
import net.jaams.weaponry.handler.trait.ParryGuardHandler;
import net.jaams.weaponry.util.ModTraits;

import tallestegg.guardvillagers.entities.Guard;
import tallestegg.guardvillagers.entities.ai.goals.RaiseShieldGoal;

/**
 * Guard Villagers' {@code RaiseShieldGoal} only raises an off-hand item that can
 * perform {@code SHIELD_BLOCK}. Guard stance and parry guard items block through
 * the generic use-item chain instead, which works for any living entity once it
 * is "using" the item, so make the goal treat those items exactly like a shield:
 * raise whatever hand holds one, respecting the same target, distance and shield
 * cooldown rules as the vanilla shield behaviour.
 */
@Mixin(value = RaiseShieldGoal.class)
public abstract class GuardVillagersRaiseShieldGoalMixin {

	@Shadow(remap = false)
	protected abstract boolean raiseShield();

	@Inject(method = "canUse", at = @At("TAIL"), cancellable = true)
	private void jaams$guardStanceCanUse(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ())
			return;
		Guard guard = ((RaiseShieldGoal) (Object) this).guard;
		if (guard == null || guard.shieldCoolDown != 0)
			return;
		ItemStack mainHand = guard.getMainHandItem();
		ItemStack offHand = guard.getOffhandItem();
		boolean mainHandStance = !mainHand.isEmpty()
				&& (ModTraits.isGuardStanceItem(mainHand) || ModTraits.isParryGuardItem(mainHand));
		boolean offHandStance = !offHand.isEmpty()
				&& (ModTraits.isGuardStanceItem(offHand) || ModTraits.isParryGuardItem(offHand));
		if (!mainHandStance && !offHandStance)
			return;
		// A charged crossbow needs the main hand free to reload and shoot.
		if (mainHandStance && mainHand.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(mainHand))
			return;
		// Same target, distance and config conditions as a raised shield.
		if (!raiseShield())
			return;
		cir.setReturnValue(true);
	}

	@Inject(method = "start", at = @At("TAIL"))
	private void jaams$guardStanceStart(CallbackInfo ci) {
		Guard guard = ((RaiseShieldGoal) (Object) this).guard;
		if (guard == null || guard.isUsingItem() || guard.shieldCoolDown != 0)
			return;
		ItemStack mainHand = guard.getMainHandItem();
		ItemStack offHand = guard.getOffhandItem();
		if (!offHand.isEmpty()
				&& (ModTraits.isGuardStanceItem(offHand) || ModTraits.isParryGuardItem(offHand))) {
			guard.startUsingItem(InteractionHand.OFF_HAND);
		} else if (!mainHand.isEmpty()
				&& (ModTraits.isGuardStanceItem(mainHand) || ModTraits.isParryGuardItem(mainHand))) {
			guard.startUsingItem(InteractionHand.MAIN_HAND);
		}
	}

	/**
	 * Vanilla goal stop goes through {@code stopUsingItem()}, which fires no
	 * use-item event, so the release logic (guard stance sweep / parry guard
	 * stop) is applied here while the guard is still holding the item up.
	 */
	@Inject(method = "stop", at = @At("HEAD"))
	private void jaams$guardStanceStop(CallbackInfo ci) {
		Guard guard = ((RaiseShieldGoal) (Object) this).guard;
		if (guard == null || guard.level().isClientSide || !guard.isUsingItem())
			return;
		ItemStack useItem = guard.getUseItem();
		if (useItem.isEmpty())
			return;
		if (ModTraits.isGuardStanceItem(useItem)) {
			GuardStanceHandler.onGuardStanceStopped(guard, useItem);
		} else if (ModTraits.isParryGuardItem(useItem)) {
			ParryGuardHandler.onParryGuardStopped(guard, useItem);
		}
	}
}
