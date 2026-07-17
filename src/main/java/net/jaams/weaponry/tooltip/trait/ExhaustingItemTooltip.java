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

public class ExhaustingItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.EXHAUSTING.get()) {
            return;
        }
        if (!ModTraits.isExhaustingItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.exhausting", "tooltip.jaams_weaponry.trait.exhausting.desc");
        if (!TooltipsConfig.TOOLTIP_EXHAUSTING_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.exhausting", ChatFormatting.RED);
        ModTooltips.addStat(stack, tooltip, "exhausting.exhaustion", ModTooltips.roundToTwoDecimals(getExhaustion(stack, tag)));
    }

    private static float getExhaustion(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ExhaustingExhaustion")) {
            return tag.getFloat("ExhaustingExhaustion");
        }
        return TraitModifierData.getExhausting(stack)
            .map((entry) -> entry.exhaustion_amount)
            .orElseGet(() -> TraitsConfig.EXHAUSTING_EXHAUSTION.get().floatValue());
    }
}
