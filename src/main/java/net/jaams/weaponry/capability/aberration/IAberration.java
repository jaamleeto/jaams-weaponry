package net.jaams.weaponry.capability.aberration;

import net.minecraft.nbt.CompoundTag;

import net.jaams.weaponry.util.ModEnums;

public interface IAberration {
	void setEffectType(ModEnums.AberrationType effectType);

	ModEnums.AberrationType getEffectType();

	void setIntensity(double intensity);

	double getIntensity();

	void setDuration(int duration);

	int getDuration();

	CompoundTag serializeNBT();

	void deserializeNBT(CompoundTag nbt);
}
