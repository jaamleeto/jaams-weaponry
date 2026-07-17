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

public class AfterStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.AFTER_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isAfterStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.after_strike", "tooltip.jaams_weaponry.trait.after_strike.desc");
        if (!TooltipsConfig.TOOLTIP_AFTER_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.after_strike", ChatFormatting.GOLD);
        addPropertiesLines(stack, tag, tooltip);
    }

    private static void addPropertiesLines(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int requiredHits = getRequiredHits(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "after_strike.required_hits", requiredHits);
        int attackCount = getAttackCount(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "after_strike.attack_count", attackCount);
        int attackIntervalTicks = getAttackInterval(stack, tag);
        double attackIntervalSeconds = attackIntervalTicks / 20.0;
        ModTooltips.addStat(stack, tooltip, "after_strike.attack_interval", attackIntervalSeconds);
        if (TooltipsConfig.TOOLTIP_AFTER_STRIKE_DAMAGE_MODIFIERS.get()) {
            float initialModifier = getInitialDamageModifier(stack, tag);
            if (initialModifier > 0.0F) {
                double roundedInitial = ModTooltips.roundToTwoDecimals(initialModifier * 100.0F);
                ModTooltips.addStat(stack, tooltip, "after_strike.initial_modifier", roundedInitial);
            }
            float decayFactor = getDamageDecayFactor(stack, tag);
            if (decayFactor > 0.0F) {
                double roundedDecay = ModTooltips.roundToTwoDecimals(decayFactor * 100.0F);
                ModTooltips.addStat(stack, tooltip, "after_strike.decay_factor", roundedDecay);
            }
        }
    }

    private static int getRequiredHits(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AfterStrikeRequiredHits")) {
            return Math.max(1, tag.getInt("AfterStrikeRequiredHits"));
        }
        int value = TraitModifierData.getAfterStrike(stack)
            .map((entry) -> entry.required_hits)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.AFTER_STRIKE_REQUIRED_HITS.get());
        return Math.max(1, value);
    }

    private static int getAttackCount(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AfterStrikeAttackCount")) {
            return Math.max(1, tag.getInt("AfterStrikeAttackCount"));
        }
        int value = TraitModifierData.getAfterStrike(stack)
            .map((entry) -> entry.attack_count)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.AFTER_STRIKE_STRIKES_COUNT.get());
        return Math.max(1, value);
    }

    private static int getAttackInterval(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AfterStrikeAttackInterval")) {
            return Math.max(1, tag.getInt("AfterStrikeAttackInterval"));
        }
        int value = TraitModifierData.getAfterStrike(stack)
            .map((entry) -> entry.attack_interval)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.AFTER_STRIKE_STRIKES_INTERVAL.get());
        return Math.max(1, value);
    }

    private static float getInitialDamageModifier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AfterStrikeInitialModifier")) {
            return Math.max(0.0F, tag.getFloat("AfterStrikeInitialModifier"));
        }
        float value = TraitModifierData.getAfterStrike(stack)
            .map((entry) -> entry.initial_modifier)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.AFTER_STRIKE_INITIAL_MODIFIER.get().floatValue());
        return Math.max(0.0F, value);
    }

    private static float getDamageDecayFactor(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AfterStrikeDecayFactor")) {
            return Math.max(0.0F, tag.getFloat("AfterStrikeDecayFactor"));
        }
        float value = TraitModifierData.getAfterStrike(stack)
            .map((entry) -> entry.decay_factor)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.AFTER_STRIKE_DECAY_FACTOR.get().floatValue());
        return Math.max(0.0F, value);
    }
}
