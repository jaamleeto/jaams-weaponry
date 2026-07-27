package net.jaams.weaponry.configuration.client;

import net.jaams.weaponry.util.ModEnums;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GunSystemClientConfig {


    public static ModConfigSpec.BooleanValue GUN_INV_SOUNDS;
    public static ModConfigSpec.EnumValue<ModEnums.KeyOption> GUN_INV_KEY;

    public static ModConfigSpec.BooleanValue SHOW_GUN_BAR;
    public static ModConfigSpec.IntValue GUN_BAR_X;
    public static ModConfigSpec.IntValue GUN_BAR_Y;
    public static ModConfigSpec.BooleanValue GUN_DEFAULT_COLOR;

    public static ModConfigSpec.BooleanValue GUN_DEFAULT_AIMING_POSE;
    public static ModConfigSpec.BooleanValue GUN_AIMING_ARM_ANIMATION;

    public static ModConfigSpec.EnumValue<ModEnums.OverlayPosition> OVERLAY_POSITION;
    public static ModConfigSpec.BooleanValue SHOW_OVERLAY;
    public static ModConfigSpec.BooleanValue BACKGROUND_ENABLED;
    public static ModConfigSpec.EnumValue<ModEnums.BackgroundColorOption> BACKGROUND_COLOR;
    public static ModConfigSpec.EnumValue<ModEnums.TransparencyOption> BACKGROUND_TRANSPARENCY;
    public static ModConfigSpec.EnumValue<ModEnums.BackgroundColorOption> BORDER_COLOR;
    public static ModConfigSpec.IntValue BORDER_THICKNESS;
    public static ModConfigSpec.BooleanValue RENDER_SLOT_0;
    public static ModConfigSpec.BooleanValue RENDER_SLOT_1;
    public static ModConfigSpec.BooleanValue RENDER_SLOT_2;
    public static ModConfigSpec.IntValue MAIN_HAND_X;
    public static ModConfigSpec.IntValue MAIN_HAND_Y;
    public static ModConfigSpec.IntValue OFF_HAND_X;
    public static ModConfigSpec.IntValue OFF_HAND_Y;

    public static ModConfigSpec.BooleanValue GUN_COOLDOWN_ANIMATION;
    public static ModConfigSpec.DoubleValue GUN_COOLDOWN_DROP;

    public static void register(ModConfigSpec.Builder builder) {
        builder.push("Gun System Client Handler");
        builder.push("Gun Client Settings");
        GUN_INV_SOUNDS = builder.comment("Enable or disable gun inventory sounds").define("Gun Inventory Sounds", true);
        GUN_INV_KEY = builder.comment("Key to press with right-click to open gun inventory (ALT, SHIFT, or CONTROL)")
                .defineEnum("Gun Inventory Key", ModEnums.KeyOption.SHIFT);
        GUN_DEFAULT_AIMING_POSE = builder.comment("Enable or disable the default aiming pose for guns")
                .define("Gun Default Aiming Pose", true);
        GUN_AIMING_ARM_ANIMATION = builder.comment("Enable or disable the gun aiming arm animation in third person")
                .define("Gun Aiming Arm Animation", true);
        GUN_COOLDOWN_ANIMATION = builder.comment("Enable or disable the cooldown animation for guns")
                .define("Gun Cooldown Animation", true);
        GUN_COOLDOWN_DROP = builder.comment("How far guns drop down during cooldown animation")
                .defineInRange("Gun Cooldown Drop", 0.15, 0.0, 2.0);
        builder.pop();
        builder.push("Gun Bar Settings");
        SHOW_GUN_BAR = builder.comment("Enable or disable the gun color bar").define("Show Gun Bar", true);
        GUN_BAR_X = builder.comment("X position of the gun color bar").defineInRange("Gun Bar X", 2, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
        GUN_BAR_Y = builder.comment("Y position of the gun color bar").defineInRange("Gun Bar Y", 13, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
        GUN_DEFAULT_COLOR = builder.comment("Use only the default color for the bar instead of bullet colors")
                .define("Gun Default Color", false);
        builder.pop();
        builder.push("Gun Overlay Settings");
        OVERLAY_POSITION = builder.comment("Overlay position on the screen").defineEnum("Overlay Position",
                ModEnums.OverlayPosition.BOTTOM);
        SHOW_OVERLAY = builder.comment("Enable or disable bullet render on Overlay").define("Show Gun Storage Overlay",
                true);
        BACKGROUND_ENABLED = builder.comment("Enable background for the overlay").define("Background Enabled", true);
        BACKGROUND_COLOR = builder.comment("Background color for the overlay").defineEnum("Background Color",
                ModEnums.BackgroundColorOption.BLACK);
        BACKGROUND_TRANSPARENCY = builder.comment("Transparency level for the overlay background")
                .defineEnum("Background Transparency", ModEnums.TransparencyOption.TRANSPARENT);
        BORDER_COLOR = builder.comment("Border color for the overlay").defineEnum("Border Color",
                ModEnums.BackgroundColorOption.WHITE);
        BORDER_THICKNESS = builder.comment("Border thickness for the overlay background")
                .defineInRange("Border Thickness", 1, 0, 3);
        RENDER_SLOT_0 = builder.comment("Enable or disable rendering of slot 0").define("Render Slot 0", true);
        RENDER_SLOT_1 = builder.comment("Enable or disable rendering of slot 1").define("Render Slot 1", true);
        RENDER_SLOT_2 = builder.comment("Enable or disable rendering of slot 2").define("Render Slot 2", true);
        builder.push("Main Hand Overlay Position");
        MAIN_HAND_X = builder.comment("X position of the main hand gun storage overlay")
                .defineInRange("Main Hand Overlay X", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MAIN_HAND_Y = builder.comment("Y position of the main hand gun storage overlay")
                .defineInRange("Main Hand Overlay Y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        builder.pop();
        builder.push("Off Hand Overlay Position");
        OFF_HAND_X = builder.comment("X position of the off hand gun storage overlay")
                .defineInRange("Off Hand Overlay X", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        OFF_HAND_Y = builder.comment("Y position of the off hand gun storage overlay")
                .defineInRange("Off Hand Overlay Y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        builder.pop();
        builder.pop();

        builder.pop();
    }
}
