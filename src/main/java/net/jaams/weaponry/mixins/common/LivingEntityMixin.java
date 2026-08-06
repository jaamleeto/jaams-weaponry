package net.jaams.weaponry.mixins.common;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

import net.jaams.weaponry.util.ModUtils;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
	private void jaam$injectIsBlocking(CallbackInfoReturnable<Boolean> cir) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Player player && player.isUsingItem()) {
			ItemStack useItem = player.getUseItem();
			if (useItem.getUseAnimation() == UseAnim.BLOCK && ModUtils.isItemWeapon(useItem)) {
				cir.setReturnValue(true);
			}
		}
	}
}
