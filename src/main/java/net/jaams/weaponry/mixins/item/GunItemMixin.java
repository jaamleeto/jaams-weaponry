package net.jaams.weaponry.mixins.item;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

import net.jaams.weaponry.util.ModGuns;

@Mixin(ItemStack.class)
public abstract class GunItemMixin {
	@Inject(method = "overrideStackedOnOther", at = @At("HEAD"), cancellable = true)
	private void jaam$overrideStackedOnOther(Slot slot, ClickAction action, Player player, CallbackInfoReturnable<Boolean> cir) {
		ItemStack gunStack = (ItemStack) (Object) this;
		if (!ModGuns.isGun(gunStack))
			return;
		boolean result = ModGuns.overrideStackedOnOther(gunStack, slot, action, player);
		cir.setReturnValue(result);
	}

	@Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
	private void jaam$overrideOtherStackedOnMe(ItemStack cursorStack, Slot slot, ClickAction action, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
		ItemStack gunStack = (ItemStack) (Object) this;
		if (!ModGuns.isGun(gunStack))
			return;
		boolean result = ModGuns.overrideOtherStackedOnMe(gunStack, cursorStack, slot, action, player, access);
		cir.setReturnValue(result);
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void jaam$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		ItemStack stack = player.getItemInHand(hand);
		if (!ModGuns.isGun(stack))
			return;
		InteractionResultHolder<ItemStack> result = ModGuns.useGun(stack, level, player, hand);
		cir.setReturnValue(result);
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void jaam$getUseDuration(CallbackInfoReturnable<Integer> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (ModGuns.isGun(stack)) {
			cir.setReturnValue(72000);
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void jaam$getUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (ModGuns.isGun(stack)) {
			cir.setReturnValue(UseAnim.CUSTOM);
		}
	}
}
