package net.jaams.weaponry.capability.gun;

import net.jaams.weaponry.util.ModGuns;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;

public class GunItemHandler extends ComponentItemHandler {
	private final ModGuns.GunType gunType;
	private final ItemStack gunStack;

	public GunItemHandler(ModGuns.GunType type, ItemStack gunStack) {
		super(gunStack, DataComponents.CONTAINER, ModGuns.getGunSlotCount(type));
		this.gunType = type;
		this.gunStack = gunStack;
	}

	@Override
	public int getSlotLimit(int slot) {
		return ModGuns.getSlotStackLimit(gunType, slot, gunStack);
	}

	@Override
	protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
		super.onContentsChanged(slot, oldStack, newStack);
		ModGuns.updateGunInventory(gunStack);
	}

	public ModGuns.GunType getGunType() {
		return gunType;
	}

	public int getAmmoCount() {
		return getStackInSlot(1).getCount();
	}
}
