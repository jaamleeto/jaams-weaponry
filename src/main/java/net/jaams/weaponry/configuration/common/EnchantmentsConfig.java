package net.jaams.weaponry.configuration.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class EnchantmentsConfig {
	
	public static ForgeConfigSpec.BooleanValue SECURE_GRIP;
	public static ForgeConfigSpec.IntValue SECURE_GRIP_MAX_LEVEL;
	public static ForgeConfigSpec.IntValue SECURE_GRIP_MIN_COST;
	public static ForgeConfigSpec.IntValue SECURE_GRIP_COST_PER_LEVEL;
	public static ForgeConfigSpec.IntValue SECURE_GRIP_COST_RANGE;
	public static ForgeConfigSpec.BooleanValue SECURE_GRIP_IS_TREASURE;
	
	public static ForgeConfigSpec.BooleanValue OVERDRIVE;
	public static ForgeConfigSpec.IntValue OVERDRIVE_MAX_LEVEL;
	public static ForgeConfigSpec.IntValue OVERDRIVE_MIN_COST;
	public static ForgeConfigSpec.IntValue OVERDRIVE_COST_PER_LEVEL;
	public static ForgeConfigSpec.IntValue OVERDRIVE_COST_RANGE;
	public static ForgeConfigSpec.IntValue OVERDRIVE_DURABILITY_COST_PER_LEVEL;
	public static ForgeConfigSpec.DoubleValue OVERDRIVE_SPEED_BONUS_PER_LEVEL;
	public static ForgeConfigSpec.BooleanValue OVERDRIVE_IS_TREASURE;
	
	public static ForgeConfigSpec.BooleanValue AFTERMATH;
	public static ForgeConfigSpec.IntValue AFTERMATH_MAX_LEVEL;
	public static ForgeConfigSpec.DoubleValue AFTERMATH_EXTRA_DAMAGE_PER_LEVEL;
	public static ForgeConfigSpec.IntValue AFTERMATH_MIN_COST;
	public static ForgeConfigSpec.IntValue AFTERMATH_COST_PER_LEVEL;
	public static ForgeConfigSpec.IntValue AFTERMATH_COST_RANGE;
	public static ForgeConfigSpec.BooleanValue AFTERMATH_IS_TREASURE;
	public static ForgeConfigSpec.IntValue AFTERMATH_I_FRAMES;
	public static ForgeConfigSpec.IntValue AFTERMATH_DELAY_TICKS;
	public static ForgeConfigSpec.DoubleValue AFTERMATH_DELAY_TICKS_PER_LEVEL;
	public static ForgeConfigSpec.BooleanValue AFTERMATH_BLOCKING_BLOCKS_DAMAGE;
	public static ForgeConfigSpec.BooleanValue AFTERMATH_PROTECTION_REDUCES_DAMAGE;
	public static ForgeConfigSpec.BooleanValue AFTERMATH_THORNS_REFLECT;
	public static ForgeConfigSpec.DoubleValue AFTERMATH_THORNS_PERCENTAGE;
	public static ForgeConfigSpec.BooleanValue AFTERMATH_SPAWN_PARTICLES;
	public static ForgeConfigSpec.IntValue AFTERMATH_PARTICLES_PER_LEVEL;
	
	public static ForgeConfigSpec.BooleanValue GHOST_CLIP;
	public static ForgeConfigSpec.IntValue GHOST_CLIP_MAX_LEVEL;
	public static ForgeConfigSpec.IntValue GHOST_CLIP_MIN_COST;
	public static ForgeConfigSpec.IntValue GHOST_CLIP_COST_PER_LEVEL;
	public static ForgeConfigSpec.IntValue GHOST_CLIP_COST_RANGE;
	public static ForgeConfigSpec.BooleanValue GHOST_CLIP_IS_TREASURE;
	public static ForgeConfigSpec.DoubleValue GHOST_CLIP_CHANCE_PER_LEVEL;
	
	public static ForgeConfigSpec.BooleanValue FRAMEGUARD;
	public static ForgeConfigSpec.IntValue FRAMEGUARD_MAX_LEVEL;
	public static ForgeConfigSpec.IntValue FRAMEGUARD_MIN_COST;
	public static ForgeConfigSpec.IntValue FRAMEGUARD_COST_PER_LEVEL;
	public static ForgeConfigSpec.IntValue FRAMEGUARD_COST_RANGE;
	public static ForgeConfigSpec.BooleanValue FRAMEGUARD_IS_TREASURE;
	public static ForgeConfigSpec.DoubleValue FRAMEGUARD_CHANCE_PER_LEVEL;
	
	public static ForgeConfigSpec.BooleanValue BACKBLAST;
	public static ForgeConfigSpec.IntValue BACKBLAST_MAX_LEVEL;
	public static ForgeConfigSpec.IntValue BACKBLAST_MIN_COST;
	public static ForgeConfigSpec.IntValue BACKBLAST_COST_PER_LEVEL;
	public static ForgeConfigSpec.IntValue BACKBLAST_COST_RANGE;
	public static ForgeConfigSpec.BooleanValue BACKBLAST_IS_TREASURE;
	public static ForgeConfigSpec.DoubleValue BACKBLAST_RECOIL_BONUS_PER_LEVEL;
	public static ForgeConfigSpec.IntValue BACKBLAST_FIRE_SHOCKWAVE_DURATION_BONUS_PER_LEVEL;
	public static ForgeConfigSpec.DoubleValue BACKBLAST_VERTICAL_RECOIL_BONUS_PER_LEVEL;
	public static ForgeConfigSpec.DoubleValue BACKBLAST_FIRE_SHOCKWAVE_BASE_DAMAGE_PER_LEVEL;
	public static ForgeConfigSpec.DoubleValue BACKBLAST_FIRE_SHOCKWAVE_RADIUS;
	public static ForgeConfigSpec.DoubleValue BACKBLAST_FIRE_SHOCKWAVE_KNOCKBACK;
	public static ForgeConfigSpec.BooleanValue BACKBLAST_FIRE_SHOCKWAVE;
	public static ForgeConfigSpec.BooleanValue BACKBLAST_EXPLOSION;
	public static ForgeConfigSpec.DoubleValue BACKBLAST_EXPLOSION_POWER;
	public static ForgeConfigSpec.BooleanValue BACKBLAST_EXPLOSION_BREAKS_BLOCKS;
	public static ForgeConfigSpec.BooleanValue BACKBLAST_FIRE_SHOCKWAVE_PARTICLE;

	public static void register(ForgeConfigSpec.Builder builder) {
		builder.push("Custom Enchantments Handler");
		
		builder.push("Secure Grip");
		SECURE_GRIP = builder.comment("Whether the Secure Grip enchantment is enabled").worldRestart().define("Secure Grip", true);
		SECURE_GRIP_MAX_LEVEL = builder.comment("Maximum level for Secure Grip enchantment").worldRestart().defineInRange("Secure Grip Max Level", 1, 1, 5);
		SECURE_GRIP_MIN_COST = builder.comment("Minimum XP cost for level 1").defineInRange("Secure Grip Min Cost", 10, 1, 50);
		SECURE_GRIP_COST_PER_LEVEL = builder.comment("Additional XP cost per level above 1").defineInRange("Secure Grip Cost Per Level", 8, 1, 20);
		SECURE_GRIP_COST_RANGE = builder.comment("XP cost range (added to min cost)").defineInRange("Secure Grip Cost Range", 15, 5, 30);
		SECURE_GRIP_IS_TREASURE = builder.comment("Whether Secure Grip is a treasure enchantment").define("Secure Grip Is Treasure", false);
		builder.pop();
		
		builder.push("Overdrive");
		OVERDRIVE = builder.comment("Whether the Overdrive enchantment is enabled").worldRestart().define("Overdrive", true);
		OVERDRIVE_MAX_LEVEL = builder.comment("Maximum level for Overdrive enchantment").worldRestart().defineInRange("Overdrive Max Level", 3, 1, 5);
		OVERDRIVE_MIN_COST = builder.comment("Minimum XP cost for level 1").defineInRange("Overdrive Min Cost", 10, 1, 50);
		OVERDRIVE_COST_PER_LEVEL = builder.comment("Additional XP cost per level above 1").defineInRange("Overdrive Cost Per Level", 8, 1, 20);
		OVERDRIVE_COST_RANGE = builder.comment("XP cost range (added to min cost)").defineInRange("Overdrive Cost Range", 15, 5, 30);
		OVERDRIVE_DURABILITY_COST_PER_LEVEL = builder.comment("Durability cost per level when using Overdrive").defineInRange("Overdrive Durability Cost Per Level", 1, 0, 10);
		OVERDRIVE_SPEED_BONUS_PER_LEVEL = builder.comment("Attack speed bonus granted per level of Overdrive enchantment").defineInRange("Overdrive Speed Bonus Per Level", 0.2, 0.0, 5.0);
		OVERDRIVE_IS_TREASURE = builder.comment("Whether Overdrive is a treasure enchantment").define("Overdrive Is Treasure", false);
		builder.pop();
		
		builder.push("Aftermath");
		AFTERMATH = builder.comment("Whether the Aftermath enchantment is enabled").worldRestart().define("Aftermath", true);
		AFTERMATH_MAX_LEVEL = builder.comment("Maximum level for Aftermath enchantment").worldRestart().defineInRange("Aftermath Max Level", 3, 1, 5);
		AFTERMATH_EXTRA_DAMAGE_PER_LEVEL = builder.comment("Extra damage dealt per level of Aftermath enchantment").defineInRange("Aftermath Damage", 2.0, 0.1, 10.0);
		AFTERMATH_MIN_COST = builder.comment("Minimum XP cost for level 1").defineInRange("Aftermath Min Cost", 10, 1, 50);
		AFTERMATH_COST_PER_LEVEL = builder.comment("Additional XP cost per level above 1").defineInRange("Aftermath Cost Per Level", 8, 1, 20);
		AFTERMATH_COST_RANGE = builder.comment("XP cost range (added to min cost)").defineInRange("Aftermath Cost Range", 15, 5, 30);
		AFTERMATH_IS_TREASURE = builder.comment("Whether Aftermath is a treasure enchantment").define("Aftermath Is Treasure", false);
		AFTERMATH_I_FRAMES = builder.comment("Invulnerability frames applied to the target after Aftermath damage triggers").defineInRange("Aftermath I-Frames", 5, 0, 60);
		AFTERMATH_DELAY_TICKS = builder.comment("Base delay in ticks before the Aftermath extra damage triggers").defineInRange("Aftermath Delay Ticks", 20, 1, 100);
		AFTERMATH_DELAY_TICKS_PER_LEVEL = builder.comment("Additional delay ticks added per level (positive values increase delay at higher levels)").defineInRange("Aftermath Delay Ticks Per Level", 10.0, -20.0, 50.0);
		AFTERMATH_BLOCKING_BLOCKS_DAMAGE = builder.comment("Whether shields or active blocking can completely nullify Aftermath damage").define("Aftermath Blocking Blocks Damage", true);
		AFTERMATH_PROTECTION_REDUCES_DAMAGE = builder.comment("Whether armor protection enchantments can reduce Aftermath incoming damage").define("Aftermath Protection Reduces Damage", true);
		AFTERMATH_THORNS_REFLECT = builder.comment("Whether the Thorns enchantment can reflect a percentage of Aftermath damage back to the attacker").define("Aftermath Thorns Reflect", true);
		AFTERMATH_THORNS_PERCENTAGE = builder.comment("Percentage of damage reflected back by Thorns per enchantment level").defineInRange("Aftermath Thorns Percentage", 0.15, 0.0, 1.0);
		AFTERMATH_SPAWN_PARTICLES = builder.comment("Whether to spawn custom crit particles when Aftermath damages a target").define("Aftermath Spawn Particles", true);
		AFTERMATH_PARTICLES_PER_LEVEL = builder.comment("Base amount of custom particles spawned per enchantment level").defineInRange("Aftermath Particles Per Level", 4, 1, 100);
		builder.pop();
		
		builder.push("Ghost Clip");
		GHOST_CLIP = builder.comment("Whether the Ghost Clip enchantment is enabled").worldRestart().define("Ghost Clip", true);
		GHOST_CLIP_MAX_LEVEL = builder.comment("Maximum level for Ghost Clip enchantment").worldRestart().defineInRange("Ghost Clip Max Level", 5, 1, 5);
		GHOST_CLIP_MIN_COST = builder.comment("Minimum XP cost for level 1").defineInRange("Ghost Clip Min Cost", 10, 1, 50);
		GHOST_CLIP_COST_PER_LEVEL = builder.comment("Additional XP cost per level above 1").defineInRange("Ghost Clip Cost Per Level", 8, 1, 20);
		GHOST_CLIP_COST_RANGE = builder.comment("XP cost range (added to min cost)").defineInRange("Ghost Clip Cost Range", 15, 5, 30);
		GHOST_CLIP_IS_TREASURE = builder.comment("Whether Ghost Clip is a treasure enchantment").define("Ghost Clip Is Treasure", false);
		GHOST_CLIP_CHANCE_PER_LEVEL = builder.comment("Base chance per level that Ghost Clip prevents ammo consumption").defineInRange("Ghost Clip Chance", 0.15, 0.0, 1.0);
		builder.pop();
		
		builder.push("Frameguard");
		FRAMEGUARD = builder.comment("Whether the Frameguard enchantment is enabled").worldRestart().define("Frameguard", true);
		FRAMEGUARD_MAX_LEVEL = builder.comment("Maximum level for Frameguard enchantment").worldRestart().defineInRange("Frameguard Max Level", 5, 1, 5);
		FRAMEGUARD_MIN_COST = builder.comment("Minimum XP cost for level 1").defineInRange("Frameguard Min Cost", 10, 1, 50);
		FRAMEGUARD_COST_PER_LEVEL = builder.comment("Additional XP cost per level above 1").defineInRange("Frameguard Cost Per Level", 8, 1, 20);
		FRAMEGUARD_COST_RANGE = builder.comment("XP cost range (added to min cost)").defineInRange("Frameguard Cost Range", 15, 5, 30);
		FRAMEGUARD_IS_TREASURE = builder.comment("Whether Frameguard is a treasure enchantment").define("Frameguard Is Treasure", false);
		FRAMEGUARD_CHANCE_PER_LEVEL = builder.comment("Base chance per level that Frameguard prevents the gun from breaking").defineInRange("Frameguard Chance", 0.15, 0.0, 1.0);
		builder.pop();
		
		builder.push("Backblast");
		BACKBLAST = builder.comment("Whether the Backblast enchantment is enabled").worldRestart().define("Backblast", true);
		BACKBLAST_MAX_LEVEL = builder.comment("Maximum level for Backblast enchantment").worldRestart().defineInRange("Backblast Max Level", 3, 1, 5);
		BACKBLAST_MIN_COST = builder.comment("Minimum XP cost for level 1").defineInRange("Backblast Min Cost", 10, 1, 50);
		BACKBLAST_COST_PER_LEVEL = builder.comment("Additional XP cost per level above 1").defineInRange("Backblast Cost Per Level", 8, 1, 20);
		BACKBLAST_COST_RANGE = builder.comment("XP cost range (added to min cost)").defineInRange("Backblast Cost Range", 15, 5, 30);
		BACKBLAST_IS_TREASURE = builder.comment("Whether Backblast is a treasure enchantment").define("Backblast Is Treasure", false);
		BACKBLAST_RECOIL_BONUS_PER_LEVEL = builder.comment("Extra recoil distance bonus per level").defineInRange("Backblast Recoil Bonus", 0.25, 0.0, 2.0);
		BACKBLAST_FIRE_SHOCKWAVE_DURATION_BONUS_PER_LEVEL = builder.comment("Extra fire duration (in seconds) applied to targets per level of the Fire Shockwave").defineInRange("Backblast Fire Shockwave Duration Bonus", 2, 0, 10);
		BACKBLAST_VERTICAL_RECOIL_BONUS_PER_LEVEL = builder.comment("Extra vertical recoil multiplier per level (default 5%, max 10%)").defineInRange("Backblast Vertical Recoil Bonus", 0.05, 0.0, 0.10);
		BACKBLAST_FIRE_SHOCKWAVE_BASE_DAMAGE_PER_LEVEL = builder.comment("Base damage dealt to targets per level when the Fire Shockwave triggers").defineInRange("Backblast Fire Shockwave Base Damage", 2.0, 0.0, 10.0);
		BACKBLAST_FIRE_SHOCKWAVE_RADIUS = builder.comment("The radius of the fire shockwave area around the user").defineInRange("Backblast Fire Shockwave Radius", 1.5, 0.5, 16.0);
		BACKBLAST_FIRE_SHOCKWAVE_KNOCKBACK = builder.comment("The base knockback strength applied to enemies by the fire shockwave").defineInRange("Backblast Fire Shockwave Knockback", 0.5, 0.0, 5.0);
		BACKBLAST_FIRE_SHOCKWAVE = builder.comment("Whether Backblast creates a fire shockwave around the user").define("Backblast Fire Shockwave", true);
		BACKBLAST_EXPLOSION = builder.comment("Whether Backblast triggers an explosion when used").define("Backblast Explosion", false);
		BACKBLAST_EXPLOSION_POWER = builder.comment("Base power of the Backblast explosion").defineInRange("Backblast Explosion Power", 2.0, 0.0, 20.0);
		BACKBLAST_EXPLOSION_BREAKS_BLOCKS = builder.comment("Whether the Backblast explosion destroys blocks in the world").define("Backblast Explosion Breaks Blocks", false);
		BACKBLAST_FIRE_SHOCKWAVE_PARTICLE = builder.comment("Whether to spawn custom orange particles on targets affected by the fire shockwave").define("Backblast Fire Shockwave Particle", true);
		builder.pop();
		builder.pop();
	}
}
