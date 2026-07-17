package net.jaams.weaponry.capability.gun;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import net.jaams.weaponry.util.ModGuns;

import javax.annotation.Nullable;

public class GunInventoryCapability implements ICapabilitySerializable<CompoundTag> {
	private final ModGuns.GunType gunType;
	private final ItemStack gunStack;
	private ItemStackHandler inventoryHandler;
	private final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(this::getOrCreateHandler);

	public GunInventoryCapability(ModGuns.GunType type, ItemStack gunStack) {
		this.gunType = type;
		this.gunStack = gunStack;
	}

	private ItemStackHandler getOrCreateHandler() {
		if (inventoryHandler == null) {
			inventoryHandler = createItemHandler();
		}
		return inventoryHandler;
	}

	private ItemStackHandler createItemHandler() {
		return new ItemStackHandler(3) {
			@Override
			public int getSlotLimit(int slot) {
				return ModGuns.getSlotStackLimit(gunType, slot, gunStack);
			}

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				ModGuns.updateGunInventory(gunStack);
			}
		};
	}

	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return inventory.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return getOrCreateHandler().serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt != null) {
			getOrCreateHandler().deserializeNBT(nbt);
		}
	}

	public ItemStackHandler getItemHandler() {
		return getOrCreateHandler();
	}

	public ModGuns.GunType getGunType() {
		return gunType;
	}

	public int getAmmoCount() {
		return getItemHandler().getStackInSlot(1).getCount();
	}

	public void invalidate() {
		inventory.invalidate();
	}
}
