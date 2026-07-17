package net.jaams.weaponry.configuration.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ProjectileClientConfig {
	
	public static ForgeConfigSpec.DoubleValue AXE_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue CLEAVER_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue RING_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue ROYAL_AXE_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue ROYAL_SPEAR_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue GIANT_SHURIKEN_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue SHURIKEN_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue KUNAI_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue PRONGED_KUNAI_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue SHARP_STONE_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue SPEAR_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue TRIDENT_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue HUNTERS_BOOMERANG_PROJECTILE_SCALE;
	public static ForgeConfigSpec.BooleanValue HUNTERS_BOOMERANG_PROJECTILE_TRAIL;
	public static ForgeConfigSpec.IntValue HUNTERS_BOOMERANG_PROJECTILE_TRAIL_SPAWN_RATE;
	
	public static ForgeConfigSpec.DoubleValue DYNAMITE_PROJECTILE_SCALE;
	public static ForgeConfigSpec.BooleanValue DYNAMITE_PROJECTILE_TRAIL;
	public static ForgeConfigSpec.IntValue DYNAMITE_PROJECTILE_TRAIL_SPAWN_RATE;
	
	public static ForgeConfigSpec.DoubleValue BROOM_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue BULLET_PROJECTILE_SCALE;
	public static ForgeConfigSpec.BooleanValue BULLET_PROJECTILE_ICON;
	
	public static ForgeConfigSpec.DoubleValue STAKE_PROJECTILE_SCALE;
	
	public static ForgeConfigSpec.DoubleValue ITEM_PROJECTILE_SCALE;

	public static void register(ForgeConfigSpec.Builder builder) {
		builder.push("Projectile Client Handler");
		builder.push("Axe Projectile");
		AXE_PROJECTILE_SCALE = builder.comment("Visual size scale of the Axe projectile").defineInRange("Projectile Visual Size", 1.7, 0.1, 10.0);
		builder.pop();
		builder.push("Cleaver Projectile");
		CLEAVER_PROJECTILE_SCALE = builder.comment("Visual size scale of the Cleaver projectile").defineInRange("Projectile Visual Size", 1.7, 0.1, 10.0);
		builder.pop();
		builder.push("Ring Projectile");
		RING_PROJECTILE_SCALE = builder.comment("Visual size scale of the Ring projectile").defineInRange("Projectile Visual Size", 1.7, 0.1, 10.0);
		builder.pop();
		
		builder.push("Royal Axe Projectile");
		ROYAL_AXE_PROJECTILE_SCALE = builder.comment("Visual size scale of the Royal Axe projectile").defineInRange("Projectile Visual Size", 1.8, 0.1, 10.0);
		builder.pop();
		
		builder.push("Royal Spear Projectile");
		ROYAL_SPEAR_PROJECTILE_SCALE = builder.comment("Visual size scale of the Royal Spear projectile").defineInRange("Projectile Visual Size", 1.0, 0.1, 10.0);
		builder.pop();
		builder.push("Giant Shuriken Projectile");
		GIANT_SHURIKEN_PROJECTILE_SCALE = builder.comment("Visual size scale of the Giant Shuriken projectile").defineInRange("Projectile Visual Size", 1.9, 0.1, 10.0);
		builder.pop();
		builder.push("Shuriken Projectile");
		SHURIKEN_PROJECTILE_SCALE = builder.comment("Visual size scale of the Shuriken projectile").defineInRange("Projectile Visual Size", 1.5, 0.1, 10.0);
		builder.pop();
		builder.push("Kunai Projectile");
		KUNAI_PROJECTILE_SCALE = builder.comment("Visual size scale of the Kunai projectile").defineInRange("Projectile Visual Size", 1.7, 0.1, 10.0);
		builder.pop();
		builder.push("Pronged Kunai Projectile");
		PRONGED_KUNAI_PROJECTILE_SCALE = builder.comment("Visual size scale of the Pronged Kunai projectile").defineInRange("Projectile Visual Size", 1.9, 0.1, 10.0);
		builder.pop();
		builder.push("Sharp Stone Projectile");
		SHARP_STONE_PROJECTILE_SCALE = builder.comment("Visual size scale of the Sharp Stone projectile").defineInRange("Projectile Visual Size", 1.9, 0.1, 10.0);
		builder.pop();
		builder.push("Spear Projectile");
		SPEAR_PROJECTILE_SCALE = builder.comment("Visual size scale of the Spear projectile").defineInRange("Projectile Visual Size", 1.0, 0.1, 10.0);
		builder.pop();
		builder.push("Trident Projectile");
		TRIDENT_PROJECTILE_SCALE = builder.comment("Visual size scale of the Trident projectile").defineInRange("Projectile Visual Size", 1.0, 0.1, 10.0);
		builder.pop();
		
		builder.push("Hunters Boomerang Projectile");
		HUNTERS_BOOMERANG_PROJECTILE_SCALE = builder.comment("Visual size scale of the Hunters Boomerang projectile").defineInRange("Projectile Visual Size", 1.7, 0.1, 10.0);
		HUNTERS_BOOMERANG_PROJECTILE_TRAIL = builder.comment("Enable or disable trail particles for Hunters Boomerang projectile").define("Projectile Trail", true);
		HUNTERS_BOOMERANG_PROJECTILE_TRAIL_SPAWN_RATE = builder.comment("Hunters Boomerang projectile trail particle spawn frequency").defineInRange("Projectile Trail Spawn Rate", 3, 1, 20);
		builder.pop();
		
		builder.push("Dynamite Projectile");
		DYNAMITE_PROJECTILE_SCALE = builder.comment("Visual size scale of the Dynamite projectile").defineInRange("Projectile Visual Size", 1.5, 0.1, 10.0);
		DYNAMITE_PROJECTILE_TRAIL = builder.comment("Enable or disable trail particles for Dynamite projectile").define("Projectile Trail", true);
		DYNAMITE_PROJECTILE_TRAIL_SPAWN_RATE = builder.comment("Dynamite projectile trail particle spawn frequency").defineInRange("Projectile Trail Spawn Rate", 3, 1, 20);
		builder.pop();
		builder.push("Broom Projectile");
		BROOM_PROJECTILE_SCALE = builder.comment("Visual size scale of the Broom projectile").defineInRange("Projectile Visual Size", 1.0, 0.1, 10.0);
		builder.pop();
		builder.push("Bullet Projectile");
		BULLET_PROJECTILE_SCALE = builder.comment("Visual size scale of the Bullet projectile").defineInRange("Projectile Visual Size", 1.0, 0.1, 10.0);
		BULLET_PROJECTILE_ICON = builder.comment("Enable or disable Bullet projectile icon").define("Projectile Icon", true);
		builder.pop();
		builder.push("Stake Projectile");
		STAKE_PROJECTILE_SCALE = builder.comment("Visual size scale of the Stake projectile").defineInRange("Projectile Visual Size", 1.6, 0.1, 10.0);
		builder.pop();
		builder.push("Item Projectile");
		ITEM_PROJECTILE_SCALE = builder.comment("Default visual size scale for generic ItemProjectile").defineInRange("Projectile Visual Size", 1.3, 0.1, 10.0);
		builder.pop();
		builder.pop(); 
	}
}
