package net.jaams.weaponry.mixins.common;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;

import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.dispenser.SmokeBombDispenser;
import net.jaams.weaponry.dispenser.ProjectileDispenser;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class DispenseItemBehaviorMixin {
	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private void jaam$onExecuteDefaultDispense(BlockSource blockSource, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		if (ModCompats.isSmokeBomb(stack)) {
			ItemStack result = SmokeBombDispenser.dispense(blockSource, stack);
			cir.setReturnValue(result);
		}
		if (ProjectileDispenser.canDispense(stack)) {
			ItemStack result = ProjectileDispenser.dispense(blockSource, stack);
			cir.setReturnValue(result);
		}
	}
}
