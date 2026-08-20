package net.jaams.weaponry.tooltip.trait;

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

public class SnoutGrudgeItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (!TraitsConfig.SNOUT_GRUDGE.get()) {
			return;
		}
		if (!ModTraits.isSnoutGrudgeItem(stack)) {
			return;
		}
		CompoundTag tag = stack.getTag();
		ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.snout_grudge", "tooltip.jaams_weaponry.trait.snout_grudge.desc");
		if (!TooltipsConfig.TOOLTIP_SNOUT_GRUDGE_PROPERTIES.get()) {
			return;
		}
		ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.snout_grudge", ChatFormatting.GOLD);
		ModTooltips.addStat(stack, tooltip, "snout_grudge.bonus_damage", getBonusDamage(stack, tag));
	}

	private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("SnoutGrudgeBonusDamage")) {
			return tag.getFloat("SnoutGrudgeBonusDamage");
		}
		return TraitModifierData.getSnoutGrudge(stack).map(entry -> entry.bonus_damage).orElseGet(() -> TraitsConfig.SNOUT_GRUDGE_BONUS_DAMAGE.get().floatValue());
	}
}
