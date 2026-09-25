package net.jaams.weaponry.configuration.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EffectsConfig {

    
    public static ModConfigSpec.BooleanValue INCAPABLE;
    public static ModConfigSpec.DoubleValue INCAPABLE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue INCAPABLE_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.BooleanValue INCAPABLE_RESTRICT_ITEM_USE;
    public static ModConfigSpec.BooleanValue INCAPABLE_RESTRICT_BLOCK_INTERACTION;
    
    public static ModConfigSpec.BooleanValue DEPLETION;
    public static ModConfigSpec.DoubleValue DEPLETION_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue DEPLETION_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue DEPLETION_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER;
    
    public static ModConfigSpec.BooleanValue KNOCKED_OUT;
    public static ModConfigSpec.DoubleValue KNOCKED_OUT_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue KNOCKED_OUT_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.BooleanValue KNOCKED_OUT_RESTRICT_ITEM_USE;
    public static ModConfigSpec.BooleanValue KNOCKED_OUT_RESTRICT_BLOCK_INTERACTION;
    public static ModConfigSpec.BooleanValue KNOCKED_OUT_RESTRICT_JUMP;
    public static ModConfigSpec.BooleanValue KNOCKED_OUT_RESTRICT_ATTACKING;
    
    public static ModConfigSpec.BooleanValue VIGOROUS_RAGE;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_KNOCKBACK_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_BASE_UPGRADE_CHANCE;
    public static ModConfigSpec.IntValue VIGOROUS_RAGE_MAX_LEVEL;
    public static ModConfigSpec.IntValue VIGOROUS_RAGE_MAX_DURATION_TICKS;
    public static ModConfigSpec.IntValue VIGOROUS_RAGE_MIN_EXTRA_TICKS;
    public static ModConfigSpec.IntValue VIGOROUS_RAGE_MAX_EXTRA_TICKS;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_UPGRADE_CHANCE_DECAY;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_HEALTH_SCALING_FACTOR;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_MIN_HEALTH_THRESHOLD;
    public static ModConfigSpec.DoubleValue VIGOROUS_RAGE_MAX_HEALTH_THRESHOLD;
    
    public static ModConfigSpec.BooleanValue WARRIORS_GRACE;
    public static ModConfigSpec.DoubleValue WARRIORS_GRACE_CRIT_DAMAGE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue WARRIORS_GRACE_DAMAGE_REDUCTION_PER_LEVEL;
    public static ModConfigSpec.DoubleValue WARRIORS_GRACE_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue WARRIORS_GRACE_KNOCKBACK_RESISTANCE_ATTRIBUTE_MULTIPLIER;
    
    public static ModConfigSpec.BooleanValue ARCHERS_GRACE;
    public static ModConfigSpec.DoubleValue ARCHERS_GRACE_DAMAGE_MULTIPLIER;
    public static ModConfigSpec.IntValue ARCHERS_GRACE_MIN_USE_TICKS;
    public static ModConfigSpec.DoubleValue ARCHERS_GRACE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER;
    public static ModConfigSpec.DoubleValue ARCHERS_GRACE_KNOCKBACK_ATTRIBUTE_MULTIPLIER;

    public static void register(ModConfigSpec.Builder builder) {
        builder.push("Custom Effects Handler");
        
        builder.push("Incapable");
        INCAPABLE = builder.comment("Enable or disable the Incapable effect (prevents attacking/using items)").define("Incapable", true);
        INCAPABLE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Attack speed multiplier when Incapable (negative = slower)").defineInRange("Incapable Attack Speed Attribute Multiplier", -0.8, -2.0, 0.0);
        INCAPABLE_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER = builder.comment("Attack damage multiplier when Incapable (negative = weaker)").defineInRange("Incapable Attack Damage Attribute Multiplier", -0.8, -2.0, 0.0);
        INCAPABLE_RESTRICT_ITEM_USE = builder.comment("Prevent item use while Incapable").define("Incapable Restrict Item Use", true);
        INCAPABLE_RESTRICT_BLOCK_INTERACTION = builder.comment("Prevent block interaction while Incapable").define("Incapable Restrict Block Interaction", true);
        builder.pop();
        
        builder.push("Depletion");
        DEPLETION = builder.comment("Enable or disable the Depletion effect (stat penalties)").define("Depletion", true);
        DEPLETION_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Attack speed multiplier when Depleted (negative = slower)").defineInRange("Depletion Attack Speed Attribute Multiplier", -0.2, -1.0, 0.0);
        DEPLETION_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Movement speed multiplier when Depleted (negative = slower)").defineInRange("Depletion Movement Speed Attribute Multiplier", -0.2, -1.0, 0.0);
        DEPLETION_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER = builder.comment("Attack damage multiplier when Depleted (negative = weaker)").defineInRange("Depletion Attack Damage Attribute Multiplier", -0.2, -1.0, 0.0);
        builder.pop();
        
        builder.push("Knocked Out");
        KNOCKED_OUT = builder.comment("Enable or disable the Knocked Out effect (severely cripples the entity)").define("Knocked Out", true);
        KNOCKED_OUT_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Attack speed multiplier when Knocked Out (negative = slower)").defineInRange("Knocked Out Attack Speed Attribute Multiplier", -1.0, -1.0, 0.0);
        KNOCKED_OUT_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Movement speed multiplier when Knocked Out (negative = slower)").defineInRange("Knocked Out Movement Speed Attribute Multiplier", -1.0, -1.0, 0.0);
        KNOCKED_OUT_RESTRICT_ITEM_USE = builder.comment("Prevent item use while Knocked Out").define("Knocked Out Restrict Item Use", true);
        KNOCKED_OUT_RESTRICT_BLOCK_INTERACTION = builder.comment("Prevent block interaction while Knocked Out").define("Knocked Out Restrict Block Interaction", true);
        KNOCKED_OUT_RESTRICT_JUMP = builder.comment("Prevent jumping while Knocked Out").define("Knocked Out Restrict Jump", true);
        KNOCKED_OUT_RESTRICT_ATTACKING = builder.comment("Prevent attacking while Knocked Out").define("Knocked Out Restrict Attacking", true);
        builder.pop();
        
        builder.push("Vigorous Rage");
        VIGOROUS_RAGE = builder.comment("Enable or disable the Vigorous Rage effect (offensive buffs)").define("Vigorous Rage", true);
        VIGOROUS_RAGE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Attack speed multiplier while Vigorous Rage is active").defineInRange("Vigorous Rage Attack Speed Attribute Multiplier", 0.3, 0.0, 2.0);
        VIGOROUS_RAGE_KNOCKBACK_ATTRIBUTE_MULTIPLIER = builder.comment("Knockback multiplier while Vigorous Rage is active").defineInRange("Vigorous Rage Knockback Attribute Multiplier", 0.5, 0.0, 2.0);
        VIGOROUS_RAGE_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Movement speed multiplier while Vigorous Rage is active").defineInRange("Vigorous Rage Movement Speed Attribute Multiplier", 0.1, 0.0, 2.0);
        VIGOROUS_RAGE_BASE_UPGRADE_CHANCE = builder.comment("Base chance to upgrade Vigorous Rage level on kill").defineInRange("Vigorous Rage Upgrade Chance", 0.2, 0.0, 1.0);
        VIGOROUS_RAGE_MAX_LEVEL = builder.comment("Maximum upgrade level for Vigorous Rage").defineInRange("Vigorous Rage Upgrade Max Level", 10, 1, 255);
        VIGOROUS_RAGE_MAX_DURATION_TICKS = builder.comment("Maximum base duration of Vigorous Rage in ticks").defineInRange("Vigorous Rage Upgrade Max Duration Ticks", 300, 20, 72000);
        VIGOROUS_RAGE_MIN_EXTRA_TICKS = builder.comment("Minimum extra ticks added on kill").defineInRange("Vigorous Rage Upgrade Min Extra Ticks On Kill", 60, 0, 1200);
        VIGOROUS_RAGE_MAX_EXTRA_TICKS = builder.comment("Maximum extra ticks added on kill").defineInRange("Vigorous Rage Upgrade Max Extra Ticks On Kill", 120, 0, 1200);
        VIGOROUS_RAGE_UPGRADE_CHANCE_DECAY = builder.comment("Chance decay per level (multiplied per level)").defineInRange("Vigorous Rage Upgrade Chance Decay Per Level", 0.8, 0.0, 1.0);
        VIGOROUS_RAGE_HEALTH_SCALING_FACTOR = builder.comment("Extra upgrade chance per %% of target max health").defineInRange("Vigorous Rage Upgrade Health Scaling Factor", 0.02, 0.0, 1.0);
        VIGOROUS_RAGE_MIN_HEALTH_THRESHOLD = builder.comment("Minimum target health for health scaling bonus").defineInRange("Vigorous Rage Upgrade Min Health Threshold", 10.0, 0.0, 1024.0);
        VIGOROUS_RAGE_MAX_HEALTH_THRESHOLD = builder.comment("Maximum target health for health scaling bonus").defineInRange("Vigorous Rage Upgrade Max Health Threshold", 100.0, 0.0, 1024.0);
        builder.pop();
        
        builder.push("Warrior's Grace");
        WARRIORS_GRACE = builder.comment("Enable or disable the Warrior's Grace effect (melee buffs)").define("Warrior's Grace", true);
        WARRIORS_GRACE_CRIT_DAMAGE_MULTIPLIER = builder.comment("Critical damage bonus per level of Warrior's Grace").defineInRange("Warrior's Grace Crit Damage Per Level", 0.15, 0.0, 2.0);
        WARRIORS_GRACE_DAMAGE_REDUCTION_PER_LEVEL = builder.comment("Incoming damage reduction per level").defineInRange("Warrior's Grace Damage Reduction Per Level", 0.05, 0.0, 0.5);
        WARRIORS_GRACE_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER = builder.comment("Attack damage multiplier while active").defineInRange("Warrior's Grace Attack Damage Attribute Multiplier", 0.3, 0.0, 2.0);
        WARRIORS_GRACE_KNOCKBACK_RESISTANCE_ATTRIBUTE_MULTIPLIER = builder.comment("Knockback resistance multiplier while active").defineInRange("Warrior's Grace Knockback Resistance Attribute Multiplier", 0.1, 0.0, 1.0);
        builder.pop();
        
        builder.push("Archer's Grace");
        ARCHERS_GRACE = builder.comment("Enable or disable the Archer's Grace effect (ranged buffs)").define("Archer's Grace", true);
        ARCHERS_GRACE_DAMAGE_MULTIPLIER = builder.comment("Ranged damage multiplier per level of Archer's Grace").defineInRange("Archer's Grace Damage Multiplier Per Level", 0.5, 0.0, 50.0);
        ARCHERS_GRACE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER = builder.comment("Attack speed multiplier while active").defineInRange("Archer's Grace Attack Speed Attribute Multiplier", 0.2, 0.0, 2.0);
        ARCHERS_GRACE_KNOCKBACK_ATTRIBUTE_MULTIPLIER = builder.comment("Knockback multiplier while active").defineInRange("Archer's Grace Knockback Attribute Multiplier", 0.2, 0.0, 2.0);
        ARCHERS_GRACE_MIN_USE_TICKS = builder.comment("Minimum ticks the ranged weapon must be drawn for the buff to apply").defineInRange("Archer's Grace Min Use Ticks", 20, 0, 300);
        builder.pop();
        builder.pop();
    }
}
