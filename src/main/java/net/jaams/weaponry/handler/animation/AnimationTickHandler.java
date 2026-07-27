package net.jaams.weaponry.handler.animation;

import net.neoforged.neoforge.client.event.ClientTickEvent;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.util.ModAnimations;


@EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class AnimationTickHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return;

        
        
        for (Player player : mc.level.players()) {
            String name = ModAnimations.getCurrentAnimationName(player);

            if (name.isEmpty() && !ModAnimations.hasRestoreAnimation(player) && !ModAnimations.hasPose(player) && !ModAnimations.hasCombinableAnimations(player)) {
                continue;
            }

            ModAnimations.advanceAnimation(player, player.tickCount);
        }

        
        ModAnimations.tickCameraShake();
    }

}
