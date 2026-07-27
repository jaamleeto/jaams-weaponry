package net.jaams.weaponry.configuration.client;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public class TooltipsConfig {


    public static ModConfigSpec.BooleanValue TOOLTIPS;
    public static ModConfigSpec.BooleanValue CONTROL_TOOLTIPS;
    public static ModConfigSpec.BooleanValue ALT_TOOLTIPS;
    public static ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_TOOLTIPS_ITEMS;
    public static ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_CONTROL_TOOLTIPS_ITEMS;
    public static ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_ALT_TOOLTIPS_ITEMS;

    public static ModConfigSpec.BooleanValue TOOLTIP_SMOKE_BOMB_PROPERTIES;

    public static ModConfigSpec.BooleanValue TOOLTIP_THROWING_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_THROWING_MODE;

    public static ModConfigSpec.BooleanValue TOOLTIP_PIERCER_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DUELIST_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_THREAT_RESPONSE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_REACH_ADVANTAGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_AFTER_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_AFTER_STRIKE_HITS;
    public static ModConfigSpec.BooleanValue TOOLTIP_AFTER_STRIKE_DAMAGE_MODIFIERS;
    public static ModConfigSpec.BooleanValue TOOLTIP_QUICK_CRAFTING_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_AQUATIC_GRUDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_ARTHROPOD_GRUDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_UNDEAD_GRUDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_TRAITOR_GRUDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_SNOUT_GRUDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_BONE_GRUDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_ANTI_AERIAL_PROPERTIES;

    public static ModConfigSpec.BooleanValue TOOLTIP_ROTTEN_GRUDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_ARMOR_BREAKER_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_BLADE_BREAKER_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_ACROBATIC_LUNGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DEXTEROUS_LUNGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_PULL_LUNGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DISENGAGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DISARM_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DISMOUNT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DISABLING_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_THROUGH_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_CLEANSING_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_OVERWHELMING_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_SUPPRESSING_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_SLASH_ASSAULT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_PIERCING_ASSAULT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_SMASH_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_SHOCK_IMPACT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_HARVEST_SWEEP_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_WILD_SWEEP_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_WHIRLING_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_FRAGILITY_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_SLIPPERY_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_EXHAUSTING_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_BRITTLE_HANDLE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_BARBED_HANDLE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_OVERSTRAIN_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_UNSTABLE_EDGE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DETONATING_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DECAPITATION_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_RAPID_BOOST_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_RAPID_BOOST_HITS;
    public static ModConfigSpec.BooleanValue TOOLTIP_POWER_BOOST_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_POWER_BOOST_HITS;
    public static ModConfigSpec.BooleanValue TOOLTIP_QUICK_SWAP_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_QUICK_SWAP_MODE;
    public static ModConfigSpec.BooleanValue TOOLTIP_BUSTER_STRIKE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_BUSTER_STRIKE_HITS;
    public static ModConfigSpec.BooleanValue TOOLTIP_BACKSTAB_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_GUARD_STANCE_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_PARRY_GUARD_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_HEAVY_HANDED_PROPERTIES;

    public static ModConfigSpec.BooleanValue TOOLTIP_BACKSTAB_SHOT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_PIERCING_SHOT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DISABLING_SHOT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_SWEEPING_SHOT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_DISARMING_SHOT_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_THROWBACK_PROPERTIES;
    public static ModConfigSpec.BooleanValue TOOLTIP_COLLECTOR_PROPERTIES;

    public static ModConfigSpec.BooleanValue TOOLTIP_GUN_INVENTORY_HINT;

    public static ModConfigSpec.BooleanValue TOOLTIP_GUN_BASE_AMMO;
    public static ModConfigSpec.BooleanValue TOOLTIP_GUN_ATTACHMENTS;
    public static ModConfigSpec.BooleanValue TOOLTIP_SLINGSHOT_BASE_AMMO;

    public static ModConfigSpec.BooleanValue TOOLTIP_REVOLVER_ROMAN_NUMERALS;

    public static void register(ModConfigSpec.Builder builder) {
        builder.push("Tooltips System Handler");

        builder.push("Tooltips Base Settings");
        TOOLTIPS = builder.comment("Enable or disable all custom tooltips added by the mod").define("Enable Tooltips",
                true);
        CONTROL_TOOLTIPS = builder
                .comment("Enable or disable tooltips shown when holding the Control key added by the mod")
                .define("Enable Control Key Tooltips", true);
        ALT_TOOLTIPS = builder.comment("Enable or disable tooltips shown when holding the Alt key added by the mod")
                .define("Enable Alt Key Tooltips", true);
        EXCLUDED_TOOLTIPS_ITEMS = builder.comment("List of item IDs excluded from all custom tooltips of the mod")
                .defineList("Excluded Tooltips Items", List.of(), (obj) -> obj instanceof String);
        EXCLUDED_CONTROL_TOOLTIPS_ITEMS = builder
                .comment("List of item IDs excluded from Control key extra info tooltips added by the mod ")
                .defineList("Excluded Control Tooltips Items", List.of(), (obj) -> obj instanceof String);
        EXCLUDED_ALT_TOOLTIPS_ITEMS = builder
                .comment("List of item IDs excluded from Alt key description tooltips added by the mod")
                .defineList("Excluded Alt Tooltips Items", List.of(), (obj) -> obj instanceof String);
        builder.pop();

        builder.push("Tooltips Misc Settings");
        builder.pop();

        builder.push("Item Features Tooltips");
        builder.push("Smoke Bomb");
        TOOLTIP_SMOKE_BOMB_PROPERTIES = builder.comment("Show advanced smoke bomb properties")
                .define("Show Smoke Bomb Properties", true);
        builder.pop();
        builder.push("Guns");
        TOOLTIP_GUN_INVENTORY_HINT = builder.comment("Show the gun inventory key hint in the tooltip")
                .define("Show Gun Inventory Hint", true);
        TOOLTIP_GUN_BASE_AMMO = builder.comment("Show the base ammo line in gun tooltips")
                .define("Show Gun Base Ammo", true);
        TOOLTIP_GUN_ATTACHMENTS = builder.comment("Show the attachments line in gun tooltips")
                .define("Show Gun Attachments", true);
        TOOLTIP_REVOLVER_ROMAN_NUMERALS = builder.comment("Use roman numerals for revolver chamber labels in tooltip")
                .define("Revolver Roman Numerals", true);
        builder.pop();
        builder.push("Slingshots");
        TOOLTIP_SLINGSHOT_BASE_AMMO = builder.comment("Show the base ammo line in slingshot tooltips")
                .define("Show Slingshot Base Ammo", true);
        builder.pop();
        builder.pop();

        builder.push("Weapon Traits Tooltips");

        builder.push("Item Traits Tooltips");

        builder.push("Piercer Strike");
        TOOLTIP_PIERCER_STRIKE_PROPERTIES = builder.comment("Show advanced piercer strike properties")
                .define("Show Piercer Strike Properties", true);
        builder.pop();

        builder.push("Duelist");
        TOOLTIP_DUELIST_PROPERTIES = builder.comment("Show advanced duelist properties")
                .define("Show Duelist Properties", true);
        builder.pop();

        builder.push("Threat Response");
        TOOLTIP_THREAT_RESPONSE_PROPERTIES = builder.comment("Show advanced threat response properties")
                .define("Show Threat Response Properties", true);
        builder.pop();

        builder.push("Reach Advantage");
        TOOLTIP_REACH_ADVANTAGE_PROPERTIES = builder.comment("Show advanced reach advantage properties")
                .define("Show Reach Advantage Properties", true);
        builder.pop();

        builder.push("After Strike");
        TOOLTIP_AFTER_STRIKE_PROPERTIES = builder.comment("Show advanced after strike properties")
                .define("Show After Strike Properties", true);
        TOOLTIP_AFTER_STRIKE_HITS = builder
                .comment("Show current and required hits tracking for after strike in the advanced tooltip")
                .define("Show After Strike Hits Tracker", true);
        TOOLTIP_AFTER_STRIKE_DAMAGE_MODIFIERS = builder
                .comment("Show initial damage modifier and decay factor percentage percentages in the advanced tooltip")
                .define("Show After Strike Damage Modifiers", false);
        builder.pop();

        builder.push("Quick Crafting");
        TOOLTIP_QUICK_CRAFTING_PROPERTIES = builder.comment("Show advanced quick crafting properties")
                .define("Show Quick Crafting Properties", true);
        builder.pop();
        builder.push("Aquatic Grudge");
        TOOLTIP_AQUATIC_GRUDGE_PROPERTIES = builder.comment("Show advanced aquatic grudge properties")
                .define("Show Aquatic Grudge Properties", true);
        builder.pop();
        builder.push("Arthropod Grudge");
        TOOLTIP_ARTHROPOD_GRUDGE_PROPERTIES = builder.comment("Show advanced arthropod grudge properties")
                .define("Show Arthropod Grudge Properties", true);
        builder.pop();
        builder.push("Undead Grudge");
        TOOLTIP_UNDEAD_GRUDGE_PROPERTIES = builder.comment("Show advanced undead grudge properties")
                .define("Show Undead Grudge Properties", true);
        builder.pop();
        builder.push("Traitor Grudge");
        TOOLTIP_TRAITOR_GRUDGE_PROPERTIES = builder.comment("Show advanced traitor grudge properties")
                .define("Show Traitor Grudge Properties", true);
        builder.pop();
        builder.push("Snout Grudge");
        TOOLTIP_SNOUT_GRUDGE_PROPERTIES = builder.comment("Show advanced snout grudge properties")
                .define("Show Snout Grudge Properties", true);
        builder.pop();
        builder.push("Bone Grudge");
        TOOLTIP_BONE_GRUDGE_PROPERTIES = builder.comment("Show advanced bone grudge properties")
                .define("Show Bone Grudge Properties", true);
        builder.pop();
        builder.push("Anti Aerial");
        TOOLTIP_ANTI_AERIAL_PROPERTIES = builder.comment("Show advanced anti aerial properties")
                .define("Show Anti Aerial Properties", true);
        builder.pop();
        builder.push("Rotten Grudge");
        TOOLTIP_ROTTEN_GRUDGE_PROPERTIES = builder.comment("Show advanced rotten grudge properties")
                .define("Show Rotten Grudge Properties", true);
        builder.pop();
        builder.push("Armor Breaker");
        TOOLTIP_ARMOR_BREAKER_PROPERTIES = builder.comment("Show advanced armor breaker properties")
                .define("Show Armor Breaker Properties", true);
        builder.pop();
        builder.push("Blade Breaker");
        TOOLTIP_BLADE_BREAKER_PROPERTIES = builder.comment("Show advanced blade breaker properties")
                .define("Show Blade Breaker Properties", true);
        builder.pop();
        builder.push("Acrobatic Lunge");
        TOOLTIP_ACROBATIC_LUNGE_PROPERTIES = builder.comment("Show advanced acrobatic lunge properties")
                .define("Show Acrobatic Lunge Properties", true);
        builder.pop();
        builder.push("Dexterous Lunge");
        TOOLTIP_DEXTEROUS_LUNGE_PROPERTIES = builder.comment("Show advanced dexterous lunge properties")
                .define("Show Dexterous Lunge Properties", true);
        builder.pop();
        builder.push("Pull Lunge");
        TOOLTIP_PULL_LUNGE_PROPERTIES = builder.comment("Show advanced pull lunge properties")
                .define("Show Pull Lunge Properties", true);
        builder.pop();
        builder.push("Disengage");
        TOOLTIP_DISENGAGE_PROPERTIES = builder.comment("Show advanced disengage properties")
                .define("Show Disengage Properties", true);
        builder.pop();
        builder.push("Disarm");
        TOOLTIP_DISARM_PROPERTIES = builder.comment("Show advanced disarm properties").define("Show Disarm Properties",
                true);
        builder.pop();
        builder.push("Dismount");
        TOOLTIP_DISMOUNT_PROPERTIES = builder.comment("Show advanced dismount properties")
                .define("Show Dismount Properties", true);
        builder.pop();
        builder.push("Disabling Strike");
        TOOLTIP_DISABLING_STRIKE_PROPERTIES = builder.comment("Show advanced disabling strike properties")
                .define("Show Disabling Strike Properties", true);
        builder.pop();
        builder.push("Through Strike");
        TOOLTIP_THROUGH_STRIKE_PROPERTIES = builder.comment("Show advanced through strike properties")
                .define("Show Through Strike Properties", true);
        builder.pop();
        builder.push("Cleansing Strike");
        TOOLTIP_CLEANSING_STRIKE_PROPERTIES = builder.comment("Show advanced cleansing strike properties")
                .define("Show Cleansing Strike Properties", true);
        builder.pop();
        builder.push("Overwhelming Strike");
        TOOLTIP_OVERWHELMING_STRIKE_PROPERTIES = builder.comment("Show advanced overwhelming strike properties")
                .define("Show Overwhelming Strike Properties", true);
        builder.pop();
        builder.push("Suppressing Strike");
        TOOLTIP_SUPPRESSING_STRIKE_PROPERTIES = builder.comment("Show advanced suppressing strike properties")
                .define("Show Suppressing Strike Properties", true);
        builder.pop();
        builder.push("Slash Assault");
        TOOLTIP_SLASH_ASSAULT_PROPERTIES = builder.comment("Show advanced slash assault properties")
                .define("Show Slash Assault Properties", true);
        builder.pop();
        builder.push("Piercing Assault");
        TOOLTIP_PIERCING_ASSAULT_PROPERTIES = builder.comment("Show advanced piercing assault properties")
                .define("Show Piercing Assault Properties", true);
        builder.pop();
        builder.push("Smash Strike");
        TOOLTIP_SMASH_STRIKE_PROPERTIES = builder.comment("Show advanced smash strike properties")
                .define("Show Smash Strike Properties", true);
        builder.pop();
        builder.push("Shock Impact");
        TOOLTIP_SHOCK_IMPACT_PROPERTIES = builder.comment("Show advanced shock impact properties")
                .define("Show Shock Impact Properties", true);
        builder.pop();
        builder.push("Harvest Sweep");
        TOOLTIP_HARVEST_SWEEP_PROPERTIES = builder.comment("Show advanced harvest sweep properties")
                .define("Show Harvest Sweep Properties", true);
        builder.pop();
        builder.push("Wild Sweep");
        TOOLTIP_WILD_SWEEP_PROPERTIES = builder.comment("Show advanced wild sweep properties")
                .define("Show Wild Sweep Properties", true);
        builder.pop();
        builder.push("Whirling Strike");
        TOOLTIP_WHIRLING_STRIKE_PROPERTIES = builder.comment("Show advanced whirling strike properties")
                .define("Show Whirling Strike Properties", true);
        builder.pop();
        builder.push("Fragility");
        TOOLTIP_FRAGILITY_PROPERTIES = builder.comment("Show advanced fragility properties")
                .define("Show Fragility Properties", true);
        builder.pop();
        builder.push("Slippery");
        TOOLTIP_SLIPPERY_PROPERTIES = builder.comment("Show advanced slippery properties")
                .define("Show Slippery Properties", true);
        builder.pop();
        builder.push("Exhausting");
        TOOLTIP_EXHAUSTING_PROPERTIES = builder.comment("Show advanced exhausting properties")
                .define("Show Exhausting Properties", true);
        builder.pop();
        builder.push("Brittle Handle");
        TOOLTIP_BRITTLE_HANDLE_PROPERTIES = builder.comment("Show advanced brittle handle properties")
                .define("Show Brittle Handle Properties", true);
        builder.pop();
        builder.push("Barbed Handle");
        TOOLTIP_BARBED_HANDLE_PROPERTIES = builder.comment("Show advanced Barbed Handle properties")
                .define("Show Barbed Handle Properties", true);
        builder.pop();
        builder.push("Overstrain");
        TOOLTIP_OVERSTRAIN_PROPERTIES = builder.comment("Show advanced overstrain properties")
                .define("Show Overstrain Properties", true);
        builder.pop();
        builder.push("Unstable Edge");
        TOOLTIP_UNSTABLE_EDGE_PROPERTIES = builder.comment("Show advanced unstable edge properties")
                .define("Show Unstable Edge Properties", true);
        builder.pop();
        builder.push("Detonating");
        TOOLTIP_DETONATING_PROPERTIES = builder.comment("Show advanced detonating properties")
                .define("Show Detonating Properties", true);
        builder.pop();
        builder.push("Decapitation");
        TOOLTIP_DECAPITATION_PROPERTIES = builder.comment("Show advanced decapitation properties")
                .define("Show Decapitation Properties", true);
        builder.pop();

        builder.push("Rapid Boost");
        TOOLTIP_RAPID_BOOST_PROPERTIES = builder.comment("Show advanced rapid boost properties")
                .define("Show Rapid Boost Properties", true);
        TOOLTIP_RAPID_BOOST_HITS = builder.comment("Show current hits tracking for rapid boost in the advanced tooltip")
                .define("Show Rapid Boost Hits Tracker", true);
        builder.pop();

        builder.push("Power Boost");
        TOOLTIP_POWER_BOOST_PROPERTIES = builder.comment("Show advanced power boost properties")
                .define("Show Power Boost Properties", true);
        TOOLTIP_POWER_BOOST_HITS = builder.comment("Show current hits tracking for power boost in the advanced tooltip")
                .define("Show Power Boost Hits Tracker", true);
        builder.pop();

        builder.push("Quick Swap");
        TOOLTIP_QUICK_SWAP_PROPERTIES = builder.comment("Show advanced quick swap properties")
                .define("Show Quick Swap Properties", true);
        TOOLTIP_QUICK_SWAP_MODE = builder.comment("Show quick swap activation mode")
                .define("Show Quick Swap Mode", true);
        builder.pop();

        builder.push("Buster Strike");
        TOOLTIP_BUSTER_STRIKE_PROPERTIES = builder.comment("Show advanced buster strike properties")
                .define("Show Buster Strike Properties", true);
        TOOLTIP_BUSTER_STRIKE_HITS = builder
                .comment("Show current and required hits tracking for buster strike in the advanced tooltip")
                .define("Show Buster Strike Hits Tracker", true);
        builder.pop();

        builder.push("Backstab");
        TOOLTIP_BACKSTAB_PROPERTIES = builder.comment("Show advanced backstab properties")
                .define("Show Backstab Properties", true);
        builder.pop();

        builder.push("Guard Stance");
        TOOLTIP_GUARD_STANCE_PROPERTIES = builder.comment("Show advanced guard stance properties")
                .define("Show Guard Stance Properties", true);
        builder.pop();

        builder.push("Parry Guard");
        TOOLTIP_PARRY_GUARD_PROPERTIES = builder.comment("Show advanced parry guard properties")
                .define("Show Parry Guard Properties", true);
        builder.pop();

        builder.push("Heavy Handed");
        TOOLTIP_HEAVY_HANDED_PROPERTIES = builder.comment("Show advanced heavy handed properties")
                .define("Show Heavy Handed Properties", true);
        builder.pop();
        builder.pop();

        builder.push("Projectile Traits Tooltips");

        builder.push("Throwable");
        TOOLTIP_THROWING_PROPERTIES = builder.comment("Show throwing properties section")
                .define("Show Throwing Properties", true);
        TOOLTIP_THROWING_MODE = builder.comment("Show throwing mode section").define("Show Throwing Mode", true);
        builder.pop();

        builder.push("Backstab Shot");
        TOOLTIP_BACKSTAB_SHOT_PROPERTIES = builder.comment("Show advanced backstab shot properties")
                .define("Show Backstab Shot Properties", true);
        builder.pop();

        builder.push("Piercing Shot");
        TOOLTIP_PIERCING_SHOT_PROPERTIES = builder.comment("Show advanced piercing shot properties")
                .define("Show Piercing Shot Properties", true);
        builder.pop();

        builder.push("Disabling Shot");
        TOOLTIP_DISABLING_SHOT_PROPERTIES = builder.comment("Show advanced disabling shot properties")
                .define("Show Disabling Shot Properties", true);
        builder.pop();

        builder.push("Sweeping Shot");
        TOOLTIP_SWEEPING_SHOT_PROPERTIES = builder.comment("Show advanced sweeping shot properties")
                .define("Show Sweeping Shot Properties", true);
        builder.pop();

        builder.push("Disarming Shot");
        TOOLTIP_DISARMING_SHOT_PROPERTIES = builder.comment("Show advanced disarming shot properties")
                .define("Show Disarming Shot Properties", true);
        builder.pop();

        builder.push("Throwback");
        TOOLTIP_THROWBACK_PROPERTIES = builder.comment("Show advanced throwback properties")
                .define("Show Throwback Properties", true);
        builder.pop();

        builder.push("Collector");
        TOOLTIP_COLLECTOR_PROPERTIES = builder.comment("Show advanced collector properties")
                .define("Show Collector Properties", true);
        builder.pop();
        builder.pop();
        builder.pop();
        builder.pop();
    }
}
