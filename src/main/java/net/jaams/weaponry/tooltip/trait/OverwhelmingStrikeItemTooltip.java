package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

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

public class OverwhelmingStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.OVERWHELMING_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isOverwhelmingStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.overwhelming_strike", "tooltip.jaams_weaponry.trait.overwhelming_strike.desc");
        if (!TooltipsConfig.TOOLTIP_OVERWHELMING_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.overwhelming_strike", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "overwhelming_strike.chance", getChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "overwhelming_strike.duration", getDuration(stack, tag) / 20.0);
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("OverwhelmingStrikeChance")) {
            return tag.getFloat("OverwhelmingStrikeChance");
        }
        return TraitModifierData.getOverwhelmingStrike(stack)
            .map((entry) -> entry.chance)
            .orElseGet(() -> TraitsConfig.OVERWHELMING_STRIKE_CHANCE.get().floatValue());
    }

    private static int getDuration(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("OverwhelmingStrikeDuration")) {
            return tag.getInt("OverwhelmingStrikeDuration");
        }
        return TraitModifierData.getOverwhelmingStrike(stack)
            .map((entry) -> entry.duration)
            .orElseGet(() -> TraitsConfig.OVERWHELMING_STRIKE_DURATION.get());
    }
}
