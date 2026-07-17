package net.jaams.weaponry.capability.aberration;

import net.minecraft.nbt.CompoundTag;

import net.jaams.weaponry.util.ModEnums;

public class AberrationImpl implements IAberration {
	private ModEnums.AberrationType effectType = ModEnums.AberrationType.NONE;
	private double intensity = 0.0;
	private int duration = 0;

	@Override
	public void setEffectType(ModEnums.AberrationType effectType) {
		this.effectType = effectType != null ? effectType : ModEnums.AberrationType.NONE;
	}

	@Override
	public ModEnums.AberrationType getEffectType() {
		return this.effectType;
	}

	@Override
	public void setIntensity(double intensity) {
		this.intensity = Math.max(0.0, intensity);
	}

	@Override
	public double getIntensity() {
		return this.intensity;
	}

	@Override
	public void setDuration(int duration) {
		this.duration = Math.max(0, duration);
	}

	@Override
	public int getDuration() {
		return this.duration;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("effectType", this.effectType.name());
		nbt.putDouble("intensity", this.intensity);
		nbt.putInt("duration", this.duration);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.effectType = nbt.contains("effectType") ? ModEnums.AberrationType.valueOf(nbt.getString("effectType")) : ModEnums.AberrationType.NONE;
		this.intensity = nbt.contains("intensity") ? nbt.getDouble("intensity") : 0.0;
		this.duration = nbt.contains("duration") ? nbt.getInt("duration") : 0;
	}
}
