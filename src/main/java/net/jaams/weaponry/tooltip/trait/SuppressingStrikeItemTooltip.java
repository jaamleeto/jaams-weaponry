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

public class SuppressingStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.SUPPRESSING_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isSuppressingStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.suppressing_strike",
                "tooltip.jaams_weaponry.trait.suppressing_strike.desc");
        if (!TooltipsConfig.TOOLTIP_SUPPRESSING_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.suppressing_strike",
                ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "suppressing_strike.chance", getChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "suppressing_strike.duration", getDuration(stack, tag) / 20.0);
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SuppressingStrikeChance")) {
            return tag.getFloat("SuppressingStrikeChance");
        }
        return TraitModifierData.getSuppressingStrike(stack)
                .map((entry) -> entry.chance)
                .orElseGet(() -> TraitsConfig.SUPPRESSING_STRIKE_CHANCE.get().floatValue());
    }

    private static int getDuration(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SuppressingStrikeDuration")) {
            return tag.getInt("SuppressingStrikeDuration");
        }
        return TraitModifierData.getSuppressingStrike(stack)
                .map((entry) -> entry.duration)
                .orElseGet(() -> TraitsConfig.SUPPRESSING_STRIKE_DURATION.get());
    }
}
