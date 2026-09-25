package net.jaams.weaponry.component.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.capability.CapHelper;
import net.jaams.weaponry.capability.gun.GunItemHandler;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Shared, server-authoritative menu implementation for all gun inventories.
 *
 * <p>The server owns the real handler on the held stack. The client receives a
 * serialized snapshot when the menu opens and uses that snapshot only as the
 * visual/prediction mirror. Normal container synchronization then updates it;
 * no extra slot packet is necessary.</p>
 */
public abstract class BaseGunGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {

    public static final HashMap<String, Object> guistate = new HashMap<>();
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
    private ItemStack boundItemReference = ItemStack.EMPTY;
    private ModGuns.GunType boundGunType;
    private int[] boundSlotLimits;

    public BaseGunGUIMenu(MenuType<?> menuType, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(menuType, id);
        this.entity = inv.player;
        this.world = inv.player.level();
        this.internal = new ItemStackHandler(Math.max(1, getSlotCount()));
        BlockPos pos;
        try {
            pos = extraData.readBlockPos();
            readBindingData(extraData);
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("[GunInventory] Failed to read packet data, using fallback: {}", e.getMessage());
            // Fallback to player position
            pos = entity.blockPosition();
            // Try to bind to hand anyway
            this.boundHand = 0;
            bindHand(ItemStack.EMPTY);
        }
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.access = ContainerLevelAccess.create(world, pos);
        JaamsWeaponryMod.LOGGER.debug(
                "[GunInventory] Menu created: type={}, side={}, bound={}, hand={}, gunType={}, stack={}, slots={}",
                menuType, world.isClientSide() ? "client" : "server", this.bound, this.boundHand,
                this.boundGunType, this.boundItemStack, this.internal.getSlots());
        setupGunSlots();
        seedLastKnownStacks();
        addPlayerInventorySlots();
        playClientSound(getOpenSound());
    }

    protected abstract int getSlotCount();

    protected abstract void setupGunSlots();

    protected abstract ResourceLocation getOpenSound();

    protected abstract ResourceLocation getCloseSound();

    protected abstract ResourceLocation getSlotSound(int slotId);

    private void seedLastKnownStacks() {
        int count = Math.min(getSlotCount(), internal.getSlots());
        for (int i = 0; i < count; i++) {
            lastKnownStacks.put(i, internal.getStackInSlot(i).copy());
        }
    }

    private void readBindingData(FriendlyByteBuf extraData) {
        if (extraData == null || !extraData.isReadable()) {
            bindBlockEntity();
            return;
        }

        try {
            int marker = extraData.readUnsignedByte();
            if (marker == ModGuns.GUN_MENU_DATA_VERSION) {
                if (!extraData.isReadable())
                    return;
                this.boundHand = extraData.readByte();
                CompoundTag encodedStack = extraData.isReadable() ? extraData.readNbt() : null;
                ItemStack transmittedStack = encodedStack == null
                        ? ItemStack.EMPTY
                        : ItemStack.parseOptional(world.registryAccess(), encodedStack);
                if (extraData.isReadable()) {
                    this.boundGunType = extraData.readEnum(ModGuns.GunType.class);
                }
                if (extraData.isReadable()) {
                    int limitCount = this.boundGunType == null
                            ? getSlotCount()
                            : ModGuns.getGunSlotCount(this.boundGunType);
                    this.boundSlotLimits = new int[limitCount];
                    for (int slot = 0; slot < this.boundSlotLimits.length && extraData.isReadable(); slot++) {
                        this.boundSlotLimits[slot] = Math.max(1, extraData.readVarInt()); // Ensure at least 1 for attachment slots
                    }
                }
                bindHand(transmittedStack);
                return;
            }

            // Compatibility with menus opened by older builds, which sent only the
            // hand byte after the block position.
            if (marker == 0 || marker == 1) {
                this.boundHand = (byte) marker;
                bindHand(ItemStack.EMPTY);
                return;
            }

            // No current gun menu uses entity binding, but retaining this fallback
            // avoids turning malformed/legacy data into an accidental hand binding.
            if (extraData.isReadable()) {
                this.boundEntity = world.getEntity(extraData.readVarInt());
                if (boundEntity != null) {
                    CapHelper.itemHandler(boundEntity).ifPresent(handler -> {
                        this.internal = handler;
                        this.bound = true;
                    });
                }
            } else {
                bindBlockEntity();
            }
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("[GunInventory] Error reading binding data: {}", e.getMessage());
            bindHand(ItemStack.EMPTY);
        }
    }

