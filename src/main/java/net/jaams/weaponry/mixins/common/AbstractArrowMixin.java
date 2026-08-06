package net.jaams.weaponry.mixins.common;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;

import net.jaams.weaponry.entity.SharpStoneProjectileEntity;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {
	@Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setArrowCount(I)V"), cancellable = true)
	private void jaam$cancelSetArrowCountForSharpStoneProjectile(EntityHitResult result, CallbackInfo ci) {
		AbstractArrow arrow = (AbstractArrow) (Object) this;
		if (result.getEntity() instanceof LivingEntity && arrow instanceof SharpStoneProjectileEntity) {
			ci.cancel();
		}
	}
}
