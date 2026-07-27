package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.List;

public class AquaticGrudgeItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (!TraitsConfig.AQUATIC_GRUDGE.get()) {
			return;
		}
		if (!ModTraits.isAquaticGrudgeItem(stack)) {
			return;
		}
		CompoundTag tag = ModComponents.get(stack);
		ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.aquatic_grudge", "tooltip.jaams_weaponry.trait.aquatic_grudge.desc");
		if (!TooltipsConfig.TOOLTIP_AQUATIC_GRUDGE_PROPERTIES.get()) {
			return;
		}
		ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.aquatic_grudge", ChatFormatting.GOLD);
		float bonusDamage = getBonusDamage(stack, tag);
		if (bonusDamage > 0.0F) {
			ModTooltips.addStat(stack, tooltip, "aquatic_grudge.bonus_damage", ModTooltips.roundToTwoDecimals(bonusDamage));
		}
	}

	private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("AquaticGrudgeBonusDamage")) {
			return tag.getFloat("AquaticGrudgeBonusDamage");
		}
		return TraitModifierData.getAquaticGrudge(stack).map(entry -> entry.bonus_damage).orElseGet(() -> TraitsConfig.AQUATIC_GRUDGE_BONUS_DAMAGE.get().floatValue());
	}
}
