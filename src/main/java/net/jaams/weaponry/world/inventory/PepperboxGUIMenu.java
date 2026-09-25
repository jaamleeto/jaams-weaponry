package net.jaams.weaponry.world.inventory;

import net.jaams.weaponry.component.gui.BaseGunGUIMenu;
import net.jaams.weaponry.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PepperboxGUIMenu extends BaseGunGUIMenu {

    public PepperboxGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ModMenus.PEPPERBOX_GUI.get(), id, inv, extraData);
    }

    @Override
    protected int getSlotCount() {
        return 7;
    }

    @Override
    protected void setupGunSlots() {
        this.customSlots.put(0, this.addSlot(createGunSlot(internal, 0, 80, 33)));
        this.customSlots.put(1, this.addSlot(createGunSlot(internal, 1, 56, 33)));
        this.customSlots.put(2, this.addSlot(createGunSlot(internal, 2, 68, 55)));
        this.customSlots.put(3, this.addSlot(createGunSlot(internal, 3, 92, 55)));
        this.customSlots.put(4, this.addSlot(createGunSlot(internal, 4, 104, 33)));
        this.customSlots.put(5, this.addSlot(createGunSlot(internal, 5, 92, 11)));
        this.customSlots.put(6, this.addSlot(createGunSlot(internal, 6, 68, 11)));
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
    protected ResourceLocation getSlotSound(int slotId) {
        if (slotId == 0) {
            return ResourceLocation.parse("jaams_weaponry:gun_system_revolver_attachment");
        }
        return ResourceLocation.parse("jaams_weaponry:gun_system_revolver_bullet");
    }
}
