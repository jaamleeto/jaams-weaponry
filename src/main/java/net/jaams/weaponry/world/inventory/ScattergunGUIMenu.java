package net.jaams.weaponry.world.inventory;

import net.jaams.weaponry.component.gui.BaseGunGUIMenu;
import net.jaams.weaponry.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScattergunGUIMenu extends BaseGunGUIMenu {

    public ScattergunGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ModMenus.SCATTERGUN_GUI.get(), id, inv, extraData);
    }

    @Override
    protected int getSlotCount() {
        return 3;
    }

    @Override
    protected void setupGunSlots() {
        this.customSlots.put(0, this.addSlot(createGunSlot(internal, 0, 62, 55)));
        this.customSlots.put(1, this.addSlot(createGunSlot(internal, 1, 80, 55)));
        this.customSlots.put(2, this.addSlot(createGunSlot(internal, 2, 98, 55)));
    }

    @Override
    protected ResourceLocation getOpenSound() {
        return ResourceLocation.parse("jaams_weaponry:gun_system_scattergun_open");
    }

    @Override
    protected ResourceLocation getCloseSound() {
        return ResourceLocation.parse("jaams_weaponry:gun_system_scattergun_closed");
    }

    @Override
    protected ResourceLocation getSlotSound(int slotId) {
        if (slotId == 1) {
            return ResourceLocation.parse("jaams_weaponry:gun_system_scattergun_bullet");
        }
        return ResourceLocation.parse("jaams_weaponry:gun_system_scattergun_attachment");
    }
}
