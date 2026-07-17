package net.jaams.weaponry.capability.gun;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import net.jaams.weaponry.util.ModGuns;

import javax.annotation.Nullable;

public class GunInventoryCapability implements ICapabilityProvider {
	private final ModGuns.GunType gunType;
	private final ItemStack gunStack;
	private final ItemStackHandler inventoryHandler;
	private final LazyOptional<ItemStackHandler> inventory;

	public GunInventoryCapability(ModGuns.GunType type, ItemStack gunStack) {
		this.gunType = type;
		this.gunStack = gunStack;
		this.inventoryHandler = createItemHandler();
		loadFromItemTag();
		this.inventory = LazyOptional.of(() -> inventoryHandler);
	}

	private void loadFromItemTag() {
		if (gunStack.isEmpty() || !gunStack.hasTag()) {
			return;
		}
		CompoundTag root = gunStack.getTag();
		if (root.contains("Inventory", CompoundTag.TAG_COMPOUND)) {
			try {
				inventoryHandler.deserializeNBT(root.getCompound("Inventory"));
			} catch (Exception ignored) {
			}
		}
	}

	private ItemStackHandler createItemHandler() {
		int slotCount = ModGuns.getGunSlotCount(gunType);
		return new ItemStackHandler(slotCount) {
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

	public ItemStackHandler getItemHandler() {
		return inventoryHandler;
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
