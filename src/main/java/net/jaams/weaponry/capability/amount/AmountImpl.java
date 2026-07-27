package net.jaams.weaponry.capability.amount;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class AmountImpl implements IAmount, INBTSerializable<CompoundTag> {
	private float damage = 0.0F;

	@Override
	public void setDamage(float damage) {
		this.damage = Math.max(0.0F, damage);
	}

	@Override
	public float getDamage() {
		return this.damage;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider registries) {
		CompoundTag nbt = new CompoundTag();
		nbt.putFloat("damage", this.damage);
		return nbt;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
		this.damage = nbt.contains("damage") ? nbt.getFloat("damage") : 0.0F;
	}
}
