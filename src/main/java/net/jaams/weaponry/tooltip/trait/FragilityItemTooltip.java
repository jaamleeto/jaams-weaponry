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

public class FragilityItemTooltip {
    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.FRAGILITY.get()) {
            return;
        }
        if (!ModTraits.isFragilityItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.fragility", "tooltip.jaams_weaponry.trait.fragility.desc");
        if (!TooltipsConfig.TOOLTIP_FRAGILITY_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.fragility", ChatFormatting.RED);
        double chance = ModTooltips.roundToTwoDecimals(getBreakChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "fragility.break_chance", chance);

        
        float threshold = getMinDurabilityThreshold(stack, tag);
        if (threshold > 0.0F) {
            double thresholdPercent = ModTooltips.roundToTwoDecimals(threshold * 100.0);
            ModTooltips.addStat(stack, tooltip, "fragility.min_durability_threshold", thresholdPercent);
        }

        
        String remainingItemId = getRemainingItemId(stack, tag);
        if (remainingItemId != null) {
            Component itemName;
            ResourceLocation loc = ResourceLocation.tryParse(remainingItemId);
            if (loc != null) {
                net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(loc);
                itemName = item != null
                        ? Component.translatable(item.getDescriptionId())
                        : Component.literal(remainingItemId);
            } else {
                itemName = Component.literal(remainingItemId);
            }
            tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.fragility.remaining_item", itemName)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static float getBreakChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("FragilityBreakChance")) {
            return tag.getFloat("FragilityBreakChance");
        }
        return TraitModifierData.getFragility(stack).map(entry -> entry.break_chance)
                .orElseGet(() -> TraitsConfig.FRAGILITY_BREAK_CHANCE.get().floatValue());
    }

    private static float getMinDurabilityThreshold(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("FragilityMinDurabilityThreshold")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("FragilityMinDurabilityThreshold")));
        }
        return TraitModifierData.getFragility(stack)
                .map(entry -> entry.min_durability_threshold)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.FRAGILITY_MIN_DURABILITY_THRESHOLD.get().floatValue());
    }

    @javax.annotation.Nullable
    private static String getRemainingItemId(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("FragilityRemainingItem")) {
            String itemId = tag.getString("FragilityRemainingItem");
            if (!itemId.isEmpty()) {
                return itemId;
            }
        }
        return TraitModifierData.getFragility(stack)
                .map(entry -> entry.remaining_item)
                .filter(java.util.Objects::nonNull)
                .map(ri -> ri.item)
                .filter(java.util.Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .orElse(null);
    }
}
