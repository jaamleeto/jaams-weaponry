package net.jaams.weaponry.mixins.compat;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;

import net.jaams.weaponry.loader.EquipmentModifierLoader;

import tallestegg.guardvillagers.entities.Guard;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Guard.class)
public abstract class GuardVillagersSpawnMixin {

	@Inject(method = "populateDefaultEquipmentSlots", at = @At("HEAD"), cancellable = true)
	private void modifyDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
		Mob self = (Mob) (Object) this;
		if (EquipmentModifierLoader.INSTANCE.applySpawnEquipment(self, random)) {
			ci.cancel();
		}
	}
}
