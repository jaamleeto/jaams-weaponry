package net.jaams.weaponry.mixins.mob;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.RandomSource;

import net.jaams.weaponry.loader.EquipmentModifierLoader;

@Mixin(WitherSkeleton.class)
public abstract class WitherSkeletonMixin {
    @Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), cancellable = true)
    private void modifyDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (EquipmentModifierLoader.INSTANCE.applySpawnEquipment(self, random)) {
            ci.cancel();
        }
    }
}
