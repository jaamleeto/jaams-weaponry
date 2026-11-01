package net.jaams.weaponry.sync;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.jaams.weaponry.network.CommonConfigSyncMessage;
import net.jaams.weaponry.JaamsWeaponryMod;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JaamsWeaponryConfigSyncReloader {
	@SubscribeEvent
	public static void onConfigReloading(ModConfigEvent.Reloading event) {
		if (event.getConfig().getType() != ModConfig.Type.COMMON) {
			return;
		}
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null || !server.isDedicatedServer()) {
			return;
		}
		byte[] data = CommonConfigSyncMessage.getServerConfigBytes();
		if (data == null) {
			return;
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new CommonConfigSyncMessage(data));
		}
	}
}
