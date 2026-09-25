package net.jaams.weaponry.gun.shoot;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;

import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.gun.helper.GunShootHelper;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;

public class DefaultShoot {
	public static void shoot(Level level, double x, double y, double z, Entity entity, ItemStack itemstack) {
		GunShootHelper.shoot(level, x, y, z, entity, itemstack, ModGuns.GunType.GUN, GunSystemCommonConfig.GUN_AMMO_FROM_GUN::get, GunSystemCommonConfig.GUN_AMMO_FROM_HAND::get, GunSystemCommonConfig.GUN_AMMO_FROM_PLAYER_INVENTORY::get, 
				() -> 1, 
				() -> 1, 
				() -> 1, 
				() -> 0.0, 
				() -> 4.5, 
				() -> 0.0, 
				() -> 0.0, 
				() -> 0.0, 
				() -> 0, 
				() -> 1.0, 
				() -> 1.0, 
				() -> 20, 
				() -> 10, 
				() -> 0.0, 
				() -> 0.0, 
				() -> 0.0, 
				() -> 1.0, 
				() -> 0.5, 
				() -> 5 
		);
	}
}
