package net.jaams.weaponry.configuration.common;

import java.util.List;
import net.jaams.weaponry.util.ModEnums;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.ForgeConfigSpec;

public class TraitsConfig {

    public static ForgeConfigSpec.BooleanValue THROWABLE;
    public static ForgeConfigSpec.BooleanValue THROWBACK;
    public static ForgeConfigSpec.BooleanValue COLLECTOR;
    public static ForgeConfigSpec.BooleanValue DISARMING_SHOT;
    public static ForgeConfigSpec.BooleanValue DISABLING_SHOT;
    public static ForgeConfigSpec.BooleanValue SWEEPING_SHOT;
    public static ForgeConfigSpec.BooleanValue BACKSTAB_SHOT;
    public static ForgeConfigSpec.BooleanValue PIERCING_SHOT;
    public static ForgeConfigSpec.BooleanValue GUARD_STANCE;
    public static ForgeConfigSpec.BooleanValue GUARD_STANCE_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue GUARD_STANCE_AREA_DAMAGE;
    public static ForgeConfigSpec.BooleanValue GUARD_STANCE_APPLY_FIRST_PERSON_TRANSFORM;
    public static ForgeConfigSpec.IntValue GUARD_STANCE_COOLDOWN_TICKS;
    public static ForgeConfigSpec.BooleanValue GUARD_STANCE_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue GUARD_STANCE_AREA_DAMAGE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue GUARD_STANCE_KNOCKBACK_FORCE;
    public static ForgeConfigSpec.DoubleValue GUARD_STANCE_AREA_RANGE;
    public static ForgeConfigSpec.DoubleValue GUARD_STANCE_BLOCK_DAMAGE_REDUCTION;
    public static ForgeConfigSpec.IntValue GUARD_STANCE_DAMAGE_PER_BLOCK;
    public static ForgeConfigSpec.IntValue GUARD_STANCE_DAMAGE_ON_STOP;
    public static ForgeConfigSpec.DoubleValue GUARD_STANCE_PARTICLE_SIZE;
    public static ForgeConfigSpec.DoubleValue GUARD_STANCE_PARTICLE_DISTANCE;
    public static ForgeConfigSpec.DoubleValue GUARD_STANCE_NO_DURABILITY_BREAK_CHANCE;
    public static ForgeConfigSpec.BooleanValue PARRY_GUARD;
    public static ForgeConfigSpec.IntValue PARRY_GUARD_COOLDOWN_TICKS;
    public static ForgeConfigSpec.BooleanValue PARRY_GUARD_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue PARRY_GUARD_BLOCK_DAMAGE_REDUCTION;
    public static ForgeConfigSpec.IntValue PARRY_GUARD_DAMAGE_PER_BLOCK;
    public static ForgeConfigSpec.IntValue PARRY_GUARD_DAMAGE_ON_STOP;

    public static ForgeConfigSpec.DoubleValue PARRY_GUARD_NO_DURABILITY_BREAK_CHANCE;
    public static ForgeConfigSpec.BooleanValue PIERCER_STRIKE;
    public static ForgeConfigSpec.BooleanValue DUELIST;
    public static ForgeConfigSpec.BooleanValue THREAT_RESPONSE;
    public static ForgeConfigSpec.BooleanValue REACH_ADVANTAGE;
    public static ForgeConfigSpec.BooleanValue AFTER_STRIKE;
    public static ForgeConfigSpec.BooleanValue RAPID_BOOST;
    public static ForgeConfigSpec.BooleanValue POWER_BOOST;
    public static ForgeConfigSpec.BooleanValue BUSTER_STRIKE;
    public static ForgeConfigSpec.BooleanValue QUICK_CRAFTING;
    public static ForgeConfigSpec.BooleanValue AQUATIC_GRUDGE;
    public static ForgeConfigSpec.BooleanValue ARTHROPOD_GRUDGE;
    public static ForgeConfigSpec.BooleanValue UNDEAD_GRUDGE;
    public static ForgeConfigSpec.BooleanValue TRAITOR_GRUDGE;
    public static ForgeConfigSpec.BooleanValue SNOUT_GRUDGE;
    public static ForgeConfigSpec.BooleanValue BONE_GRUDGE;
    public static ForgeConfigSpec.BooleanValue ANTI_AERIAL;

    public static ForgeConfigSpec.BooleanValue ROTTEN_GRUDGE;
    public static ForgeConfigSpec.BooleanValue ARMOR_BREAKER;
    public static ForgeConfigSpec.BooleanValue BLADE_BREAKER;
    public static ForgeConfigSpec.BooleanValue ACROBATIC_LUNGE;
    public static ForgeConfigSpec.BooleanValue DEXTEROUS_LUNGE;
    public static ForgeConfigSpec.BooleanValue PULL_LUNGE;
    public static ForgeConfigSpec.BooleanValue DISENGAGE;
    public static ForgeConfigSpec.BooleanValue DISARM;
    public static ForgeConfigSpec.BooleanValue DISMOUNT;
    public static ForgeConfigSpec.BooleanValue DISABLING_STRIKE;
    public static ForgeConfigSpec.BooleanValue THROUGH_STRIKE;
    public static ForgeConfigSpec.BooleanValue CLEANSING_STRIKE;
    public static ForgeConfigSpec.BooleanValue OVERWHELMING_STRIKE;
    public static ForgeConfigSpec.BooleanValue SMASH_STRIKE;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_DAMAGE_PER_BLOCK;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_MAX_BONUS_DAMAGE;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_RESIDUAL_DAMAGE_BASE;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_RESIDUAL_DAMAGE_PER_BLOCK;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_MAX_RESIDUAL_DAMAGE;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_SMASH_RADIUS;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_SHAKE_INTENSITY;
    public static ForgeConfigSpec.IntValue SMASH_STRIKE_SHAKE_RESET_DELAY;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_ALLY_DAMAGE_MULTIPLIER;
    public static ForgeConfigSpec.IntValue SMASH_STRIKE_DURABILITY_DAMAGE_BASE;
    public static ForgeConfigSpec.DoubleValue SMASH_STRIKE_DURABILITY_DAMAGE_PER_BLOCK;
    public static ForgeConfigSpec.IntValue SMASH_STRIKE_MAX_DURABILITY_DAMAGE;

    public static ForgeConfigSpec.BooleanValue SHOCK_IMPACT;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_MAX_BONUS_DAMAGE;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_MAX_RESIDUAL_DAMAGE;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_SMASH_RADIUS;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_MIN_KNOCKBACK_STRENGTH;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_MAX_KNOCKBACK_STRENGTH;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_KNOCKBACK_SCALING_FACTOR;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_SHAKE_INTENSITY;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_SHAKE_RESET_DELAY;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_DURABILITY_DAMAGE_BASE;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_MAX_DURABILITY_DAMAGE;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_EXHAUSTION;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_DEPLETION_CHANCE;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_DEPLETION_DURATION;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_DEPLETION_LEVEL;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_DEPLETION_MAX_LEVEL;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_DEPLETION_MAX_DURATION;
    public static ForgeConfigSpec.BooleanValue SHOCK_IMPACT_ENABLE_DEPLETION;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_PLAYER_VERTICAL_IMPULSE;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_ENTITY_VERTICAL_IMPULSE;
    public static ForgeConfigSpec.BooleanValue SHOCK_IMPACT_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_COOLDOWN_TICKS;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_BASE_DAMAGE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_OFFHAND_COOLDOWN_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue SHOCK_IMPACT_OFFHAND_POWER_MULTIPLIER;
    public static ForgeConfigSpec.EnumValue<ModEnums.ShockImpactMode> SHOCK_IMPACT_ACTIVATION_MODE;
    public static ForgeConfigSpec.BooleanValue SHOCK_IMPACT_REQUIRE_CROUCHING;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_CHARGE_DURATION_TICKS;
    public static ForgeConfigSpec.EnumValue<UseAnim> SHOCK_IMPACT_CHARGE_ANIMATION;
    public static ForgeConfigSpec.IntValue SHOCK_IMPACT_MIN_CHARGE_TICKS;

    public static ForgeConfigSpec.BooleanValue BACKSTAB;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_MULTIPLIER_NORMAL;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_MULTIPLIER_SNEAKING;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_MULTIPLIER_INVISIBLE;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_MULTIPLIER_SNEAKING_INVISIBLE;
    public static ForgeConfigSpec.IntValue BACKSTAB_DURABILITY_PENALTY;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_RIGHT_CLICK_DURABILITY_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_RIGHT_CLICK_FORWARD_IMPULSE;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_MAX_DISTANCE;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_MAX_ANGLE;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_WEAKNESS_CHANCE;
    public static ForgeConfigSpec.IntValue BACKSTAB_WEAKNESS_DURATION;
    public static ForgeConfigSpec.IntValue BACKSTAB_WEAKNESS_LEVEL;
    public static ForgeConfigSpec.IntValue BACKSTAB_RIGHT_CLICK_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue BACKSTAB_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_DARKNESS_BONUS;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_MOVING_TARGET_PENALTY;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_GRACE_PERIOD_SECONDS;
    public static ForgeConfigSpec.DoubleValue BACKSTAB_RIGHT_CLICK_DAMAGE_BONUS;

    public static ForgeConfigSpec.BooleanValue HEAVY_HANDED;
    public static ForgeConfigSpec.DoubleValue HEAVY_HANDED_MOVEMENT_SPEED_REDUCTION;
    public static ForgeConfigSpec.DoubleValue HEAVY_HANDED_ATTACK_SPEED_REDUCTION;
    public static ForgeConfigSpec.DoubleValue HEAVY_HANDED_ATTACK_DAMAGE_REDUCTION;
    public static ForgeConfigSpec.DoubleValue HEAVY_HANDED_DURABILITY_FACTOR;
    public static ForgeConfigSpec.DoubleValue HEAVY_HANDED_DAMAGE_FACTOR;
    public static ForgeConfigSpec.DoubleValue HEAVY_HANDED_MAX_REDUCTION;

    public static ForgeConfigSpec.BooleanValue SPARRING_STRIKE;
    public static ForgeConfigSpec.BooleanValue SLASH_ASSAULT;

    public static ForgeConfigSpec.BooleanValue PIERCING_ASSAULT;
    public static ForgeConfigSpec.DoubleValue PIERCING_ASSAULT_DASH_DISTANCE;
    public static ForgeConfigSpec.DoubleValue PIERCING_ASSAULT_RANGE;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_PIERCE_COOLDOWN;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_NO_TARGET_COOLDOWN;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_DURABILITY_COST;
    public static ForgeConfigSpec.EnumValue<ModEnums.PiercingAssaultMode> PIERCING_ASSAULT_ACTIVATION_MODE;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_CHARGE_DURATION_TICKS;
    public static ForgeConfigSpec.EnumValue<UseAnim> PIERCING_ASSAULT_CHARGE_ANIMATION;
    public static ForgeConfigSpec.DoubleValue PIERCING_ASSAULT_MIN_DAMAGE;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_MIN_CHARGE_TICKS;
    public static ForgeConfigSpec.BooleanValue PIERCING_ASSAULT_DASH_SWING;
    public static ForgeConfigSpec.BooleanValue PIERCING_ASSAULT_ATTACK_SWING;
    public static ForgeConfigSpec.BooleanValue PIERCING_ASSAULT_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue PIERCING_ASSAULT_DEPLETION_CHANCE;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_DEPLETION_DURATION;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_DEPLETION_LEVEL;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_DEPLETION_MAX_LEVEL;
    public static ForgeConfigSpec.IntValue PIERCING_ASSAULT_DEPLETION_MAX_DURATION;
    public static ForgeConfigSpec.BooleanValue PIERCING_ASSAULT_ENABLE_DEPLETION;
    public static ForgeConfigSpec.DoubleValue SLASH_ASSAULT_DASH_DISTANCE;
    public static ForgeConfigSpec.DoubleValue SLASH_ASSAULT_RANGE;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_SLASH_COOLDOWN;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_NO_TARGET_COOLDOWN;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_DURABILITY_COST;
    public static ForgeConfigSpec.EnumValue<ModEnums.SlashAssaultMode> SLASH_ASSAULT_ACTIVATION_MODE;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_CHARGE_DURATION_TICKS;
    public static ForgeConfigSpec.EnumValue<UseAnim> SLASH_ASSAULT_CHARGE_ANIMATION;
    public static ForgeConfigSpec.DoubleValue SLASH_ASSAULT_MIN_DAMAGE;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_MIN_CHARGE_TICKS;
    public static ForgeConfigSpec.BooleanValue SLASH_ASSAULT_DASH_SWING;
    public static ForgeConfigSpec.BooleanValue SLASH_ASSAULT_ATTACK_SWING;
    public static ForgeConfigSpec.BooleanValue SLASH_ASSAULT_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue SLASH_ASSAULT_DEPLETION_CHANCE;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_DEPLETION_DURATION;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_DEPLETION_LEVEL;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_DEPLETION_MAX_LEVEL;
    public static ForgeConfigSpec.IntValue SLASH_ASSAULT_DEPLETION_MAX_DURATION;
    public static ForgeConfigSpec.BooleanValue SLASH_ASSAULT_ENABLE_DEPLETION;

