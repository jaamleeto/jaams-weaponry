package net.jaams.weaponry.world.inventory;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.component.gui.BaseGunGUIMenu;
import net.jaams.weaponry.init.ModMenus;
import net.jaams.weaponry.network.ScattergunGUISlotMessage;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ScattergunGUIMenu extends BaseGunGUIMenu {

    public ScattergunGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ModMenus.SCATTERGUN_GUI.get(), id, inv, extraData);
    }

    @Override
    protected int getSlotCount() {
        return 3;
    }

    protected void setupGunSlots() {
        this.customSlots.put(
            0,
            this.addSlot(
                new SlotItemHandler(internal, 0, 62, 55) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                        if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, 0);
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player playerIn) {
                        return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
                    }

                    @Override
                    public void setChanged() {
                        super.setChanged();
                        slotChanged(0);
                    }
                }
            )
        );
        this.customSlots.put(
            1,
            this.addSlot(
                new SlotItemHandler(internal, 1, 80, 55) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                        if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, 1);
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player playerIn) {
                        return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
                    }

                    @Override
                    public void setChanged() {
                        super.setChanged();
                        slotChanged(1);
                    }
                }
            )
        );
        this.customSlots.put(
            2,
            this.addSlot(
                new SlotItemHandler(internal, 2, 98, 55) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        ModGuns.GunType type = ModGuns.getGunType(boundItemStack);
                        if (type != null) return ModGuns.canPlaceInGunSlot(boundItemStack, stack, type, 2);
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player playerIn) {
                        return !boundItemStack.isEmpty() && boundItemMatcher != null && boundItemMatcher.get();
                    }

                    @Override
                    public void setChanged() {
                        super.setChanged();
                        slotChanged(2);
                    }
                }
            )
        );
    }

    @Override
    protected ResourceLocation getOpenSound() {
        return new ResourceLocation("jaams_weaponry:gun_system_scattergun_open");
    }

    @Override
    protected ResourceLocation getCloseSound() {
        return new ResourceLocation("jaams_weaponry:gun_system_scattergun_closed");
    }

    @Override
    protected void sendSlotPacket(int slotid) {
        JaamsWeaponryMod.PACKET_HANDLER.sendToServer(new ScattergunGUISlotMessage(slotid, x, y, z, 0, 0));
        ScattergunGUISlotMessage.handleSlotAction(entity, slotid, 0, 0, x, y, z);
    }
}
