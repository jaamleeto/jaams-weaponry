package net.jaams.weaponry.configuration.common;

import net.minecraftforge.common.ForgeConfigSpec;

import net.jaams.weaponry.util.ModEnums;

public class GunSystemCommonConfig {

    public static ForgeConfigSpec.BooleanValue GUN_INVENTORY;
    public static ForgeConfigSpec.BooleanValue GUN_AMMO_FROM_GUN;
    public static ForgeConfigSpec.BooleanValue GUN_AMMO_FROM_PLAYER_INVENTORY;
    public static ForgeConfigSpec.BooleanValue GUN_AMMO_FROM_HAND;
    public static ForgeConfigSpec.BooleanValue GUN_BUNDLE_INTERACTION;

    public static ForgeConfigSpec.BooleanValue GUN_COOLDOWN_GLOBAL;
    public static ForgeConfigSpec.BooleanValue GUN_COOLDOWN_BY_TYPE;

    public static ForgeConfigSpec.IntValue GUN_PISTOL_MAX_AMMO;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_MAX_AMMO;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_MAX_AMMO;

    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_PROJECTILE_DAMAGE_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_PISTOL_SHOOT_PROJECTILE_PIERCING_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_PISTOL_SHOOT_PROJECTILE_COUNT;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_INACCURACY;
    public static ForgeConfigSpec.IntValue GUN_PISTOL_SHOOT_COOLDOWN;
    public static ForgeConfigSpec.IntValue GUN_PISTOL_SHOOT_OFFHAND_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_DEFAULT_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_SPREAD_ANGLE;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_GUN_SHOT_SIZE;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_GUN_SHOT_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_SHAKE_INTENSITY;
    public static ForgeConfigSpec.IntValue GUN_PISTOL_SHOOT_SHAKE_RESET_DELAY;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_RECOIL_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_CROUCH_RECOIL_REDUCTION;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_VERTICAL_RECOIL_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_XROT_RECOIL_INTENSITY;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_PROJECTILE_SPEED;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_MUZZLE_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_PISTOL_SHOOT_MAGAZINE_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_PISTOL_SHOOT_AMMO_CONSUMPTION;
    public static ForgeConfigSpec.IntValue GUN_PISTOL_SHOOT_ATTACHMENT_CONSUMPTION;
    public static ForgeConfigSpec.EnumValue<ModEnums.GunFirePattern> GUN_PISTOL_SHOOT_FIRE_PATTERN;

    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_SHOOT_PROJECTILE_COUNT;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_INACCURACY;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_SHOOT_COOLDOWN;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_SHOOT_OFFHAND_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_DEFAULT_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_SPREAD_ANGLE;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_GUN_SHOT_SIZE;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_GUN_SHOT_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_SHAKE_INTENSITY;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_SHOOT_SHAKE_RESET_DELAY;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_RECOIL_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_CROUCH_RECOIL_REDUCTION;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_VERTICAL_RECOIL_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_XROT_RECOIL_INTENSITY;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_PROJECTILE_SPEED;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_MUZZLE_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_SCATTERGUN_SHOOT_MAGAZINE_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_SHOOT_AMMO_CONSUMPTION;
    public static ForgeConfigSpec.IntValue GUN_SCATTERGUN_SHOOT_ATTACHMENT_CONSUMPTION;
    public static ForgeConfigSpec.EnumValue<ModEnums.GunFirePattern> GUN_SCATTERGUN_SHOOT_FIRE_PATTERN;

    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_SHOOT_PROJECTILE_COUNT;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_INACCURACY;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_SHOOT_COOLDOWN;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_SHOOT_OFFHAND_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_DEFAULT_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_SPREAD_ANGLE;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_GUN_SHOT_SIZE;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_GUN_SHOT_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_SHAKE_INTENSITY;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_SHOOT_SHAKE_RESET_DELAY;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_RECOIL_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_CROUCH_RECOIL_REDUCTION;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_VERTICAL_RECOIL_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_XROT_RECOIL_INTENSITY;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_PROJECTILE_SPEED;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_MUZZLE_MODIFIER;
    public static ForgeConfigSpec.DoubleValue GUN_SHOTGUN_SHOOT_MAGAZINE_MODIFIER;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_SHOOT_AMMO_CONSUMPTION;
    public static ForgeConfigSpec.IntValue GUN_SHOTGUN_SHOOT_ATTACHMENT_CONSUMPTION;
    public static ForgeConfigSpec.EnumValue<ModEnums.GunFirePattern> GUN_SHOTGUN_SHOOT_FIRE_PATTERN;

