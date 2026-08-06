package net.jaams.weaponry.mixins.item;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
	@Unique
	private boolean shouldUseCustomThrow() {
		return ItemFeaturesConfig.TRIDENT_USE_CUSTOM_THROW.get();
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void jaam$onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (shouldUseCustomThrow()) {
			cir.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(hand)));
		}
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
	private void jaam$onReleaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
		if (shouldUseCustomThrow() && entity instanceof Player) {
			ci.cancel();
		}
	}
}