    public static ForgeConfigSpec.BooleanValue HARVEST_SWEEP;
    public static ForgeConfigSpec.IntValue HARVEST_SWEEP_RANGE;
    public static ForgeConfigSpec.IntValue HARVEST_SWEEP_TILL_RANGE;
    public static ForgeConfigSpec.IntValue HARVEST_SWEEP_MAX_BLOCKS;
    public static ForgeConfigSpec.BooleanValue HARVEST_SWEEP_CAN_HARVEST;
    public static ForgeConfigSpec.IntValue HARVEST_SWEEP_DURABILITY_COST_PER_BLOCK;
    public static ForgeConfigSpec.BooleanValue HARVEST_SWEEP_DURABILITY_PER_BLOCK;
    public static ForgeConfigSpec.IntValue HARVEST_SWEEP_TILL_DURABILITY_COST;
    public static ForgeConfigSpec.IntValue HARVEST_SWEEP_HARVEST_DURABILITY_COST;

    public static ForgeConfigSpec.BooleanValue WILD_SWEEP;
    public static ForgeConfigSpec.IntValue WILD_SWEEP_RADIUS;

    public static ForgeConfigSpec.BooleanValue WHIRLING_STRIKE;
    public static ForgeConfigSpec.DoubleValue WHIRLING_STRIKE_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue WHIRLING_STRIKE_BASE_ATTACK_RANGE;
    public static ForgeConfigSpec.DoubleValue WHIRLING_STRIKE_DUAL_WIELD_RANGE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue WHIRLING_STRIKE_DUAL_WIELD_DAMAGE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue WHIRLING_STRIKE_MAX_DAMAGE_CAP;
    public static ForgeConfigSpec.IntValue WHIRLING_STRIKE_ITEM_DAMAGE_INTERVAL;
    public static ForgeConfigSpec.IntValue WHIRLING_STRIKE_ATTACK_INTERVAL;
    public static ForgeConfigSpec.IntValue WHIRLING_STRIKE_ITEM_DAMAGE_AMOUNT;
    public static ForgeConfigSpec.IntValue WHIRLING_STRIKE_SINGLE_WIELD_BLOCK_DAMAGE;
    public static ForgeConfigSpec.IntValue WHIRLING_STRIKE_DUAL_WIELD_BLOCK_DAMAGE;
    public static ForgeConfigSpec.DoubleValue WHIRLING_STRIKE_USE_DISTANCE;
    public static ForgeConfigSpec.IntValue WHIRLING_STRIKE_PARTICLE_TICK_INTERVAL;
    public static ForgeConfigSpec.IntValue WILD_SWEEP_DURABILITY_COST;
    public static ForgeConfigSpec.IntValue WILD_SWEEP_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue WILD_SWEEP_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WILD_SWEEP_BREAKABLE_BLOCKS;

    public static ForgeConfigSpec.IntValue THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MIN_RANGE;
    public static ForgeConfigSpec.IntValue THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MAX_RANGE;
    public static ForgeConfigSpec.BooleanValue THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_MAX_RANGE;
    public static ForgeConfigSpec.BooleanValue THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_BLOCK_HIT;
    public static ForgeConfigSpec.DoubleValue THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_SPEED;

    public static ForgeConfigSpec.IntValue THROWBACK_RING_PROJECTILE_MIN_RANGE;
    public static ForgeConfigSpec.IntValue THROWBACK_RING_PROJECTILE_MAX_RANGE;
    public static ForgeConfigSpec.BooleanValue THROWBACK_RING_PROJECTILE_RETURN_ON_MAX_RANGE;
    public static ForgeConfigSpec.BooleanValue THROWBACK_RING_PROJECTILE_RETURN_ON_ENTITY_HIT;
    public static ForgeConfigSpec.BooleanValue THROWBACK_RING_PROJECTILE_RETURN_ON_BLOCK_HIT;
    public static ForgeConfigSpec.DoubleValue THROWBACK_RING_PROJECTILE_RETURN_SPEED;

    public static ForgeConfigSpec.IntValue COLLECTOR_HUNTERS_BOOMERANG_PROJECTILE_MAX_ITEMS;

    public static ForgeConfigSpec.DoubleValue DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_CHANCE;
    public static ForgeConfigSpec.BooleanValue DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_REQUIRE_CRITICAL;
    public static ForgeConfigSpec.IntValue DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_DURABILITY_COST;
    public static ForgeConfigSpec.BooleanValue DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_MOUNT_ITEM;

    public static ForgeConfigSpec.DoubleValue DISABLING_SHOT_SHURIKEN_PROJECTILE_CHANCE;
    public static ForgeConfigSpec.IntValue DISABLING_SHOT_SHURIKEN_PROJECTILE_COOLDOWN;

    public static ForgeConfigSpec.DoubleValue SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_RADIUS;
    public static ForgeConfigSpec.DoubleValue SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_DAMAGE_FACTOR;

    public static ForgeConfigSpec.DoubleValue BACKSTAB_SHOT_PRONGED_KUNAI_PROJECTILE_MULTIPLIER;

    public static ForgeConfigSpec.DoubleValue PIERCING_SHOT_KUNAI_PROJECTILE_CHANCE;
    public static ForgeConfigSpec.DoubleValue PIERCING_SHOT_KUNAI_PROJECTILE_BONUS_DAMAGE;

    public static ForgeConfigSpec.DoubleValue PIERCER_STRIKE_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue PIERCER_STRIKE_MIN_ARMOR;
    public static ForgeConfigSpec.BooleanValue PIERCER_STRIKE_REQUIRE_FULLY_CHARGED;
    public static ForgeConfigSpec.IntValue PIERCER_STRIKE_DURABILITY_COST;

    public static ForgeConfigSpec.DoubleValue DUELIST_BONUS_DAMAGE;
    public static ForgeConfigSpec.BooleanValue DUELIST_REQUIRE_FULLY_CHARGED;
    public static ForgeConfigSpec.IntValue DUELIST_DURABILITY_COST;

    public static ForgeConfigSpec.DoubleValue THREAT_RESPONSE_BONUS_DAMAGE;
    public static ForgeConfigSpec.BooleanValue THREAT_RESPONSE_REQUIRE_FULLY_CHARGED;
    public static ForgeConfigSpec.IntValue THREAT_RESPONSE_DURABILITY_COST;

    public static ForgeConfigSpec.DoubleValue REACH_ADVANTAGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.DoubleValue REACH_ADVANTAGE_MIN_DISTANCE;
    public static ForgeConfigSpec.DoubleValue REACH_ADVANTAGE_MAX_DISTANCE;
    public static ForgeConfigSpec.BooleanValue REACH_ADVANTAGE_REQUIRE_FULLY_CHARGED;
    public static ForgeConfigSpec.IntValue REACH_ADVANTAGE_DURABILITY_COST;

    public static ForgeConfigSpec.IntValue AFTER_STRIKE_REQUIRED_HITS;
    public static ForgeConfigSpec.IntValue AFTER_STRIKE_STRIKES_COUNT;
    public static ForgeConfigSpec.IntValue AFTER_STRIKE_STRIKES_INTERVAL;
    public static ForgeConfigSpec.DoubleValue AFTER_STRIKE_INITIAL_MODIFIER;
    public static ForgeConfigSpec.DoubleValue AFTER_STRIKE_DECAY_FACTOR;
    public static ForgeConfigSpec.BooleanValue AFTER_STRIKE_REQUIRES_CHARGED;
    public static ForgeConfigSpec.BooleanValue AFTER_STRIKE_CRITICAL_TRIGGERS;
    public static ForgeConfigSpec.IntValue AFTER_STRIKE_DURABILITY_COST;

    public static ForgeConfigSpec.IntValue BUSTER_STRIKE_REQUIRED_HITS;
    public static ForgeConfigSpec.DoubleValue BUSTER_STRIKE_BONUS_MULTIPLIER;
    public static ForgeConfigSpec.IntValue BUSTER_STRIKE_DURABILITY_PENALTY;
    public static ForgeConfigSpec.DoubleValue BUSTER_STRIKE_REMOVE_CHANCE;
    public static ForgeConfigSpec.BooleanValue BUSTER_STRIKE_REQUIRES_CHARGED;

    public static ForgeConfigSpec.ConfigValue<String> QUICK_CRAFTING_INGREDIENT;
    public static ForgeConfigSpec.IntValue QUICK_CRAFTING_INGREDIENT_COUNT;
    public static ForgeConfigSpec.ConfigValue<String> QUICK_CRAFTING_RESULT;
    public static ForgeConfigSpec.IntValue QUICK_CRAFTING_RESULT_COUNT;
    public static ForgeConfigSpec.IntValue QUICK_CRAFTING_USE_DURATION;
    public static ForgeConfigSpec.IntValue QUICK_CRAFTING_DURABILITY_COST;
    public static ForgeConfigSpec.IntValue QUICK_CRAFTING_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue QUICK_CRAFTING_GLOBAL_COOLDOWN;

    public static ForgeConfigSpec.DoubleValue AQUATIC_GRUDGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue AQUATIC_GRUDGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue ARTHROPOD_GRUDGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue ARTHROPOD_GRUDGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue UNDEAD_GRUDGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue UNDEAD_GRUDGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue TRAITOR_GRUDGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue TRAITOR_GRUDGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue SNOUT_GRUDGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue SNOUT_GRUDGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue BONE_GRUDGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue BONE_GRUDGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue ANTI_AERIAL_BONUS_DAMAGE;
    public static ForgeConfigSpec.IntValue ANTI_AERIAL_DURABILITY_COST;

