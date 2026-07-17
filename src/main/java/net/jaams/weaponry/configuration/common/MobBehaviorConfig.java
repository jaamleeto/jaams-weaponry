package net.jaams.weaponry.configuration.common;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class MobBehaviorConfig {



    public static ForgeConfigSpec.BooleanValue GUN_MOBS_SHOOT;
    public static ForgeConfigSpec.BooleanValue GUN_MOBS_NEED_AMMO;
    public static ForgeConfigSpec.IntValue GUN_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue GUN_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.IntValue GUN_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue GUN_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.DoubleValue GUN_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue GUN_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue GUN_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue GUN_MOB_BEHAVIOR_MIN_SHOOT_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUN_MOB_BEHAVIOR_MELEE_DISTANCE;
    public static ForgeConfigSpec.BooleanValue GUN_MOB_BEHAVIOR_CHECK_CLEAR_SHOT;
    public static ForgeConfigSpec.BooleanValue GUN_MOB_BEHAVIOR_USE_GUN_BASE_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue GUN_MOB_BEHAVIOR_GUN_BASE_COOLDOWN_MULTIPLIER;

    public static ForgeConfigSpec.IntValue THROWABLE_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue THROWABLE_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.IntValue THROWABLE_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue THROWABLE_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.DoubleValue THROWABLE_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue THROWABLE_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue THROWABLE_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue THROWABLE_MOB_BEHAVIOR_MIN_THROW_DISTANCE;
    public static ForgeConfigSpec.DoubleValue THROWABLE_MOB_BEHAVIOR_MELEE_DISTANCE;
    public static ForgeConfigSpec.BooleanValue THROWABLE_MOB_BEHAVIOR_CHECK_CLEAR_SHOT;
    public static ForgeConfigSpec.DoubleValue THROWABLE_MOB_BEHAVIOR_CRITICAL_CHANCE;
    public static ForgeConfigSpec.DoubleValue THROWABLE_MOB_BEHAVIOR_INACCURACY_MULTIPLIER;
    public static ForgeConfigSpec.BooleanValue THROWABLE_MOBS_THROW;



    public static ForgeConfigSpec.BooleanValue QUICK_SWAP_MOBS_ENABLED;
    public static ForgeConfigSpec.IntValue QUICK_SWAP_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue QUICK_SWAP_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS;



    public static ForgeConfigSpec.BooleanValue SMOKE_BOMB_MOBS_USE;
    public static ForgeConfigSpec.IntValue SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.IntValue SMOKE_BOMB_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue SMOKE_BOMB_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.DoubleValue SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SMOKE_BOMB_MOB_BEHAVIOR_EFFECT_RADIUS;
    public static ForgeConfigSpec.DoubleValue SMOKE_BOMB_MOB_BEHAVIOR_USE_PROBABILITY;

    public static ForgeConfigSpec.BooleanValue SLINGSHOT_MOBS_SHOOT;
    public static ForgeConfigSpec.BooleanValue SLINGSHOT_MOBS_NEED_AMMO;
    public static ForgeConfigSpec.IntValue SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.IntValue SLINGSHOT_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS;
    public static ForgeConfigSpec.IntValue SLINGSHOT_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS;
    public static ForgeConfigSpec.DoubleValue SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SLINGSHOT_MOB_BEHAVIOR_MIN_SHOOT_DISTANCE;
    public static ForgeConfigSpec.DoubleValue SLINGSHOT_MOB_BEHAVIOR_MELEE_DISTANCE;
    public static ForgeConfigSpec.BooleanValue SLINGSHOT_MOB_BEHAVIOR_CHECK_CLEAR_SHOT;
    public static ForgeConfigSpec.DoubleValue SLINGSHOT_MOB_BEHAVIOR_INACCURACY_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SLINGSHOT_MOB_BEHAVIOR_THROW_PROBABILITY;
    public static ForgeConfigSpec.ConfigValue<String> SLINGSHOT_DEFAULT_AMMO;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> SLINGSHOT_MOB_AMMO_OVERRIDES;

    public static void register(ForgeConfigSpec.Builder builder) {
        builder.push("Mob Behaviors Handler");


        builder.push("Mob Gun Shoot Behavior");
        GUN_MOBS_SHOOT = builder.comment("Allow mobs to shoot guns when equipped").define("Mobs Shoot Guns", true);
        GUN_MOBS_NEED_AMMO = builder.comment("Require mobs to have ammunition to shoot guns").define("Mobs Need Ammo", false);
        GUN_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS = builder
                .comment(
                        "Minimum cooldown (in ticks) between actions. Each shot picks a random value between Min and Max (20 ticks = 1 second).")
                .defineInRange("Gun Cooldown Min Ticks", 40, 5, 1200);
        GUN_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS = builder
                .comment(
                        "Maximum cooldown (in ticks) between actions. Each shot picks a random value between Min and Max.")
                .defineInRange("Gun Cooldown Max Ticks", 80, 5, 1200);
        GUN_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS = builder
                .comment("Minimum initial cooldown (in ticks) before first shot after acquiring a target")
                .defineInRange("Gun Initial Cooldown Min Ticks", 40, 0, 800);
        GUN_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS = builder
                .comment("Maximum initial cooldown (in ticks) before first shot after acquiring a target")
                .defineInRange("Gun Initial Cooldown Max Ticks", 120, 0, 800);
        GUN_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Easy difficulty (higher = slower shots)")
                        .defineInRange("Gun Cooldown Easy Multiplier", 3.5, 0.1, 5.0);
        GUN_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Normal difficulty")
                        .defineInRange("Gun Cooldown Normal Multiplier", 2.5, 0.1, 5.0);
        GUN_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Hard difficulty (lower = faster shots)")
                        .defineInRange("Gun Cooldown Hard Multiplier", 1.5, 0.1, 5.0);
        GUN_MOB_BEHAVIOR_MIN_SHOOT_DISTANCE = builder
                .comment(
                        "Minimum distance (in blocks) for the mob to shoot. If the target is closer, the mob will prefer melee if within melee range, or wait/advance. Set to 0 to disable.")
                .defineInRange("Gun Min Shoot Distance", 3.0, 0.0, 10.0);
        GUN_MOB_BEHAVIOR_MELEE_DISTANCE = builder
                .comment(
                        "Distance (in blocks) at which the mob will perform a melee attack with the gun instead of shooting.")
                .defineInRange("Gun Melee Distance", 2.5, 1.0, 5.0);
        GUN_MOB_BEHAVIOR_CHECK_CLEAR_SHOT = builder
                .comment(
                        "If true, mobs will raytrace from their eyes to the target's center and only shoot if no solid blocks are in the way. If false, uses the vanilla line-of-sight check.")
                .define("Gun Check Clear Shot", true);
        GUN_MOB_BEHAVIOR_USE_GUN_BASE_COOLDOWN = builder
                .comment(
                        "If true, mobs will use the gun's base cooldown (from Gun System config) instead of the flat mob behavior cooldown. "
                                + "This means pistols fire faster than shotguns, matching each gun's intended fire rate.")
                .define("Gun Use Base Cooldown", false);
        GUN_MOB_BEHAVIOR_GUN_BASE_COOLDOWN_MULTIPLIER = builder
                .comment(
                        "Multiplier applied to the gun's base cooldown when 'Gun Use Base Cooldown' is true. "
                                + "1.0 = same fire rate as players. Higher = slower for mobs.")
                .defineInRange("Gun Base Cooldown Multiplier", 2.0, 0.1, 10.0);
        builder.pop();


        builder.push("Mob Throwable Behavior");
        THROWABLE_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS = builder
                .comment(
                        "Minimum cooldown (in ticks) between throws. Each throw picks a random value between Min and Max (20 ticks = 1 second).")
                .defineInRange("Throwable Cooldown Min Ticks", 60, 5, 1200);
        THROWABLE_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS = builder
                .comment(
                        "Maximum cooldown (in ticks) between throws. Each throw picks a random value between Min and Max.")
                .defineInRange("Throwable Cooldown Max Ticks", 120, 5, 1200);
        THROWABLE_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS = builder
                .comment("Minimum initial cooldown (in ticks) before first throw after acquiring a target")
                .defineInRange("Throwable Initial Cooldown Min Ticks", 40, 0, 800);
        THROWABLE_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS = builder
                .comment("Maximum initial cooldown (in ticks) before first throw after acquiring a target")
                .defineInRange("Throwable Initial Cooldown Max Ticks", 120, 0, 800);
        THROWABLE_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Easy difficulty (higher = slower throws)")
                        .defineInRange("Throwable Cooldown Easy Multiplier", 3.5, 0.1, 5.0);
        THROWABLE_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Normal difficulty")
                        .defineInRange("Throwable Cooldown Normal Multiplier", 2.5, 0.1, 5.0);
        THROWABLE_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Hard difficulty (lower = faster throws)")
                        .defineInRange("Throwable Cooldown Hard Multiplier", 1.5, 0.1, 5.0);
        THROWABLE_MOB_BEHAVIOR_MIN_THROW_DISTANCE = builder
                .comment(
                        "Minimum distance (in blocks) for the mob to throw. If the target is closer, the mob will prefer melee if within melee range, or wait/advance. Set to 0 to disable.")
                .defineInRange("Throwable Min Throw Distance", 3.0, 0.0, 10.0);
        THROWABLE_MOB_BEHAVIOR_MELEE_DISTANCE = builder
                .comment(
                        "Distance (in blocks) at which the mob will perform a melee attack with the throwable instead of throwing.")
                .defineInRange("Throwable Melee Distance", 2.5, 1.0, 5.0);
        THROWABLE_MOB_BEHAVIOR_CHECK_CLEAR_SHOT = builder
                .comment(
                        "If true, mobs will raytrace from their eyes to the target's center and only throw if no solid blocks are in the way. If false, uses the vanilla line-of-sight check.")
                .define("Throwable Check Clear Shot", true);
        THROWABLE_MOB_BEHAVIOR_CRITICAL_CHANCE = builder
                .comment(
                        "Probability (0.0 to 1.0) that a mob's throw will be a critical hit (deals extra damage). 0 = never critical, 1 = always critical.")
                .defineInRange("Throwable Critical Chance", 0.15, 0.0, 1.0);
        THROWABLE_MOB_BEHAVIOR_INACCURACY_MULTIPLIER = builder
                .comment(
                        "Multiplier for the throwable's inaccuracy when thrown by a mob. "
                                + "1.0 = normal accuracy (as defined by the item). "
                                + "Higher values = less accurate (more spread). "
                                + "0.0 = perfect aim.")
                .defineInRange("Throwable Inaccuracy Multiplier", 1.0, 0.0, 10.0);
        THROWABLE_MOBS_THROW = builder.comment("Allow mobs to throw throwable items when equipped")
                .define("Mobs Throw Throwables", true);
        builder.pop();


        builder.push("Mob Quick Swap Behavior");
        QUICK_SWAP_MOBS_ENABLED = builder.comment("Allow mobs to use quick swap when equipped").define("Mobs Use Quick Swap", true);
        QUICK_SWAP_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS = builder
                .comment("Minimum initial cooldown (in ticks) before first quick swap after acquiring a target")
                .defineInRange("Quick Swap Initial Cooldown Min Ticks", 60, 0, 800);
        QUICK_SWAP_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS = builder
                .comment("Maximum initial cooldown (in ticks) before first quick swap after acquiring a target")
                .defineInRange("Quick Swap Initial Cooldown Max Ticks", 180, 0, 800);
        builder.pop();


        builder.push("Mob Smoke Bomb Behavior");
        SMOKE_BOMB_MOBS_USE = builder.comment("Allow mobs to use smoke bombs when equipped")
                .define("Mobs Use Smoke Bombs", true);
        SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS = builder
                .comment("Minimum cooldown (in ticks) between smoke bomb uses. Each use picks a random value between Min and Max (20 ticks = 1 second).")
                .defineInRange("Smoke Bomb Cooldown Min Ticks", 200, 5, 2400);
        SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS = builder
                .comment("Maximum cooldown (in ticks) between smoke bomb uses. Each use picks a random value between Min and Max.")
                .defineInRange("Smoke Bomb Cooldown Max Ticks", 400, 5, 2400);
        SMOKE_BOMB_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS = builder
                .comment("Minimum initial cooldown (in ticks) before first smoke bomb use after acquiring a target")
                .defineInRange("Smoke Bomb Initial Cooldown Min Ticks", 80, 0, 800);
        SMOKE_BOMB_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS = builder
                .comment("Maximum initial cooldown (in ticks) before first smoke bomb use after acquiring a target")
                .defineInRange("Smoke Bomb Initial Cooldown Max Ticks", 200, 0, 800);
        SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Easy difficulty (higher = slower uses)")
                        .defineInRange("Smoke Bomb Cooldown Easy Multiplier", 3.5, 0.1, 5.0);
        SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Normal difficulty")
                        .defineInRange("Smoke Bomb Cooldown Normal Multiplier", 2.5, 0.1, 5.0);
        SMOKE_BOMB_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Hard difficulty (lower = faster uses)")
                        .defineInRange("Smoke Bomb Cooldown Hard Multiplier", 1.5, 0.1, 5.0);
        SMOKE_BOMB_MOB_BEHAVIOR_EFFECT_RADIUS = builder
                .comment("Radius (in blocks) in which the smoke bomb applies effects to entities")
                .defineInRange("Smoke Bomb Effect Radius", 3.0, 1.0, 10.0);
        SMOKE_BOMB_MOB_BEHAVIOR_USE_PROBABILITY = builder
                .comment("Probability (0.0 to 1.0) that the mob will attempt to use the smoke bomb when all conditions are met. 1.0 = always, 0.75 = 75%.")
                .defineInRange("Smoke Bomb Use Probability", 0.85, 0.0, 1.0);
        builder.pop();

        builder.push("Mob Slingshot Behavior");
        SLINGSHOT_MOBS_SHOOT = builder.comment("Allow mobs to use slingshots when equipped").define("Mobs Use Slingshots", true);
        SLINGSHOT_MOBS_NEED_AMMO = builder
                .comment("If true, mobs require valid ammo in their offhand to shoot. If false, mobs will use the default ammo from config.")
                .define("Mobs Need Ammo", false);
        SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_MIN_TICKS = builder
                .comment("Minimum cooldown (in ticks) between shots. Each shot picks a random value between Min and Max (20 ticks = 1 second).")
                .defineInRange("Slingshot Cooldown Min Ticks", 60, 5, 1200);
        SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_MAX_TICKS = builder
                .comment("Maximum cooldown (in ticks) between shots. Each shot picks a random value between Min and Max.")
                .defineInRange("Slingshot Cooldown Max Ticks", 120, 5, 1200);
        SLINGSHOT_MOB_BEHAVIOR_INITIAL_COOLDOWN_MIN_TICKS = builder
                .comment("Minimum initial cooldown (in ticks) before first shot after acquiring a target")
                .defineInRange("Slingshot Initial Cooldown Min Ticks", 40, 0, 800);
        SLINGSHOT_MOB_BEHAVIOR_INITIAL_COOLDOWN_MAX_TICKS = builder
                .comment("Maximum initial cooldown (in ticks) before first shot after acquiring a target")
                .defineInRange("Slingshot Initial Cooldown Max Ticks", 120, 0, 800);
        SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_EASY_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Easy difficulty (higher = slower shots)")
                        .defineInRange("Slingshot Cooldown Easy Multiplier", 3.5, 0.1, 5.0);
        SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_NORMAL_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Normal difficulty")
                        .defineInRange("Slingshot Cooldown Normal Multiplier", 2.5, 0.1, 5.0);
        SLINGSHOT_MOB_BEHAVIOR_COOLDOWN_HARD_MULTIPLIER = builder
                        .comment("Cooldown multiplier on Hard difficulty (lower = faster shots)")
                        .defineInRange("Slingshot Cooldown Hard Multiplier", 1.5, 0.1, 5.0);
        SLINGSHOT_MOB_BEHAVIOR_MIN_SHOOT_DISTANCE = builder
                .comment("Minimum distance (in blocks) for the mob to shoot. If the target is closer, the mob will prefer melee if within melee range, or wait/advance. Set to 0 to disable.")
                .defineInRange("Slingshot Min Shoot Distance", 2.0, 0.0, 10.0);
        SLINGSHOT_MOB_BEHAVIOR_MELEE_DISTANCE = builder
                .comment("Distance (in blocks) at which the mob will perform a melee attack with the slingshot instead of shooting.")
                .defineInRange("Slingshot Melee Distance", 2.5, 1.0, 5.0);
        SLINGSHOT_MOB_BEHAVIOR_CHECK_CLEAR_SHOT = builder
                .comment("If true, mobs will raytrace from their eyes to the target's center and only shoot if no solid blocks are in the way. If false, uses the vanilla line-of-sight check.")
                .define("Slingshot Check Clear Shot", true);
        SLINGSHOT_MOB_BEHAVIOR_INACCURACY_MULTIPLIER = builder
                .comment("Multiplier for the slingshot's inaccuracy when used by a mob. "
                        + "1.0 = normal accuracy (as defined by the item). "
                        + "Higher values = less accurate (more spread). "
                        + "0.0 = perfect aim.")
                .defineInRange("Slingshot Inaccuracy Multiplier", 1.0, 0.0, 10.0);
        SLINGSHOT_MOB_BEHAVIOR_THROW_PROBABILITY = builder
                .comment("Probability (0.0 to 1.0) that the mob will attempt to shoot when all conditions are met. 1.0 = always, 0.75 = 75%.")
                .defineInRange("Slingshot Throw Probability", 0.75, 0.0, 1.0);
        SLINGSHOT_DEFAULT_AMMO = builder
                .comment("Default ammo item for mobs that don't have ammo in their offhand. Format: 'modid:item'.")
                .define("Default Ammo", "minecraft:oak_planks");
        SLINGSHOT_MOB_AMMO_OVERRIDES = builder
                .comment("List of mob-type=ammo overrides for default slingshot ammo. "
                        + "Format: 'mob_registry_name=item_registry_name'. "
                        + "Examples: 'minecraft:piglin=minecraft:blackstone', "
                        + "'minecraft:stray=minecraft:blue_ice'.")
                .defineList("Mob Ammo Overrides",
                        java.util.Arrays.asList(
                                "minecraft:piglin=minecraft:netherrack",
                                "minecraft:piglin_brute=minecraft:blackstone",
                                "minecraft:zombified_piglin=minecraft:blackstone",
                                "minecraft:stray=minecraft:blue_ice"),
                        obj -> obj instanceof String);

        builder.pop();
    }
}
