package net.jaams.weaponry.configuration.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CameraEffectsConfig {
	public static ModConfigSpec.BooleanValue SHAKE;

	public static void register(ModConfigSpec.Builder builder) {
		builder.push("Camera Effects Handler");
		SHAKE = builder.comment("Enable or disable screen shake effects on certain actions (explosions, heavy hits, etc.)").define("Screen Shake", true);
		builder.pop();
	}
}
