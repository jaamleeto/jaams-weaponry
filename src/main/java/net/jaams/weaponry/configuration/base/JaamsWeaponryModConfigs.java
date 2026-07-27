package net.jaams.weaponry.configuration.base;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class JaamsWeaponryModConfigs {

	public static void register(ModContainer container) {
		container.registerConfig(ModConfig.Type.CLIENT, JaamsWeaponryClientConfiguration.SPEC, "jaams/weaponry/jaams_weaponry_client.toml");
		container.registerConfig(ModConfig.Type.COMMON, JaamsWeaponryCommonConfiguration.SPEC, "jaams/weaponry/jaams_weaponry_common.toml");
	}
}
