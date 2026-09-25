package net.jaams.weaponry.gun.shoot;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;

import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.gun.helper.GunShootHelper;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;

public class ScattergunShoot {
	public static void shoot(Level level, double x, double y, double z, Entity entity, ItemStack itemstack) {
		GunShootHelper.shoot(level, x, y, z, entity, itemstack, ModGuns.GunType.SCATTERGUN, GunSystemCommonConfig.GUN_AMMO_FROM_GUN::get, GunSystemCommonConfig.GUN_AMMO_FROM_HAND::get, GunSystemCommonConfig.GUN_AMMO_FROM_PLAYER_INVENTORY::get,
				GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_AMMO_CONSUMPTION::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_ATTACHMENT_CONSUMPTION::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_COUNT::get,
				GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_SPREAD_ANGLE::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_SPEED::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_INACCURACY::get,
				GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER::get,
				GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_GUN_SHOT_SIZE::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_GUN_SHOT_DISTANCE::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_COOLDOWN::get,
				GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_OFFHAND_COOLDOWN::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_RECOIL_DISTANCE::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_CROUCH_RECOIL_REDUCTION::get,
				GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_VERTICAL_RECOIL_MULTIPLIER::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_XROT_RECOIL_INTENSITY::get, GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_SHAKE_INTENSITY::get,
				GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_SHAKE_RESET_DELAY::get);
	}
}
