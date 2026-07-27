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

public class OverstrainItemTooltip {
    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.OVERSTRAIN.get()) {
            return;
        }
        if (!ModTraits.isOverstrainItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.overstrain", "tooltip.jaams_weaponry.trait.overstrain.desc");
        if (!TooltipsConfig.TOOLTIP_OVERSTRAIN_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.overstrain", ChatFormatting.RED);
        double chance = ModTooltips.roundToTwoDecimals(getChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "overstrain.chance", chance);
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("OverstrainChance")) {
            return tag.getFloat("OverstrainChance");
        }
        return TraitModifierData.getOverstrain(stack).map(entry -> entry.chance)
                .orElseGet(() -> TraitsConfig.OVERSTRAIN_CHANCE.get().floatValue());
    }
}
