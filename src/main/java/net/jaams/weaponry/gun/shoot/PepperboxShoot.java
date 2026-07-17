package net.jaams.weaponry.gun.shoot;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.configuration.common.MobBehaviorConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.gun.helper.GunShootHelper;
import net.jaams.weaponry.handler.gun.GunActionsHandler;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.packet.VisualRecoilPacket;
import net.jaams.weaponry.gun.helper.GunAttachmentHelper;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PepperboxShoot {

	public static void shoot(Level level, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (!(entity instanceof LivingEntity living)) {
			return;
		}

		boolean isPlayer = living instanceof Player;
		Player player = isPlayer ? (Player) living : null;
		boolean isClientSide = level.isClientSide();
		boolean isCreative = isPlayer && player != null && player.isCreative();
		boolean mobBypassAmmo = !isPlayer && !MobBehaviorConfig.GUN_MOBS_NEED_AMMO.get();
		boolean bypassAmmo = isCreative || mobBypassAmmo;

		GunItemData.GunEntry gunData = GunItemData.getGunData(itemstack);
		boolean useGunAmmo = GunShootHelper.getFinalAmmoSource(itemstack, "GunAmmoFromGun",
				GunSystemCommonConfig.GUN_AMMO_FROM_GUN::get, gunData != null ? gunData.ammo_from_gun : null);
		boolean useHandAmmo = GunShootHelper.getFinalAmmoSource(itemstack, "GunAmmoFromHand",
				GunSystemCommonConfig.GUN_AMMO_FROM_HAND::get, gunData != null ? gunData.ammo_from_hand : null);
		boolean useInventoryAmmo = isPlayer && GunShootHelper.getFinalAmmoSource(itemstack,
				"GunAmmoFromPlayerInventory", GunSystemCommonConfig.GUN_AMMO_FROM_PLAYER_INVENTORY::get,
				gunData != null ? gunData.ammo_from_player_inventory : null);

		int finalAmmoConsumption = Math.max(0,
				ModUtils.getConfigOrNbtInt(itemstack, "GunAmmoConsumption",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_AMMO_CONSUMPTION::get)
						+ GunAttachmentHelper.getAmmoConsumptionAddend(itemstack)
						- GunAttachmentHelper.getAmmoConsumptionSubtrahend(itemstack));

		List<FiredBullet> shotPlan = buildShotPlan(living, itemstack, useGunAmmo, useHandAmmo, useInventoryAmmo,
				finalAmmoConsumption);
		boolean hasAmmo = !shotPlan.isEmpty();

		if (!hasAmmo && !bypassAmmo) {
			if (!isClientSide) {
				GunShootHelper.playEmptySound(level, x, y, z, entity, itemstack, ModGuns.GunType.PEPPERBOX);
			}
			return;
		}

		GunItemData.ShootEntry shootData = GunItemData.getShootData(itemstack);
		GunItemData.ParticleEntry particleData = GunItemData.getData(itemstack).map(d -> d.particle).orElse(null);

		if (!isClientSide) {
			GunShootHelper.playShootSound(level, x, y, z, living, itemstack, ModGuns.GunType.PEPPERBOX);
			GunActionsHandler.handleGunShot(level, living, itemstack,
					(float) GunShootHelper.getFinalDoubleParticle(itemstack, "GunShotSize",
							GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_GUN_SHOT_SIZE::get, particleData),
					(float) GunShootHelper.getFinalDoubleParticle(itemstack, "GunShotDistance",
							GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_GUN_SHOT_DISTANCE::get, particleData));
		}

		int cooldownTicks = (int) Math.max(1,
				GunShootHelper.getFinalDouble(itemstack, "GunCooldown",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_COOLDOWN::get, shootData)
						* GunAttachmentHelper.getCooldownMultiplier(itemstack)
						+ GunAttachmentHelper.getCooldownAddend(itemstack)
						- GunAttachmentHelper.getCooldownSubtrahend(itemstack));
		if (player != null) {
			int offhandCd = (int) (GunShootHelper.getFinalDouble(itemstack, "GunOffhandCooldown",
					() -> GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_OFFHAND_COOLDOWN.get(), shootData)
					+ GunAttachmentHelper.getOffhandCooldownAddend(itemstack)
					- GunAttachmentHelper.getOffhandCooldownSubtrahend(itemstack));
			ModGuns.applyCooldowns(player, itemstack, cooldownTicks, offhandCd);
			ModGuns.applyPhysicalRecoil(player, itemstack,
					(float) (GunShootHelper.getFinalDouble(itemstack, "GunRecoilDistance",
							GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_RECOIL_DISTANCE::get, shootData)
							* GunAttachmentHelper.getRecoilMultiplier(itemstack)
							+ GunAttachmentHelper.getRecoilAddend(itemstack)
							- GunAttachmentHelper.getRecoilSubtrahend(itemstack)),
					(float) (GunShootHelper.getFinalDouble(itemstack, "GunCrouchRecoilReduction",
							GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_CROUCH_RECOIL_REDUCTION::get, shootData)
							* GunAttachmentHelper.getCrouchRecoilReductionMultiplier(itemstack)
							+ GunAttachmentHelper.getCrouchRecoilReductionAddend(itemstack)
							- GunAttachmentHelper.getCrouchRecoilReductionSubtrahend(itemstack)),
					(float) (GunShootHelper.getFinalDouble(itemstack, "GunVerticalRecoilMultiplier",
							GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_VERTICAL_RECOIL_MULTIPLIER::get, shootData)
							* GunAttachmentHelper.getVerticalRecoilMultiplier(itemstack)
							+ GunAttachmentHelper.getVerticalRecoilAddend(itemstack)
							- GunAttachmentHelper.getVerticalRecoilSubtrahend(itemstack)));
			if (player instanceof ServerPlayer serverPlayer) {
				JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
						new VisualRecoilPacket((float) (GunShootHelper.getFinalDouble(itemstack, "GunXRotRecoilIntensity",
								GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_XROT_RECOIL_INTENSITY::get, shootData)
								* GunAttachmentHelper.getXRotRecoilMultiplier(itemstack)
								+ GunAttachmentHelper.getXRotRecoilAddend(itemstack)
								- GunAttachmentHelper.getXRotRecoilSubtrahend(itemstack))));
			}
			ModUtils.applyShakeEffect(player,
					GunShootHelper.getFinalDouble(itemstack, "GunShakeIntensity",
							GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_SHAKE_INTENSITY::get, shootData)
							* GunAttachmentHelper.getShakeIntensityMultiplier(itemstack)
							+ GunAttachmentHelper.getShakeIntensityAddend(itemstack)
							- GunAttachmentHelper.getShakeIntensitySubtrahend(itemstack),
					GunShootHelper.getFinalInt(itemstack, "GunShakeResetDelay",
							GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_SHAKE_RESET_DELAY::get, shootData)
							+ GunAttachmentHelper.getShakeResetDelayAddend(itemstack)
							- GunAttachmentHelper.getShakeResetDelaySubtrahend(itemstack));
		}

		if (!isClientSide) {
			if (shotPlan.isEmpty()) {
				// Mob bypass with no ammo: fire generic projectiles so mobs can still shoot
				for (int i = 0; i < ModGuns.REVOLVER_CHAMBER_COUNT; i++) {
					spawnPepperboxProjectile(level, living, itemstack, ItemStack.EMPTY, shootData);
				}
			} else {
				for (FiredBullet fired : shotPlan) {
					spawnPepperboxProjectile(level, living, itemstack, fired.bullet, shootData);
				}
			}
		}

		if (!bypassAmmo) {
			consumePepperboxPlan(itemstack, living, shotPlan, finalAmmoConsumption);
			consumePepperboxAttachment(itemstack, living);
		}

		ModGuns.updateGunInventory(itemstack);
	}

	private enum AmmoSource { GUN, HAND, INVENTORY }

	private record FiredBullet(ItemStack bullet, AmmoSource source) {
	}

	private static List<FiredBullet> buildShotPlan(LivingEntity living, ItemStack gunStack, boolean useGunAmmo,
			boolean useHandAmmo, boolean useInventoryAmmo, int ammoConsumption) {
		List<FiredBullet> plan = new ArrayList<>();
		if (useGunAmmo) {
			for (ItemStack bullet : collectLoadedBullets(gunStack, ammoConsumption)) {
				plan.add(new FiredBullet(bullet, AmmoSource.GUN));
			}
		}
		if (!plan.isEmpty()) {
			return plan;
		}
		if (useHandAmmo) {
			ItemStack handAmmo = getAmmoFromHands(living, gunStack, ammoConsumption);
			if (!handAmmo.isEmpty()) {
				plan.add(new FiredBullet(handAmmo, AmmoSource.HAND));
			}
		}
		if (plan.isEmpty() && useInventoryAmmo && living instanceof Player player) {
			for (ItemStack bullet : getDistinctAmmoFromInventory(player, gunStack, ModGuns.REVOLVER_CHAMBER_COUNT,
					ammoConsumption)) {
				plan.add(new FiredBullet(bullet, AmmoSource.INVENTORY));
			}
		}
		return plan;
	}

	private static ItemStack getAmmoFromHands(LivingEntity living, ItemStack gunStack, int minCount) {
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack handStack = living.getItemInHand(hand);
			if (GunShootHelper.isSameStack(handStack, gunStack))
				continue;
			if (!handStack.isEmpty() && handStack.getCount() >= minCount
					&& GunShootHelper.isValidAmmo(gunStack, handStack, ModGuns.GunType.PEPPERBOX)) {
				return handStack.copy();
			}
		}
		return ItemStack.EMPTY;
	}

	private static List<ItemStack> getDistinctAmmoFromInventory(Player player, ItemStack gunStack, int maxCount,
			int minStackCount) {
		List<ItemStack> result = new ArrayList<>();
		Set<Item> seen = new HashSet<>();
		for (ItemStack invStack : player.getInventory().items) {
			if (invStack.isEmpty() || GunShootHelper.isSameStack(invStack, gunStack))
				continue;
			if (!GunShootHelper.isValidAmmo(gunStack, invStack, ModGuns.GunType.PEPPERBOX))
				continue;
			if (invStack.getCount() < minStackCount)
				continue;
			if (!seen.add(invStack.getItem()))
				continue;
			result.add(invStack.copy());
			if (result.size() >= maxCount)
				break;
		}
		return result;
	}

	private static void spawnPepperboxProjectile(Level level, LivingEntity living, ItemStack itemstack,
			ItemStack bullet, GunItemData.ShootEntry shootData) {
		ModGuns.spawnProjectile(level, living, itemstack, bullet,
				GunShootHelper.getFinalInt(itemstack, "GunProjectileCount",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_COUNT::get, shootData),
				GunShootHelper.getFinalDouble(itemstack, "GunSpreadAngle",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_SPREAD_ANGLE::get, shootData),
				GunShootHelper.getFinalDouble(itemstack, "GunProjectileSpeed",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_SPEED::get, shootData),
				GunShootHelper.getFinalDouble(itemstack, "GunProjectileInaccuracy",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_INACCURACY::get, shootData),
				GunShootHelper.getFinalDouble(itemstack, "GunProjectileDamageModifier",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_DAMAGE_MODIFIER::get, shootData),
				GunShootHelper.getFinalDouble(itemstack, "GunProjectileKnockbackModifier",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER::get, shootData),
				GunShootHelper.getFinalInt(itemstack, "GunProjectilePiercingModifier",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_PIERCING_MODIFIER::get, shootData));
	}

	private static void consumePepperboxPlan(ItemStack gunStack, LivingEntity entity, List<FiredBullet> shotPlan,
			int ammoConsumption) {
		boolean hasGunAmmo = false;
		boolean hasPlayerAmmo = false;
		for (FiredBullet fired : shotPlan) {
			if (fired.source == AmmoSource.GUN) {
				hasGunAmmo = true;
			} else {
				hasPlayerAmmo = true;
			}
		}
		if (hasGunAmmo) {
			consumePepperboxAmmo(gunStack, entity, ammoConsumption);
		}
		if (hasPlayerAmmo && entity instanceof Player player) {
			consumePlayerAmmo(player, gunStack, shotPlan, ammoConsumption);
		}
	}

	private static void consumePlayerAmmo(Player player, ItemStack gunStack, List<FiredBullet> shotPlan,
			int ammoConsumption) {
		for (FiredBullet fired : shotPlan) {
			if (fired.source == AmmoSource.HAND) {
				for (InteractionHand hand : InteractionHand.values()) {
					ItemStack handStack = player.getItemInHand(hand);
					if (GunShootHelper.isSameStack(handStack, gunStack))
						continue;
					if (!handStack.isEmpty() && handStack.is(fired.bullet.getItem())) {
						handStack.shrink(ammoConsumption);
						break;
					}
				}
			} else if (fired.source == AmmoSource.INVENTORY) {
				for (ItemStack invStack : player.getInventory().items) {
					if (invStack.isEmpty() || !invStack.is(fired.bullet.getItem()))
						continue;
					invStack.shrink(ammoConsumption);
					break;
				}
			}
		}
	}

	private static List<ItemStack> collectLoadedBullets(ItemStack gunStack, int minCount) {
		List<ItemStack> bullets = new ArrayList<>();
		gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			for (int slot = 1; slot <= ModGuns.REVOLVER_CHAMBER_COUNT; slot++) {
				ItemStack stackInSlot = handler.getStackInSlot(slot);
				if (!stackInSlot.isEmpty() && stackInSlot.getCount() >= minCount) {
					bullets.add(stackInSlot.copy());
				}
			}
		});
		return bullets;
	}

	private static void consumePepperboxAmmo(ItemStack gunStack, LivingEntity entity, int ammoConsumption) {
		gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			boolean isPlayer = entity instanceof Player;
			int ghostClipLevel = isPlayer ? gunStack.getEnchantmentLevel(ModEnchantments.GHOST_CLIP.get()) : 0;
			double noConsumeChance = isPlayer && ghostClipLevel > 0
					? EnchantmentsConfig.GHOST_CLIP_CHANCE_PER_LEVEL.get() * ghostClipLevel
					: 0.0;
			for (int slot = 1; slot <= ModGuns.REVOLVER_CHAMBER_COUNT; slot++) {
				ItemStack ammoInSlot = handler.getStackInSlot(slot);
				if (ammoInSlot.isEmpty() || ammoInSlot.getCount() < ammoConsumption)
					continue;
				boolean consumeAmmo = true;
				if (isPlayer && noConsumeChance > 0.0) {
					if (entity.level().getRandom().nextDouble() < noConsumeChance) {
						consumeAmmo = false;
					}
				}
				if (consumeAmmo) {
					ammoInSlot.shrink(ammoConsumption);
				}
			}
		});
	}

	private static void consumePepperboxAttachment(ItemStack gunStack, LivingEntity entity) {
		int attachmentConsumption = Math.max(0,
				ModUtils.getConfigOrNbtInt(gunStack, "GunAttachmentConsumption",
						GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_ATTACHMENT_CONSUMPTION::get)
						+ GunAttachmentHelper.getAttachmentConsumptionAddend(gunStack)
						- GunAttachmentHelper.getAttachmentConsumptionSubtrahend(gunStack));
		gunStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			ItemStack attachment = handler.getStackInSlot(0);
			if (attachment.isEmpty())
				return;
			if (attachment.getMaxDamage() > 0) {
				int newDamage = attachment.getDamageValue() + attachmentConsumption;
				if (newDamage >= attachment.getMaxDamage()) {
					attachment.setCount(0);
				} else {
					attachment.setDamageValue(newDamage);
				}
			} else {
				attachment.shrink(attachmentConsumption);
			}
			handler.insertItem(0, attachment, false);
		});
		if (gunStack.isDamageableItem()) {
			gunStack.hurtAndBreak(1, entity, p -> {
			});
		}
	}
}
