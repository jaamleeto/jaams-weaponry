package net.jaams.weaponry.configuration.client;

import net.jaams.weaponry.util.ModEnums;
import net.neoforged.neoforge.common.ModConfigSpec;

public class AssortedClientConfig {

    public static ModConfigSpec.BooleanValue WHIRLING_STRIKE_ARM_ANIMATION;

    public static void register(ModConfigSpec.Builder builder) {
        builder.push("Assorted Client Handler");
        builder.push("Custom Animations Handler");
        builder.push("Item Traits Animations");
        builder.push("Whirling Strike");
        WHIRLING_STRIKE_ARM_ANIMATION = builder
                .comment("Enable or disable the whirling strike arm animation in third person")
                .define("Whirling Strike Arm Animation", true);
        builder.pop();
        builder.pop();
        builder.push("Gun System Animations");
        GunSystemClientConfig.GUN_COOLDOWN_FIRST_PERSON_ANIMATION = builder
                .comment("Enable or disable the first person cooldown animation for guns")
                .define("Gun Cooldown First Person Animation", true);
        GunSystemClientConfig.GUN_COOLDOWN_THIRD_PERSON_ANIMATION = builder
                .comment("When disabled the arm keeps the normal stance while the gun is on cooldown")
                .define("Gun Cooldown Third Person Animation", false);
        builder.pop();
        builder.pop();
        GunSystemClientConfig.register(builder);
        builder.pop();
    }
}
