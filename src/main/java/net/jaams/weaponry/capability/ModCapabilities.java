package net.jaams.weaponry.capability;

import net.jaams.weaponry.capability.aberration.IAberration;
import net.jaams.weaponry.capability.amount.IAmount;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IAmount.class);
        event.register(IAberration.class);
    }
}
