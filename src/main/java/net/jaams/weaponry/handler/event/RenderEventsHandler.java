package net.jaams.weaponry.handler.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderEventsHandler {

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
