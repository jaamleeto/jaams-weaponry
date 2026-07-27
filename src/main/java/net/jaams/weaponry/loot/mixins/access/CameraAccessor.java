package net.jaams.weaponry.mixins.access;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Camera;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor
    public void setDetached(boolean value);

    @Accessor
    public void setXRot(float xRot);

    @Accessor
    public void setYRot(float yRot);
}
