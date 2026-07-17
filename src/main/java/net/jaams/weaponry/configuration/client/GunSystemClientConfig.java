package net.jaams.weaponry.configuration.client;

import net.jaams.weaponry.util.ModEnums;
import net.minecraftforge.common.ForgeConfigSpec;

public class GunSystemClientConfig {


    public static ForgeConfigSpec.BooleanValue GUN_INV_SOUNDS;
    public static ForgeConfigSpec.EnumValue<ModEnums.KeyOption> GUN_INV_KEY;

    public static ForgeConfigSpec.BooleanValue SHOW_GUN_BARS;
    public static ForgeConfigSpec.IntValue ATTACHMENT_BAR_X;
    public static ForgeConfigSpec.IntValue ATTACHMENT_BAR_Y;
    public static ForgeConfigSpec.IntValue AMMO_BAR_X;
    public static ForgeConfigSpec.IntValue AMMO_BAR_Y;
    public static ForgeConfigSpec.BooleanValue AMMO_BAR_DEFAULT_COLOR;
    public static ForgeConfigSpec.BooleanValue AMMO_BAR_SLOT_COLORS;

    public static ForgeConfigSpec.BooleanValue GUN_AIMING_POSE;

    public static ForgeConfigSpec.BooleanValue GUN_AIMING_POSE_BLEND;
    public static ForgeConfigSpec.BooleanValue GUN_COOLDOWN_THIRD_PERSON_ANIMATION;

    public static ForgeConfigSpec.EnumValue<ModEnums.OverlayPosition> OVERLAY_POSITION;
    public static ForgeConfigSpec.BooleanValue SHOW_OVERLAY;
    public static ForgeConfigSpec.BooleanValue BACKGROUND_ENABLED;
    public static ForgeConfigSpec.EnumValue<ModEnums.BackgroundColorOption> BACKGROUND_COLOR;
    public static ForgeConfigSpec.EnumValue<ModEnums.TransparencyOption> BACKGROUND_TRANSPARENCY;
    public static ForgeConfigSpec.EnumValue<ModEnums.BackgroundColorOption> BORDER_COLOR;
    public static ForgeConfigSpec.IntValue BORDER_THICKNESS;
    public static ForgeConfigSpec.BooleanValue RENDER_SLOT_0;
    public static ForgeConfigSpec.BooleanValue RENDER_SLOT_1;
    public static ForgeConfigSpec.BooleanValue RENDER_SLOT_2;
    public static ForgeConfigSpec.BooleanValue GUN_PEPPERBOX_OVERLAY_EXTENDED;
    public static ForgeConfigSpec.BooleanValue GUN_PEPPERBOX_OVERLAY_TWO_ROWS;
    public static ForgeConfigSpec.IntValue MAIN_HAND_X;
    public static ForgeConfigSpec.IntValue MAIN_HAND_Y;
    public static ForgeConfigSpec.IntValue OFF_HAND_X;
    public static ForgeConfigSpec.IntValue OFF_HAND_Y;

    public static ForgeConfigSpec.BooleanValue GUN_COOLDOWN_FIRST_PERSON_ANIMATION;

    public static void register(ForgeConfigSpec.Builder builder) {
        builder.push("Gun System Client Handler");
        builder.push("Gun Client Settings");
        GUN_INV_SOUNDS = builder.comment("Enable or disable gun inventory sounds").define("Gun Inventory Sounds", true);
        GUN_INV_KEY = builder.comment("Key to press with right-click to open gun inventory (ALT, SHIFT, or CONTROL)")
                .defineEnum("Gun Inventory Key", ModEnums.KeyOption.SHIFT);
        builder.pop();
        builder.push("Gun Pose Settings");
        GUN_AIMING_POSE = builder
                .comment("Enable or disable the gun aiming pose in third person. Disables the aiming pose for all guns")
                .define("Gun Aiming Pose", true);
        GUN_AIMING_POSE_BLEND = builder
                .comment("Enable or disable the smooth blend when entering/leaving the aiming pose and when swinging")
                .define("Gun Aiming Pose Blend", true);
        builder.pop();
        builder.push("Gun Bar Settings");
        SHOW_GUN_BARS = builder.comment(
                "Enable or disable the gun color bars shown on gun items. When disabled, both the attachment bar and the ammo bar are hidden")
                .define("Show Gun Bars", true);
        ATTACHMENT_BAR_X = builder.comment("X position of the attachment bar")
                .defineInRange("Attachment Bar X", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        ATTACHMENT_BAR_Y = builder.comment("Y position of the attachment bar")
                .defineInRange("Attachment Bar Y", 13, Integer.MIN_VALUE, Integer.MAX_VALUE);
        AMMO_BAR_X = builder.comment("X position of the ammo bar").defineInRange("Ammo Bar X", 2, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
        AMMO_BAR_Y = builder.comment("Y position of the ammo bar").defineInRange("Ammo Bar Y", 13, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
        AMMO_BAR_DEFAULT_COLOR = builder
                .comment("Use only the default color for the ammo bar instead of bullet colors")
                .define("Ammo Bar Default Color", false);
        AMMO_BAR_SLOT_COLORS = builder.comment(
                "For revolvers and pepperboxes: color each slot of the ammo bar with that slot's bullet color instead of the current slot's color")
                .define("Ammo Bar Slot Colors", false);
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
        builder.push("Pepperbox Overlay Settings");
        GUN_PEPPERBOX_OVERLAY_EXTENDED = builder.comment(
                "For pepperboxes, show every loaded barrel in the storage overlay. When disabled, only the first loaded barrel is shown (chamber 1 has priority)")
                .define("Pepperbox Extended Overlay", true);
        GUN_PEPPERBOX_OVERLAY_TWO_ROWS = builder.comment(
                "For the extended pepperbox overlay, when many barrels are loaded arrange them in two rows so the overlay takes less horizontal space and does not collide with other HUD elements")
                .define("Pepperbox Overlay Two Rows", true);
        builder.pop();
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
