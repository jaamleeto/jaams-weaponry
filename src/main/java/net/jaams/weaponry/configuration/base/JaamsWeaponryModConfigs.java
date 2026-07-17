package net.jaams.weaponry.configuration.base;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.jaams.weaponry.JaamsWeaponryMod;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JaamsWeaponryModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, JaamsWeaponryClientConfiguration.SPEC, "jaams/weaponry/jaams_weaponry_client.toml");
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, JaamsWeaponryCommonConfiguration.SPEC, "jaams/weaponry/jaams_weaponry_common.toml");
		});
	}
}
