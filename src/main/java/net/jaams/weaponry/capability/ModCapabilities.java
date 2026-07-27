package net.jaams.weaponry.capability;

import net.jaams.weaponry.capability.gun.GunItemHandler;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        // Guns are data-driven (NBT GunType / item tags / weapon_attributes JSON), so any
        // item can be a gun: register the provider for every item and bail out for non-guns.
        for (Item item : BuiltInRegistries.ITEM) {
            event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> {
                ModGuns.GunType type = ModGuns.getGunType(stack);
                return type != null ? new GunItemHandler(type, stack) : null;
            }, item);
        }
    }
}
