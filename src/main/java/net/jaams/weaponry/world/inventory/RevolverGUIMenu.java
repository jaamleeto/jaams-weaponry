package net.jaams.weaponry.world.inventory;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.component.gui.BaseGunGUIMenu;
import net.jaams.weaponry.init.ModMenus;
import net.jaams.weaponry.network.RevolverGUISlotMessage;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class RevolverGUIMenu extends BaseGunGUIMenu {

    public RevolverGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ModMenus.REVOLVER_GUI.get(), id, inv, extraData);
    }

    @Override
    protected int getSlotCount() {
        return 7;
    }

    @Override
    protected void setupGunSlots() {
        this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 80, 33) {
            private final int slot = 0;

            @Override
            public boolean mayPlace(ItemStack stack) {
                ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, slot);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(slot);
            }
        }));

        this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 56, 33) {
            private final int slot = 1;

            @Override
            public boolean mayPlace(ItemStack stack) {
                ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, slot);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(slot);
            }
        }));

        this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 68, 55) {
            private final int slot = 2;

            @Override
            public boolean mayPlace(ItemStack stack) {
                ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, slot);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(slot);
            }
        }));

        this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 92, 55) {
            private final int slot = 3;

            @Override
            public boolean mayPlace(ItemStack stack) {
                ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, slot);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(slot);
            }
        }));

        this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 104, 33) {
            private final int slot = 4;

            @Override
            public boolean mayPlace(ItemStack stack) {
                ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, slot);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(slot);
            }
        }));

        this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 92, 11) {
            private final int slot = 5;

            @Override
            public boolean mayPlace(ItemStack stack) {
                ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, slot);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(slot);
            }
        }));

        this.customSlots.put(6, this.addSlot(new SlotItemHandler(internal, 6, 68, 11) {
            private final int slot = 6;

            @Override
            public boolean mayPlace(ItemStack stack) {
                ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, slot);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                slotChanged(slot);
            }
        }));
    }

    @Override
    protected ResourceLocation getOpenSound() {
        return ResourceLocation.parse("jaams_weaponry:gun_system_revolver_open");
    }

    @Override
    protected ResourceLocation getCloseSound() {
        return ResourceLocation.parse("jaams_weaponry:gun_system_revolver_closed");
    }

    @Override
    protected void sendSlotPacket(int slotid) {
        PacketDistributor.sendToServer(new RevolverGUISlotMessage(slotid, x, y, z, 0, 0));
        RevolverGUISlotMessage.handleSlotAction(entity, slotid, 0, 0, x, y, z);
    }
}
