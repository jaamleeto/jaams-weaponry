package net.jaams.weaponry.capability.amount;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

public class AmountProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
	public static final Capability<IAmount> AMOUNT = CapabilityManager.get(new CapabilityToken<>() {
	});
	private final IAmount impl = new AmountImpl();
	private final LazyOptional<IAmount> optional = LazyOptional.of(() -> impl);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == AMOUNT ? optional.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return impl.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		impl.deserializeNBT(nbt);
	}
}
