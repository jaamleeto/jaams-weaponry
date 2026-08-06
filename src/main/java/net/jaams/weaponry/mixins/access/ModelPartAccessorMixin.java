package net.jaams.weaponry.mixins.access;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.model.geom.ModelPart;

import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccessorMixin {
    @Accessor("children")
    Map<String, ModelPart> getChildren();
}
