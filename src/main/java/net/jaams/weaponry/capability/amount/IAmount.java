package net.jaams.weaponry.capability.amount;

import net.minecraft.nbt.CompoundTag;

public interface IAmount {
	void setDamage(float damage);

	float getDamage();

	CompoundTag serializeNBT();

	void deserializeNBT(CompoundTag nbt);
}
