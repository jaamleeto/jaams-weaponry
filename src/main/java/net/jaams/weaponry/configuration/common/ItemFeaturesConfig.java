package net.jaams.weaponry.configuration.common;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ItemFeaturesConfig {
    
    public static ModConfigSpec.BooleanValue SHORT_BOW_AUTO_SHOOT;
    
    public static ModConfigSpec.BooleanValue STAKE_CROSSBOW_ALT_SHOOT;
    
    public static ModConfigSpec.BooleanValue TRIDENT_USE_CUSTOM_THROW;
    
    public static ModConfigSpec.BooleanValue NUNCHAKU_PLAY_SPRINT_SOUND;
    public static ModConfigSpec.BooleanValue NUNCHAKU_PLAY_SWING_SOUND;
    
    public static ModConfigSpec.BooleanValue SMOKE_BOMB_MECHANIC;
    public static ModConfigSpec.DoubleValue SMOKE_BOMB_PUSH_FORCE;
    public static ModConfigSpec.DoubleValue SMOKE_BOMB_UPWARD_PUSH_FORCE;
    public static ModConfigSpec.BooleanValue SMOKE_BOMB_RESPECT_KNOCKBACK_RESISTANCE;
    public static ModConfigSpec.DoubleValue SMOKE_BOMB_SELF_BLIND_PROBABILITY;
    public static ModConfigSpec.DoubleValue SMOKE_BOMB_ENEMY_BLIND_PROBABILITY;
    public static ModConfigSpec.DoubleValue SMOKE_BOMB_HIT_PROBABILITY;
    public static ModConfigSpec.IntValue SMOKE_BOMB_PARTICLE_COUNT;
    public static ModConfigSpec.DoubleValue SMOKE_BOMB_PARTICLE_RANGE;
    public static ModConfigSpec.IntValue SMOKE_BOMB_COOLDOWN;
    public static ModConfigSpec.BooleanValue SMOKE_BOMB_USE_DURABILITY;
    
    public static ModConfigSpec.IntValue RAGERS_BOTTLE_USE_DURATION;
    public static ModConfigSpec.IntValue RAGERS_BOTTLE_EFFECT_DURATION;
    public static ModConfigSpec.IntValue RAGERS_BOTTLE_EFFECT_AMPLIFIER;
    
    public static ModConfigSpec.IntValue WARRIORS_BOTTLE_USE_DURATION;
    public static ModConfigSpec.IntValue WARRIORS_BOTTLE_EFFECT_DURATION;
    public static ModConfigSpec.IntValue WARRIORS_BOTTLE_EFFECT_AMPLIFIER;
    
    public static ModConfigSpec.IntValue ARCHERS_BOTTLE_USE_DURATION;
    public static ModConfigSpec.IntValue ARCHERS_BOTTLE_EFFECT_DURATION;
    public static ModConfigSpec.IntValue ARCHERS_BOTTLE_EFFECT_AMPLIFIER;
    
    public static ModConfigSpec.BooleanValue SLINGSHOT_AMMO_FROM_INVENTORY;
    public static ModConfigSpec.ConfigValue<List<? extends String>> SLINGSHOT_AMMO_ITEMS;
    public static ModConfigSpec.ConfigValue<List<? extends String>> SLINGSHOT_PLACEABLE_ITEMS;
    public static void register(ModConfigSpec.Builder builder) {
        builder.push("Item Features Handler");
        
        builder.push("Weapons");
        
        builder.push("Trident");
        TRIDENT_USE_CUSTOM_THROW = builder.comment("Trident use custom throw instead of vanilla")
                .define("Trident Custom Throw", true);
        builder.pop();
        
        builder.push("Nunchaku");
        NUNCHAKU_PLAY_SPRINT_SOUND = builder.comment("If true, chain sounds will play while sprinting with Nunchakus")
                .define("Nunchaku Sprint Sound", true);
        NUNCHAKU_PLAY_SWING_SOUND = builder
                .comment("If true, a chain sound will play when swinging the Nunchaku in the air")
                .define("Nunchaku Swing Sound", true);
        builder.pop();
        builder.pop();
        
        builder.push("Bows");
        
        builder.push("Short Bow");
        SHORT_BOW_AUTO_SHOOT = builder.comment("Short Bow auto shoot on hold").define("Short Bow Auto Shoot", true);
        builder.pop();
        builder.pop();
        
        builder.push("Crossbows");
        
        builder.push("Stake Crossbow");
        STAKE_CROSSBOW_ALT_SHOOT = builder.comment("Stake Crossbow alternative shoot mode")
                .define("Stake Crossbow Alt Shoot", true);
        builder.pop();
        builder.pop();
        
        builder.push("Miscs");
        
        builder.push("Smoke Bomb");
        SMOKE_BOMB_MECHANIC = builder.comment(
                "Enable custom smoke bomb mechanic (push, blindness, particles, etc.) for items tagged as smoke bombs")
                .define("Smoke Bomb Mechanic", true);
        SMOKE_BOMB_PUSH_FORCE = builder.comment("Horizontal push force applied to the player")
                .defineInRange("Smoke Bomb Push Force", 1.5D, 0.0D, 10.0D);
        SMOKE_BOMB_UPWARD_PUSH_FORCE = builder.comment("Upward push force when looking downward")
                .defineInRange("Smoke Bomb Upward Push Force", 0.8D, 0.0D, 5.0D);
        SMOKE_BOMB_RESPECT_KNOCKBACK_RESISTANCE = builder
                .comment("Whether knockback resistance reduces the push effect")
                .define("Respect Knockback Resistance", true);
        SMOKE_BOMB_SELF_BLIND_PROBABILITY = builder.comment("Probability that the user gets blinded (0.0 - 1.0)")
                .defineInRange("Self Blind Probability", 0.1D, 0.0D, 1.0D);
        SMOKE_BOMB_ENEMY_BLIND_PROBABILITY = builder.comment("Probability that nearby enemies get blinded (0.0 - 1.0)")
                .defineInRange("Enemy Blind Probability", 0.5D, 0.0D, 1.0D);
        SMOKE_BOMB_HIT_PROBABILITY = builder.comment("Probability to trigger smoke bomb effect when hitting an entity")
                .defineInRange("Hit Trigger Probability", 0.2D, 0.0D, 1.0D);
        SMOKE_BOMB_PARTICLE_COUNT = builder.comment("Number of particles to spawn")
                .defineInRange("Smoke Bomb Particle Count", 120, 10, 1000);
        SMOKE_BOMB_PARTICLE_RANGE = builder.comment("Spread range of the particles")
                .defineInRange("Smoke Bomb Particle Range", 3.0D, 0.5D, 10.0D);
        SMOKE_BOMB_COOLDOWN = builder.comment("Cooldown in ticks after using the smoke bomb")
                .defineInRange("Smoke Bomb Cooldown", 40, 0, 300);
        SMOKE_BOMB_USE_DURABILITY = builder.comment(
                "If true, smoke bombs that have durability will lose 1 durability point instead of consuming the whole item from the stack")
                .define("Smoke Bomb Use Durability", false);
        builder.pop();
        
        builder.push("Bottles");
        
        builder.push("Ragers Bottle");
        RAGERS_BOTTLE_USE_DURATION = builder.comment("Ticks required to drink Rager's Bottle (Standard is 32)")
                .defineInRange("Ragers Bottle Use Duration", 32, 1, 72000);
        RAGERS_BOTTLE_EFFECT_DURATION = builder
                .comment("Duration in ticks for the effect applied by Rager's Bottle (20 ticks = 1s)")
                .defineInRange("Ragers Bottle Effect Duration", 200, 1, 72000);
        RAGERS_BOTTLE_EFFECT_AMPLIFIER = builder
                .comment("Amplifier level of the effect (0 = Level I, 1 = Level II, 2 = Level III)")
                .defineInRange("Ragers Bottle Effect Amplifier", 1, 0, 255);
        builder.pop();
        
        builder.push("Warriors Bottle");
        WARRIORS_BOTTLE_USE_DURATION = builder.comment("Ticks required to drink Warrior's Bottle (Standard is 32)")
                .defineInRange("Warriors Bottle Use Duration", 32, 1, 72000);
        WARRIORS_BOTTLE_EFFECT_DURATION = builder
                .comment("Duration in ticks for the effect applied by Warrior's Bottle (20 ticks = 1s)")
                .defineInRange("Warriors Bottle Effect Duration", 300, 1, 72000);
        WARRIORS_BOTTLE_EFFECT_AMPLIFIER = builder
                .comment("Amplifier level of the effect (0 = Level I, 1 = Level II, 2 = Level III)")
                .defineInRange("Warriors Bottle Effect Amplifier", 2, 0, 255);
        builder.pop();
        
        builder.push("Archers Bottle");
        ARCHERS_BOTTLE_USE_DURATION = builder.comment("Ticks required to drink Archer's Bottle (Standard is 32)")
                .defineInRange("Archers Bottle Use Duration", 32, 1, 72000);
        ARCHERS_BOTTLE_EFFECT_DURATION = builder
                .comment("Duration in ticks for the effect applied by Archer's Bottle (20 ticks = 1s)")
                .defineInRange("Archers Bottle Effect Duration", 300, 1, 72000);
        ARCHERS_BOTTLE_EFFECT_AMPLIFIER = builder
                .comment("Amplifier level of the effect (0 = Level I, 1 = Level II, 2 = Level III)")
                .defineInRange("Archers Bottle Effect Amplifier", 2, 0, 255);
        builder.pop();
        builder.pop();
        builder.pop();
        
        builder.push("Slingshot");
        SLINGSHOT_AMMO_FROM_INVENTORY = builder
                .comment("Allow Slingshots to pull ammo from the player's inventory (not just offhand/mainhand)")
                .define("Ammo From Inventory", true);
        SLINGSHOT_AMMO_ITEMS = builder
                .comment("List of item registry names that slingshots can use as ammo by default. "
                        + "Can be overridden per-slingshot with the SlingshotAmmoItems NBT list. "
                        + "If empty, any item is valid ammo.")
                .defineList("Default Ammo Items",
                        java.util.Arrays.asList(
                                
                                "minecraft:oak_planks", "minecraft:spruce_planks",
                                "minecraft:birch_planks", "minecraft:jungle_planks",
                                "minecraft:acacia_planks", "minecraft:dark_oak_planks",
                                "minecraft:mangrove_planks", "minecraft:cherry_planks",
                                "minecraft:crimson_planks", "minecraft:warped_planks",
                                
                                "minecraft:oak_log", "minecraft:spruce_log",
                                "minecraft:birch_log", "minecraft:jungle_log",
                                "minecraft:acacia_log", "minecraft:dark_oak_log",
                                "minecraft:mangrove_log", "minecraft:cherry_log",
                                "minecraft:crimson_stem", "minecraft:warped_stem",
                                
                                "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log",
                                "minecraft:stripped_birch_log", "minecraft:stripped_jungle_log",
                                "minecraft:stripped_acacia_log", "minecraft:stripped_dark_oak_log",
                                "minecraft:stripped_mangrove_log", "minecraft:stripped_cherry_log",
                                "minecraft:stripped_crimson_stem", "minecraft:stripped_warped_stem",
                                
                                "minecraft:oak_wood", "minecraft:spruce_wood",
                                "minecraft:birch_wood", "minecraft:jungle_wood",
                                "minecraft:acacia_wood", "minecraft:dark_oak_wood",
                                "minecraft:mangrove_wood", "minecraft:cherry_wood",
                                "minecraft:crimson_hyphae", "minecraft:warped_hyphae",
                                
                                "minecraft:stripped_oak_wood", "minecraft:stripped_spruce_wood",
                                "minecraft:stripped_birch_wood", "minecraft:stripped_jungle_wood",
                                "minecraft:stripped_acacia_wood", "minecraft:stripped_dark_oak_wood",
                                "minecraft:stripped_mangrove_wood", "minecraft:stripped_cherry_wood",
                                "minecraft:stripped_crimson_hyphae", "minecraft:stripped_warped_hyphae",
                                
                                "minecraft:stone", "minecraft:cobblestone",
                                "minecraft:smooth_stone", "minecraft:stone_bricks",
                                "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks",
                                "minecraft:chiseled_stone_bricks",
                                "minecraft:cobbled_deepslate", "minecraft:deepslate",
                                "minecraft:deepslate_bricks", "minecraft:cracked_deepslate_bricks",
                                "minecraft:deepslate_tiles", "minecraft:cracked_deepslate_tiles",
                                "minecraft:chiseled_deepslate", "minecraft:polished_deepslate",
                                "minecraft:andesite", "minecraft:diorite", "minecraft:granite",
                                "minecraft:tuff", "minecraft:basalt", "minecraft:smooth_basalt",
                                "minecraft:blackstone", "minecraft:polished_blackstone",
                                "minecraft:polished_blackstone_bricks",
                                "minecraft:cracked_polished_blackstone_bricks",
                                "minecraft:chiseled_polished_blackstone",
                                "minecraft:netherrack", "minecraft:end_stone",
                                "minecraft:end_stone_bricks"),
                        obj -> obj instanceof String);
        SLINGSHOT_PLACEABLE_ITEMS = builder
                .comment("List of item registry names that slingshots can place in the world. "
                        + "If empty, defaults to the ammo list.")
                .defineList("Default Placeable Items",
                        java.util.Arrays.asList(
                                "minecraft:anvil",
                                "minecraft:chipped_anvil",
                                "minecraft:damaged_anvil"),
                        obj -> obj instanceof String);
        builder.pop();
        builder.pop();
    }
}
