package net.jaams.weaponry.handler.projectile;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.entity.ItemProjectileEntity;

public class ProjectilePhysicsHandler {
	private static final int MAX_SLIME_BOUNCES = 3;
	private final ItemProjectileEntity projectile;

	public ProjectilePhysicsHandler(ItemProjectileEntity projectile) {
		this.projectile = projectile;
	}

	public void onHitBlock(BlockHitResult result, ItemStack stack) {
		String id = ModUtils.getItemIdLowercase(stack);
		if (projectile.isUnderWater() || projectile.isInWater()) {
			return;
		}
		if (id.contains("bucket") || id.contains("bottle")) {
			return;
		}
		if (id.contains("slime")) {
			handleSlimeBounce(result, stack);
		} else if (id.contains("honey")) {
			handleHoneyImpact(result, stack);
		} else if (id.contains("wool")) {
			handleWoolBounce(result, stack);
		}
	}

	private void handleSlimeBounce(BlockHitResult result, ItemStack stack) {
		if (projectile.getSlimeBounceCount() < MAX_SLIME_BOUNCES) {
			Vec3 motion = projectile.getDeltaMovement();
			Direction hitFace = result.getDirection();
			Vec3 normal = new Vec3(hitFace.getStepX(), hitFace.getStepY(), hitFace.getStepZ());
			Vec3 bounce = motion.subtract(normal.scale(motion.dot(normal) * 2));
			double bounceFactor = 0.45 + projectile.level().random.nextDouble() * 0.1;
			Vec3 newMotion = bounce.scale(bounceFactor);
			newMotion = newMotion.add((projectile.level().random.nextDouble() - 0.5) * 0.05, (projectile.level().random.nextDouble() - 0.5) * 0.05, (projectile.level().random.nextDouble() - 0.5) * 0.05);
			projectile.setDeltaMovement(newMotion);
			projectile.hasImpulse = true;
			projectile.setInGround(false);
			projectile.setNoGravityTicks(2);
			projectile.incrementSlimeBounceCount();
			spawnBreakParticles(stack, 12);
			playBounceSound();
		} else {
			projectile.setInGround(true);
			spawnBreakParticles(stack, 8);
		}
	}

	private void handleWoolBounce(BlockHitResult result, ItemStack stack) {
		if (projectile.getSlimeBounceCount() < 1) {
			Vec3 motion = projectile.getDeltaMovement();
			Direction hitFace = result.getDirection();
			Vec3 normal = new Vec3(hitFace.getStepX(), hitFace.getStepY(), hitFace.getStepZ());
			Vec3 bounce = motion.subtract(normal.scale(motion.dot(normal) * 2));
			Vec3 newMotion = bounce.scale(0.15);
			projectile.setDeltaMovement(newMotion);
			projectile.hasImpulse = true;
			projectile.setInGround(false);
			projectile.incrementSlimeBounceCount();
			playWoolSound();
		} else {
			projectile.setInGround(true);
		}
	}

	private void handleHoneyImpact(BlockHitResult result, ItemStack stack) {
		spawnBreakParticles(stack, 10);
		playHoneySound();
		if (projectile.level() instanceof ServerLevel serverLevel) {
			BlockPos pos = result.getBlockPos();
			serverLevel.sendParticles(ParticleTypes.DRIPPING_HONEY, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 6, 0.25, 0.15, 0.25, 0.0);
		}
	}

	private void spawnBreakParticles(ItemStack stack, int count) {
		if (!(projectile.level() instanceof ServerLevel serverLevel))
			return;
		serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), projectile.getX(), projectile.getY() + 0.25, projectile.getZ(), count, 0.15, 0.15, 0.15, 0.08);
	}

	private void playBounceSound() {
		projectile.level().playSound(null, projectile.blockPosition(), SoundEvents.SLIME_BLOCK_STEP, SoundSource.BLOCKS, 1.1F, 0.85F + projectile.level().random.nextFloat() * 0.3F);
	}

	private void playHoneySound() {
		projectile.level().playSound(null, projectile.blockPosition(), SoundEvents.HONEY_BLOCK_STEP, SoundSource.BLOCKS, 0.9F, 0.7F + projectile.level().random.nextFloat() * 0.4F);
	}

	private void playWoolSound() {
		projectile.level().playSound(null, projectile.blockPosition(), SoundEvents.WOOL_FALL, SoundSource.BLOCKS, 0.5F, 1.2F);
	}
}
