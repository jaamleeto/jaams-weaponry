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

public class UndeadGrudgeItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (!TraitsConfig.UNDEAD_GRUDGE.get()) {
			return;
		}
		if (!ModTraits.isUndeadGrudgeItem(stack)) {
			return;
		}
		CompoundTag tag = ModComponents.get(stack);
		ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.undead_grudge", "tooltip.jaams_weaponry.trait.undead_grudge.desc");
		if (!TooltipsConfig.TOOLTIP_UNDEAD_GRUDGE_PROPERTIES.get()) {
			return;
		}
		ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.undead_grudge", ChatFormatting.GOLD);
		float bonusDamage = getBonusDamage(stack, tag);
		if (bonusDamage > 0.0F) {
			ModTooltips.addStat(stack, tooltip, "undead_grudge.bonus_damage", ModTooltips.roundToTwoDecimals(bonusDamage));
		}
	}

	private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("UndeadGrudgeBonusDamage")) {
			return tag.getFloat("UndeadGrudgeBonusDamage");
		}
		return TraitModifierData.getUndeadGrudge(stack).map(entry -> entry.bonus_damage).orElseGet(() -> TraitsConfig.UNDEAD_GRUDGE_BONUS_DAMAGE.get().floatValue());
	}
}