    private void bindHand(ItemStack transmittedStack) {
        ItemStack currentStack = boundHand == 0 ? entity.getMainHandItem() : entity.getOffhandItem();
        this.boundItemReference = currentStack;

        // A server-created menu uses the actual held stack. On the client, use
        // the server snapshot so the first frame is correct even if the regular
        // player-inventory update arrives a few packets later.
        ItemStack handlerStack = world.isClientSide() && !transmittedStack.isEmpty()
                ? transmittedStack.copy()
                : currentStack;
        if (this.boundGunType == null) {
            this.boundGunType = ModGuns.getGunType(handlerStack);
        }
        this.boundItemStack = world.isClientSide() ? handlerStack.copy() : currentStack;
        this.internal = createGunHandler(handlerStack);
        this.bound = this.internal != null;
        this.boundItemMatcher = this::isBoundGunPresent;
    }

    private IItemHandler createGunHandler(ItemStack stack) {
        if (stack == null || stack.isEmpty() || boundGunType == null)
            return null;
        // Use the type and limits supplied by the server instead of re-detecting
        // them from client-only datapack/config state.
        if (world.isClientSide()) {
            return new GunItemHandler(boundGunType, stack, boundSlotLimits, false);
        }
        return new GunItemHandler(boundGunType, stack);
    }

    private void bindBlockEntity() {
        this.boundBlockEntity = this.world.getBlockEntity(new BlockPos(x, y, z));
        if (boundBlockEntity != null) {
            CapHelper.itemHandler(boundBlockEntity).ifPresent(capability -> {
                this.internal = capability;
                this.bound = true;
            });
        }
    }

    private boolean isBoundGunPresent() {
        if (!bound || boundItemMatcher == null)
            return false;
        ItemStack current = boundHand == 0 ? entity.getMainHandItem() : entity.getOffhandItem();
        if (current.isEmpty() || boundItemStack.isEmpty())
            return false;

        if (!world.isClientSide()) {
            // Reference equality is intentional on the server. It prevents a
            // menu from continuing to edit a gun after the player swaps it for
            // another stack, including another copy of the same item.
            return current == boundItemReference
                    && ModGuns.isSameGunForInventory(current, boundItemStack);
        }
        // The server is authoritative for component identity. The client only
        // needs the hand/item anchor here; its component snapshot can legitimately
        // lag one inventory synchronization packet behind the menu mirror.
        return current.getItem() == boundItemStack.getItem()
                && current.getCount() == boundItemStack.getCount();
    }

    protected final ModGuns.GunType getBoundGunType() {
        return boundGunType;
    }

    protected final boolean isBoundItemPresent() {
        return isBoundGunPresent();
    }

    private boolean isBoundGunStack(ItemStack candidate) {
        if (candidate == null || candidate.isEmpty())
            return false;
        ItemStack current = boundHand == 0 ? entity.getMainHandItem() : entity.getOffhandItem();
        return candidate == boundItemReference
                || candidate == current
                || ItemStack.matches(candidate, boundItemStack);
    }

