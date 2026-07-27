package net.jaams.weaponry.configuration.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ItemStatusBarConfig {

    public static ModConfigSpec.BooleanValue SHOW_AFTER_STRIKE_BAR;
    public static ModConfigSpec.IntValue AFTER_STRIKE_BAR_X;
    public static ModConfigSpec.IntValue AFTER_STRIKE_BAR_Y;
    public static ModConfigSpec.ConfigValue<String> AFTER_STRIKE_BAR_COLOR;

    public static ModConfigSpec.BooleanValue SHOW_RAPID_BOOST_BAR;
    public static ModConfigSpec.IntValue RAPID_BOOST_BAR_X;
    public static ModConfigSpec.IntValue RAPID_BOOST_BAR_Y;
    public static ModConfigSpec.ConfigValue<String> RAPID_BOOST_BAR_COLOR;

    public static ModConfigSpec.BooleanValue SHOW_POWER_BOOST_BAR;
    public static ModConfigSpec.IntValue POWER_BOOST_BAR_X;
    public static ModConfigSpec.IntValue POWER_BOOST_BAR_Y;
    public static ModConfigSpec.ConfigValue<String> POWER_BOOST_BAR_COLOR;

    public static ModConfigSpec.BooleanValue SHOW_BUSTER_STRIKE_BAR;
    public static ModConfigSpec.IntValue BUSTER_STRIKE_BAR_X;
    public static ModConfigSpec.IntValue BUSTER_STRIKE_BAR_Y;
    public static ModConfigSpec.ConfigValue<String> BUSTER_STRIKE_BAR_COLOR;

    public static void register(ModConfigSpec.Builder builder) {
        builder.push("Item Status Bars Handler");
        builder.push("Item Traits Bar Settings");
        builder.push("After Strike Bar Settings");
        SHOW_AFTER_STRIKE_BAR = builder.comment("Show or hide the After Strike status bar").define("Show After Strike Bar", true);
        AFTER_STRIKE_BAR_X = builder.comment("X offset of the After Strike bar").defineInRange("Bar X Offset", 2, -100, 100);
        AFTER_STRIKE_BAR_Y = builder.comment("Y offset of the After Strike bar").defineInRange("Bar Y Offset", 13, -100, 100);
        AFTER_STRIKE_BAR_COLOR = builder.comment("Hex color (AARRGGBB) for the After Strike bar").define("Bar Hex Color", "FFFFFFFF");
        builder.pop();
        builder.push("Rapid Boost Bar Settings");
        SHOW_RAPID_BOOST_BAR = builder.comment("Show or hide the Rapid Boost status bar").define("Show Rapid Boost Bar", true);
        RAPID_BOOST_BAR_X = builder.comment("X offset of the Rapid Boost bar").defineInRange("Bar X Offset", 2, -100, 100);
        RAPID_BOOST_BAR_Y = builder.comment("Y offset of the Rapid Boost bar").defineInRange("Bar Y Offset", 13, -100, 100);
        RAPID_BOOST_BAR_COLOR = builder.comment("Hex color (AARRGGBB) for the Rapid Boost bar").define("Bar Hex Color", "FF55CFFF");
        builder.pop();
        builder.push("Power Boost Bar Settings");
        SHOW_POWER_BOOST_BAR = builder.comment("Show or hide the Power Boost status bar").define("Show Power Boost Bar", true);
        POWER_BOOST_BAR_X = builder.comment("X offset of the Power Boost bar").defineInRange("Bar X Offset", 2, -100, 100);
        POWER_BOOST_BAR_Y = builder.comment("Y offset of the Power Boost bar").defineInRange("Bar Y Offset", 13, -100, 100);
        POWER_BOOST_BAR_COLOR = builder.comment("Hex color (AARRGGBB) for the Power Boost bar").define("Bar Hex Color", "FFFF4444");
        builder.pop();
        builder.push("Buster Strike Bar Settings");
        SHOW_BUSTER_STRIKE_BAR = builder.comment("Show or hide the Buster Strike status bar").define("Show Buster Strike Bar", true);
        BUSTER_STRIKE_BAR_X = builder.comment("X offset of the Buster Strike bar").defineInRange("Bar X Offset", 2, -100, 100);
        BUSTER_STRIKE_BAR_Y = builder.comment("Y offset of the Buster Strike bar").defineInRange("Bar Y Offset", 13, -100, 100);
        BUSTER_STRIKE_BAR_COLOR = builder.comment("Hex color (AARRGGBB) for the Buster Strike bar").define("Bar Hex Color", "FFFF4444");
        builder.pop();
        builder.pop();
        builder.pop();
    }
}
