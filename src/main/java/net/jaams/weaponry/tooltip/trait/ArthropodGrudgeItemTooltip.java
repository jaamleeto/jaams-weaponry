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

public class ArthropodGrudgeItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (!TraitsConfig.ARTHROPOD_GRUDGE.get()) {
			return;
		}
		if (!ModTraits.isArthropodGrudgeItem(stack)) {
			return;
		}
		CompoundTag tag = stack.getTag();
		ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.arthropod_grudge", "tooltip.jaams_weaponry.trait.arthropod_grudge.desc");
		if (!TooltipsConfig.TOOLTIP_ARTHROPOD_GRUDGE_PROPERTIES.get()) {
			return;
		}
		ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.arthropod_grudge", ChatFormatting.GOLD);
		float bonusDamage = getBonusDamage(stack, tag);
		if (bonusDamage > 0.0F) {
			ModTooltips.addStat(stack, tooltip, "arthropod_grudge.bonus_damage", ModTooltips.roundToTwoDecimals(bonusDamage));
		}
	}

	private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("ArthropodGrudgeBonusDamage")) {
			return tag.getFloat("ArthropodGrudgeBonusDamage");
		}
		return TraitModifierData.getArthropodGrudge(stack).map(entry -> entry.bonus_damage).orElseGet(() -> TraitsConfig.ARTHROPOD_GRUDGE_BONUS_DAMAGE.get().floatValue());
	}
}
