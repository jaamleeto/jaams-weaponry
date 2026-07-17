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

public class ThroughStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.THROUGH_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isThroughStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.through_strike", "tooltip.jaams_weaponry.trait.through_strike.desc");
        if (!TooltipsConfig.TOOLTIP_THROUGH_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.through_strike", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "through_strike.chance", getChance(stack, tag) * 100.0);
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ThroughStrikeChance")) {
            return tag.getFloat("ThroughStrikeChance");
        }
        return TraitModifierData.getThroughStrike(stack)
            .map((entry) -> entry.chance)
            .orElseGet(() -> TraitsConfig.THROUGH_STRIKE_CHANCE.get().floatValue());
    }
}
