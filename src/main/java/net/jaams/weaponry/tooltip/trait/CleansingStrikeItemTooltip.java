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

public class CleansingStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.CLEANSING_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isCleansingStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.cleansing_strike", "tooltip.jaams_weaponry.trait.cleansing_strike.desc");
        if (!TooltipsConfig.TOOLTIP_CLEANSING_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.cleansing_strike", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "cleansing_strike.chance", getChance(stack, tag) * 100.0);
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("CleansingStrikeChance")) {
            return tag.getFloat("CleansingStrikeChance");
        }
        return TraitModifierData.getCleansingStrike(stack)
            .map((entry) -> entry.chance)
            .orElseGet(() -> TraitsConfig.CLEANSING_STRIKE_CHANCE.get().floatValue());
    }
}
