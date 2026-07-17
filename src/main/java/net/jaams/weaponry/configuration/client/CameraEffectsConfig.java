package net.jaams.weaponry.configuration.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class CameraEffectsConfig {
	public static ForgeConfigSpec.BooleanValue SHAKE;

	public static void register(ForgeConfigSpec.Builder builder) {
		builder.push("Camera Effects Handler");
		SHAKE = builder.comment("Enable or disable screen shake effects on certain actions (explosions, heavy hits, etc.)").define("Screen Shake", true);
		builder.pop();
	}
}
