package net.jaams.weaponry.configuration.base;

import net.neoforged.neoforge.common.ModConfigSpec;

import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.client.ProjectileClientConfig;
import net.jaams.weaponry.configuration.client.ItemStatusBarConfig;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.client.CreativeTabConfig;
import net.jaams.weaponry.configuration.client.CameraEffectsConfig;
import net.jaams.weaponry.configuration.client.AssortedClientConfig;

public class JaamsWeaponryClientConfiguration {
	public static final ModConfigSpec SPEC;
	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		AssortedClientConfig.register(builder);
		CameraEffectsConfig.register(builder);
		CreativeTabConfig.register(builder);
		GunSystemClientConfig.register(builder);
		ItemStatusBarConfig.register(builder);
		ProjectileClientConfig.register(builder);
		TooltipsConfig.register(builder);
		SPEC = builder.build();
	}
}
