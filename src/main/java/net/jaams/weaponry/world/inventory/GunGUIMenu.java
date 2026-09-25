package net.jaams.weaponry.world.inventory;

import net.jaams.weaponry.component.gui.BaseGunGUIMenu;
import net.jaams.weaponry.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GunGUIMenu extends BaseGunGUIMenu {

    public GunGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ModMenus.GUN_GUI.get(), id, inv, extraData);
    }

    @Override
    protected int getSlotCount() {
        return 2;
    }

    @Override
    protected void setupGunSlots() {
        this.customSlots.put(1, this.addSlot(createGunSlot(internal, 1, 80, 55)));
    }

    @Override
    protected ResourceLocation getOpenSound() {
        return ResourceLocation.parse("jaams_weaponry:gun_system_pistol_open");
    }

    @Override
    protected ResourceLocation getCloseSound() {
        return ResourceLocation.parse("jaams_weaponry:gun_system_pistol_closed");
    }

    @Override
    protected ResourceLocation getSlotSound(int slotId) {
        return ResourceLocation.parse("jaams_weaponry:gun_system_pistol_bullet");
    }
}
