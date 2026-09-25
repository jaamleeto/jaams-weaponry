package net.jaams.weaponry.capability.gun;

import net.jaams.weaponry.util.ModGuns;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;

public class GunItemHandler extends ComponentItemHandler {
	private final ModGuns.GunType gunType;
	private final ItemStack gunStack;
	private final int[] authoritativeSlotLimits;
	private final boolean enforceSlotRules;

	public GunItemHandler(ModGuns.GunType type, ItemStack gunStack) {
		this(type, gunStack, null, true);
	}

	/**
	 * Client menu mirror constructor. Limits come from the server's menu payload;
	 * compatibility rules are still enforced by the server menu.
	 */
	public GunItemHandler(ModGuns.GunType type, ItemStack gunStack, int[] slotLimits,
			boolean enforceSlotRules) {
		super(gunStack, DataComponents.CONTAINER, ModGuns.getGunSlotCount(type));
		this.gunType = type;
		this.gunStack = gunStack;
		this.authoritativeSlotLimits = slotLimits == null ? null : slotLimits.clone();
		this.enforceSlotRules = enforceSlotRules;
	}

	@Override
	public int getSlotLimit(int slot) {
		if (authoritativeSlotLimits != null && slot >= 0 && slot < authoritativeSlotLimits.length) {
			return Math.max(0, authoritativeSlotLimits[slot]);
		}
		return ModGuns.getSlotStackLimit(gunType, slot, gunStack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		// Empty stacks must remain valid: the vanilla menu sync writes air into
		// empty slots through setStackInSlot, which throws on invalid stacks.
		if (stack.isEmpty())
			return true;
		if (!super.isItemValid(slot, stack))
			return false;
		return !enforceSlotRules || ModGuns.canPlaceInGunSlot(gunStack, stack, gunType, slot);
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