    public static void register(ForgeConfigSpec.Builder builder) {
        builder.push("Gun System Common Handler");
        builder.push("Gun Inventory Settings");
        GUN_INVENTORY = builder.comment("Enable or disable the gun inventory").define("Gun Inventory", true);
        builder.pop();
        builder.push("Gun Cooldown Settings");
        GUN_COOLDOWN_GLOBAL = builder
                .comment("If true, firing any gun will trigger a cooldown on ALL guns in the player's inventory.")
                .define("Global Cooldown", false);
        GUN_COOLDOWN_BY_TYPE = builder.comment(
                "If true, firing a gun will trigger a cooldown on all guns of the SAME TYPE (e.g., shooting a pistol cools down all pistols). Ignored if Global Cooldown is true.")
                .define("Cooldown By Gun Type", true);
        builder.pop();
        builder.push("Gun Ammo Settings");
        GUN_AMMO_FROM_GUN = builder.comment("Take bullets from the gun's inventory").define("Bullet From Gun Inventory",
                true);
        GUN_AMMO_FROM_PLAYER_INVENTORY = builder.comment("Take bullets from the player's inventory")
                .define("Bullet From Player Inventory", false);
        GUN_AMMO_FROM_HAND = builder.comment("Take bullets from the player's hands").define("Bullet From Player Hands",
                false);
        builder.pop();
        builder.push("Gun Interaction Settings");
        GUN_BUNDLE_INTERACTION = builder
                .comment("Enable bundle-like insert/extract behavior when right-clicking guns in containers")
                .define("Bundle-Like Gun Interaction", true);
        builder.pop();
        builder.push("Gun Capacity Settings");
        GUN_PISTOL_MAX_AMMO = builder.comment("Default maximum ammo for pistols (max 64)")
                .defineInRange("Pistol Max Ammo Capacity", 16, 1, 64);
        GUN_SCATTERGUN_MAX_AMMO = builder.comment("Default maximum ammo for scatterguns (max 64)")
                .defineInRange("Scattergun Max Ammo Capacity", 32, 1, 64);
        GUN_SHOTGUN_MAX_AMMO = builder.comment("Default maximum ammo for shotguns (max 64)")
                .defineInRange("Shotgun Max Ammo Capacity", 8, 1, 64);
        builder.pop();
        builder.push("Gun Shoot Settings");
        builder.push("Pistol Shoot");
        GUN_PISTOL_SHOOT_PROJECTILE_DAMAGE_MODIFIER = builder
                .comment("Projectile damage modifier (only applied if > 0)")
                .defineInRange("Projectile Damage Modifier", 6.0, 0.0, 100.0);
        GUN_PISTOL_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER = builder
                .comment("Projectile knockback modifier (only applied if > 0)")
                .defineInRange("Projectile Knockback Modifier", 0.1, 0.0, 5.0);
        GUN_PISTOL_SHOOT_PROJECTILE_PIERCING_MODIFIER = builder
                .comment("Projectile piercing modifier (only applied if > 0)")
                .defineInRange("Projectile Piercing Modifier", 0, 0, 10);
        GUN_PISTOL_SHOOT_PROJECTILE_COUNT = builder.comment("Number of projectiles fired per shot (1 = normal pistol)")
                .defineInRange("Projectile Count", 1, 1, 100);
        GUN_PISTOL_SHOOT_INACCURACY = builder
                .comment("Inaccuracy/spread in degrees for the pistol shoot (0 = perfect aim)")
                .defineInRange("Inaccuracy", 0.0, 0.0, 10.0);
        GUN_PISTOL_SHOOT_COOLDOWN = builder.comment("Cooldown for pistol in ticks").defineInRange("Cooldown", 30, 0,
                72000);
        GUN_PISTOL_SHOOT_OFFHAND_COOLDOWN = builder.comment("Offhand cooldown for pistol")
                .defineInRange("Offhand Cooldown", 10, 0, 72000);
        GUN_PISTOL_SHOOT_DEFAULT_MODIFIER = builder.comment("Default modifier for pistol")
                .defineInRange("Default Modifier", 1.0, 0.1, 10.0);
        GUN_PISTOL_SHOOT_SPREAD_ANGLE = builder.comment("Spread angle for pistol shot (degrees)")
                .defineInRange("Spread Angle", 0.0, 0.0, 30.0);
        GUN_PISTOL_SHOOT_FIRE_PATTERN = builder
                .comment("Fire pattern for pistol: DEFAULT, LINE, HORIZONTAL, VERTICAL, CIRCLE, STAR, DIAMOND")
                .defineEnum("Shot Fire Pattern", ModEnums.GunFirePattern.DEFAULT);
        GUN_PISTOL_SHOOT_GUN_SHOT_SIZE = builder.comment("Gun shot size for pistol").defineInRange("Gun Shot Size", 0.8,
                0.1, 50.0);
        GUN_PISTOL_SHOOT_GUN_SHOT_DISTANCE = builder.comment("Gun shot distance for pistol")
                .defineInRange("Gun Shot Distance", 0.8, 0.1, 50.0);
        GUN_PISTOL_SHOOT_SHAKE_INTENSITY = builder.comment("Shake intensity for pistol")
                .defineInRange("Shake Intensity", 0.5, 0.0, 50.0);
        GUN_PISTOL_SHOOT_SHAKE_RESET_DELAY = builder.comment("Shake reset delay for pistol")
                .defineInRange("Shake Reset Delay", 5, 0, 50);
        GUN_PISTOL_SHOOT_RECOIL_DISTANCE = builder.comment("Recoil distance for pistol").defineInRange("Recoil", 0.5,
                0.0, 50.0);
        GUN_PISTOL_SHOOT_VERTICAL_RECOIL_MULTIPLIER = builder.comment("Vertical recoil multiplier for pistol")
                .defineInRange("Recoil Vertical Multiplier", 0.45, 0.0, 2.0);
        GUN_PISTOL_SHOOT_CROUCH_RECOIL_REDUCTION = builder.comment("Crouch recoil reduction for pistol")
                .defineInRange("Recoil Crouch Reduction", 0.6, 0.0, 1.0);
        GUN_PISTOL_SHOOT_XROT_RECOIL_INTENSITY = builder.comment("X rotation recoil intensity for pistol")
                .defineInRange("Recoil Pitch Kick", 1.75, 0.0, 10.0);
        GUN_PISTOL_SHOOT_PROJECTILE_SPEED = builder.comment("Projectile base speed for pistol")
                .defineInRange("Projectile Speed", 4.5, 0.1, 50.0);
        GUN_PISTOL_SHOOT_MUZZLE_MODIFIER = builder.comment("Muzzle modifier for pistol")
                .defineInRange("Muzzle Modifier", 1.5, 0.1, 5.0);
        GUN_PISTOL_SHOOT_MAGAZINE_MODIFIER = builder.comment("Magazine modifier for pistol")
                .defineInRange("Magazine Modifier", 2.0, 0.1, 5.0);
        GUN_PISTOL_SHOOT_AMMO_CONSUMPTION = builder.comment("How many bullets are consumed per shot")
                .defineInRange("Ammo Consumption", 1, 1, 64);
        GUN_PISTOL_SHOOT_ATTACHMENT_CONSUMPTION = builder.comment("Attachment items consumed per pistol shot").defineInRange("Attachment Consumption", 1, 1, 64);
        builder.pop();
        builder.push("Scattergun Shoot");
        GUN_SCATTERGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER = builder.comment("Projectile damage modifier for scattergun")
                .defineInRange("Projectile Damage Modifier", 4.0, 0.0, 100.0);
        GUN_SCATTERGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER = builder
                .comment("Projectile knockback modifier for scattergun")
                .defineInRange("Projectile Knockback Modifier", 0.3, 0.0, 5.0);
        GUN_SCATTERGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER = builder
                .comment("Projectile piercing modifier for scattergun")
                .defineInRange("Projectile Piercing Modifier", 0, 0, 10);
        GUN_SCATTERGUN_SHOOT_PROJECTILE_COUNT = builder.comment("Number of projectiles fired per shot (scattergun)")
                .defineInRange("Projectile Count", 2, 1, 100);
        GUN_SCATTERGUN_SHOOT_INACCURACY = builder.comment("Inaccuracy for scattergun").defineInRange("Inaccuracy", 0.0,
                0.0, 15.0);
        GUN_SCATTERGUN_SHOOT_COOLDOWN = builder.comment("Cooldown for scattergun in ticks").defineInRange("Cooldown",
                40, 0, 72000);
        GUN_SCATTERGUN_SHOOT_OFFHAND_COOLDOWN = builder.comment("Offhand cooldown for scattergun")
                .defineInRange("Offhand Cooldown", 20, 0, 72000);
        GUN_SCATTERGUN_SHOOT_DEFAULT_MODIFIER = builder.comment("Default modifier for scattergun")
                .defineInRange("Default Modifier", 1.0, 0.1, 10.0);
        GUN_SCATTERGUN_SHOOT_SPREAD_ANGLE = builder.comment("Spread angle for scattergun shot")
                .defineInRange("Spread Angle", 5.0, 0.0, 40.0);
        GUN_SCATTERGUN_SHOOT_FIRE_PATTERN = builder.comment("Fire pattern for scattergun")
                .defineEnum("Shot Fire Pattern", ModEnums.GunFirePattern.HORIZONTAL);
        GUN_SCATTERGUN_SHOOT_GUN_SHOT_SIZE = builder.comment("Gun shot size for scattergun")
                .defineInRange("Gun Shot Size", 1.2, 0.1, 50.0);
        GUN_SCATTERGUN_SHOOT_GUN_SHOT_DISTANCE = builder.comment("Gun shot distance for scattergun")
                .defineInRange("Gun Shot Distance", 1.0, 0.1, 50.0);
        GUN_SCATTERGUN_SHOOT_SHAKE_INTENSITY = builder.comment("Shake intensity for scattergun")
                .defineInRange("Shake Intensity", 1.5, 0.0, 50.0);
        GUN_SCATTERGUN_SHOOT_SHAKE_RESET_DELAY = builder.comment("Shake reset delay for scattergun")
                .defineInRange("Shake Reset Delay", 6, 0, 50);
        GUN_SCATTERGUN_SHOOT_RECOIL_DISTANCE = builder.comment("Recoil distance for scattergun").defineInRange("Recoil",
                        0.7, 0.0, 50.0);
        GUN_SCATTERGUN_SHOOT_VERTICAL_RECOIL_MULTIPLIER = builder.comment("Vertical recoil multiplier for scattergun")
                        .defineInRange("Recoil Vertical Multiplier", 0.35, 0.0, 2.0);
        GUN_SCATTERGUN_SHOOT_CROUCH_RECOIL_REDUCTION = builder.comment("Crouch recoil reduction for scattergun")
                .defineInRange("Recoil Crouch Reduction", 0.5, 0.0, 1.0);
        GUN_SCATTERGUN_SHOOT_XROT_RECOIL_INTENSITY = builder.comment("X rotation recoil intensity for scattergun")
                .defineInRange("Recoil Pitch Kick", 2.5, 0.0, 10.0);
        GUN_SCATTERGUN_SHOOT_PROJECTILE_SPEED = builder.comment("Projectile base speed for scattergun")
                .defineInRange("Projectile Speed", 3.8, 0.1, 50.0);
        GUN_SCATTERGUN_SHOOT_MUZZLE_MODIFIER = builder.comment("Muzzle modifier for scattergun")
                .defineInRange("Muzzle Modifier", 1.8, 0.1, 5.0);
        GUN_SCATTERGUN_SHOOT_MAGAZINE_MODIFIER = builder.comment("Magazine modifier for scattergun")
                .defineInRange("Magazine Modifier", 2.5, 0.1, 5.0);
        GUN_SCATTERGUN_SHOOT_AMMO_CONSUMPTION = builder.comment("Ammo consumed per shot for scattergun")
                .defineInRange("Ammo Consumption", 2, 1, 64);
        GUN_SCATTERGUN_SHOOT_ATTACHMENT_CONSUMPTION = builder.comment("Attachment items consumed per scattergun shot").defineInRange("Attachment Consumption", 2, 1, 64);
        builder.pop();
        builder.push("Shotgun Shoot");
        GUN_SHOTGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER = builder.comment("Projectile damage modifier for shotgun")
                .defineInRange("Projectile Damage Modifier", 5.5, 0.0, 100.0);
        GUN_SHOTGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER = builder.comment("Projectile knockback modifier for shotgun")
                .defineInRange("Projectile Knockback Modifier", 0.5, 0.0, 5.0);
        GUN_SHOTGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER = builder.comment("Projectile piercing modifier for shotgun")
                .defineInRange("Projectile Piercing Modifier", 1, 0, 10);
        GUN_SHOTGUN_SHOOT_PROJECTILE_COUNT = builder.comment("Number of projectiles fired per shot (shotgun)")
                .defineInRange("Projectile Count", 4, 1, 100);
        GUN_SHOTGUN_SHOOT_INACCURACY = builder.comment("Inaccuracy for shotgun").defineInRange("Inaccuracy", 0.05, 0.0,
                20.0);
        GUN_SHOTGUN_SHOOT_COOLDOWN = builder.comment("Cooldown for shotgun in ticks").defineInRange("Cooldown", 60, 0,
                72000);
        GUN_SHOTGUN_SHOOT_OFFHAND_COOLDOWN = builder.comment("Offhand cooldown for shotgun")
                .defineInRange("Offhand Cooldown", 20, 0, 72000);
        GUN_SHOTGUN_SHOOT_DEFAULT_MODIFIER = builder.comment("Default modifier for shotgun")
                .defineInRange("Default Modifier", 1.0, 0.1, 10.0);
        GUN_SHOTGUN_SHOOT_SPREAD_ANGLE = builder.comment("Spread angle for shotgun shot").defineInRange("Spread Angle",
                5.5, 0.0, 45.0);
        GUN_SHOTGUN_SHOOT_FIRE_PATTERN = builder.comment("Fire pattern for shotgun").defineEnum("Shot Fire Pattern",
                ModEnums.GunFirePattern.HORIZONTAL);
        GUN_SHOTGUN_SHOOT_GUN_SHOT_SIZE = builder.comment("Gun shot size for shotgun").defineInRange("Gun Shot Size",
                1.5, 0.1, 50.0);
        GUN_SHOTGUN_SHOOT_GUN_SHOT_DISTANCE = builder.comment("Gun shot distance for shotgun")
                .defineInRange("Gun Shot Distance", 1.2, 0.1, 50.0);
        GUN_SHOTGUN_SHOOT_SHAKE_INTENSITY = builder.comment("Shake intensity for shotgun")
                .defineInRange("Shake Intensity", 2.5, 0.0, 50.0);
        GUN_SHOTGUN_SHOOT_SHAKE_RESET_DELAY = builder.comment("Shake reset delay for shotgun")
                .defineInRange("Shake Reset Delay", 8, 0, 50);
        GUN_SHOTGUN_SHOOT_RECOIL_DISTANCE = builder.comment("Recoil distance for shotgun").defineInRange("Recoil", 1.8,
                0.0, 50.0);
        GUN_SHOTGUN_SHOOT_VERTICAL_RECOIL_MULTIPLIER = builder.comment("Vertical recoil multiplier for shotgun")
                                .defineInRange("Recoil Vertical Multiplier", 0.5, 0.0, 2.0);
        GUN_SHOTGUN_SHOOT_CROUCH_RECOIL_REDUCTION = builder.comment("Crouch recoil reduction for shotgun")
                .defineInRange("Recoil Crouch Reduction", 0.4, 0.0, 1.0);
        GUN_SHOTGUN_SHOOT_XROT_RECOIL_INTENSITY = builder.comment("X rotation recoil intensity for shotgun")
                .defineInRange("Recoil Pitch Kick", 3.5, 0.0, 12.0);
        GUN_SHOTGUN_SHOOT_PROJECTILE_SPEED = builder.comment("Projectile base speed for shotgun")
                .defineInRange("Projectile Speed", 3.5, 0.1, 50.0);
        GUN_SHOTGUN_SHOOT_MUZZLE_MODIFIER = builder.comment("Muzzle modifier for shotgun")
                .defineInRange("Muzzle Modifier", 2.0, 0.1, 5.0);
        GUN_SHOTGUN_SHOOT_MAGAZINE_MODIFIER = builder.comment("Magazine modifier for shotgun")
                .defineInRange("Magazine Modifier", 3.0, 0.1, 5.0);
        GUN_SHOTGUN_SHOOT_AMMO_CONSUMPTION = builder.comment("Ammo consumed per shot for shotgun")
                .defineInRange("Ammo Consumption", 1, 1, 64);
        GUN_SHOTGUN_SHOOT_ATTACHMENT_CONSUMPTION = builder.comment("Attachment items consumed per shotgun shot").defineInRange("Attachment Consumption", 1, 1, 64);
        builder.pop();
        builder.pop();
        builder.pop();
    }
}
