package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.List;

public class QuickCraftingItemTooltip {
	public static void add(ItemStack stack, List<Component> tooltip) {
		if (!TraitsConfig.QUICK_CRAFTING.get()) {
			return;
		}
		if (!ModTraits.isQuickCraftingItem(stack)) {
			return;
		}
		CompoundTag tag = ModComponents.get(stack);
		ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.quick_crafting", "tooltip.jaams_weaponry.trait.quick_crafting.desc");
		if (!TooltipsConfig.TOOLTIP_QUICK_CRAFTING_PROPERTIES.get()) {
			return;
		}
		ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.quick_crafting", ChatFormatting.GOLD);
		addIngredientAndResultLines(stack, tag, tooltip);
		addCooldownLine(stack, tag, tooltip);
	}

	private static void addIngredientAndResultLines(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
		String ingredientId = getIngredient(stack, tag);
		String resultId = getResult(stack, tag);
		int ingredientCount = getIngredientCount(stack, tag);
		int resultCount = getResultCount(stack, tag);
		net.minecraft.world.item.Item ingredientItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse(ingredientId));
		net.minecraft.world.item.Item resultItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse(resultId));
		Component ingredientBaseName = ingredientItem != null ? Component.translatable(ingredientItem.getDescriptionId()) : Component.literal(ingredientId);
		Component resultBaseName = resultItem != null ? Component.translatable(resultItem.getDescriptionId()) : Component.literal(resultId);
		Component ingredientName = Component.empty().append(ingredientBaseName).append(Component.literal(" x" + ingredientCount).withStyle(ChatFormatting.DARK_GRAY));
		Component resultName = Component.empty().append(resultBaseName).append(Component.literal(" x" + resultCount).withStyle(ChatFormatting.DARK_GRAY));
		Component ingredientComponent = Component.translatable("tooltip.jaams_weaponry.properties.quick_crafting_ingredient", ingredientName).withStyle(ChatFormatting.GRAY);
		Component resultComponent = Component.translatable("tooltip.jaams_weaponry.properties.quick_crafting_result", resultName).withStyle(ChatFormatting.GRAY);
		tooltip.add(ingredientComponent);
		tooltip.add(resultComponent);
	}

	private static void addCooldownLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
		int cooldownTicks = getCooldown(stack, tag);
		if (cooldownTicks > 0) {
			ModTooltips.addStat(stack, tooltip, "cooldown", cooldownTicks / 20.0);
		}
	}

	private static String getIngredient(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("QuickCraftingIngredient")) {
			return tag.getString("QuickCraftingIngredient");
		}
		return TraitModifierData.getQuickCrafting(stack).map(entry -> entry.ingredient).filter(java.util.Objects::nonNull).orElseGet(() -> TraitsConfig.QUICK_CRAFTING_INGREDIENT.get());
	}

	private static int getIngredientCount(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("QuickCraftingIngredientCount")) {
			return Math.max(1, tag.getInt("QuickCraftingIngredientCount"));
		}
		return TraitModifierData.getQuickCrafting(stack).map(entry -> entry.ingredient_count).filter(java.util.Objects::nonNull).orElseGet(() -> TraitsConfig.QUICK_CRAFTING_INGREDIENT_COUNT.get());
	}

	private static String getResult(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("QuickCraftingResult")) {
			return tag.getString("QuickCraftingResult");
		}
		return TraitModifierData.getQuickCrafting(stack).map(entry -> entry.result).filter(java.util.Objects::nonNull).orElseGet(() -> TraitsConfig.QUICK_CRAFTING_RESULT.get());
	}

	private static int getResultCount(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("QuickCraftingResultCount")) {
			return Math.max(1, tag.getInt("QuickCraftingResultCount"));
		}
		return TraitModifierData.getQuickCrafting(stack).map(entry -> entry.result_count).filter(java.util.Objects::nonNull).orElseGet(() -> TraitsConfig.QUICK_CRAFTING_RESULT_COUNT.get());
	}

	private static int getCooldown(ItemStack stack, CompoundTag tag) {
		if (tag != null && tag.contains("QuickCraftingCooldown")) {
			return tag.getInt("QuickCraftingCooldown");
		}
		return TraitModifierData.getQuickCrafting(stack).map(entry -> entry.cooldown).filter(java.util.Objects::nonNull).orElseGet(() -> TraitsConfig.QUICK_CRAFTING_COOLDOWN.get());
	}
}
