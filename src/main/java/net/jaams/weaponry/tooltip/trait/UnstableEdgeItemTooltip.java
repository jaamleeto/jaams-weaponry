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

public class UnstableEdgeItemTooltip {
    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.UNSTABLE_EDGE.get()) {
            return;
        }
        if (!ModTraits.isUnstableEdgeItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.unstable_edge", "tooltip.jaams_weaponry.trait.unstable_edge.desc");
        if (!TooltipsConfig.TOOLTIP_UNSTABLE_EDGE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.unstable_edge", ChatFormatting.RED);
        ModTooltips.addStat(stack, tooltip, "unstable_edge.max_multiplier", ModTooltips.roundToTwoDecimals(getMaxMultiplier(stack, tag) * 100.0));
        ModTooltips.addStat(stack, tooltip, "unstable_edge.min_multiplier", ModTooltips.roundToTwoDecimals(getMinMultiplier(stack, tag) * 100.0));
    }

    private static float getMinMultiplier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("UnstableEdgeMinDamageMultiplier")) {
            return tag.getFloat("UnstableEdgeMinDamageMultiplier");
        }
        return TraitModifierData.getUnstableEdge(stack).map(entry -> entry.min_damage_multiplier)
                .orElseGet(() -> TraitsConfig.UNSTABLE_EDGE_MIN_MULTIPLIER.get().floatValue());
    }

    private static float getMaxMultiplier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("UnstableEdgeMaxDamageMultiplier")) {
            return tag.getFloat("UnstableEdgeMaxDamageMultiplier");
        }
        return TraitModifierData.getUnstableEdge(stack).map(entry -> entry.max_damage_multiplier)
                .orElseGet(() -> TraitsConfig.UNSTABLE_EDGE_MAX_MULTIPLIER.get().floatValue());
    }
}
