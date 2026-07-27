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

public class BusterStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.BUSTER_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isBusterStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.buster_strike",
                "tooltip.jaams_weaponry.trait.buster_strike.desc");
        if (!TooltipsConfig.TOOLTIP_BUSTER_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.buster_strike",
                ChatFormatting.GOLD);
        addPropertiesLines(stack, tag, tooltip);
    }

    private static void addPropertiesLines(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int requiredHits = getRequiredHits(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "buster_strike.required_hits", requiredHits);
        float bonusMultiplier = getBonusMultiplier(stack, tag);
        if (bonusMultiplier > 0.0F) {
            double roundedMultiplier = ModTooltips.roundToTwoDecimals(bonusMultiplier * 100.0F);
            ModTooltips.addStat(stack, tooltip, "buster_strike.bonus_multiplier", roundedMultiplier);
        }
    }

    private static int getRequiredHits(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BusterStrikeRequiredHits")) {
            return Math.max(1, tag.getInt("BusterStrikeRequiredHits"));
        }
        int value = TraitModifierData.getBusterStrike(stack)
                .map((entry) -> entry.required_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BUSTER_STRIKE_REQUIRED_HITS.get());
        return Math.max(1, value);
    }

    private static float getBonusMultiplier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BusterStrikeBonusMultiplier")) {
            return Math.max(0.0F, tag.getFloat("BusterStrikeBonusMultiplier"));
        }
        float value = TraitModifierData.getBusterStrike(stack)
                .map((entry) -> entry.bonus_multiplier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.BUSTER_STRIKE_BONUS_MULTIPLIER.get().floatValue());
        return Math.max(0.0F, value);
    }
}
