package net.jaams.weaponry.handler.animation;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.util.ModAnimations;


@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AnimationTickHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

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
