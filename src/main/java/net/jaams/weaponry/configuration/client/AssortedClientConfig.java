package net.jaams.weaponry.configuration.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class AssortedClientConfig {

    public static ForgeConfigSpec.BooleanValue WHIRLING_STRIKE_ARM_ANIMATION;

    public static void register(ForgeConfigSpec.Builder builder) {
        builder.push("Assorted Client Handler");
        builder.push("Weapon Traits");
        builder.push("Item Traits Settings");
        builder.push("Whirling Strike");
        WHIRLING_STRIKE_ARM_ANIMATION = builder
                .comment("Enable or disable the whirling strike arm animation in third person")
                .define("Whirling Strike Arm Animation", true);
        builder.pop();
        builder.pop();
        builder.pop();
        builder.pop();
    }
}
