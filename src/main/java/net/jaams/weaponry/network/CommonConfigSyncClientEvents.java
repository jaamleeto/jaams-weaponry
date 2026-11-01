package net.jaams.weaponry.network;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

import net.jaams.weaponry.JaamsWeaponryMod;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonConfigSyncClientEvents {
	@SubscribeEvent
	public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
		CommonConfigSyncMessage.clearLastSyncedBytes();
	}

	@SubscribeEvent
	public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		if (CommonConfigSyncMessage.lastSyncedBytes() != null) {
			CommonConfigSyncMessage.restoreLocalConfig();
		}
	}
}
