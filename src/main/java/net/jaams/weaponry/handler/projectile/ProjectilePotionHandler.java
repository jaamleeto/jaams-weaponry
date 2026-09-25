package net.jaams.weaponry.handler.projectile;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.entity.ItemProjectileEntity;

import javax.annotation.Nullable;

import java.util.List;

public class ProjectilePotionHandler {
	private final ItemProjectileEntity projectile;

	public ProjectilePotionHandler(ItemProjectileEntity projectile) {
		this.projectile = projectile;
	}

	public void onHitEntity(Entity target, ItemStack stack) {
		if (stack.is(Items.TIPPED_ARROW)) {
			handleTippedArrowImpact(target, stack);
		} else if (stack.is(Items.SPECTRAL_ARROW)) {
			handleSpectralArrowImpact(target);
		} else if (stack.getItem() instanceof PotionItem) {
			handlePotionImpact(target, true);
		}
	}

	public void onHitBlock(BlockHitResult result, ItemStack stack) {
		if (stack.getItem() instanceof PotionItem) {
			handlePotionImpact(null, false);
		}
	}

	private void handleTippedArrowImpact(Entity target, ItemStack stack) {
		if (projectile.level().isClientSide || !(target instanceof LivingEntity living))
			return;
		PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		Entity effectSource = projectile.getEffectSource();
		for (MobEffectInstance instance : contents.getAllEffects()) {
			Holder<MobEffect> effect = instance.getEffect();
			if (effect.value().isInstantenous()) {
				effect.value().applyInstantenousEffect(projectile, projectile.getOwner(), living, instance.getAmplifier(), 1.0);
			} else {
				int duration = instance.getDuration();
				if (duration > 20) {
					living.addEffect(new MobEffectInstance(effect, duration, instance.getAmplifier(), instance.isAmbient(), instance.isVisible()), effectSource);
				}
			}
		}
		playArrowImpactSound();
		projectile.discard();
	}

	private void handleSpectralArrowImpact(Entity target) {
		if (projectile.level().isClientSide || !(target instanceof LivingEntity living))
			return;
		living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, false, true));
		playArrowImpactSound();
		projectile.discard();
	}

	private void handlePotionImpact(@Nullable Entity hitEntity, boolean hitEntityDirectly) {
		ItemStack stack = projectile.getProjectileItem();
		if (!(stack.getItem() instanceof PotionItem))
			return;
		if (projectile.level().isClientSide) {
			return;
		}
		Level level = projectile.level();
		level.playSound(null, projectile.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
		PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		List<MobEffectInstance> effects = new java.util.ArrayList<>();
		contents.getAllEffects().forEach(effects::add);
		boolean isWater = contents.is(Potions.WATER) && effects.isEmpty();
		boolean isLingering = stack.is(Items.LINGERING_POTION);
		if (isWater) {
			applyWater();
		} else if (isLingering) {
			makeAreaOfEffectCloud(stack, contents);
		} else {
			double splashRadius = stack.is(Items.SPLASH_POTION) ? 4.0D : 2.8D;
			applySplash(effects, hitEntityDirectly ? hitEntity : null, splashRadius);
		}
		boolean instant = false;
		for (MobEffectInstance instance : effects) {
			if (instance.getEffect().value().isInstantenous()) {
				instant = true;
				break;
			}
		}
		int particleId = instant ? 2007 : 2002;
		level.levelEvent(particleId, projectile.blockPosition(), contents.getColor());
		projectile.discard();
	}

	private void playArrowImpactSound() {
		Level level = projectile.level();
		level.playSound(null, projectile.blockPosition(), SoundEvents.ARROW_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	private void applySplash(List<MobEffectInstance> effects, @Nullable Entity directHit, double radius) {
		AABB aabb = projectile.getBoundingBox().inflate(radius, radius / 2, radius);
		List<LivingEntity> list = projectile.level().getEntitiesOfClass(LivingEntity.class, aabb);
		Entity effectSource = projectile.getEffectSource();
		for (LivingEntity living : list) {
			if (!living.isAffectedByPotions())
				continue;
			double distSq = projectile.distanceToSqr(living);
			if (distSq >= radius * radius)
				continue;
			double factor = (living == directHit) ? 1.0D : (1.0D - Math.sqrt(distSq) / radius);
			for (MobEffectInstance instance : effects) {
				Holder<MobEffect> effect = instance.getEffect();
				if (effect.value().isInstantenous()) {
					effect.value().applyInstantenousEffect(projectile, projectile.getOwner(), living, instance.getAmplifier(), factor);
				} else {
					int duration = instance.mapDuration(d -> (int) (factor * d + 0.5D));
					if (duration > 20) {
						living.addEffect(new MobEffectInstance(effect, duration, instance.getAmplifier(), instance.isAmbient(), instance.isVisible()), effectSource);
					}
				}
			}
		}
	}

	private void makeAreaOfEffectCloud(ItemStack stack, PotionContents contents) {
		AreaEffectCloud cloud = new AreaEffectCloud(projectile.level(), projectile.getX(), projectile.getY(), projectile.getZ());
		if (projectile.getOwner() instanceof LivingEntity owner) {
			cloud.setOwner(owner);
		}
		cloud.setRadius(3.0F);
		cloud.setRadiusOnUse(-0.5F);
		cloud.setWaitTime(10);
		cloud.setDuration(600);
		cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
		cloud.setPotionContents(contents);
		contents.customColor().ifPresent(c -> cloud.setPotionContents(new net.minecraft.world.item.alchemy.PotionContents(java.util.Optional.empty(), java.util.Optional.of(c), java.util.List.of())));
		projectile.level().addFreshEntity(cloud);
	}

	private void applyWater() {
		Level level = projectile.level();
		AABB aabb = projectile.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
		for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
			double distSq = projectile.distanceToSqr(living);
			if (distSq >= 16.0D)
				continue;
			if (living.isOnFire()) {
				living.extinguishFire();
			}
			if (living.isSensitiveToWater()) {
				living.hurt(projectile.damageSources().indirectMagic(projectile, projectile.getOwner()), 1.0F);
			}
		}
		for (Axolotl axolotl : level.getEntitiesOfClass(Axolotl.class, aabb)) {
			axolotl.rehydrate();
		}
		extinguishNearbyFire(level, projectile.blockPosition());
	}

	private void extinguishNearbyFire(Level level, BlockPos center) {
		if (!(level instanceof ServerLevel serverLevel))
			return;
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, -1, -3), center.offset(3, 2, 3))) {
			BlockState state = level.getBlockState(pos);
			if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
				serverLevel.destroyBlock(pos, false);
			} else if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
				serverLevel.setBlock(pos, state.setValue(CampfireBlock.LIT, false), 3);
				serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}
}
