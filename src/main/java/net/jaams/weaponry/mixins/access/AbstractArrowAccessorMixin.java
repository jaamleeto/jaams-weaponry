package net.jaams.weaponry.mixins.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.projectile.AbstractArrow;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessorMixin {
	@Accessor("inGround")
	boolean isInGround();

	@Invoker("setPierceLevel")
	void invokeSetPierceLevel(byte level);
}
