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

public class ReachAdvantageItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.REACH_ADVANTAGE.get()) {
            return;
        }
        if (!ModTraits.isReachAdvantageItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.reach_advantage", "tooltip.jaams_weaponry.trait.reach_advantage.desc");
        if (!TooltipsConfig.TOOLTIP_REACH_ADVANTAGE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.reach_advantage", ChatFormatting.GOLD);
        addDamageBonusLine(stack, tag, tooltip);
    }

    private static void addDamageBonusLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float bonusDamage = getBonusDamage(stack, tag);
        if (bonusDamage > 0.0F) {
            double roundedDamage = ModTooltips.roundToTwoDecimals(bonusDamage);
            ModTooltips.addStat(stack, tooltip, "reach_advantage.bonus_damage", roundedDamage);
        }
        float minDistance = getMinDistance(stack, tag);
        if (minDistance > 0.0F) {
            double roundedMin = ModTooltips.roundToTwoDecimals(minDistance);
            ModTooltips.addStat(stack, tooltip, "reach_advantage.min_distance", roundedMin);
        }
        float maxDistance = getMaxDistance(stack, tag);
        if (maxDistance > 0.0F) {
            double roundedMax = ModTooltips.roundToTwoDecimals(maxDistance);
            ModTooltips.addStat(stack, tooltip, "reach_advantage.max_distance", roundedMax);
        }
    }

    private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ReachAdvantageBonusDamage")) {
            return tag.getFloat("ReachAdvantageBonusDamage");
        }
        return TraitModifierData.getReachAdvantage(stack)
            .map((entry) -> entry.bonus_damage)
            .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_BONUS_DAMAGE.get().floatValue());
    }

    private static float getMinDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ReachAdvantageMinDistance")) {
            return tag.getFloat("ReachAdvantageMinDistance");
        }
        return TraitModifierData.getReachAdvantage(stack)
            .map((entry) -> entry.min_distance)
            .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_MIN_DISTANCE.get().floatValue());
    }

    private static float getMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ReachAdvantageMaxDistance")) {
            return tag.getFloat("ReachAdvantageMaxDistance");
        }
        return TraitModifierData.getReachAdvantage(stack)
            .map((entry) -> entry.max_distance)
            .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_MAX_DISTANCE.get().floatValue());
    }
}
