package net.jaams.weaponry.mixins.client;
import net.jaams.weaponry.util.ModComponents;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.item.NunchakuItem;

@Mixin(ItemOverrides.class)
public abstract class ItemModelMixin {

	@Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
	private void jaam$resolve(BakedModel model, ItemStack stack, ClientLevel level,
			LivingEntity entity, int seed,
			CallbackInfoReturnable<BakedModel> cir) {
		String skin = getSkinFromStack(stack);
		if (skin == null) return;

		ModelResourceLocation modelLoc = getSkinModel(skin);
		if (modelLoc == null) {
			modelLoc = getNunchakuSkinModel(skin, entity, stack);
		}
		if (modelLoc == null) return;

		BakedModel customModel = Minecraft.getInstance().getModelManager().getModel(modelLoc);
		if (customModel == null || customModel == Minecraft.getInstance().getModelManager().getMissingModel()) return;

		cir.setReturnValue(customModel);
	}

	private static String getSkinFromStack(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return null;

		CompoundTag tag = ModComponents.get(stack);
		if (tag != null && tag.contains("ItemSkin")) {
			String raw = tag.getString("ItemSkin").toLowerCase();
				String skin = normalizeSkinName(raw);
				if (skin != null) return skin;
		}

		boolean isKatana = stack.is(ModTags.KATANAS);
		boolean isBoomerang = stack.is(ModTags.HUNTERS_BOOMERANGS);
		boolean isNunchaku = stack.is(ModTags.NUNCHAKUS);
		boolean isBroadsword = stack.is(ModTags.BROADSWORDS);
		boolean isButterflySword = stack.is(ModTags.BUTTERFLY_SWORDS);
		boolean isLongsword = stack.is(ModTags.LONGSWORDS);
		if (!isKatana && !isBoomerang && !isNunchaku && !isBroadsword && !isButterflySword && !isLongsword) return null;

		String displayName = stack.getDisplayName().getString().toLowerCase();
		if (displayName.startsWith("[") && displayName.endsWith("]")) {
			String raw = displayName.substring(1, displayName.length() - 1);
			String skin = normalizeSkinName(raw);
			if (skin != null) return skin;
		}
		String skin = normalizeSkinName(displayName);
		if (skin != null) return skin;

		return null;
	}

	private static ModelResourceLocation getSkinModel(String skin) {
		return switch (skin) {
			case "rengoku" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_rengoku"));
			case "mitsuri" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_mitsuri"));
			case "zenitsu" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_zenitsu"));
			case "inosuke" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_inosuke"));
			case "sokka" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_sokka"));
			case "macuahuitl" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_macuahuitl"));
			case "rita" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_rita"));
			case "blood_sword" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", "item/skin_blood_sword"));
			default -> null;
		};
	}

	private static String normalizeSkinName(String input) {
		String normalized = input.replaceAll("[\\s_]+", "_").replaceAll("[^a-z0-9_]", "").toLowerCase();
		return switch (normalized) {
			case "rengoku" -> "rengoku";
			case "mitsuri" -> "mitsuri";
			case "zenitsu" -> "zenitsu";
			case "inosuke" -> "inosuke";
			case "sokka" -> "sokka";
			case "macuahuitl" -> "macuahuitl";
			case "rita" -> "rita";
			case "blood_sword", "bloodsword" -> "blood_sword";
			case "rock_lee", "rocklee" -> "rock_lee";
			case "michaelangelo" -> "michaelangelo";
			default -> null;
		};
	}

	private static ModelResourceLocation getNunchakuSkinModel(String skin, LivingEntity entity, ItemStack stack) {
		boolean isActive = entity != null && stack != null && isNunchakuActive(entity, stack);
		return switch (skin) {
			case "rock_lee" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", isActive ? "item/skin_rock_lee_active" : "item/skin_rock_lee_idle"));
			case "michaelangelo" -> ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("jaams_weaponry", isActive ? "item/skin_michaelangelo_active" : "item/skin_michaelangelo_idle"));
			default -> null;
		};
	}

	private static boolean isNunchakuTagItem(Item item) {
		return item.builtInRegistryHolder().is(ModTags.NUNCHAKUS);
	}

	private static boolean isNunchakuActive(LivingEntity entity, ItemStack stack) {
		if (entity instanceof Player player) {
			if (net.jaams.weaponry.util.ModUtils.hasRestrictedEffect(player)) return false;
			boolean isHolding = player.getMainHandItem() == stack || player.getOffhandItem() == stack;
			if (!isHolding) return false;
			if (player.getCooldowns().isOnCooldown(stack.getItem())) return false;
			if (player.isSprinting()) return true;
			boolean isUsingWhirlingStrike = player.isUsingItem() && ModTraits.isWhirlingStrikeItem(player.getUseItem());
			boolean isActiveItem = player.getUseItem() == stack || player.getUseItem().getItem() instanceof NunchakuItem
					|| isNunchakuTagItem(player.getUseItem().getItem()) || isUsingWhirlingStrike;
			if (player.isUsingItem() && isActiveItem) return true;
			boolean isSwinging = player.swingTime > 0 && player.swingingArm != null
					&& ((player.swingingArm == InteractionHand.MAIN_HAND && player.getMainHandItem() == stack)
							|| (player.swingingArm == InteractionHand.OFF_HAND && player.getOffhandItem() == stack))
					&& player.getAttackStrengthScale(0.0F) >= 0.5F
					&& player.level().clip(new ClipContext(player.getEyePosition(1.0F),
							player.getEyePosition(1.0F).add(player.getLookAngle().scale(6.5)),
							ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
						.getType() != HitResult.Type.BLOCK;
			if (isSwinging) return true;
			boolean isRidingActive = false;
			if (player.getVehicle() instanceof LivingEntity vehicle) {
				boolean isVehicleMoving = vehicle.getDeltaMovement().length() > 0.15;
				boolean isVehicleSprinting = vehicle.isSprinting();
				isRidingActive = isHolding && (isVehicleMoving || isVehicleSprinting);
			}
			if (isRidingActive) return true;
		} else if (entity instanceof Mob mob) {
			if (mob.isNoAi() || net.jaams.weaponry.util.ModUtils.hasRestrictedEffect(mob)) return false;
			boolean isHolding = mob.getMainHandItem() == stack || mob.getOffhandItem() == stack;
			if (!isHolding) return false;
			if (mob.isSprinting()) return true;
			if (mob.isUsingItem() && (mob.getUseItem() == stack || mob.getUseItem().getItem() instanceof NunchakuItem
					|| isNunchakuTagItem(mob.getUseItem().getItem()))) return true;
			if (mob.swingTime > 0 && mob.swingingArm != null
					&& ((mob.swingingArm == InteractionHand.MAIN_HAND && mob.getMainHandItem() == stack)
							|| (mob.swingingArm == InteractionHand.OFF_HAND && mob.getOffhandItem() == stack))) {
				return true;
			}
			boolean isRidingActive = false;
			if (mob.getVehicle() instanceof LivingEntity vehicle) {
				boolean isVehicleMoving = vehicle.getDeltaMovement().length() > 0.15;
				boolean isVehicleSprinting = vehicle.isSprinting();
				isRidingActive = isHolding && (isVehicleMoving || isVehicleSprinting);
			}
			if (isRidingActive) return true;
		}
		return false;
	}
}