    public static ForgeConfigSpec.DoubleValue ROTTEN_GRUDGE_BONUS_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ARMOR_BREAKER_CHANCE;
    public static ForgeConfigSpec.IntValue ARMOR_BREAKER_DURABILITY_DAMAGE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ARMOR_BREAKER_SLOTS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ARMOR_BREAKER_IMMUNE_ITEMS;
    public static ForgeConfigSpec.DoubleValue BLADE_BREAKER_CHANCE;
    public static ForgeConfigSpec.IntValue BLADE_BREAKER_DURABILITY_DAMAGE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> BLADE_BREAKER_IMMUNE_ITEMS;
    public static ForgeConfigSpec.DoubleValue ACROBATIC_LUNGE_STRENGTH;
    public static ForgeConfigSpec.DoubleValue ACROBATIC_LUNGE_MAX_DISTANCE;
    public static ForgeConfigSpec.DoubleValue ACROBATIC_LUNGE_MAX_VERTICAL_PULL;
    public static ForgeConfigSpec.DoubleValue ACROBATIC_LUNGE_DISTANCE_SCALING;
    public static ForgeConfigSpec.IntValue ACROBATIC_LUNGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue DEXTEROUS_LUNGE_PULL_STRENGTH;
    public static ForgeConfigSpec.DoubleValue DEXTEROUS_LUNGE_ATTRACT_STRENGTH;
    public static ForgeConfigSpec.DoubleValue DEXTEROUS_LUNGE_MAX_DISTANCE;
    public static ForgeConfigSpec.DoubleValue DEXTEROUS_LUNGE_MAX_VERTICAL_PULL;
    public static ForgeConfigSpec.DoubleValue DEXTEROUS_LUNGE_DISTANCE_SCALING;
    public static ForgeConfigSpec.IntValue DEXTEROUS_LUNGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue PULL_LUNGE_STRENGTH;
    public static ForgeConfigSpec.DoubleValue PULL_LUNGE_MAX_DISTANCE;
    public static ForgeConfigSpec.DoubleValue PULL_LUNGE_MAX_VERTICAL_PULL;
    public static ForgeConfigSpec.DoubleValue PULL_LUNGE_DISTANCE_SCALING;
    public static ForgeConfigSpec.IntValue PULL_LUNGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue DISENGAGE_STRENGTH;
    public static ForgeConfigSpec.DoubleValue DISENGAGE_MAX_DISTANCE;
    public static ForgeConfigSpec.DoubleValue DISENGAGE_MAX_VERTICAL_PUSH;
    public static ForgeConfigSpec.DoubleValue DISENGAGE_DISTANCE_SCALING;
    public static ForgeConfigSpec.IntValue DISENGAGE_DURABILITY_COST;
    public static ForgeConfigSpec.DoubleValue DISARM_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DISARM_NON_DISARMABLE_ITEMS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DISARM_NON_DISARMABLE_ENTITIES;
    public static ForgeConfigSpec.DoubleValue DISMOUNT_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DISMOUNT_NON_DISMOUNTABLE_ENTITIES;
    public static ForgeConfigSpec.DoubleValue DISABLING_STRIKE_CHANCE;
    public static ForgeConfigSpec.IntValue DISABLING_STRIKE_COOLDOWN;
    public static ForgeConfigSpec.DoubleValue THROUGH_STRIKE_CHANCE;
    public static ForgeConfigSpec.DoubleValue CLEANSING_STRIKE_CHANCE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CLEANSING_STRIKE_BLACKLISTED_EFFECTS;
    public static ForgeConfigSpec.DoubleValue OVERWHELMING_STRIKE_CHANCE;
    public static ForgeConfigSpec.IntValue OVERWHELMING_STRIKE_DURATION;
    public static ForgeConfigSpec.BooleanValue SUPPRESSING_STRIKE;
    public static ForgeConfigSpec.DoubleValue SUPPRESSING_STRIKE_CHANCE;
    public static ForgeConfigSpec.IntValue SUPPRESSING_STRIKE_DURATION;
    public static ForgeConfigSpec.IntValue ROTTEN_GRUDGE_DURABILITY_COST;

    public static ForgeConfigSpec.BooleanValue FRAGILITY;
    public static ForgeConfigSpec.BooleanValue SLIPPERY;
    public static ForgeConfigSpec.BooleanValue EXHAUSTING;
    public static ForgeConfigSpec.BooleanValue BRITTLE_HANDLE;
    public static ForgeConfigSpec.BooleanValue BARBED_HANDLE;
    public static ForgeConfigSpec.BooleanValue OVERSTRAIN;
    public static ForgeConfigSpec.BooleanValue UNSTABLE_EDGE;
    public static ForgeConfigSpec.BooleanValue DETONATING;
    public static ForgeConfigSpec.BooleanValue DECAPITATION;
    public static ForgeConfigSpec.DoubleValue DECAPITATION_CHANCE;
    public static ForgeConfigSpec.DoubleValue DECAPITATION_CRITICAL_MULTIPLIER;

    public static ForgeConfigSpec.BooleanValue QUICK_SWAP;
    public static ForgeConfigSpec.BooleanValue QUICK_SWAP_REQUIRE_CROUCH;
    public static ForgeConfigSpec.IntValue QUICK_SWAP_MAIN_HAND_COOLDOWN;
    public static ForgeConfigSpec.IntValue QUICK_SWAP_OFF_HAND_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue QUICK_SWAP_GLOBAL_COOLDOWN;
    public static ForgeConfigSpec.EnumValue<ModEnums.QuickSwapMode> QUICK_SWAP_ACTIVATION_MODE;
    public static ForgeConfigSpec.IntValue QUICK_SWAP_CHARGE_DURATION_TICKS;
    public static ForgeConfigSpec.EnumValue<UseAnim> QUICK_SWAP_CHARGE_ANIMATION;

    public static ForgeConfigSpec.IntValue RAPID_BOOST_MAX_HITS;
    public static ForgeConfigSpec.DoubleValue RAPID_BOOST_INCREMENT;

    public static ForgeConfigSpec.IntValue POWER_BOOST_MAX_HITS;
    public static ForgeConfigSpec.DoubleValue POWER_BOOST_INCREMENT;
    public static ForgeConfigSpec.DoubleValue FRAGILITY_BREAK_CHANCE;
    public static ForgeConfigSpec.DoubleValue FRAGILITY_MIN_DURABILITY_THRESHOLD;
    public static ForgeConfigSpec.DoubleValue FRAGILITY_REMAINING_ITEM_CHANCE;
    public static ForgeConfigSpec.DoubleValue SLIPPERY_CHANCE;
    public static ForgeConfigSpec.DoubleValue SLIPPERY_THROW_DISTANCE;
    public static ForgeConfigSpec.DoubleValue EXHAUSTING_EXHAUSTION;
    public static ForgeConfigSpec.IntValue BRITTLE_HANDLE_EXTRA_DURABILITY;
    public static ForgeConfigSpec.DoubleValue BARBED_HANDLE_DAMAGE_RETURN_FACTOR;
    public static ForgeConfigSpec.DoubleValue OVERSTRAIN_CHANCE;
    public static ForgeConfigSpec.IntValue OVERSTRAIN_EFFECT_DURATION;
    public static ForgeConfigSpec.IntValue OVERSTRAIN_EFFECT_AMPLIFIER;
    public static ForgeConfigSpec.DoubleValue UNSTABLE_EDGE_MIN_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue UNSTABLE_EDGE_MAX_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue DETONATING_EXPLODE_CHANCE;
    public static ForgeConfigSpec.DoubleValue DETONATING_EXPLOSION_POWER;
    public static ForgeConfigSpec.BooleanValue DETONATING_BREAK_BLOCKS;
    public static ForgeConfigSpec.BooleanValue DETONATING_DAMAGE_OWNER;

