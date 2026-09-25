package net.jaams.weaponry.configuration.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CreativeTabConfig {
	public static ModConfigSpec.ConfigValue<String> CREATIVE_TAB_ICON;

	public static void register(ModConfigSpec.Builder builder) {
		
		builder.push("Creative Tab Handler");
		CREATIVE_TAB_ICON = builder.comment("The item to use as the creative tab icon (requires restart)").worldRestart().define("Creative Tab Icon", "jaams_weaponry:iron_buster_sword");
		builder.pop();
	}
}
