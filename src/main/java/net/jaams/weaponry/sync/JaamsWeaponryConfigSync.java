package net.jaams.weaponry.sync;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;

import net.minecraft.server.level.ServerPlayer;

import net.jaams.weaponry.network.CommonConfigSyncMessage;
import net.jaams.weaponry.JaamsWeaponryMod;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JaamsWeaponryConfigSync {
	@SubscribeEvent
	public static void onModDataSync(OnDatapackSyncEvent event) {
		byte[] data = CommonConfigSyncMessage.getServerConfigBytes();
		if (data == null) {
			return;
		}
		for (ServerPlayer player : event.getPlayers()) {
			if (player.level().isClientSide) {
				continue;
			}
			JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new CommonConfigSyncMessage(data));
		}
	}
}
