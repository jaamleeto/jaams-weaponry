package net.jaams.weaponry.mixins.access;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {
	@Accessor("model")
	EntityModel<?> getModel();
}
