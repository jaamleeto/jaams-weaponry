package net.jaams.weaponry.handler.behavior.projectile;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;

import net.jaams.weaponry.handler.projectile.ProjectileSpecialHandler;
import net.jaams.weaponry.handler.projectile.ProjectilePotionHandler;
import net.jaams.weaponry.handler.projectile.ProjectilePlacementHandler;
import net.jaams.weaponry.handler.projectile.ProjectilePhysicsHandler;
import net.jaams.weaponry.entity.ItemProjectileEntity;

public class ItemProjectileBehaviorHandler {
	private final ItemProjectileEntity projectile;
	private final ProjectilePhysicsHandler physicsHandler;
	private final ProjectilePlacementHandler placementHandler;
	private final ProjectileSpecialHandler specialHandler;
	private final ProjectilePotionHandler potionHandler;

	public ItemProjectileBehaviorHandler(ItemProjectileEntity projectile) {
		this.projectile = projectile;
		this.physicsHandler = new ProjectilePhysicsHandler(projectile);
		this.placementHandler = new ProjectilePlacementHandler(projectile);
		this.specialHandler = new ProjectileSpecialHandler(projectile);
		this.potionHandler = new ProjectilePotionHandler(projectile);
	}

	public void onTick(ServerLevel serverLevel) {
		ItemStack stack = projectile.getProjectileItem();
		specialHandler.onTick(serverLevel, stack);
	}

	public void onHitEntity(EntityHitResult result) {
		ItemStack stack = projectile.getProjectileItem();
		potionHandler.onHitEntity(result.getEntity(), stack);
		specialHandler.onHitEntity(result.getEntity(), stack);
	}

	public void onHitBlock(BlockHitResult result) {
		ItemStack stack = projectile.getProjectileItem();
		placementHandler.onHitBlock(result, stack);
		physicsHandler.onHitBlock(result, stack);
		potionHandler.onHitBlock(result, stack);
		specialHandler.onHitBlock(result, stack);
	}
}
