package net.jaams.weaponry.handler.client;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Minecraft;

import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.configuration.client.CameraEffectsConfig;
import net.jaams.weaponry.capability.aberration.AberrationProvider;

@EventBusSubscriber(value = Dist.CLIENT)
public class CameraEventsHandler {
    @SubscribeEvent
    public static void onCameraEffect(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Minecraft minecraft = Minecraft.getInstance();
        if (player != null && CameraEffectsConfig.SHAKE.get() && !minecraft.isPaused() && minecraft.screen == null) {
            AberrationProvider.get(player).ifPresent(aberration -> {
                ModEnums.AberrationType effectType = aberration.getEffectType();
                double intensity = aberration.getIntensity();
                int duration = aberration.getDuration();
                if (intensity > 0.0 && duration > 0 && effectType != ModEnums.AberrationType.NONE) {
                    switch (effectType) {
                        case SHAKE:
                            double randomShake = (-1.0 + 2.0 * Math.random()) * intensity;
                            double randomUpward = Math.random() * (intensity * 0.75);
                            event.setPitch((float) (event.getPitch() + randomShake));
                            event.setRoll((float) (event.getRoll() + randomShake));
                            event.setYaw((float) (event.getYaw() + randomShake));
                            break;
                        case BLUR:
                            float fovModifier = (float) (intensity * 0.1);
                            break;
                        case DISTORT:
                            double distortShake = (-0.5 + Math.random()) * intensity * 0.5;
                            event.setYaw((float) (event.getYaw() + distortShake));
                            event.setPitch((float) (event.getPitch() + distortShake * 0.5));
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onHandRender(RenderHandEvent event) {
		Player player = Minecraft.getInstance().player;
		if (player != null) {
			ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
			ItemStack stack = event.getItemStack();
			if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND && player.getUseItem().getItem() instanceof CrossbowItem) {
				if (event.getHand() == InteractionHand.OFF_HAND) {
					event.setCanceled(true);
				}
			} else if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.OFF_HAND && player.getUseItem().getItem() instanceof CrossbowItem) {
				if (event.getHand() == InteractionHand.MAIN_HAND) {
					event.setCanceled(true);
				}
			}
			if (mainHandItem.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(mainHandItem)) {
				if (event.getHand() == InteractionHand.OFF_HAND) {
					event.setCanceled(true);
				}
			}
			if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND && player.getUseItem().getItem() instanceof BowItem) {
				if (event.getHand() == InteractionHand.OFF_HAND) {
					event.setCanceled(true);
				}
			} else if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.OFF_HAND && player.getUseItem().getItem() instanceof BowItem) {
				if (event.getHand() == InteractionHand.MAIN_HAND) {
					event.setCanceled(true);
				}
			}
		}
	}
}
