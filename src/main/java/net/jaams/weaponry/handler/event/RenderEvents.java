package net.jaams.weaponry.handler.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.client.Minecraft;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderArm(RenderArmEvent event) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderHand(RenderHandEvent event) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            event.setCanceled(true);
        }
    }
}
