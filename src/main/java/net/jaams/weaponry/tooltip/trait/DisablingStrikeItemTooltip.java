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

public class DisablingStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.DISABLING_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isDisablingStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.disabling_strike", "tooltip.jaams_weaponry.trait.disabling_strike.desc");
        if (!TooltipsConfig.TOOLTIP_DISABLING_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.disabling_strike", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "disabling_strike.chance", getChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "disabling_strike.cooldown", getCooldown(stack, tag) / 20.0);
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisablingStrikeChance")) {
            return tag.getFloat("DisablingStrikeChance");
        }
        return TraitModifierData.getDisablingStrike(stack)
            .map((entry) -> entry.chance)
            .orElseGet(() -> TraitsConfig.DISABLING_STRIKE_CHANCE.get().floatValue());
    }

    private static int getCooldown(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisablingStrikeCooldown")) {
            return tag.getInt("DisablingStrikeCooldown");
        }
        return TraitModifierData.getDisablingStrike(stack)
            .map((entry) -> entry.cooldown)
            .orElseGet(() -> TraitsConfig.DISABLING_STRIKE_COOLDOWN.get());
    }
}