    protected Slot createGunSlot(IItemHandler handler, int index, int x, int y) {
        return new SlotItemHandler(handler, index, x, y) {
            			@Override
            			public boolean mayPlace(ItemStack stack) {
            				if (!isBoundItemPresent() || isBoundGunStack(stack))
            					return false;
            				// Rules are enforced on both sides (1.20.1 parity): the client
            				// prediction must reject the same items as the server, otherwise
            				// invalid clicks show up briefly and play slot sounds.
            				ModGuns.GunType type = getBoundGunType();
            				return type != null
            						&& ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, index)
            						&& super.mayPlace(stack);
            			}

            @Override
            public boolean mayPickup(Player playerIn) {
                return isBoundItemPresent() && super.mayPickup(playerIn);
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(index);
            }
        };
    }

    protected void addPlayerInventorySlots() {
        for (int si = 0; si < 3; ++si) {
            for (int sj = 0; sj < 9; ++sj) {
                this.addSlot(new Slot(entity.getInventory(), sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
            }
        }
        for (int si = 0; si < 9; ++si) {
            this.addSlot(new Slot(entity.getInventory(), si, 8 + si * 18, 142));
        }
    }

    protected void slotChanged(int slotid) {
        if (!world.isClientSide() || slotid < 0 || slotid >= internal.getSlots())
            return;
        ItemStack current = internal.getStackInSlot(slotid);
        ItemStack previous = lastKnownStacks.getOrDefault(slotid, ItemStack.EMPTY);
        if (!ItemStack.matches(current, previous)) {
            lastKnownStacks.put(slotid, current.copy());
            playSlotChangeSound(slotid, previous, current);
        }
    }

    private void playSlotChangeSound(int slotid, ItemStack previous, ItemStack current) {
        if (!GunSystemClientConfig.GUN_INV_SOUNDS.get() || !world.isClientSide())
            return;
        SoundEvent sound = null;
        boolean isInsert = previous.isEmpty() && !current.isEmpty();
        boolean isExtract = !previous.isEmpty() && current.isEmpty();
        boolean isSwap = !previous.isEmpty() && !current.isEmpty()
                && !ItemStack.isSameItemSameComponents(previous, current);
        if (isInsert) {
            sound = ModGuns.getItemSound(boundItemStack, current);
        } else if (isExtract) {
            sound = ModGuns.getItemSound(boundItemStack, previous);
        } else if (isSwap) {
            sound = ModGuns.getItemSound(boundItemStack, previous);
            if (sound == null) {
                sound = ModGuns.getItemSound(boundItemStack, current);
            }
        }
        if (sound == null) {
            ResourceLocation loc = getSlotSound(slotid);
            if (loc != null) {
                sound = BuiltInRegistries.SOUND_EVENT.get(loc);
            }
        }
        if (sound == null)
            return;
        float pitch = 0.9F + entity.getRandom().nextFloat() * 0.2F;
        world.playLocalSound(x + 0.5, y + 0.5, z + 0.5, sound, SoundSource.PLAYERS, 1.0F, pitch, false);
    }

    @Override
    public boolean stillValid(Player player) {
        if (boundItemMatcher != null)
            return isBoundGunPresent();
        if (boundBlockEntity != null) {
            return AbstractContainerMenu.stillValid(access, player,
                    boundBlockEntity.getBlockState().getBlock());
        }
        if (boundEntity != null)
            return boundEntity.isAlive();
        return bound;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (index < 0 || index >= slots.size())
            return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem() || !slot.mayPickup(playerIn))
            return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getItem();
        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;
        if (isBoundGunStack(stackInSlot))
            return ItemStack.EMPTY;

        ItemStack original = stackInSlot.copy();
        int customCount = getSlotCount();
        int handlerSlot = getHandlerSlotIndex(slot);
        if (handlerSlot >= 0) {
            // Gun slot -> player inventory. Extract through the handler so the
            // component-backed gun is always updated on the authoritative side.
            ItemStack extracted = internal.extractItem(handlerSlot, stackInSlot.getCount(), false);
            if (extracted.isEmpty())
                return ItemStack.EMPTY;
            boolean anyMoved = moveItemStackTo(extracted, customCount, slots.size(), true);
            if (!anyMoved) {
                internal.insertItem(handlerSlot, extracted, false);
                return ItemStack.EMPTY;
            }
            if (!extracted.isEmpty()) {
                ItemStack rejected = internal.insertItem(handlerSlot, extracted, false);
                if (!rejected.isEmpty() && !playerIn.level().isClientSide()) {
                    playerIn.drop(rejected, false);
                }
            }
            			slot.onQuickCraft(stackInSlot, original);
            			// Quick moves bypass Slot.set/setChanged, so report the gun slots
            			// directly to keep the per-slot sounds working on the client.
            			notifyGunSlotsChanged();
            			return original;
            		}

        // Player inventory -> gun slots. The handler returns the remainder,
        // leaving every item it could not accept in the source slot.
        ModGuns.GunType type = getBoundGunType();
        ItemStack remainder = type != null ? ModGuns.insertIntoGun(internal, boundItemStack, stackInSlot, type)
                : stackInSlot;
        int inserted = stackInSlot.getCount() - remainder.getCount();
        if (inserted <= 0) {
            // The item may still be moved between the two player inventory
            // sections, matching vanilla quick-move behaviour.
            if (index < customCount + 27) {
                if (!moveItemStackTo(stackInSlot, customCount + 27, slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stackInSlot, customCount, customCount + 27, false)) {
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }

        stackInSlot.shrink(inserted);
        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        		slot.onTake(playerIn, original.copyWithCount(inserted));
        		notifyGunSlotsChanged();
        		return original;
        	}

        	// Quick moves manipulate the gun handler directly and never trigger
        	// Slot.setChanged, so without this the per-slot change sounds are skipped.
        	private void notifyGunSlotsChanged() {
        		if (internal == null)
        			return;
        		for (int slotId = 0; slotId < internal.getSlots(); slotId++) {
        			slotChanged(slotId);
        		}
        	}

    private int getHandlerSlotIndex(Slot slot) {
        for (Map.Entry<Integer, Slot> entry : customSlots.entrySet()) {
            if (entry.getValue() == slot)
                return entry.getKey();
        }
        return -1;
    }

    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);
        if (bound && boundItemMatcher != null && !isBoundGunPresent() && !isClosing) {
            isClosing = true;
            entity.closeContainer();
        }
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        playClientSound(getCloseSound());
    }

    protected void playClientSound(ResourceLocation sound) {
        if (sound == null || !world.isClientSide() || !GunSystemClientConfig.GUN_INV_SOUNDS.get())
            return;
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(sound);
        if (soundEvent == null)
            return;
        world.playLocalSound(x + 0.5, y + 0.5, z + 0.5, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F,
                false);
    }

    @Override
    public Map<Integer, Slot> get() {
        return customSlots;
    }
}
