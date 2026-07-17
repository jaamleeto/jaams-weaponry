
package net.jaams.weaponry.item;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;

public class NunchakuItem extends SwordItem {
	public NunchakuItem() {
		super(new Tier() {
			public int getUses() {
				return 220;
			}

			public float getSpeed() {
				return 2f;
			}

			public float getAttackDamageBonus() {
				return 0f;
			}

			public int getLevel() {
				return 0;
			}

			public int getEnchantmentValue() {
				return 15;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks")));
			}
		}, 3, -2f, new Item.Properties());
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (!(entity instanceof Player player) || world.isClientSide() || player.isDeadOrDying() || player.isSpectator()) {
			return;
		}
		boolean isActive = !player.getCooldowns().isOnCooldown(this) && !ModUtils.hasRestrictedEffect(player) && !player.isSwimming() && !player.isUsingItem();
		boolean hasNunchaku = player.getMainHandItem().getItem() instanceof NunchakuItem || player.getOffhandItem().getItem() instanceof NunchakuItem;
		if (isActive && player.isSprinting() && hasNunchaku && ItemFeaturesConfig.NUNCHAKU_PLAY_SPRINT_SOUND.get()) {
			if (player.tickCount % 10 == 0) {
				float volume = (player.getMainHandItem().getItem() instanceof NunchakuItem && player.getOffhandItem().getItem() instanceof NunchakuItem) ? 0.35F : 0.25F;
				float pitch = 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F;
				ModUtils.playSound(player, "jaams_weaponry:nunchaku_chain", SoundSource.AMBIENT, volume, pitch);
			}
		}
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		Level world = entity.level();
		if (!(entity instanceof Player player) || world.isClientSide() || player.isDeadOrDying() || player.isSpectator()) {
			return retval;
		}
		boolean isActive = !player.getCooldowns().isOnCooldown(this) && !ModUtils.hasRestrictedEffect(player) && !player.isSwimming() && !player.isUsingItem();
		if (isActive && ItemFeaturesConfig.NUNCHAKU_PLAY_SWING_SOUND.get()) {
			if (player.getAttackStrengthScale(0.0F) >= 0.5F) {
				HitResult hitResult = world.clip(new ClipContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookAngle().scale(6.5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
				if (hitResult.getType() != HitResult.Type.BLOCK) {
					float volume = 0.25F;
					float pitch = 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F;
					ModUtils.playSound(player, "jaams_weaponry:nunchaku_chain", SoundSource.AMBIENT, volume, pitch);
				}
			}
		}
		return retval;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		if (entity == null || sourceentity == null) {
			return false;
		}
		RandomSource random = sourceentity.level().random;
		float pitchChain = 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F;
		float pitchHit = 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F;
		ModUtils.playSound(sourceentity, "jaams_weaponry:nunchaku_chain", SoundSource.PLAYERS, 0.35F, pitchChain);
		ModUtils.playSound(sourceentity, "jaams_weaponry:nunchaku_hit", SoundSource.PLAYERS, 0.35F, pitchHit);
		return retval;
	}
}
