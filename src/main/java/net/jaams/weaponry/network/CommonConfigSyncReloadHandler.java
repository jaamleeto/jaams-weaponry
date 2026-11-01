package net.jaams.weaponry.network;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.client.Minecraft;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.configuration.base.JaamsWeaponryModConfigs;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfigSyncReloadHandler {
	private static final String COMMON_CONFIG_FILE = JaamsWeaponryModConfigs.CONFIG_FOLDER + "/jaams_weaponry_common.toml";

	private static boolean shouldReapply(ModConfig config) {
		if (FMLEnvironment.dist != Dist.CLIENT) {
			return false;
		}
		if (!COMMON_CONFIG_FILE.equals(config.getFileName())) {
			return false;
		}
		if (CommonConfigSyncMessage.lastSyncedBytes() == null || CommonConfigSyncMessage.isApplying()) {
			return false;
		}
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft != null && minecraft.getSingleplayerServer() == null;
	}

	@SubscribeEvent
	public static void onConfigLoading(ModConfigEvent.Loading event) {
		ModConfig config = event.getConfig();
		if (shouldReapply(config)) {
			CommonConfigSyncMessage.reapplyLastSync();
		}
	}

	@SubscribeEvent
	public static void onConfigReloading(ModConfigEvent.Reloading event) {
		ModConfig config = event.getConfig();
		if (shouldReapply(config)) {
			CommonConfigSyncMessage.reapplyLastSync();
		}
	}
}
