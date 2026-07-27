package net.jaams.weaponry.component.gui;

import net.jaams.weaponry.capability.CapHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.Container;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.configuration.client.GunSystemClientConfig;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public abstract class BaseGunGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public final int x, y, z;
	private final ContainerLevelAccess access;
	protected IItemHandler internal;
	protected final Map<Integer, Slot> customSlots = new HashMap<>();
	private final Map<Integer, ItemStack> lastKnownStacks = new HashMap<>();
	protected boolean bound = false;
	protected Supplier<Boolean> boundItemMatcher = null;
	protected Entity boundEntity = null;
	protected BlockEntity boundBlockEntity = null;
	protected ItemStack boundItemStack = ItemStack.EMPTY;
	protected byte boundHand;
	protected boolean isClosing = false;

	public BaseGunGUIMenu(MenuType<?> menuType, int id, Inventory inv, FriendlyByteBuf extraData) {
		super(menuType, id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(getSlotCount());
		BlockPos pos = extraData.readBlockPos();
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.access = ContainerLevelAccess.create(world, pos);
		readBindingData(extraData);
		setupGunSlots();
		addPlayerInventorySlots();
		playClientSound(getOpenSound());
	}

	protected abstract int getSlotCount();

	protected abstract void setupGunSlots();

	protected abstract ResourceLocation getOpenSound();

	protected abstract ResourceLocation getCloseSound();

	protected abstract void sendSlotPacket(int slotid);

	private void readBindingData(FriendlyByteBuf extraData) {
		if (extraData == null)
			return;
		if (extraData.readableBytes() == 1) {
			this.boundHand = extraData.readByte();
			ItemStack itemstack = boundHand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem();
			this.boundItemStack = itemstack;
			this.boundItemMatcher = () -> itemstack == (boundHand == 0 ? this.entity.getMainHandItem() : this.entity.getOffhandItem());
			CapHelper.itemHandler(itemstack).ifPresent(capability -> {
				this.internal = capability;
				this.bound = true;
			});
		} else if (extraData.readableBytes() > 1) {
			extraData.readByte();
			boundEntity = world.getEntity(extraData.readVarInt());
			if (boundEntity != null) {
				CapHelper.itemHandler(boundEntity).ifPresent(capability -> {
					this.internal = capability;
					this.bound = true;
				});
			}
		} else {
			boundBlockEntity = this.world.getBlockEntity(new BlockPos(x, y, z));
			if (boundBlockEntity != null) {
				CapHelper.itemHandler(boundBlockEntity).ifPresent(capability -> {
					this.internal = capability;
					this.bound = true;
				});
			}
		}
	}

	protected void addPlayerInventorySlots() {
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(entity.getInventory(), sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(entity.getInventory(), si, 8 + si * 18, 142));
	}

	protected Slot createGunSlot(IItemHandler handler, int index, int x, int y) {
		return new SlotItemHandler(handler, index, x, y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				if (!boundItemStack.isEmpty() && ItemStack.matches(stack, boundItemStack)) {
					return false;
				}
				return super.mayPlace(stack);
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
			}

			@Override
			public void setChanged() {
				super.setChanged();
				slotChanged(index);
			}
		};
	}

	protected void slotChanged(int slotid) {
		if (world.isClientSide()) {
			ItemStack current = internal.getStackInSlot(slotid);
			if (!current.equals(lastKnownStacks.getOrDefault(slotid, ItemStack.EMPTY))) {
				sendSlotPacket(slotid);
				lastKnownStacks.put(slotid, current.copy());
			}
		}
	}

	@Override
	public boolean stillValid(Player player) {
		if (bound) {
			if (boundItemMatcher != null) {
				return boundItemMatcher.get();
			} else if (boundBlockEntity != null) {
				return AbstractContainerMenu.stillValid(access, player, boundBlockEntity.getBlockState().getBlock());
			} else if (boundEntity != null) {
				return boundEntity.isAlive();
			}
		}
		return false;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		Slot slot = slots.get(index);
		if (slot == null || !slot.hasItem())
			return ItemStack.EMPTY;
		ItemStack stackInSlot = slot.getItem();
		if (!boundItemStack.isEmpty() && ItemStack.matches(stackInSlot, boundItemStack)) {
			return ItemStack.EMPTY;
		}
		if (!isClosing && !boundItemStack.isEmpty() && ItemStack.matches(stackInSlot, boundItemStack)) {
			isClosing = true;
			playerIn.closeContainer();
			return ItemStack.EMPTY;
		}
		ItemStack original = stackInSlot.copy();
		int customCount = getSlotCount();
		if (index < customCount) {
			if (!moveItemStackTo(stackInSlot, customCount, slots.size(), true))
				return ItemStack.EMPTY;
			slot.onQuickCraft(stackInSlot, original);
		} else {
			if (!moveItemStackTo(stackInSlot, 0, customCount, false)) {
				if (index < customCount + 27) {
					if (!moveItemStackTo(stackInSlot, customCount + 27, slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!moveItemStackTo(stackInSlot, customCount, customCount + 27, false))
						return ItemStack.EMPTY;
				}
				return ItemStack.EMPTY;
			}
		}
		if (stackInSlot.getCount() == 0)
			slot.set(ItemStack.EMPTY);
		else
			slot.setChanged();
		if (stackInSlot.getCount() == original.getCount())
			return ItemStack.EMPTY;
		slot.onTake(playerIn, stackInSlot);
		return original;
	}

	@Override
	public void slotsChanged(Container inventory) {
		super.slotsChanged(inventory);
		if (bound && boundItemMatcher != null) {
			if (!(boundHand == 0 ? entity.getMainHandItem() == boundItemStack : entity.getOffhandItem() == boundItemStack)) {
				isClosing = true;
				entity.closeContainer();
			}
		}
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		playClientSound(getCloseSound());
	}

	protected void playClientSound(ResourceLocation sound) {
		if (sound == null || !GunSystemClientConfig.GUN_INV_SOUNDS.get() || !world.isClientSide())
			return;
		world.playLocalSound(x + 0.5, y + 0.5, z + 0.5, BuiltInRegistries.SOUND_EVENT.get(sound), SoundSource.PLAYERS, 1.0F, 1.0F, false);
	}

	@Override
	public Map<Integer, Slot> get() {
		return customSlots;
	}
}
