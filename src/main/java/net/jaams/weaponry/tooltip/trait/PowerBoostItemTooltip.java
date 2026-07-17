package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class PowerBoostItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.POWER_BOOST.get()) {
            return;
        }
        if (!ModTraits.isPowerBoostItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.power_boost",
                "tooltip.jaams_weaponry.trait.power_boost.desc");
        if (!TooltipsConfig.TOOLTIP_POWER_BOOST_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.power_boost", ChatFormatting.GOLD);
        addPropertiesLines(stack, tag, tooltip);
    }

    private static void addPropertiesLines(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int maxHits = getMaxHits(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "power_boost.max_hits", maxHits);
        float increment = getIncrement(stack, tag);
        double roundedIncrement = ModTooltips.roundToTwoDecimals(increment);
        ModTooltips.addStat(stack, tooltip, "power_boost.increment", roundedIncrement);
    }

    private static int getMaxHits(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PowerBoostMaxHits")) {
            return Math.max(1, tag.getInt("PowerBoostMaxHits"));
        }
        int value = TraitModifierData.getPowerBoost(stack)
                .map((entry) -> entry.max_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.POWER_BOOST_MAX_HITS.get());
        return Math.max(1, value);
    }

    private static float getIncrement(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PowerBoostIncrement")) {
            return Math.max(0.0F, tag.getFloat("PowerBoostIncrement"));
        }
        float value = TraitModifierData.getPowerBoost(stack)
                .map((entry) -> entry.increment)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.POWER_BOOST_INCREMENT.get().floatValue());
        return Math.max(0.0F, value);
    }
}
