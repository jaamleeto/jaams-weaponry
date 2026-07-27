package net.jaams.weaponry.tooltip.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.Locale;
import java.util.List;

public class SmokeBombItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (stack == null || tooltip == null || stack.isEmpty()) {
			return;
		}
		if (!isSmokeBombEnabled(stack)) {
			return;
		}
		if (isLongDescEnabled(stack)) {
			ModTooltips.addLongDescription(stack, tooltip, "tooltip.jaams_weaponry.smoke_bomb.long_desc", ChatFormatting.GRAY);
		}
		if (!TooltipsConfig.TOOLTIP_SMOKE_BOMB_PROPERTIES.get()) {
			return;
		}
		addSmokeBombPropertiesIfEnabled(stack, tooltip);
	}

	private static boolean isSmokeBombEnabled(ItemStack stack) {
		if (!ItemFeaturesConfig.SMOKE_BOMB_MECHANIC.get()) {
			return false;
		}
		if (!ModCompats.isSmokeBomb(stack)) {
			return false;
		}
		Boolean trait = ModUtils.getBooleanNBT(stack, "SmokeBomb");
		if (trait != null) {
			return trait;
		}
		return true;
	}

	private static boolean isLongDescEnabled(ItemStack stack) {
		Boolean longDesc = ModUtils.getBooleanNBT(stack, "SmokeBombLongDesc");
		if (longDesc != null) {
			return longDesc;
		}
		return true;
	}

	private static void addSmokeBombPropertiesIfEnabled(ItemStack stack, List<Component> tooltip) {
		ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.smoke_bomb", ChatFormatting.GOLD);
		addEffectStats(stack, tooltip, "Self");
		addEffectStats(stack, tooltip, "Enemy");
		addPushForce(stack, tooltip);
		addCooldown(stack, tooltip);
	}

	private static void addEffectStats(ItemStack stack, List<Component> tooltip, String targetType) {
		double probability = ModUtils.getConfigOrNbtDouble(stack, "SmokeBomb" + targetType + "EffectProbability",
				targetType.equals("Self") ? ItemFeaturesConfig.SMOKE_BOMB_SELF_BLIND_PROBABILITY::get : ItemFeaturesConfig.SMOKE_BOMB_ENEMY_BLIND_PROBABILITY::get);
		if (probability > 0.0) {
			String effectId = ModUtils.getConfigOrNbtString(stack, "SmokeBomb" + targetType + "Effect", () -> "minecraft:blindness");
			String lowerTarget = targetType.toLowerCase(Locale.ROOT);
			Component effectName = Component.translatable("effect." + effectId.replace(":", "."));
			int chancePercent = (int) (probability * 100.0);
			Component statComponent = Component.translatable("tooltip.jaams_weaponry.properties.smoke_bomb_" + lowerTarget + "_effect", effectName, chancePercent).withStyle(ChatFormatting.GRAY);
			ModTooltips.addLongDescriptionComponent(stack, tooltip, statComponent);
		}
	}

	private static void addPushForce(ItemStack stack, List<Component> tooltip) {
		double pushForce = ModUtils.getConfigOrNbtDouble(stack, "SmokeBombPushForce", ItemFeaturesConfig.SMOKE_BOMB_PUSH_FORCE::get);
		if (pushForce > 0.0) {
			ModTooltips.addStat(stack, tooltip, "smoke_bomb_push_force", pushForce);
		}
	}

	private static void addCooldown(ItemStack stack, List<Component> tooltip) {
		int cooldownTicks = ModUtils.getConfigOrNbtInt(stack, "SmokeBombCooldown", ItemFeaturesConfig.SMOKE_BOMB_COOLDOWN::get);
		if (cooldownTicks > 0) {
			ModTooltips.addStat(stack, tooltip, "cooldown", cooldownTicks / 20.0);
		}
	}
}
