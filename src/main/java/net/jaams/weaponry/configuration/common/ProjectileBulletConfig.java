package net.jaams.weaponry.configuration.common;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ProjectileBulletConfig {

    public static ForgeConfigSpec.DoubleValue BULLET_PROJECTILE_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue BULLET_PROJECTILE_BASE_KNOCKBACK;
    public static ForgeConfigSpec.IntValue BULLET_PROJECTILE_PIERCING_LEVEL;
    public static ForgeConfigSpec.IntValue BULLET_PROJECTILE_IGNORE_TICKS;
    public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_ALLOW_CRITICALS;
    public static ForgeConfigSpec.DoubleValue BULLET_PROJECTILE_WATER_INERTIA;
    public static ForgeConfigSpec.DoubleValue BULLET_PROJECTILE_HITBOX_WIDTH;
    public static ForgeConfigSpec.DoubleValue BULLET_PROJECTILE_HITBOX_HEIGHT;
    public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT;
    public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_DISABLE_SHIELD;
    public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED;
    public static ForgeConfigSpec.IntValue BULLET_PROJECTILE_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.IntValue BULLET_PROJECTILE_MAX_TICKS_IN_AIR;
    public static ForgeConfigSpec.IntValue BULLET_PROJECTILE_MAX_TICKS_IN_GROUND;
    public static ForgeConfigSpec.ConfigValue<String> BULLET_PROJECTILE_COLOR;
    public static ForgeConfigSpec.IntValue BULLET_PROJECTILE_NO_GRAVITY_DURATION;
    public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_INITIAL_NO_GRAVITY;

    public static ForgeConfigSpec.DoubleValue FIRE_BULLET_PROJECTILE_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue FIRE_BULLET_PROJECTILE_BASE_KNOCKBACK;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_PIERCING_LEVEL;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_IGNORE_TICKS;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_ALLOW_CRITICALS;
    public static ForgeConfigSpec.DoubleValue FIRE_BULLET_PROJECTILE_WATER_INERTIA;
    public static ForgeConfigSpec.DoubleValue FIRE_BULLET_PROJECTILE_HITBOX_WIDTH;
    public static ForgeConfigSpec.DoubleValue FIRE_BULLET_PROJECTILE_HITBOX_HEIGHT;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_DISABLE_SHIELD;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_MAX_TICKS_IN_AIR;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND;
    public static ForgeConfigSpec.ConfigValue<String> FIRE_BULLET_PROJECTILE_COLOR;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_NO_GRAVITY_DURATION;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_INITIAL_NO_GRAVITY;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_IGNITE_BLOCKS;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_IGNITE_FLAMMABLE_ONLY;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_CAN_LIGHT_SPECIAL;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_IGNITE_BLOCK_DURATION;
    public static ForgeConfigSpec.IntValue FIRE_BULLET_PROJECTILE_SET_ON_FIRE_SECONDS;
    public static ForgeConfigSpec.BooleanValue FIRE_BULLET_PROJECTILE_SHOW_LAVA_PARTICLES;

    public static ForgeConfigSpec.DoubleValue HEAVY_BULLET_PROJECTILE_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue HEAVY_BULLET_PROJECTILE_BASE_KNOCKBACK;
    public static ForgeConfigSpec.IntValue HEAVY_BULLET_PROJECTILE_PIERCING_LEVEL;
    public static ForgeConfigSpec.IntValue HEAVY_BULLET_PROJECTILE_IGNORE_TICKS;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_ALLOW_CRITICALS;
    public static ForgeConfigSpec.DoubleValue HEAVY_BULLET_PROJECTILE_WATER_INERTIA;
    public static ForgeConfigSpec.DoubleValue HEAVY_BULLET_PROJECTILE_HITBOX_WIDTH;
    public static ForgeConfigSpec.DoubleValue HEAVY_BULLET_PROJECTILE_HITBOX_HEIGHT;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_DISABLE_SHIELD;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED;
    public static ForgeConfigSpec.IntValue HEAVY_BULLET_PROJECTILE_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.IntValue HEAVY_BULLET_PROJECTILE_MAX_TICKS_IN_AIR;
    public static ForgeConfigSpec.IntValue HEAVY_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND;
    public static ForgeConfigSpec.ConfigValue<String> HEAVY_BULLET_PROJECTILE_COLOR;
    public static ForgeConfigSpec.IntValue HEAVY_BULLET_PROJECTILE_NO_GRAVITY_DURATION;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_INITIAL_NO_GRAVITY;
    public static ForgeConfigSpec.DoubleValue HEAVY_BULLET_PROJECTILE_KNOCKED_OUT_CHANCE;
    public static ForgeConfigSpec.IntValue HEAVY_BULLET_PROJECTILE_KNOCKED_OUT_DURATION;
    public static ForgeConfigSpec.BooleanValue HEAVY_BULLET_PROJECTILE_SHOW_IRON_PARTICLES;

    public static ForgeConfigSpec.DoubleValue GLOWING_BULLET_PROJECTILE_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue GLOWING_BULLET_PROJECTILE_BASE_KNOCKBACK;
    public static ForgeConfigSpec.IntValue GLOWING_BULLET_PROJECTILE_PIERCING_LEVEL;
    public static ForgeConfigSpec.IntValue GLOWING_BULLET_PROJECTILE_IGNORE_TICKS;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_ALLOW_CRITICALS;
    public static ForgeConfigSpec.DoubleValue GLOWING_BULLET_PROJECTILE_WATER_INERTIA;
    public static ForgeConfigSpec.DoubleValue GLOWING_BULLET_PROJECTILE_HITBOX_WIDTH;
    public static ForgeConfigSpec.DoubleValue GLOWING_BULLET_PROJECTILE_HITBOX_HEIGHT;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_DISABLE_SHIELD;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED;
    public static ForgeConfigSpec.IntValue GLOWING_BULLET_PROJECTILE_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.IntValue GLOWING_BULLET_PROJECTILE_MAX_TICKS_IN_AIR;
    public static ForgeConfigSpec.IntValue GLOWING_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND;
    public static ForgeConfigSpec.ConfigValue<String> GLOWING_BULLET_PROJECTILE_COLOR;
    public static ForgeConfigSpec.IntValue GLOWING_BULLET_PROJECTILE_NO_GRAVITY_DURATION;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_INITIAL_NO_GRAVITY;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_GLOWING;
    public static ForgeConfigSpec.IntValue GLOWING_BULLET_PROJECTILE_GLOW_DURATION;
    public static ForgeConfigSpec.DoubleValue GLOWING_BULLET_PROJECTILE_MAGIC_DAMAGE_CHANCE;
    public static ForgeConfigSpec.DoubleValue GLOWING_BULLET_PROJECTILE_MAGIC_DAMAGE;
    public static ForgeConfigSpec.BooleanValue GLOWING_BULLET_PROJECTILE_SHOW_AMETHYST_PARTICLES;

    public static ForgeConfigSpec.DoubleValue SHARP_BULLET_PROJECTILE_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue SHARP_BULLET_PROJECTILE_BASE_KNOCKBACK;
    public static ForgeConfigSpec.IntValue SHARP_BULLET_PROJECTILE_PIERCING_LEVEL;
    public static ForgeConfigSpec.IntValue SHARP_BULLET_PROJECTILE_IGNORE_TICKS;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_ALLOW_CRITICALS;
    public static ForgeConfigSpec.DoubleValue SHARP_BULLET_PROJECTILE_WATER_INERTIA;
    public static ForgeConfigSpec.DoubleValue SHARP_BULLET_PROJECTILE_HITBOX_WIDTH;
    public static ForgeConfigSpec.DoubleValue SHARP_BULLET_PROJECTILE_HITBOX_HEIGHT;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_DISABLE_SHIELD;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED;
    public static ForgeConfigSpec.IntValue SHARP_BULLET_PROJECTILE_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.IntValue SHARP_BULLET_PROJECTILE_MAX_TICKS_IN_AIR;
    public static ForgeConfigSpec.IntValue SHARP_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND;
    public static ForgeConfigSpec.ConfigValue<String> SHARP_BULLET_PROJECTILE_COLOR;
    public static ForgeConfigSpec.IntValue SHARP_BULLET_PROJECTILE_NO_GRAVITY_DURATION;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_INITIAL_NO_GRAVITY;
    public static ForgeConfigSpec.DoubleValue SHARP_BULLET_PROJECTILE_BYPASS_ARMOR_CHANCE;
    public static ForgeConfigSpec.DoubleValue SHARP_BULLET_PROJECTILE_BYPASS_ARMOR_DAMAGE;
    public static ForgeConfigSpec.BooleanValue SHARP_BULLET_PROJECTILE_SHOW_PRISMARINE_PARTICLES;

    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_BASE_KNOCKBACK;
    public static ForgeConfigSpec.IntValue ECHO_BULLET_PROJECTILE_PIERCING_LEVEL;
    public static ForgeConfigSpec.IntValue ECHO_BULLET_PROJECTILE_IGNORE_TICKS;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_ALLOW_CRITICALS;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_WATER_INERTIA;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_HITBOX_WIDTH;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_HITBOX_HEIGHT;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_DISABLE_SHIELD;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED;
    public static ForgeConfigSpec.IntValue ECHO_BULLET_PROJECTILE_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS;
    public static ForgeConfigSpec.IntValue ECHO_BULLET_PROJECTILE_MAX_TICKS_IN_AIR;
    public static ForgeConfigSpec.IntValue ECHO_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND;
    public static ForgeConfigSpec.ConfigValue<String> ECHO_BULLET_PROJECTILE_COLOR;
    public static ForgeConfigSpec.IntValue ECHO_BULLET_PROJECTILE_NO_GRAVITY_DURATION;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_INITIAL_NO_GRAVITY;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_HOMING;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_HOMING_SPEED;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_SEARCH_RANGE;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_SHOW_ECHO_SHARD_PARTICLES;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ECHO_BULLET_PROJECTILE_IGNORED_ENTITIES;
    public static ForgeConfigSpec.BooleanValue ECHO_BULLET_PROJECTILE_PLAY_HOMING_SOUND;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_TARGETING_OWNER_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_HOSTILE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_GLOWING_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_LOS_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue ECHO_BULLET_PROJECTILE_HEIGHT_PENALTY;

    public static void register(ForgeConfigSpec.Builder builder) {

        builder.push("Bullet Projectile");
        BULLET_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Bullet projectile")
                .defineInRange("Base Damage", 5.0, 0.5, 64.0);
        BULLET_PROJECTILE_BASE_KNOCKBACK = builder
                .comment("Base knockback strength applied to hit entities by the Bullet projectile")
                .defineInRange("Base Knockback", 0.15, 0.0, 4.0);
        BULLET_PROJECTILE_PIERCING_LEVEL = builder
                .comment("Number of additional entities the Bullet projectile can pierce through")
                .defineInRange("Piercing Level", 0, 0, 50);
        BULLET_PROJECTILE_IGNORE_TICKS = builder
                .comment("Initial ticks during which the Bullet projectile ignores collisions")
                .defineInRange("Ignore Ticks", 2, 0, 200);
        BULLET_PROJECTILE_ALLOW_CRITICALS = builder.comment("Whether the Bullet projectile can deal critical hits")
                .define("Critical Hits", true);
        BULLET_PROJECTILE_WATER_INERTIA = builder
                .comment("Water inertia factor for the Bullet projectile (lower = more slowdown)")
                .defineInRange("Water Inertia", 0.95, 0.0, 1.0);
        BULLET_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Bullet projectile")
                .defineInRange("Hitbox Width", 0.15, 0.05, 1.0);
        BULLET_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Bullet projectile")
                .defineInRange("Hitbox Height", 0.15, 0.05, 1.0);
        BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT = builder
                .comment("Whether the Bullet projectile damages weapon durability when hitting an entity")
                .define("Break On Entity Hit", true);
        BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT = builder
                .comment("Whether the Bullet projectile damages weapon durability when hitting a block")
                .define("Break On Block Hit", true);
        BULLET_PROJECTILE_DISABLE_SHIELD = builder.comment("Whether the Bullet projectile can disable shields")
                .define("Disable Shield", false);
        BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED = builder
                .comment("Whether the weapon breaks when the Bullet projectile piercing level is exhausted")
                .define("Break On Piercing Exhausted", true);
        BULLET_PROJECTILE_MAX_BLOCK_BREAKS = builder.comment("Maximum number of blocks the Bullet projectile can break")
                .defineInRange("Max Block Breaks", 3, 0, 64);
        BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS = builder
                .comment("Whether the weapon breaks after the Bullet projectile reaches max block breaks")
                .define("Break After Max Block Breaks", true);
        BULLET_PROJECTILE_MAX_TICKS_IN_AIR = builder
                .comment("Maximum ticks the Bullet projectile can exist while flying in air (0 = no limit)")
                .defineInRange("Max Ticks In Air", 100, 0, 800);
        BULLET_PROJECTILE_MAX_TICKS_IN_GROUND = builder
                .comment("Maximum ticks the Bullet projectile stays stuck in ground (0 = no limit)")
                .defineInRange("Max Ticks In Ground", 100, 0, 800);
        BULLET_PROJECTILE_COLOR = builder
                .comment("Color of the Bullet projectile in hexadecimal (examples: 0xFFFFD700, #FFFFD700 or FFFFD700)")
                .define("Color", "0xFFFFD700");
        BULLET_PROJECTILE_NO_GRAVITY_DURATION = builder
                .comment("Ticks the Bullet projectile ignores gravity (0 = always affected by gravity)")
                .defineInRange("No Gravity Duration", 60, 0, 1200);
        BULLET_PROJECTILE_INITIAL_NO_GRAVITY = builder.comment("Whether the Bullet projectile starts with no gravity")
                .define("Initial No Gravity", true);
        builder.pop();

        builder.push("Fire Bullet Projectile");
        FIRE_BULLET_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Fire Bullet projectile")
                .defineInRange("Base Damage", 4.0, 0.5, 64.0);
        FIRE_BULLET_PROJECTILE_BASE_KNOCKBACK = builder
                .comment("Base knockback strength applied to hit entities by the Fire Bullet projectile")
                .defineInRange("Base Knockback", 0.10, 0.0, 4.0);
        FIRE_BULLET_PROJECTILE_PIERCING_LEVEL = builder
                .comment("Number of additional entities the Fire Bullet projectile can pierce through")
                .defineInRange("Piercing Level", 0, 0, 50);
        FIRE_BULLET_PROJECTILE_IGNORE_TICKS = builder
                .comment("Initial ticks during which the Fire Bullet projectile ignores collisions")
                .defineInRange("Ignore Ticks", 2, 0, 200);
        FIRE_BULLET_PROJECTILE_ALLOW_CRITICALS = builder
                .comment("Whether the Fire Bullet projectile can deal critical hits").define("Critical Hits", true);
        FIRE_BULLET_PROJECTILE_WATER_INERTIA = builder
                .comment("Water inertia factor for the Fire Bullet projectile (lower = more slowdown)")
                .defineInRange("Water Inertia", 0.60, 0.0, 1.0);
        FIRE_BULLET_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Fire Bullet projectile")
                .defineInRange("Hitbox Width", 0.18, 0.05, 1.0);
        FIRE_BULLET_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Fire Bullet projectile")
                .defineInRange("Hitbox Height", 0.18, 0.05, 1.0);
        FIRE_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT = builder
                .comment("Whether the Fire Bullet projectile damages weapon durability when hitting an entity")
                .define("Break On Entity Hit", true);
        FIRE_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT = builder
                .comment("Whether the Fire Bullet projectile damages weapon durability when hitting a block")
                .define("Break On Block Hit", true);
        FIRE_BULLET_PROJECTILE_DISABLE_SHIELD = builder
                .comment("Whether the Fire Bullet projectile can disable shields").define("Disable Shield", false);
        FIRE_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED = builder
                .comment("Whether the weapon breaks when the Fire Bullet projectile piercing level is exhausted")
                .define("Break On Piercing Exhausted", true);
        FIRE_BULLET_PROJECTILE_MAX_BLOCK_BREAKS = builder
                .comment("Maximum number of blocks the Fire Bullet projectile can break")
                .defineInRange("Max Block Breaks", 1, 0, 64);
        FIRE_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS = builder
                .comment("Whether the weapon breaks after the Fire Bullet projectile reaches max block breaks")
                .define("Break After Max Block Breaks", true);
        FIRE_BULLET_PROJECTILE_MAX_TICKS_IN_AIR = builder
                .comment("Maximum ticks the Fire Bullet projectile can exist while flying in air (0 = no limit)")
                .defineInRange("Max Ticks In Air", 100, 0, 800);
        FIRE_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND = builder
                .comment("Maximum ticks the Fire Bullet projectile stays stuck in ground (0 = no limit)")
                .defineInRange("Max Ticks In Ground", 100, 0, 800);
        FIRE_BULLET_PROJECTILE_COLOR = builder.comment(
                "Color of the Fire Bullet projectile in hexadecimal (examples: 0xFFFF4500, #FFFF4500 or FFFF4500)")
                .define("Color", "0xFFFF4500");
        FIRE_BULLET_PROJECTILE_NO_GRAVITY_DURATION = builder
                .comment("Ticks the Fire Bullet projectile ignores gravity (0 = always affected by gravity)")
                .defineInRange("No Gravity Duration", 60, 0, 1200);
        FIRE_BULLET_PROJECTILE_INITIAL_NO_GRAVITY = builder
                .comment("Whether the Fire Bullet projectile starts with no gravity")
                .define("Initial No Gravity", true);
        FIRE_BULLET_PROJECTILE_IGNITE_BLOCKS = builder.comment("Whether the fire bullet ignites blocks on impact")
                .define("Ignite Blocks", true);
        FIRE_BULLET_PROJECTILE_IGNITE_FLAMMABLE_ONLY = builder
                .comment("If true, only ignites flammable blocks. If false, can place fire on any block")
                .define("Ignite Flammable Only", true);
        FIRE_BULLET_PROJECTILE_CAN_LIGHT_SPECIAL = builder
                .comment("Whether the fire bullet can light special blocks like campfires, candles and candle cakes")
                .define("Can Light Special Blocks", true);
        FIRE_BULLET_PROJECTILE_IGNITE_BLOCK_DURATION = builder.comment(
                "How long (in ticks) the fire lasts on ignited blocks. 20 ticks = 1 second. (0 = default fire behavior)")
                .defineInRange("Ignite Block Duration", 80, 0, 600);
        FIRE_BULLET_PROJECTILE_SET_ON_FIRE_SECONDS = builder
                .comment("How many seconds the hit entity will be set on fire (0 = no fire)")
                .defineInRange("Set Entity On Fire Seconds", 8, 0, 60);
        FIRE_BULLET_PROJECTILE_SHOW_LAVA_PARTICLES = builder
                .comment("Whether to show lava particles when hitting entities or blocks")
                .define("Show Lava Particles", true);
        builder.pop();

        builder.push("Heavy Bullet Projectile");
        HEAVY_BULLET_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Heavy Bullet projectile")
                .defineInRange("Base Damage", 10.0, 0.5, 100.0);
        HEAVY_BULLET_PROJECTILE_BASE_KNOCKBACK = builder
                .comment("Base knockback strength applied to hit entities by the Heavy Bullet projectile")
                .defineInRange("Base Knockback", 0.45, 0.0, 6.0);
        HEAVY_BULLET_PROJECTILE_PIERCING_LEVEL = builder
                .comment("Number of additional entities the Heavy Bullet projectile can pierce through")
                .defineInRange("Piercing Level", 0, 0, 20);
        HEAVY_BULLET_PROJECTILE_IGNORE_TICKS = builder
                .comment("Initial ticks during which the Heavy Bullet projectile ignores collisions")
                .defineInRange("Ignore Ticks", 3, 0, 200);
        HEAVY_BULLET_PROJECTILE_ALLOW_CRITICALS = builder
                .comment("Whether the Heavy Bullet projectile can deal critical hits").define("Critical Hits", true);
        HEAVY_BULLET_PROJECTILE_WATER_INERTIA = builder
                .comment("Water inertia factor for the Heavy Bullet projectile (lower = more slowdown)")
                .defineInRange("Water Inertia", 0.85, 0.0, 1.0);
        HEAVY_BULLET_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Heavy Bullet projectile")
                .defineInRange("Hitbox Width", 0.25, 0.05, 1.0);
        HEAVY_BULLET_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Heavy Bullet projectile")
                .defineInRange("Hitbox Height", 0.25, 0.05, 1.0);
        HEAVY_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT = builder
                .comment("Whether the Heavy Bullet projectile damages weapon durability when hitting an entity")
                .define("Break On Entity Hit", true);
        HEAVY_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT = builder
                .comment("Whether the Heavy Bullet projectile damages weapon durability when hitting a block")
                .define("Break On Block Hit", true);
        HEAVY_BULLET_PROJECTILE_DISABLE_SHIELD = builder
                .comment("Whether the Heavy Bullet projectile can disable shields").define("Disable Shield", true);
        HEAVY_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED = builder
                .comment("Whether the weapon breaks when the Heavy Bullet projectile piercing level is exhausted")
                .define("Break On Piercing Exhausted", true);
        HEAVY_BULLET_PROJECTILE_MAX_BLOCK_BREAKS = builder
                .comment("Maximum number of blocks the Heavy Bullet projectile can break")
                .defineInRange("Max Block Breaks", 6, 0, 64);
        HEAVY_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS = builder
                .comment("Whether the weapon breaks after the Heavy Bullet projectile reaches max block breaks")
                .define("Break After Max Block Breaks", true);
        HEAVY_BULLET_PROJECTILE_MAX_TICKS_IN_AIR = builder
                .comment("Maximum ticks the Heavy Bullet projectile can exist while flying in air (0 = no limit)")
                .defineInRange("Max Ticks In Air", 100, 0, 800);
        HEAVY_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND = builder
                .comment("Maximum ticks the Heavy Bullet projectile stays stuck in ground (0 = no limit)")
                .defineInRange("Max Ticks In Ground", 100, 0, 800);
        HEAVY_BULLET_PROJECTILE_COLOR = builder
                .comment("Color of the Heavy Bullet projectile in hexadecimal (examples: 0x696969, #696969 or 696969)")
                .define("Color", "0x696969");
        HEAVY_BULLET_PROJECTILE_NO_GRAVITY_DURATION = builder
                .comment("Ticks the Heavy Bullet projectile ignores gravity (0 = always affected by gravity)")
                .defineInRange("No Gravity Duration", 0, 0, 1200);
        HEAVY_BULLET_PROJECTILE_INITIAL_NO_GRAVITY = builder
                .comment("Whether the Heavy Bullet projectile starts with no gravity")
                .define("Initial No Gravity", false);
        HEAVY_BULLET_PROJECTILE_KNOCKED_OUT_CHANCE = builder.comment("Chance to apply Knocked Out effect (0.0 - 1.0)")
                .defineInRange("Knocked Out Chance", 0.35, 0.0, 1.0);
        HEAVY_BULLET_PROJECTILE_KNOCKED_OUT_DURATION = builder.comment("Duration of Knocked Out effect in ticks")
                .defineInRange("Knocked Out Duration", 60, 0, 600);
        HEAVY_BULLET_PROJECTILE_SHOW_IRON_PARTICLES = builder.comment("Whether to show iron ingot particles on hit")
                .define("Show Iron Particles", true);
        builder.pop();

        builder.push("Glowing Bullet Projectile");
        GLOWING_BULLET_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Glowing Bullet projectile")
                .defineInRange("Base Damage", 4.5, 0.5, 64.0);
        GLOWING_BULLET_PROJECTILE_BASE_KNOCKBACK = builder
                .comment("Base knockback strength applied to hit entities by the Glowing Bullet projectile")
                .defineInRange("Base Knockback", 0.12, 0.0, 4.0);
        GLOWING_BULLET_PROJECTILE_PIERCING_LEVEL = builder
                .comment("Number of additional entities the Glowing Bullet projectile can pierce through")
                .defineInRange("Piercing Level", 0, 0, 50);
        GLOWING_BULLET_PROJECTILE_IGNORE_TICKS = builder
                .comment("Initial ticks during which the Glowing Bullet projectile ignores collisions")
                .defineInRange("Ignore Ticks", 2, 0, 200);
        GLOWING_BULLET_PROJECTILE_ALLOW_CRITICALS = builder
                .comment("Whether the Glowing Bullet projectile can deal critical hits").define("Critical Hits", true);
        GLOWING_BULLET_PROJECTILE_WATER_INERTIA = builder
                .comment("Water inertia factor for the Glowing Bullet projectile (lower = more slowdown)")
                .defineInRange("Water Inertia", 0.92, 0.0, 1.0);
        GLOWING_BULLET_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Glowing Bullet projectile")
                .defineInRange("Hitbox Width", 0.16, 0.05, 1.0);
        GLOWING_BULLET_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Glowing Bullet projectile")
                .defineInRange("Hitbox Height", 0.16, 0.05, 1.0);
        GLOWING_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT = builder
                .comment("Whether the Glowing Bullet projectile damages weapon durability when hitting an entity")
                .define("Break On Entity Hit", true);
        GLOWING_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT = builder
                .comment("Whether the Glowing Bullet projectile damages weapon durability when hitting a block")
                .define("Break On Block Hit", true);
        GLOWING_BULLET_PROJECTILE_DISABLE_SHIELD = builder
                .comment("Whether the Glowing Bullet projectile can disable shields").define("Disable Shield", false);
        GLOWING_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED = builder
                .comment("Whether the weapon breaks when the Glowing Bullet projectile piercing level is exhausted")
                .define("Break On Piercing Exhausted", true);
        GLOWING_BULLET_PROJECTILE_MAX_BLOCK_BREAKS = builder
                .comment("Maximum number of blocks the Glowing Bullet projectile can break")
                .defineInRange("Max Block Breaks", 2, 0, 64);
        GLOWING_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS = builder
                .comment("Whether the weapon breaks after the Glowing Bullet projectile reaches max block breaks")
                .define("Break After Max Block Breaks", true);
        GLOWING_BULLET_PROJECTILE_MAX_TICKS_IN_AIR = builder
                .comment("Maximum ticks the Glowing Bullet projectile can exist while flying in air (0 = no limit)")
                .defineInRange("Max Ticks In Air", 100, 0, 800);
        GLOWING_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND = builder
                .comment("Maximum ticks the Glowing Bullet projectile stays stuck in ground (0 = no limit)")
                .defineInRange("Max Ticks In Ground", 100, 0, 800);
        GLOWING_BULLET_PROJECTILE_COLOR = builder
                .comment(
                        "Color of the Glowing Bullet projectile in hexadecimal (examples: 0xDA70D6, #DA70D6 or DA70D6)")
                .define("Color", "0xDA70D6");
        GLOWING_BULLET_PROJECTILE_NO_GRAVITY_DURATION = builder
                .comment("Ticks the Glowing Bullet projectile ignores gravity (0 = always affected by gravity)")
                .defineInRange("No Gravity Duration", 60, 0, 1200);
        GLOWING_BULLET_PROJECTILE_INITIAL_NO_GRAVITY = builder
                .comment("Whether the Glowing Bullet projectile starts with no gravity")
                .define("Initial No Gravity", true);
        GLOWING_BULLET_PROJECTILE_GLOWING = builder
                .comment("Whether the Glowing Bullet projectile applies the Glowing effect to hit entities")
                .define("Glowing", true);
        GLOWING_BULLET_PROJECTILE_GLOW_DURATION = builder.comment("Duration of Glowing effect in ticks")
                .defineInRange("Glowing Duration", 200, 0, 1200);
        GLOWING_BULLET_PROJECTILE_MAGIC_DAMAGE_CHANCE = builder.comment("Chance to deal extra magic damage (0.0 - 1.0)")
                .defineInRange("Magic Damage Chance", 0.25, 0.0, 1.0);
        GLOWING_BULLET_PROJECTILE_MAGIC_DAMAGE = builder.comment("Amount of extra magic damage")
                .defineInRange("Magic Damage", 3.0, 0.0, 20.0);
        GLOWING_BULLET_PROJECTILE_SHOW_AMETHYST_PARTICLES = builder.comment("Whether to show amethyst particles on hit")
                .define("Show Amethyst Particles", true);
        builder.pop();

        builder.push("Sharp Bullet Projectile");
        SHARP_BULLET_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Sharp Bullet projectile")
                .defineInRange("Base Damage", 5.5, 0.5, 80.0);
        SHARP_BULLET_PROJECTILE_BASE_KNOCKBACK = builder
                .comment("Base knockback strength applied to hit entities by the Sharp Bullet projectile")
                .defineInRange("Base Knockback", 0.18, 0.0, 4.0);
        SHARP_BULLET_PROJECTILE_PIERCING_LEVEL = builder
                .comment("Number of additional entities the Sharp Bullet projectile can pierce through")
                .defineInRange("Piercing Level", 16, 0, 50);
        SHARP_BULLET_PROJECTILE_IGNORE_TICKS = builder
                .comment("Initial ticks during which the Sharp Bullet projectile ignores collisions")
                .defineInRange("Ignore Ticks", 2, 0, 200);
        SHARP_BULLET_PROJECTILE_ALLOW_CRITICALS = builder
                .comment("Whether the Sharp Bullet projectile can deal critical hits").define("Critical Hits", true);
        SHARP_BULLET_PROJECTILE_WATER_INERTIA = builder
                .comment("Water inertia factor for the Sharp Bullet projectile (lower = more slowdown)")
                .defineInRange("Water Inertia", 0.90, 0.0, 1.0);
        SHARP_BULLET_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Sharp Bullet projectile")
                .defineInRange("Hitbox Width", 0.12, 0.05, 1.0);
        SHARP_BULLET_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Sharp Bullet projectile")
                .defineInRange("Hitbox Height", 0.12, 0.05, 1.0);
        SHARP_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT = builder
                .comment("Whether the Sharp Bullet projectile damages weapon durability when hitting an entity")
                .define("Break On Entity Hit", true);
        SHARP_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT = builder
                .comment("Whether the Sharp Bullet projectile damages weapon durability when hitting a block")
                .define("Break On Block Hit", true);
        SHARP_BULLET_PROJECTILE_DISABLE_SHIELD = builder
                .comment("Whether the Sharp Bullet projectile can disable shields").define("Disable Shield", false);
        SHARP_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED = builder
                .comment("Whether the weapon breaks when the Sharp Bullet projectile piercing level is exhausted")
                .define("Break On Piercing Exhausted", true);
        SHARP_BULLET_PROJECTILE_MAX_BLOCK_BREAKS = builder
                .comment("Maximum number of blocks the Sharp Bullet projectile can break")
                .defineInRange("Max Block Breaks", 3, 0, 64);
        SHARP_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS = builder
                .comment("Whether the weapon breaks after the Sharp Bullet projectile reaches max block breaks")
                .define("Break After Max Block Breaks", true);
        SHARP_BULLET_PROJECTILE_MAX_TICKS_IN_AIR = builder
                .comment("Maximum ticks the Sharp Bullet projectile can exist while flying in air (0 = no limit)")
                .defineInRange("Max Ticks In Air", 100, 0, 800);
        SHARP_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND = builder
                .comment("Maximum ticks the Sharp Bullet projectile stays stuck in ground (0 = no limit)")
                .defineInRange("Max Ticks In Ground", 100, 0, 800);
        SHARP_BULLET_PROJECTILE_COLOR = builder.comment(
                "Color of the Sharp Bullet projectile in hexadecimal (examples: 0xFF00FFFF, #FF00FFFF or FF00FFFF)")
                .define("Color", "0xFF00FFFF");
        SHARP_BULLET_PROJECTILE_NO_GRAVITY_DURATION = builder
                .comment("Ticks the Sharp Bullet projectile ignores gravity (0 = always affected by gravity)")
                .defineInRange("No Gravity Duration", 60, 0, 1200);
        SHARP_BULLET_PROJECTILE_INITIAL_NO_GRAVITY = builder
                .comment("Whether the Sharp Bullet projectile starts with no gravity")
                .define("Initial No Gravity", true);
        SHARP_BULLET_PROJECTILE_BYPASS_ARMOR_CHANCE = builder
                .comment("Chance to deal armor-bypassing damage (0.0 - 1.0)")
                .defineInRange("Bypass Armor Chance", 0.40, 0.0, 1.0);
        SHARP_BULLET_PROJECTILE_BYPASS_ARMOR_DAMAGE = builder.comment("Amount of extra damage that bypasses armor")
                .defineInRange("Bypass Armor Damage", 4.0, 0.0, 30.0);
        SHARP_BULLET_PROJECTILE_SHOW_PRISMARINE_PARTICLES = builder
                .comment("Whether to show prismarine shard particles on hit").define("Show Prismarine Particles", true);
        builder.pop();

        builder.push("Echo Bullet Projectile");
        ECHO_BULLET_PROJECTILE_BASE_DAMAGE = builder.comment("Base damage dealt by the Echo Bullet projectile")
                .defineInRange("Base Damage", 5.5, 0.5, 64.0);
        ECHO_BULLET_PROJECTILE_BASE_KNOCKBACK = builder
                .comment("Base knockback strength applied to hit entities by the Echo Bullet projectile")
                .defineInRange("Base Knockback", 0.20, 0.0, 4.0);
        ECHO_BULLET_PROJECTILE_PIERCING_LEVEL = builder
                .comment("Number of additional entities the Echo Bullet projectile can pierce through")
                .defineInRange("Piercing Level", 0, 0, 50);
        ECHO_BULLET_PROJECTILE_IGNORE_TICKS = builder
                .comment("Initial ticks during which the Echo Bullet projectile ignores collisions")
                .defineInRange("Ignore Ticks", 2, 0, 200);
        ECHO_BULLET_PROJECTILE_ALLOW_CRITICALS = builder
                .comment("Whether the Echo Bullet projectile can deal critical hits").define("Critical Hits", true);
        ECHO_BULLET_PROJECTILE_WATER_INERTIA = builder
                .comment("Water inertia factor for the Echo Bullet projectile (lower = more slowdown)")
                .defineInRange("Water Inertia", 0.88, 0.0, 1.0);
        ECHO_BULLET_PROJECTILE_HITBOX_WIDTH = builder.comment("Hitbox width of the Echo Bullet projectile")
                .defineInRange("Hitbox Width", 0.14, 0.05, 1.0);
        ECHO_BULLET_PROJECTILE_HITBOX_HEIGHT = builder.comment("Hitbox height of the Echo Bullet projectile")
                .defineInRange("Hitbox Height", 0.14, 0.05, 1.0);
        ECHO_BULLET_PROJECTILE_BREAK_ON_ENTITY_HIT = builder
                .comment("Whether the Echo Bullet projectile damages weapon durability when hitting an entity")
                .define("Break On Entity Hit", true);
        ECHO_BULLET_PROJECTILE_BREAK_ON_BLOCK_HIT = builder
                .comment("Whether the Echo Bullet projectile damages weapon durability when hitting a block")
                .define("Break On Block Hit", true);
        ECHO_BULLET_PROJECTILE_DISABLE_SHIELD = builder
                .comment("Whether the Echo Bullet projectile can disable shields").define("Disable Shield", false);
        ECHO_BULLET_PROJECTILE_BREAK_ON_PIERCING_EXHAUSTED = builder
                .comment("Whether the weapon breaks when the Echo Bullet projectile piercing level is exhausted")
                .define("Break On Piercing Exhausted", true);
        ECHO_BULLET_PROJECTILE_MAX_BLOCK_BREAKS = builder
                .comment("Maximum number of blocks the Echo Bullet projectile can break")
                .defineInRange("Max Block Breaks", 3, 0, 64);
        ECHO_BULLET_PROJECTILE_BREAK_AFTER_MAX_BLOCK_BREAKS = builder
                .comment("Whether the weapon breaks after the Echo Bullet projectile reaches max block breaks")
                .define("Break After Max Block Breaks", true);
        ECHO_BULLET_PROJECTILE_MAX_TICKS_IN_AIR = builder
                .comment("Maximum ticks the Echo Bullet projectile can exist while flying in air (0 = no limit)")
                .defineInRange("Max Ticks In Air", 100, 0, 800);
        ECHO_BULLET_PROJECTILE_MAX_TICKS_IN_GROUND = builder
                .comment("Maximum ticks the Echo Bullet projectile stays stuck in ground (0 = no limit)")
                .defineInRange("Max Ticks In Ground", 100, 0, 800);
        ECHO_BULLET_PROJECTILE_COLOR = builder.comment(
                "Color of the Echo Bullet projectile in hexadecimal (examples: 0xFF008B8B, #FF008B8B or FF008B8B)")
                .define("Color", "0xFF008B8B");
        ECHO_BULLET_PROJECTILE_NO_GRAVITY_DURATION = builder
                .comment("Ticks the Echo Bullet projectile ignores gravity (0 = always affected by gravity)")
                .defineInRange("No Gravity Duration", 60, 0, 1200);
        ECHO_BULLET_PROJECTILE_INITIAL_NO_GRAVITY = builder
                .comment("Whether the Echo Bullet projectile starts with no gravity")
                .define("Initial No Gravity", true);
        ECHO_BULLET_PROJECTILE_HOMING = builder.comment("Whether the Echo Bullet projectile can homing")
                .define("Homing", true);
        ECHO_BULLET_PROJECTILE_HOMING_SPEED = builder.comment("Homing speed (higher = faster tracking)")
                .defineInRange("Homing Speed", 1.6, 0.5, 4.0);
        ECHO_BULLET_PROJECTILE_SEARCH_RANGE = builder.comment("Homing search range for targets")
                .defineInRange("Search Range", 16.0, 4.0, 40.0);
        ECHO_BULLET_PROJECTILE_SHOW_ECHO_SHARD_PARTICLES = builder.comment("Whether to show sculk particles on hit")
                .define("Show Echo Shard Particles", true);
        ECHO_BULLET_PROJECTILE_IGNORED_ENTITIES = builder
                .comment("List of entity types the Echo Bullet projectile should ignore")
                .defineList("Ignored Entities", List.of("minecraft:armor_stand"), obj -> obj instanceof String);
        ECHO_BULLET_PROJECTILE_PLAY_HOMING_SOUND = builder.comment("Whether to play sound while homing towards target")
                .define("Play Homing Sound", true);
        ECHO_BULLET_PROJECTILE_TARGETING_OWNER_MULTIPLIER = builder
                .comment("Multiplier for targets that are attacking the owner (lower = higher priority)")
                .defineInRange("Targeting Owner Multiplier", 0.05, 0.01, 1.0);
        ECHO_BULLET_PROJECTILE_HOSTILE_MULTIPLIER = builder
                .comment("Multiplier for hostile or angry mobs (lower = higher priority)")
                .defineInRange("Hostile Multiplier", 0.25, 0.01, 1.0);
        ECHO_BULLET_PROJECTILE_GLOWING_MULTIPLIER = builder
                .comment("Multiplier for entities with Glowing effect (lower = higher priority)")
                .defineInRange("Glowing Multiplier", 0.15, 0.01, 1.0);
        ECHO_BULLET_PROJECTILE_LOS_MULTIPLIER = builder
                .comment("Multiplier when the projectile has clear line of sight to target (lower = higher priority)")
                .defineInRange("Line of Sight Multiplier", 0.4, 0.01, 1.0);
        ECHO_BULLET_PROJECTILE_HEIGHT_PENALTY = builder
                .comment("Height difference penalty multiplier (higher = stronger penalty for vertical distance)")
                .defineInRange("Height Penalty", 8.0, 0.0, 50.0);
        builder.pop();
    }
}