    public static void register(ForgeConfigSpec.Builder builder) {
        builder.push("Weapon Traits Handler");
        builder.push("Traits Global Toggles");
        builder.comment("These global toggles have PRIORITY over any per-item NBT/JSON configuration. Disabling a trait here disables it entirely regardless of item data.");
        builder.push("Projectile Traits Toggles");
        THROWABLE = builder.comment("Enable or disable the Throwable trait globally").define("Throwable", true);
        THROWBACK = builder.comment("Enable or disable the Throwback trait globally").define("Throwback", true);
        COLLECTOR = builder.comment("Enable or disable the Collector trait globally").define("Collector", true);
        DISARMING_SHOT = builder.comment("Enable or disable the Disarming Shot trait globally").define("Disarming Shot", true);
        DISABLING_SHOT = builder.comment("Enable or disable the Disabling Shot trait globally").define("Disabling Shot", true);
        SWEEPING_SHOT = builder.comment("Enable or disable the Sweeping Shot trait globally").define("Sweeping Shot", true);
        BACKSTAB_SHOT = builder.comment("Enable or disable the Backstab Shot trait globally").define("Backstab Shot", true);
        PIERCING_SHOT = builder.comment("Enable or disable the Piercing Shot trait globally").define("Piercing Shot", true);
        builder.pop();
        builder.push("Item Traits Toggles");
        builder.comment("These global toggles have PRIORITY over per-item NBT/JSON configuration.");
        GUARD_STANCE = builder.comment("Enables the Guard Stance blocking system for weapons with blocking capability")
                .define("Guard Stance", true);
        PARRY_GUARD = builder.comment("Enables the Parry Guard blocking system for weapons with parry guard capability")
                .define("Parry Guard", true);
        PIERCER_STRIKE = builder.comment("Enable or disable the Piercer Strike trait globally").define("Piercer Strike", true);
        DUELIST = builder.comment("Enable or disable the Duelist trait globally").define("Duelist", true);
        THREAT_RESPONSE = builder.comment("Enable or disable the Threat Response trait globally").define("Threat Response", true);
        REACH_ADVANTAGE = builder.comment("Enable or disable the Reach Advantage trait globally").define("Reach Advantage", true);
        AFTER_STRIKE = builder.comment("Enable or disable the After Strike trait globally").define("After Strike", true);
        QUICK_CRAFTING = builder.comment("Enable or disable the Quick Crafting trait globally").define("Quick Crafting", true);
        QUICK_SWAP = builder.comment("Enable or disable the Quick Swap trait globally").define("Quick Swap", true);
        RAPID_BOOST = builder.comment("Enable or disable the Rapid Boost trait globally").define("Rapid Boost", true);
        POWER_BOOST = builder.comment("Enable or disable the Power Boost trait globally").define("Power Boost", true);
        BUSTER_STRIKE = builder.comment("Enable or disable the Buster Strike trait globally").define("Buster Strike", true);
        AQUATIC_GRUDGE = builder.comment("Enable or disable the Aquatic Grudge trait globally").define("Aquatic Grudge", true);
        ARTHROPOD_GRUDGE = builder.comment("Enable or disable the Arthropod Grudge trait globally").define("Arthropod Grudge", true);
        UNDEAD_GRUDGE = builder.comment("Enable or disable the Undead Grudge trait globally").define("Undead Grudge", true);
        TRAITOR_GRUDGE = builder.comment("Enable or disable the Traitor Grudge trait globally").define("Traitor Grudge", true);
        SNOUT_GRUDGE = builder.comment("Enable or disable the Snout Grudge trait globally").define("Snout Grudge", true);
        BONE_GRUDGE = builder.comment("Enable or disable the Bone Grudge trait globally").define("Bone Grudge", true);
        ANTI_AERIAL = builder.comment("Enable or disable the Anti Aerial trait globally").define("Anti Aerial", true);

        ROTTEN_GRUDGE = builder.comment("Enable or disable the Rotten Grudge trait globally").define("Rotten Grudge", true);
        ARMOR_BREAKER = builder.comment("Enable or disable the Armor Breaker trait globally").define("Armor Breaker", true);
        BLADE_BREAKER = builder.comment("Enable or disable the Blade Breaker trait globally").define("Blade Breaker", true);
        ACROBATIC_LUNGE = builder.comment("Enable or disable the Acrobatic Lunge trait globally").define("Acrobatic Lunge", true);
        DEXTEROUS_LUNGE = builder.comment("Enable or disable the Dexterous Lunge trait globally").define("Dexterous Lunge", true);
        PULL_LUNGE = builder.comment("Enable or disable the Pull Lunge trait globally").define("Pull Lunge", true);
        DISENGAGE = builder.comment("Enable or disable the Disengage trait globally").define("Disengage", true);
        DISARM = builder.comment("Enable or disable the Disarm trait globally").define("Disarm", true);
        DISMOUNT = builder.comment("Enable or disable the Dismount trait globally").define("Dismount", true);
        DISABLING_STRIKE = builder.comment("Enable or disable the Disabling Strike trait globally").define("Disabling Strike", true);
        THROUGH_STRIKE = builder.comment("Enable or disable the Through Strike trait globally").define("Through Strike", true);
        CLEANSING_STRIKE = builder.comment("Enable or disable the Cleansing Strike trait globally").define("Cleansing Strike", true);
        OVERWHELMING_STRIKE = builder.comment("Enable or disable the Overwhelming Strike trait globally").define("Overwhelming Strike", true);
        SUPPRESSING_STRIKE = builder.comment("Enable or disable the Suppressing Strike trait globally").define("Suppressing Strike", true);
        SMASH_STRIKE = builder.comment("Enable or disable the Smash Strike trait globally").define("Smash Strike", true);
        SHOCK_IMPACT = builder.comment("Enable or disable the Shock Impact trait globally").define("Shock Impact", true);
        BACKSTAB = builder.comment("Enable or disable the Backstab trait globally").define("Backstab", true);
        HEAVY_HANDED = builder.comment("Enable or disable the Heavy Handed trait globally").define("Heavy Handed", true);
        SPARRING_STRIKE = builder.comment("Enable or disable the Sparring Strike trait globally").define("Sparring Strike", true);
        HARVEST_SWEEP = builder.comment("Enable or disable the Harvest Sweep trait globally").define("Harvest Sweep", true);
        SLASH_ASSAULT = builder.comment("Enable or disable the Slash Assault trait globally").define("Slash Assault", true);
        PIERCING_ASSAULT = builder.comment("Enable or disable the Piercing Assault trait globally").define("Piercing Assault", true);
        WILD_SWEEP = builder.comment("Enable or disable the Wild Sweep trait globally").define("Wild Sweep", true);
        WHIRLING_STRIKE = builder.comment("Enable or disable the Whirling Strike trait globally").define("Whirling Strike", true);
        FRAGILITY = builder.comment("Enable or disable the Fragility trait globally").define("Fragility", true);
        SLIPPERY = builder.comment("Enable or disable the Slippery trait globally").define("Slippery", true);
        EXHAUSTING = builder.comment("Enable or disable the Exhausting trait globally").define("Exhausting", true);
        BRITTLE_HANDLE = builder.comment("Enable or disable the Brittle Handle trait globally").define("Brittle Handle", true);
        BARBED_HANDLE = builder.comment("Enable or disable the Barbed Handle trait globally").define("Barbed Handle", true);
        OVERSTRAIN = builder.comment("Enable or disable the Overstrain trait globally").define("Overstrain", true);
        UNSTABLE_EDGE = builder.comment("Enable or disable the Unstable Edge trait globally").define("Unstable Edge", true);
        DETONATING = builder.comment("Enable or disable the Detonating trait globally").define("Detonating", true);
        DECAPITATION = builder.comment("Enable or disable the Decapitation trait globally").define("Decapitation", true);
        builder.pop();
        builder.pop();
        builder.push("Projectile Traits Settings");
        builder.push("Throwable");
        ThrowableConfig.register(builder);
        builder.pop();
        builder.push("Throwback");
        builder.push("Hunter's Boomerang Projectile");
        THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MIN_RANGE = builder.comment("Minimum range (blocks) before throwback activates").defineInRange("Min Range", 6, 1, 50);
        THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MAX_RANGE = builder.comment("Maximum range (blocks) for throwback").defineInRange("Max Range", 24, 3, 100);
        THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_MAX_RANGE = builder.comment("Return to owner when reaching max range").define("Return On Max Range", true);
        THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_ENTITY_HIT = builder.comment("Return to owner after hitting an entity").define("Return On Entity Hit", true);
        THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_ON_BLOCK_HIT = builder.comment("Return to owner after hitting a block").define("Return On Block Hit", false);
        THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_SPEED = builder.comment("How fast the projectile returns").defineInRange("Return Speed", 0.85, 0.1, 2.0);
        builder.pop();
        builder.push("Ring Projectile");
        THROWBACK_RING_PROJECTILE_MIN_RANGE = builder.comment("Minimum range (blocks) before throwback activates for Ring").defineInRange("Min Range", 4, 1, 40);
        THROWBACK_RING_PROJECTILE_MAX_RANGE = builder.comment("Maximum range (blocks) for throwback of Ring").defineInRange("Max Range", 16, 3, 80);
        THROWBACK_RING_PROJECTILE_RETURN_ON_MAX_RANGE = builder.comment("Return Ring to owner when reaching max range").define("Return On Max Range", true);
        THROWBACK_RING_PROJECTILE_RETURN_ON_ENTITY_HIT = builder.comment("Return Ring to owner after hitting an entity").define("Return On Entity Hit", true);
        THROWBACK_RING_PROJECTILE_RETURN_ON_BLOCK_HIT = builder.comment("Return Ring to owner after hitting a block").define("Return On Block Hit", false);
        THROWBACK_RING_PROJECTILE_RETURN_SPEED = builder.comment("Return speed for Ring projectile").defineInRange("Return Speed", 0.95, 0.1, 2.0);
        builder.pop();
        builder.pop();
        builder.push("Collector");
        builder.push("Hunter's Boomerang Projectile");
        COLLECTOR_HUNTERS_BOOMERANG_PROJECTILE_MAX_ITEMS = builder.comment("Maximum items the Collector trait can pick up per throw").defineInRange("Max Collected Items", 3, 0, 6);
        builder.pop();
        builder.pop();
        builder.push("Disarming Shot");
        builder.push("Hunter's Boomerang Projectile");
        DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_CHANCE = builder.comment("Chance to disarm on hit (0.0-1.0)").defineInRange("Disarm Chance", 0.05, 0.0, 1.0);
        DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_REQUIRE_CRITICAL = builder.comment("Only disarm on critical hits").define("Require Critical Hit", false);
        DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_DURABILITY_COST = builder.comment("Extra durability cost when disarming shot triggers").defineInRange("Extra Durability Cost", 1,
                0, 50);
        DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_MOUNT_ITEM = builder.comment("Mount the disarmed item on the projectile").define("Mount Disarmed Item", true);
        builder.pop();
        builder.pop();
        builder.push("Disabling Shot");
        builder.push("Shuriken Projectile");
        DISABLING_SHOT_SHURIKEN_PROJECTILE_CHANCE = builder.comment("Chance to apply disabling effect (0.0-1.0)").defineInRange("Disable Chance", 0.25, 0.0, 1.0);
        DISABLING_SHOT_SHURIKEN_PROJECTILE_COOLDOWN = builder.comment("Cooldown in ticks between disabling shot triggers").defineInRange("Disable Cooldown Ticks", 60, 0, 1200);
        builder.pop();
        builder.pop();
        builder.push("Sweeping Shot");
        builder.push("Giant Shuriken Projectile");
        SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_RADIUS = builder.comment("AOE radius for sweeping shot effect").defineInRange("Sweeping Radius", 2.0, 0.5, 10.0);
        SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_DAMAGE_FACTOR = builder.comment("Damage multiplier for AOE sweeping hit").defineInRange("Sweeping Damage Factor", 0.5,
                0.0, 2.0);
        builder.pop();
        builder.pop();
        builder.push("Backstab Shot");
        builder.push("Pronged Kunai Projectile");
        BACKSTAB_SHOT_PRONGED_KUNAI_PROJECTILE_MULTIPLIER = builder.comment("Damage multiplier when hitting from behind").defineInRange("Backstab Damage Multiplier", 1.5,
                1.0, 5.0);
        builder.pop();
        builder.pop();
        builder.push("Piercing Shot");
        builder.push("Kunai Projectile");
        PIERCING_SHOT_KUNAI_PROJECTILE_CHANCE = builder.comment("Chance to pierce through targets (0.0-1.0)").defineInRange("Piercing Chance", 0.5, 0.0, 1.0);
        PIERCING_SHOT_KUNAI_PROJECTILE_BONUS_DAMAGE = builder.comment("Extra damage when piercing shot triggers").defineInRange("Piercing Bonus Damage", 2.0, 0.0, 100.0);
        builder.pop();
        builder.pop();
        builder.pop();
        builder.push("Item Traits Settings");
        builder.push("Guard Stance");
        GUARD_STANCE_COOLDOWN = builder.comment("Apply cooldown after blocking with Guard Stance")
                .define("Guard Stance Cooldown", true);
        GUARD_STANCE_AREA_DAMAGE = builder.comment("Apply area damage when Guard Stance blocking ends")
                .define("Guard Stance Area Damage", true);
        GUARD_STANCE_APPLY_FIRST_PERSON_TRANSFORM = builder
                .comment("Apply first-person sword blocking transformation in ItemInHandRenderer")
                .define("Guard Stance Apply First Person Transform", true);
        GUARD_STANCE_COOLDOWN_TICKS = builder.comment("Cooldown in ticks after blocking ends")
                .defineInRange("Cooldown Ticks", 20, 0, 72000);
        GUARD_STANCE_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the guard_stance trait in the inventory when the cooldown triggers, preventing abuse by switching between multiple guard stance weapons")
                .define("Global Cooldown", true);
        GUARD_STANCE_AREA_DAMAGE_MULTIPLIER = builder
                .comment("Area damage multiplier applied to weapon damage when blocking ends")
                .defineInRange("Area Damage Multiplier", 0.35, 0.0, 100.0);
        GUARD_STANCE_KNOCKBACK_FORCE = builder.comment("Knockback force applied to enemies in the blocking area")
                .defineInRange("Knockback Force", 0.4, 0.0, 10.0);
        GUARD_STANCE_AREA_RANGE = builder.comment("Radius of the blocking area effect")
                .defineInRange("Area Range", 1.5, 0.0, 10.0);
        GUARD_STANCE_BLOCK_DAMAGE_REDUCTION = builder
                .comment("Damage reduction applied when blocking (0.5 = 50% reduction)")
                .defineInRange("Block Damage Reduction", 1.0, 0.0, 1.0);
        GUARD_STANCE_DAMAGE_PER_BLOCK = builder.comment("Durability damage per blocked attack")
                .defineInRange("Damage Per Block", 1, 0, 100);
        GUARD_STANCE_DAMAGE_ON_STOP = builder
                .comment("Durability damage on stop blocking (multiplied by incoming damage)")
                .defineInRange("Damage On Stop", 1, 0, 100);
        GUARD_STANCE_PARTICLE_SIZE = builder.comment("Particle size for the blocking sweep effect")
                .defineInRange("Particle Size", 1.0, 0.1, 10.0);
        GUARD_STANCE_PARTICLE_DISTANCE = builder.comment("Distance for blocking sweep particles")
                .defineInRange("Particle Distance", 1.5, 0.1, 10.0);
        GUARD_STANCE_NO_DURABILITY_BREAK_CHANCE = builder.comment(
                "Chance for items without durability to break when blocking an attack (0.0 = disabled, 1.0 = always breaks)")
                .defineInRange("No Durability Break Chance", 0.0, 0.0, 1.0);
        builder.pop();
        builder.push("Parry Guard");
        PARRY_GUARD_COOLDOWN_TICKS = builder.comment("Cooldown in ticks after a successful parry guard")
                .defineInRange("Cooldown Ticks", 20, 0, 72000);
        PARRY_GUARD_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the parry_guard trait in the inventory when a successful parry triggers, preventing abuse by switching between multiple parry guard weapons")
                .define("Global Cooldown", true);
        PARRY_GUARD_BLOCK_DAMAGE_REDUCTION = builder
                .comment("Damage reduction applied when parry guarding (0.5 = 50% reduction)")
                .defineInRange("Block Damage Reduction", 0.5, 0.0, 1.0);
        PARRY_GUARD_DAMAGE_PER_BLOCK = builder.comment("Durability damage per parry guarded attack")
                .defineInRange("Damage Per Block", 1, 0, 100);
        PARRY_GUARD_DAMAGE_ON_STOP = builder
                .comment("Durability damage on parry guard end (when no attack is blocked)")
                .defineInRange("Damage On Stop", 1, 0, 100);

        PARRY_GUARD_NO_DURABILITY_BREAK_CHANCE = builder.comment(
                "Chance for items without durability to break when parry guarding an attack (0.0 = disabled, 1.0 = always breaks)")
                .defineInRange("No Durability Break Chance", 0.0, 0.0, 1.0);
        builder.pop();
        builder.push("Piercer Strike");
        PIERCER_STRIKE_BONUS_DAMAGE = builder.comment("Bonus damage dealt to armored targets").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        PIERCER_STRIKE_MIN_ARMOR = builder.comment("Minimum armor value the target must have for the trait to trigger")
                .defineInRange("Min Armor", 4, 0, 30);
        PIERCER_STRIKE_REQUIRE_FULLY_CHARGED = builder.comment("Require fully charged attack to trigger").define("Require Fully Charged", true);
        PIERCER_STRIKE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Duelist");
        DUELIST_BONUS_DAMAGE = builder.comment("Bonus damage when not targeting the attacking entity").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        DUELIST_REQUIRE_FULLY_CHARGED = builder.comment("Require fully charged attack to trigger").define("Require Fully Charged", true);
        DUELIST_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Threat Response");
        THREAT_RESPONSE_BONUS_DAMAGE = builder.comment("Bonus damage against the entity that last attacked you").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        THREAT_RESPONSE_REQUIRE_FULLY_CHARGED = builder.comment("Require fully charged attack to trigger").define("Require Fully Charged", true);
        THREAT_RESPONSE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Reach Advantage");
        REACH_ADVANTAGE_BONUS_DAMAGE = builder.comment("Bonus damage per block of distance").defineInRange("Bonus Damage per Block", 0.5, 0.0, 10.0);
        REACH_ADVANTAGE_MIN_DISTANCE = builder.comment("Minimum distance in blocks to trigger").defineInRange("Min Distance Blocks", 2.0, 0.0, 100.0);
        REACH_ADVANTAGE_MAX_DISTANCE = builder.comment("Maximum distance in blocks for bonus").defineInRange("Max Distance Blocks", 16.0, 0.0, 100.0);
        REACH_ADVANTAGE_REQUIRE_FULLY_CHARGED = builder.comment("Require fully charged attack to trigger").define("Require Fully Charged", true);
        REACH_ADVANTAGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("After Strike");
        AFTER_STRIKE_REQUIRED_HITS = builder.comment("Hits required to trigger After Strike flurry").defineInRange("Required Hits", 3, 1, 100);
        AFTER_STRIKE_STRIKES_COUNT = builder.comment("Number of rapid strikes in the flurry").defineInRange("Flurry Strikes Count", 1, 1, 10);
        AFTER_STRIKE_STRIKES_INTERVAL = builder.comment("Ticks between each flurry strike").defineInRange("Strikes Interval Ticks", 5, 1, 100);
        AFTER_STRIKE_INITIAL_MODIFIER = builder.comment("Damage modifier for the first flurry hit").defineInRange("Initial Damage Modifier", 0.5, 0.0, 5.0);
        AFTER_STRIKE_DECAY_FACTOR = builder.comment("Damage decay per consecutive flurry hit").defineInRange("Damage Decay Factor", 0.5, 0.0, 1.0);
        AFTER_STRIKE_REQUIRES_CHARGED = builder.comment("Require fully charged attack to trigger flurry").define("Require Fully Charged", true);
        AFTER_STRIKE_CRITICAL_TRIGGERS = builder.comment(
                "If true, landing a critical hit will instantly trigger the After Strike flurry, bypassing the hit counter.")
                .define("Critical Triggers Flurry", true);
        AFTER_STRIKE_DURABILITY_COST = builder.comment("Durability cost per flurry trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Buster Strike");
        BUSTER_STRIKE_REQUIRED_HITS = builder.comment("Hits required to trigger Buster Strike").defineInRange("Required Hits", 3, 1, 100);
        BUSTER_STRIKE_BONUS_MULTIPLIER = builder.comment("Bonus damage multiplier for the empowered hit").defineInRange("Bonus Damage Multiplier", 0.7, 0.0, 5.0);
        BUSTER_STRIKE_DURABILITY_PENALTY = builder.comment("Extra durability damage when triggering").defineInRange("Durability Penalty", 3, 0, 100);
        BUSTER_STRIKE_REMOVE_CHANCE = builder.comment("Chance to reset hit counter after trigger (0.0-1.0)").defineInRange("Tag Remove Chance", 0.3, 0.0, 1.0);
        BUSTER_STRIKE_REQUIRES_CHARGED = builder.comment("Require fully charged attack to trigger").define("Require Fully Charged", true);
        builder.pop();
        builder.push("Quick Crafting");
        QUICK_CRAFTING_INGREDIENT = builder.comment("Default ingredient item ID").define("Default Ingredient Item ID", "jaams_weaponry:short_stick");
        QUICK_CRAFTING_INGREDIENT_COUNT = builder.comment("Default ingredient count").defineInRange("Default Ingredient Count", 1, 1, 64);
        QUICK_CRAFTING_RESULT = builder.comment("Default result item ID").define("Default Result Item ID", "jaams_weaponry:stake");
        QUICK_CRAFTING_RESULT_COUNT = builder.comment("Default result count").defineInRange("Default Result Count", 1, 1, 64);
        QUICK_CRAFTING_USE_DURATION = builder.comment("Ticks required to perform the Quick Crafting action").defineInRange("Use Duration Ticks", 30, 1, 72000);
        QUICK_CRAFTING_DURABILITY_COST = builder.comment("Durability cost per use").defineInRange("Durability Cost", 1, 0, 100);
        QUICK_CRAFTING_COOLDOWN = builder.comment("Cooldown in ticks between uses").defineInRange("Cooldown Ticks", 20, 0, 1200);
        QUICK_CRAFTING_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the quick_crafting trait in the inventory when the cooldown triggers, preventing abuse by switching between multiple quick crafting weapons")
                .define("Global Cooldown", true);
        builder.pop();
        builder.push("Aquatic Grudge");
        AQUATIC_GRUDGE_BONUS_DAMAGE = builder.comment("Bonus damage vs aquatic mobs").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        AQUATIC_GRUDGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Arthropod Grudge");
        ARTHROPOD_GRUDGE_BONUS_DAMAGE = builder.comment("Bonus damage vs arthropods").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        ARTHROPOD_GRUDGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Undead Grudge");
        UNDEAD_GRUDGE_BONUS_DAMAGE = builder.comment("Bonus damage vs undead").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        UNDEAD_GRUDGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Traitor Grudge");
        TRAITOR_GRUDGE_BONUS_DAMAGE = builder.comment("Bonus damage vs illagers").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        TRAITOR_GRUDGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Snout Grudge");
        SNOUT_GRUDGE_BONUS_DAMAGE = builder.comment("Bonus damage vs piglins").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        SNOUT_GRUDGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Bone Grudge");
        BONE_GRUDGE_BONUS_DAMAGE = builder.comment("Bonus damage vs skeletons").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        BONE_GRUDGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Anti Aerial");
        ANTI_AERIAL_BONUS_DAMAGE = builder.comment("Bonus damage vs flying mobs").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        ANTI_AERIAL_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();

        builder.push("Rotten Grudge");
        ROTTEN_GRUDGE_BONUS_DAMAGE = builder.comment("Bonus damage vs zombies").defineInRange("Bonus Damage", 5.0, 0.0, 100.0);
        ROTTEN_GRUDGE_DURABILITY_COST = builder.comment("Durability cost per trigger").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Armor Breaker");
        ARMOR_BREAKER_CHANCE = builder.comment("Chance to damage armor on hit").defineInRange("Chance", 0.25, 0.0, 1.0);
        ARMOR_BREAKER_DURABILITY_DAMAGE = builder.comment("Durability damage dealt to armor").defineInRange("Armor Durability Damage", 1, 1, 100);
        ARMOR_BREAKER_SLOTS = builder.defineList("Affected Slots", List.of("head", "chest", "legs", "feet"),
                (obj) -> obj instanceof String);
        ARMOR_BREAKER_IMMUNE_ITEMS = builder.comment(
                "Items that cannot be damaged by Armor Breaker (registry names, e.g. minecraft:netherite_helmet)")
                .defineList("Immune Items", List.of(), (obj) -> obj instanceof String);
        builder.pop();
        builder.push("Blade Breaker");
        BLADE_BREAKER_CHANCE = builder.comment("Chance to damage held item on hit").defineInRange("Chance", 0.25, 0.0, 1.0);
        BLADE_BREAKER_DURABILITY_DAMAGE = builder.comment("Durability damage dealt to held item").defineInRange("Held Item Durability Damage", 1, 1, 100);
        BLADE_BREAKER_IMMUNE_ITEMS = builder.comment(
                "Items that cannot be damaged by Blade Breaker (registry names, e.g. minecraft:netherite_sword)")
                .defineList("Immune Items", List.of(), (obj) -> obj instanceof String);
        builder.pop();
        builder.push("Acrobatic Lunge");
        ACROBATIC_LUNGE_STRENGTH = builder.comment("Pull strength toward target").defineInRange("Pull Strength", 2.5, 0.0, 5.0);
        ACROBATIC_LUNGE_MAX_DISTANCE = builder.comment("Max pull distance in blocks").defineInRange("Max Pull Distance", 20.0, 1.0, 100.0);
        ACROBATIC_LUNGE_MAX_VERTICAL_PULL = builder.comment("Max vertical pull distance").defineInRange("Max Vertical Pull", 0.5, 0.0, 5.0);
        ACROBATIC_LUNGE_DISTANCE_SCALING = builder.comment("Distance scaling factor").defineInRange("Distance Scaling", 1.0, 0.0, 5.0);
        ACROBATIC_LUNGE_DURABILITY_COST = builder.comment("Durability cost per use").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Dexterous Lunge");
        DEXTEROUS_LUNGE_PULL_STRENGTH = builder.comment("Pull strength toward target").defineInRange("Pull Strength", 3.5, 0.0, 5.0);
        DEXTEROUS_LUNGE_ATTRACT_STRENGTH = builder.comment("Attract strength for item pickup").defineInRange("Attract Strength", 2.5, 0.0, 5.0);
        DEXTEROUS_LUNGE_MAX_DISTANCE = builder.comment("Max pull distance in blocks").defineInRange("Max Pull Distance", 20.0, 1.0, 100.0);
        DEXTEROUS_LUNGE_MAX_VERTICAL_PULL = builder.comment("Max vertical pull distance").defineInRange("Max Vertical Pull", 0.5, 0.0, 5.0);
        DEXTEROUS_LUNGE_DISTANCE_SCALING = builder.comment("Distance scaling factor").defineInRange("Distance Scaling", 1.0, 0.0, 5.0);
        DEXTEROUS_LUNGE_DURABILITY_COST = builder.comment("Durability cost per use").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Pull Lunge");
        PULL_LUNGE_STRENGTH = builder.comment("Pull strength toward target").defineInRange("Pull Strength", 2.5, 0.0, 5.0);
        PULL_LUNGE_MAX_DISTANCE = builder.comment("Max pull distance in blocks").defineInRange("Max Pull Distance", 20.0, 1.0, 100.0);
        PULL_LUNGE_MAX_VERTICAL_PULL = builder.comment("Max vertical pull distance").defineInRange("Max Vertical Pull", 0.5, 0.0, 5.0);
        PULL_LUNGE_DISTANCE_SCALING = builder.comment("Distance scaling factor").defineInRange("Distance Scaling", 1.0, 0.0, 5.0);
        PULL_LUNGE_DURABILITY_COST = builder.comment("Durability cost per use").defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Disengage");
        DISENGAGE_STRENGTH = builder.comment("Rebound strength away from target").defineInRange("Rebound Strength", 5.0, 0.0, 20.0);
        DISENGAGE_MAX_DISTANCE = builder.defineInRange("Max Push Distance", 20.0, 1.0, 100.0);
        DISENGAGE_MAX_VERTICAL_PUSH = builder.defineInRange("Max Vertical Push", 0.5, 0.0, 5.0);
        DISENGAGE_DISTANCE_SCALING = builder.defineInRange("Distance Scaling", 1.0, 0.0, 5.0);
        DISENGAGE_DURABILITY_COST = builder.defineInRange("Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Disarm");
        DISARM_CHANCE = builder.comment("Chance to disarm on hit").defineInRange("Disarm Chance", 0.15, 0.0, 1.0);
        DISARM_NON_DISARMABLE_ITEMS = builder.comment("Items that cannot be disarmed (registry names)")
                .defineList("Non-Disarmable Items", List.of(), (obj) -> obj instanceof String);
        DISARM_NON_DISARMABLE_ENTITIES = builder
                .comment("Entities that cannot be disarmed (registry names, e.g. minecraft:zombie)")
                .defineList("Non-Disarmable Entities", List.of("minecraft:wither"), (obj) -> obj instanceof String);
        builder.pop();
        builder.push("Dismount");
        DISMOUNT_CHANCE = builder.comment("Chance to dismount on hit").defineInRange("Dismount Chance", 0.25, 0.0, 1.0);
        DISMOUNT_NON_DISMOUNTABLE_ENTITIES = builder
                .comment("Entities that cannot be dismounted (registry names, e.g. minecraft:player)")
                .defineList("Non-Dismountable Entities", List.of(), (obj) -> obj instanceof String);
        builder.pop();
        builder.push("Disabling Strike");
        DISABLING_STRIKE_CHANCE = builder.comment("Chance to disable on hit").defineInRange("Chance", 0.5, 0.0, 1.0);
        DISABLING_STRIKE_COOLDOWN = builder.comment("Cooldown between uses in ticks").defineInRange("Item Cooldown Ticks", 60, 1, 72000);
        builder.pop();
        builder.push("Through Strike");
        THROUGH_STRIKE_CHANCE = builder.comment("Chance to pierce through target").defineInRange("Chance", 0.5, 0.0, 1.0);
        builder.pop();
        builder.push("Cleansing Strike");
        CLEANSING_STRIKE_CHANCE = builder.comment("Chance to cleanse effects on hit").defineInRange("Chance", 0.1, 0.0, 1.0);
        CLEANSING_STRIKE_BLACKLISTED_EFFECTS = builder
                .comment(
                        "List of potion/effect IDs (resource locations) that Cleansing Strike will NOT remove. E.g. \"minecraft:regeneration\"")
                .defineList("Blacklisted Effects", List.of(), (obj) -> obj instanceof String);
        builder.pop();
        builder.push("Overwhelming Strike");
        OVERWHELMING_STRIKE_CHANCE = builder.comment("Chance to knock out on hit").defineInRange("Chance", 0.05, 0.0, 1.0);
        OVERWHELMING_STRIKE_DURATION = builder.comment("Knockout duration in ticks").defineInRange("Knocked Out Duration Ticks", 60, 1, 72000);
        builder.pop();
        builder.push("Suppressing Strike");
        SUPPRESSING_STRIKE_CHANCE = builder.comment("Chance to suppress on hit").defineInRange("Chance", 0.05, 0.0, 1.0);
        SUPPRESSING_STRIKE_DURATION = builder.comment("Suppression duration in ticks").defineInRange("Incapable Duration Ticks", 100, 1, 72000);
        builder.pop();

        builder.push("Shock Impact");
        SHOCK_IMPACT_MAX_BONUS_DAMAGE = builder.comment("Max bonus damage").defineInRange("Max Bonus Damage", 150.0, 0.0, 500.0);
        SHOCK_IMPACT_MAX_RESIDUAL_DAMAGE = builder.comment("Max residual damage").defineInRange("Max Residual Damage", 35.0, 0.0, 200.0);
        SHOCK_IMPACT_SMASH_RADIUS = builder.comment("Smash AOE radius in blocks").defineInRange("Smash Radius", 3.0, 0.0, 10.0);
        SHOCK_IMPACT_MIN_KNOCKBACK_STRENGTH = builder.comment("Minimum knockback strength").defineInRange("Min Knockback Strength", 0.15, 0.0, 5.0);
        SHOCK_IMPACT_MAX_KNOCKBACK_STRENGTH = builder.comment("Maximum knockback strength").defineInRange("Max Knockback Strength", 0.6, 0.0, 5.0);
        SHOCK_IMPACT_KNOCKBACK_SCALING_FACTOR = builder.comment("Knockback scaling per damage").defineInRange("Knockback Scaling Factor", 0.04, 0.0, 1.0);
        SHOCK_IMPACT_SHAKE_INTENSITY = builder.comment("Screen shake intensity").defineInRange("Screen Shake Intensity", 5.0, 0.0, 10.0);
        SHOCK_IMPACT_SHAKE_RESET_DELAY = builder.comment("Screen shake reset delay in ticks").defineInRange("Screen Shake Reset Delay", 12, 0, 100);
        SHOCK_IMPACT_DURABILITY_DAMAGE_BASE = builder.comment("Base durability damage").defineInRange("Durability Damage Base", 4, 0, 100);
        SHOCK_IMPACT_MAX_DURABILITY_DAMAGE = builder.comment("Max durability damage").defineInRange("Max Durability Damage", 1200, 0, 10000);
        SHOCK_IMPACT_EXHAUSTION = builder.comment("Exhaustion applied on use").defineInRange("Exhaustion", 0.5, 0.0, 10.0);
        SHOCK_IMPACT_DEPLETION_CHANCE = builder.comment("Chance to apply mining fatigue").defineInRange("Depletion Chance", 1.0, 0.0, 1.0);
        SHOCK_IMPACT_DEPLETION_DURATION = builder.comment("Depletion effect duration").defineInRange("Depletion Duration Ticks", 160, 0, 72000);
        SHOCK_IMPACT_DEPLETION_LEVEL = builder.comment("Depletion effect level").defineInRange("Depletion Level", 4, 0, 10);
        SHOCK_IMPACT_DEPLETION_MAX_LEVEL = builder.comment("Depletion max level").defineInRange("Depletion Max Level", 8, 0, 10);
        SHOCK_IMPACT_DEPLETION_MAX_DURATION = builder.comment("Depletion max duration ticks").defineInRange("Depletion Max Duration Ticks", 1000, 0, 72000);
        SHOCK_IMPACT_ENABLE_DEPLETION = builder.comment("Enable mining fatigue on use").define("Enable Depletion", true);
        SHOCK_IMPACT_PLAYER_VERTICAL_IMPULSE = builder.comment("Vertical impulse applied to player").defineInRange("Player Vertical Impulse", 0.9, 0.0, 3.0);
        SHOCK_IMPACT_ENTITY_VERTICAL_IMPULSE = builder.comment("Vertical impulse applied to entities").defineInRange("Entity Vertical Impulse", 0.7, 0.0, 3.0);
        SHOCK_IMPACT_GLOBAL_COOLDOWN = builder.comment(
                        "Apply cooldown to all items with the shock_impact trait in the inventory, preventing abuse by switching between multiple shock impact weapons")
                .define("Global Cooldown", true);
        SHOCK_IMPACT_COOLDOWN_TICKS = builder.comment("Cooldown in ticks between uses").defineInRange("Cooldown Ticks", 160, 0, 1200);
        SHOCK_IMPACT_BASE_DAMAGE_MULTIPLIER = builder.comment("Base damage multiplier").defineInRange("Base Damage Multiplier", 0.6, 0.0, 2.0);
        SHOCK_IMPACT_OFFHAND_COOLDOWN_MULTIPLIER = builder.comment("Offhand cooldown multiplier").defineInRange("Offhand Cooldown Multiplier", 1.5, 0.1, 5.0);
        SHOCK_IMPACT_OFFHAND_POWER_MULTIPLIER = builder.comment("Offhand power multiplier").defineInRange("Offhand Power Multiplier", 0.7, 0.0, 1.0);
        SHOCK_IMPACT_ACTIVATION_MODE = builder.comment(
                "Activation mode: INSTANT_ON_RIGHT_CLICK (right-click while crouching), CHARGE_AND_RELEASE (hold right-click while crouching, release to slam), CHARGE_AND_FINISH_USING (hold until fully charged), CHARGE_RELEASE_AND_FINISH (slam on release or when fully charged)")
                .defineEnum("Activation Mode", ModEnums.ShockImpactMode.INSTANT_ON_RIGHT_CLICK);
        SHOCK_IMPACT_REQUIRE_CROUCHING = builder.comment(
                        "Require the player to be crouching (holding shift) to activate the shock impact")
                .define("Require Crouching", true);
        SHOCK_IMPACT_CHARGE_DURATION_TICKS = builder.comment("Maximum use duration in ticks for charge modes")
                .defineInRange("Charge Duration Ticks", 72000, 20, 72000);
        SHOCK_IMPACT_CHARGE_ANIMATION = builder.comment("Use animation played in charge modes")
                .defineEnum("Charge Animation", UseAnim.BOW);
        SHOCK_IMPACT_MIN_CHARGE_TICKS = builder.comment(
                "Minimum charge ticks required before the shock impact activates in charge modes")
                .defineInRange("Minimum Charge Ticks", 5, 0, 100);
        builder.pop();

        builder.push("Slash Assault");
        SLASH_ASSAULT_DASH_DISTANCE = builder.comment("Dash distance in blocks").defineInRange("Dash Distance", 2.5, 0.1, 5.0);
        SLASH_ASSAULT_RANGE = builder.comment("Slash attack range in blocks").defineInRange("Slash Range", 2.5, 1.0, 10.0);
        SLASH_ASSAULT_SLASH_COOLDOWN = builder.comment("Cooldown after slash in ticks").defineInRange("Slash Cooldown Ticks", 80, 1, 200);
        SLASH_ASSAULT_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the slash_assault trait in the inventory when the cooldown triggers, preventing abuse by switching between multiple slash assault weapons")
                .define("Global Cooldown", true);
        SLASH_ASSAULT_NO_TARGET_COOLDOWN = builder.comment("Cooldown when no target hit").defineInRange("No Target Cooldown Ticks", 40, 1, 200);
        SLASH_ASSAULT_DURABILITY_COST = builder.comment("Durability cost per use").defineInRange("Durability Cost", 2, 0, 100);
        SLASH_ASSAULT_ACTIVATION_MODE = builder.comment(
                "Activation mode: SPRINT_CLICK (sprint + right-click), INSTANT_ON_RIGHT_CLICK (right-click), CHARGE_AND_RELEASE (hold right-click, release to dash), CHARGE_AND_FINISH_USING (hold until fully charged), CHARGE_RELEASE_AND_FINISH (dash on release or when fully charged)")
                .defineEnum("Activation Mode", ModEnums.SlashAssaultMode.SPRINT_CLICK);
        SLASH_ASSAULT_CHARGE_DURATION_TICKS = builder.comment("Maximum use duration in ticks for charge modes")
                .defineInRange("Charge Duration Ticks", 72000, 20, 72000);
        SLASH_ASSAULT_CHARGE_ANIMATION = builder.comment("Use animation played in charge modes")
                .defineEnum("Charge Animation", UseAnim.BOW);
        SLASH_ASSAULT_MIN_DAMAGE = builder.comment("Minimum damage dealt").defineInRange("Minimum Damage", 1.0, 0.0, 10.0);
        SLASH_ASSAULT_MIN_CHARGE_TICKS = builder.comment(
                "Minimum charge ticks required before the slash assault activates in charge modes (only applies if charge >= this value)")
                .defineInRange("Minimum Charge Ticks", 5, 0, 100);
        SLASH_ASSAULT_DASH_SWING = builder.comment("Enable hand swing animation when dashing")
                .define("Dash Swing", true);
        SLASH_ASSAULT_ATTACK_SWING = builder.comment("Enable hand swing animation when attacking enemies")
                .define("Attack Swing", true);
        SLASH_ASSAULT_ENABLE_DEPLETION = builder.comment("Enable mining fatigue on use").define("Enable Depletion", true);
        SLASH_ASSAULT_DEPLETION_CHANCE = builder.comment("Chance to apply mining fatigue").defineInRange("Depletion Chance", 1.0, 0.0, 1.0);
        SLASH_ASSAULT_DEPLETION_DURATION = builder.comment("Depletion effect duration").defineInRange("Depletion Duration Ticks", 20, 0, 72000);
        SLASH_ASSAULT_DEPLETION_LEVEL = builder.comment("Depletion effect level").defineInRange("Depletion Level", 4, 0, 10);
        SLASH_ASSAULT_DEPLETION_MAX_LEVEL = builder.comment("Depletion max level").defineInRange("Depletion Max Level", 8, 0, 10);
        SLASH_ASSAULT_DEPLETION_MAX_DURATION = builder.comment("Depletion max duration ticks").defineInRange("Depletion Max Duration Ticks", 1000, 0, 72000);
        builder.pop();

        builder.push("Piercing Assault");
        PIERCING_ASSAULT_DASH_DISTANCE = builder.comment("Dash distance in blocks").defineInRange("Dash Distance", 2.5, 0.1, 5.0);
        PIERCING_ASSAULT_RANGE = builder.comment("Pierce attack range in blocks").defineInRange("Pierce Range", 3.0, 1.0, 10.0);
        PIERCING_ASSAULT_PIERCE_COOLDOWN = builder.comment("Cooldown after pierce in ticks").defineInRange("Pierce Cooldown Ticks", 80, 1, 200);
        PIERCING_ASSAULT_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the piercing_assault trait in the inventory when the cooldown triggers, preventing abuse by switching between multiple piercing assault weapons")
                .define("Global Cooldown", true);
        PIERCING_ASSAULT_NO_TARGET_COOLDOWN = builder.comment("Cooldown when no target hit").defineInRange("No Target Cooldown Ticks", 40, 1, 200);
        PIERCING_ASSAULT_DURABILITY_COST = builder.comment("Durability cost per use").defineInRange("Durability Cost", 2, 0, 100);
        PIERCING_ASSAULT_ACTIVATION_MODE = builder.comment(
                "Activation mode: SPRINT_CLICK (sprint + right-click), INSTANT_ON_RIGHT_CLICK (right-click), CHARGE_AND_RELEASE (hold right-click, release to dash), CHARGE_AND_FINISH_USING (hold until fully charged), CHARGE_RELEASE_AND_FINISH (dash on release or when fully charged)")
                .defineEnum("Activation Mode", ModEnums.PiercingAssaultMode.SPRINT_CLICK);
        PIERCING_ASSAULT_CHARGE_DURATION_TICKS = builder.comment("Maximum use duration in ticks for charge modes")
                .defineInRange("Charge Duration Ticks", 72000, 20, 72000);
        PIERCING_ASSAULT_CHARGE_ANIMATION = builder.comment("Use animation played in charge modes")
                .defineEnum("Charge Animation", UseAnim.BOW);
        PIERCING_ASSAULT_MIN_DAMAGE = builder.comment("Minimum damage dealt").defineInRange("Minimum Damage", 1.0, 0.0, 10.0);
        PIERCING_ASSAULT_MIN_CHARGE_TICKS = builder.comment(
                "Minimum charge ticks required before the piercing assault activates in charge modes (only applies if charge >= this value)")
                .defineInRange("Minimum Charge Ticks", 5, 0, 100);
        PIERCING_ASSAULT_DASH_SWING = builder.comment("Enable hand swing animation when dashing")
                .define("Dash Swing", true);
        PIERCING_ASSAULT_ATTACK_SWING = builder.comment("Enable hand swing animation when attacking enemies")
                .define("Attack Swing", true);
        PIERCING_ASSAULT_ENABLE_DEPLETION = builder.comment("Enable mining fatigue on use").define("Enable Depletion", true);
        PIERCING_ASSAULT_DEPLETION_CHANCE = builder.comment("Chance to apply mining fatigue").defineInRange("Depletion Chance", 1.0, 0.0, 1.0);
        PIERCING_ASSAULT_DEPLETION_DURATION = builder.comment("Depletion effect duration").defineInRange("Depletion Duration Ticks", 20, 0, 72000);
        PIERCING_ASSAULT_DEPLETION_LEVEL = builder.comment("Depletion effect level").defineInRange("Depletion Level", 4, 0, 10);
        PIERCING_ASSAULT_DEPLETION_MAX_LEVEL = builder.comment("Depletion max level").defineInRange("Depletion Max Level", 8, 0, 10);
        PIERCING_ASSAULT_DEPLETION_MAX_DURATION = builder.comment("Depletion max duration ticks").defineInRange("Depletion Max Duration Ticks", 1000, 0, 72000);
        builder.pop();

        builder.push("Harvest Sweep");
        HARVEST_SWEEP_RANGE = builder.comment("Harvest radius in blocks").defineInRange("Harvest Range", 3, 0, 5);
        HARVEST_SWEEP_TILL_RANGE = builder.comment("Till radius in blocks").defineInRange("Till Range", 3, 0, 5);
        HARVEST_SWEEP_MAX_BLOCKS = builder.comment("Max blocks affected per use").defineInRange("Max Harvest Blocks", 9, 1, 64);
        HARVEST_SWEEP_CAN_HARVEST = builder.comment("Whether the trait can harvest mature crops")
                .define("Can Harvest", true);
        HARVEST_SWEEP_DURABILITY_COST_PER_BLOCK = builder.comment("Durability cost per block affected").defineInRange("Durability Cost Per Block", 1, 0, 100);
        HARVEST_SWEEP_DURABILITY_PER_BLOCK = builder
                .comment("If true, durability cost is multiplied by the number of affected blocks")
                .define("Durability Per Block", true);
        HARVEST_SWEEP_TILL_DURABILITY_COST = builder
                .comment("Durability cost per till block (overrides Durability Cost Per Block for tilling)")
                .defineInRange("Till Durability Cost", 1, 0, 100);
        HARVEST_SWEEP_HARVEST_DURABILITY_COST = builder
                .comment("Durability cost per harvest block (overrides Durability Cost Per Block for harvesting)")
                .defineInRange("Harvest Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Wild Sweep");
        WILD_SWEEP_RADIUS = builder.comment("Break radius in blocks").defineInRange("Break Radius", 3, 0, 5);
        WILD_SWEEP_DURABILITY_COST = builder.comment("Durability cost per use").defineInRange("Durability Cost", 2, 0, 100);
        WILD_SWEEP_COOLDOWN = builder.comment("Cooldown in ticks between uses").defineInRange("Cooldown Ticks", 15, 0, 200);
        WILD_SWEEP_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the wild_sweep trait in the inventory when the cooldown triggers, preventing abuse by switching between multiple wild sweep weapons")
                .define("Global Cooldown", true);
        WILD_SWEEP_BREAKABLE_BLOCKS = builder
                .comment(
                        "List of block IDs (or block ID patterns containing these strings) that can be broken by Wild Sweep")
                .defineList("Breakable Block Patterns",
                        List.of("bamboo", "tatami", "sugar_cane", "tall_grass", "double_plant"),
                        (obj) -> obj instanceof String);
        builder.pop();
        builder.push("Whirling Strike");
        WHIRLING_STRIKE_BASE_DAMAGE = builder.comment("Base spin damage per hit").defineInRange("Base Damage", 2.0, 0.0, 100.0);
        WHIRLING_STRIKE_BASE_ATTACK_RANGE = builder.comment("Base attack range in blocks").defineInRange("Base Attack Range", 2.5, 0.5, 10.0);
        WHIRLING_STRIKE_DUAL_WIELD_RANGE_MULTIPLIER = builder.comment("Range multiplier when dual wielding").defineInRange("Dual Wield Range Multiplier", 1.5, 1.0,
                5.0);
        WHIRLING_STRIKE_DUAL_WIELD_DAMAGE_MULTIPLIER = builder.comment("Damage multiplier when dual wielding").defineInRange("Dual Wield Damage Multiplier", 2.0, 1.0,
                10.0);
        WHIRLING_STRIKE_MAX_DAMAGE_CAP = builder.comment("Max damage cap per hit").defineInRange("Max Damage Cap", 8.0, 1.0, 100.0);
        WHIRLING_STRIKE_ITEM_DAMAGE_INTERVAL = builder.comment("Ticks between durability damage").defineInRange("Item Damage Interval Ticks", 20, 1, 200);
        WHIRLING_STRIKE_ATTACK_INTERVAL = builder.comment("Ticks between attacks").defineInRange("Attack Interval Ticks", 5, 1, 100);
        WHIRLING_STRIKE_ITEM_DAMAGE_AMOUNT = builder.comment("Durability damage per interval").defineInRange("Item Damage Amount", 2, 0, 100);
        WHIRLING_STRIKE_SINGLE_WIELD_BLOCK_DAMAGE = builder.comment("Block damage when single wielding").defineInRange("Single Wield Block Damage", 2, 0, 100);
        WHIRLING_STRIKE_DUAL_WIELD_BLOCK_DAMAGE = builder.comment("Block damage when dual wielding").defineInRange("Dual Wield Block Damage", 4, 0, 100);
        WHIRLING_STRIKE_USE_DISTANCE = builder.comment("Use distance in blocks").defineInRange("Use Distance", 1.5, 0.5, 10.0);
        WHIRLING_STRIKE_PARTICLE_TICK_INTERVAL = builder.comment("Ticks between particle effects").defineInRange("Particle Tick Interval", 8, 1, 100);
        builder.pop();
        builder.push("Fragility");
        FRAGILITY_BREAK_CHANCE = builder.comment("Chance to break on use").defineInRange("Break Chance", 0.15, 0.0, 1.0);
        FRAGILITY_MIN_DURABILITY_THRESHOLD = builder.comment("Minimum durability threshold (as a fraction of max durability) for fragility to trigger. E.g. 0.5 means the item must be at or below 50% durability. Default 0.0 (no threshold).").defineInRange("Minimum Durability Threshold", 0.0, 0.0, 1.0);
        FRAGILITY_REMAINING_ITEM_CHANCE = builder.comment("Default chance to drop the configured remaining item when the item breaks from fragility. Only used if no per-item chance is set.").defineInRange("Remaining Item Drop Chance", 1.0, 0.0, 1.0);
        builder.pop();
        builder.push("Slippery");
        SLIPPERY_CHANCE = builder.comment("Chance to disarm self on hit").defineInRange("Self Disarm Chance", 0.08, 0.0, 1.0);
        SLIPPERY_THROW_DISTANCE = builder.comment("Distance weapon is thrown").defineInRange("Throw Distance", 1.2, 0.0, 5.0);
        builder.pop();
        builder.push("Exhausting");
        EXHAUSTING_EXHAUSTION = builder.comment("Exhaustion added per hit").defineInRange("Exhaustion Per Hit", 0.5, 0.0, 10.0);
        builder.pop();
        builder.push("Brittle Handle");
        BRITTLE_HANDLE_EXTRA_DURABILITY = builder.comment("Extra durability damage taken").defineInRange("Extra Durability Cost", 1, 0, 100);
        builder.pop();
        builder.push("Barbed Handle");
        BARBED_HANDLE_DAMAGE_RETURN_FACTOR = builder.comment("Damage returned to attacker").defineInRange("Damage Return Factor", 0.15, 0.0, 1.0);
        builder.pop();
        builder.push("Overstrain");
        OVERSTRAIN_CHANCE = builder.comment("Chance to apply mining fatigue").defineInRange("Effect Chance", 0.12, 0.0, 1.0);
        OVERSTRAIN_EFFECT_DURATION = builder.comment("Effect duration in ticks").defineInRange("Effect Duration Ticks", 100, 1, 72000);
        OVERSTRAIN_EFFECT_AMPLIFIER = builder.comment("Effect amplifier level").defineInRange("Effect Amplifier", 0, 0, 10);
        builder.pop();
        builder.push("Unstable Edge");
        UNSTABLE_EDGE_MIN_MULTIPLIER = builder.comment("Minimum random damage multiplier").defineInRange("Min Damage Multiplier", 0.5, 0.0, 5.0);
        UNSTABLE_EDGE_MAX_MULTIPLIER = builder.comment("Maximum random damage multiplier").defineInRange("Max Damage Multiplier", 1.0, 0.0, 5.0);
        builder.pop();
        builder.push("Detonating");
        DETONATING_EXPLODE_CHANCE = builder.comment("Chance to explode on hit").defineInRange("Explode Chance", 0.15, 0.0, 1.0);
        DETONATING_EXPLOSION_POWER = builder.comment("Explosion power").defineInRange("Explosion Power", 2.0, 0.0, 20.0);
        DETONATING_BREAK_BLOCKS = builder.comment("Whether explosion breaks blocks").define("Break Blocks", false);
        DETONATING_DAMAGE_OWNER = builder.comment("Whether explosion damages the owner").define("Damage Owner", true);
        builder.pop();
        builder.push("Decapitation");
        DECAPITATION_CHANCE = builder.comment("Chance to decapitate on kill").defineInRange("Chance", 0.05, 0.0, 1.0);
        DECAPITATION_CRITICAL_MULTIPLIER = builder.comment("Critical hit multiplier for decapitation").defineInRange("Critical Multiplier", 2.0, 1.0, 10.0);
        builder.pop();
        builder.push("Smash Strike");
        SMASH_STRIKE_DAMAGE_PER_BLOCK = builder.comment("Damage per block fallen").defineInRange("Damage Per Fall Block", 0.5, 0.0, 10.0);
        SMASH_STRIKE_MAX_BONUS_DAMAGE = builder.comment("Max bonus damage from fall").defineInRange("Max Bonus Damage", 55.0, 0.0, 500.0);
        SMASH_STRIKE_RESIDUAL_DAMAGE_BASE = builder.comment("Base residual damage").defineInRange("Residual Damage Base", 1.0, 0.0, 100.0);
        SMASH_STRIKE_RESIDUAL_DAMAGE_PER_BLOCK = builder.comment("Residual damage per block fallen").defineInRange("Residual Damage Per Fall Block", 0.2, 0.0,
                10.0);
        SMASH_STRIKE_MAX_RESIDUAL_DAMAGE = builder.comment("Max residual damage").defineInRange("Max Residual Damage", 20.0, 0.0, 200.0);
        SMASH_STRIKE_SMASH_RADIUS = builder.comment("Smash AOE radius in blocks").defineInRange("Smash Radius", 1.0, 0.0, 10.0);
        SMASH_STRIKE_SHAKE_INTENSITY = builder.comment("Screen shake intensity").defineInRange("Screen Shake Intensity", 1.5, 0.0, 10.0);
        SMASH_STRIKE_SHAKE_RESET_DELAY = builder.comment("Shake reset delay in ticks").defineInRange("Screen Shake Reset Delay Ticks", 5, 0, 100);
        SMASH_STRIKE_ALLY_DAMAGE_MULTIPLIER = builder.comment("Damage multiplier for allies").defineInRange("Ally Damage Multiplier", 0.1, 0.0, 1.0);
        SMASH_STRIKE_DURABILITY_DAMAGE_BASE = builder.comment("Base durability damage").defineInRange("Durability Damage Base", 5, 0, 100);
        SMASH_STRIKE_DURABILITY_DAMAGE_PER_BLOCK = builder.comment("Durability damage per block fallen").defineInRange("Durability Damage Per Fall Block", 1.0, 0.0,
                100.0);
        SMASH_STRIKE_MAX_DURABILITY_DAMAGE = builder.comment("Max durability damage").defineInRange("Max Durability Damage", 1000, 0, 10000);
        builder.pop();
        builder.push("Rapid Boost");
        RAPID_BOOST_MAX_HITS = builder.comment("Hits required to trigger boost").defineInRange("Max Hits", 3, 1, 100);
        RAPID_BOOST_INCREMENT = builder.comment("Attack speed increase per hit").defineInRange("Attack Speed Increment Per Hit", 0.5, 0.0, 10.0);
        builder.pop();
        builder.push("Power Boost");
        POWER_BOOST_MAX_HITS = builder.comment("Hits required to trigger boost").defineInRange("Max Hits", 3, 1, 100);
        POWER_BOOST_INCREMENT = builder.comment("Damage increase per hit").defineInRange("Damage Increment Per Hit", 1.0, 0.0, 100.0);
        builder.pop();
        builder.push("Quick Swap");
        QUICK_SWAP_REQUIRE_CROUCH = builder
                .comment("Require the player to be crouching (holding shift) to trigger the quick swap")
                .define("Require Crouch", true);
        QUICK_SWAP_MAIN_HAND_COOLDOWN = builder.comment("Main hand cooldown in ticks").defineInRange("Main Hand Cooldown Ticks", 20, 0, 1200);
        QUICK_SWAP_OFF_HAND_COOLDOWN = builder.comment("Off hand cooldown in ticks").defineInRange("Off Hand Cooldown Ticks", 20, 0, 1200);
        QUICK_SWAP_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the quick_swap trait in the inventory when the cooldown triggers, preventing abuse by switching between multiple quick swap weapons")
                .define("Global Cooldown", true);
        QUICK_SWAP_ACTIVATION_MODE = builder.comment(
                "Activation mode: INSTANT_ON_RIGHT_CLICK (swap immediately on right-click), CHARGE_AND_RELEASE (hold right-click, release to swap), CHARGE_AND_FINISH_USING (hold until fully charged to swap), CHARGE_RELEASE_AND_FINISH (swap on release OR when fully charged)")
                .defineEnum("Activation Mode", ModEnums.QuickSwapMode.INSTANT_ON_RIGHT_CLICK);
        QUICK_SWAP_CHARGE_DURATION_TICKS = builder.comment("Maximum use duration in ticks for charge modes")
                .defineInRange("Charge Duration Ticks", 72000, 20, 72000);
        QUICK_SWAP_CHARGE_ANIMATION = builder.comment("Use animation played in charge modes")
                .defineEnum("Charge Animation", UseAnim.BOW);
        builder.pop();
        builder.push("Backstab");
        BACKSTAB_MULTIPLIER_NORMAL = builder.comment("Damage multiplier when attacking from behind").defineInRange("Backstab Multiplier Normal", 2.0, 1.0, 10.0);
        BACKSTAB_MULTIPLIER_SNEAKING = builder.comment("Damage multiplier when sneaking from behind").defineInRange("Backstab Multiplier Sneaking", 2.5, 1.0, 10.0);
        BACKSTAB_MULTIPLIER_INVISIBLE = builder.comment("Damage multiplier when invisible").defineInRange("Backstab Multiplier Invisible", 3.0, 1.0, 10.0);
        BACKSTAB_MULTIPLIER_SNEAKING_INVISIBLE = builder.comment("Damage multiplier when sneaking and invisible").defineInRange("Backstab Multiplier Sneaking Invisible", 3.5,
                1.0, 10.0);
        BACKSTAB_DURABILITY_PENALTY = builder.comment("Extra durability damage on backstab").defineInRange("Durability Penalty", 3, 0, 100);
        BACKSTAB_RIGHT_CLICK_DURABILITY_MULTIPLIER = builder.comment("Durability multiplier for right-click backstab").defineInRange("Right Click Durability Multiplier", 2.0,
                0.0, 10.0);
        BACKSTAB_RIGHT_CLICK_FORWARD_IMPULSE = builder.comment("Forward impulse on right-click backstab").defineInRange("Right Click Forward Impulse", 0.8, 0.0, 5.0);
        BACKSTAB_MAX_DISTANCE = builder.comment("Max distance for backstab detection").defineInRange("Max Distance", 2.0, 0.5, 10.0);
        BACKSTAB_MAX_ANGLE = builder.comment("Max angle for backstab detection").defineInRange("Max Angle Degrees", 45.0, 0.0, 180.0);
        BACKSTAB_WEAKNESS_CHANCE = builder.comment("Chance to apply weakness on backstab").defineInRange("Weakness Chance", 0.0, 0.0, 1.0);
        BACKSTAB_WEAKNESS_DURATION = builder.comment("Weakness duration in ticks").defineInRange("Weakness Duration Ticks", 60, 0, 1200);
        BACKSTAB_WEAKNESS_LEVEL = builder.comment("Weakness effect level").defineInRange("Weakness Level", 1, 0, 10);
        BACKSTAB_RIGHT_CLICK_COOLDOWN = builder.comment("Cooldown for right-click backstab in ticks").defineInRange("Right Click Cooldown Ticks", 20, 0, 200);
        BACKSTAB_GLOBAL_COOLDOWN = builder.comment(
                "Apply cooldown to all items with the backstab trait in the inventory when the backstab cooldown triggers, preventing abuse by switching between multiple backstab weapons")
                .define("Global Cooldown", true);
        BACKSTAB_DARKNESS_BONUS = builder.comment("Damage bonus when target is in darkness").defineInRange("Darkness Bonus", 1.5, 0.0, 10.0);
        BACKSTAB_MOVING_TARGET_PENALTY = builder.comment("Damage penalty against moving targets").defineInRange("Moving Target Penalty", 0.5, 0.0, 5.0);
        BACKSTAB_GRACE_PERIOD_SECONDS = builder.comment("Grace period for backstab detection in seconds").defineInRange("Grace Period Seconds", 0.2, 0.0, 2.0);
        BACKSTAB_RIGHT_CLICK_DAMAGE_BONUS = builder.comment("Bonus damage on right-click backstab").defineInRange("Right Click Damage Bonus", 2.5, 0.0, 100.0);
        builder.pop();
        builder.push("Heavy Handed");
        HEAVY_HANDED_MOVEMENT_SPEED_REDUCTION = builder.comment(
                "Base movement speed reduction when dual-wielding. 0.05 = 5%% reduction")
                .defineInRange("Movement Speed Reduction", 0.05, 0.0, 1.0);
        HEAVY_HANDED_ATTACK_SPEED_REDUCTION = builder.comment(
                "Base attack speed reduction when dual-wielding. 0.05 = 5%% reduction")
                .defineInRange("Attack Speed Reduction", 0.05, 0.0, 1.0);
        HEAVY_HANDED_ATTACK_DAMAGE_REDUCTION = builder.comment(
                "Base attack damage reduction when dual-wielding. 0.03 = 3%% reduction")
                .defineInRange("Attack Damage Reduction", 0.03, 0.0, 1.0);
        HEAVY_HANDED_DURABILITY_FACTOR = builder.comment(
                "Extra reduction per 100 max durability. 0.01 = 1%% per 100 durability. A diamond sword (1561 dur) would add ~15.6%% reduction.")
                .defineInRange("Durability Factor", 0.01, 0.0, 0.5);
        HEAVY_HANDED_DAMAGE_FACTOR = builder.comment(
                "Extra reduction per point of attack damage. 0.005 = 0.5%% per damage point. A 8-damage sword would add 4%% reduction.")
                .defineInRange("Damage Factor", 0.005, 0.0, 0.1);
        HEAVY_HANDED_MAX_REDUCTION = builder.comment(
                "Maximum total reduction cap. 0.50 = at most 50%% stat reduction.")
                .defineInRange("Max Reduction", 0.50, 0.0, 0.95);
        builder.pop();
        builder.pop();
        builder.pop();
    }
}
