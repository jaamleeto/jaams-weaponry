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

public class SmashStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.SMASH_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isSmashStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.smash_strike",
                "tooltip.jaams_weaponry.trait.smash_strike.desc");
        if (!TooltipsConfig.TOOLTIP_SMASH_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.smash_strike",
                ChatFormatting.GOLD);
        addMaxBonusDamageLine(stack, tag, tooltip);
        addDamagePerBlockLine(stack, tag, tooltip);
        addSmashRadiusLine(stack, tag, tooltip);
        addMinFallDistanceLine(stack, tag, tooltip);
    }

    private static void addMaxBonusDamageLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float maxBonusDamage = getMaxBonusDamage(stack, tag);
        double roundedDamage = ModTooltips.roundToTwoDecimals(maxBonusDamage);
        ModTooltips.addStat(stack, tooltip, "smash_strike.max_bonus_damage", roundedDamage);
    }

    private static void addDamagePerBlockLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float damagePerBlock = getDamagePerBlock(stack, tag);
        double roundedDamage = ModTooltips.roundToTwoDecimals(damagePerBlock);
        ModTooltips.addStat(stack, tooltip, "smash_strike.damage_per_block", roundedDamage);
    }

    private static void addSmashRadiusLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float smashRadius = getSmashRadius(stack, tag);
        double roundedRadius = ModTooltips.roundToTwoDecimals(smashRadius);
        ModTooltips.addStat(stack, tooltip, "smash_strike.smash_radius", roundedRadius);
    }

    private static void addMinFallDistanceLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float minFallDistance = 2.0F;
        ModTooltips.addStat(stack, tooltip, "smash_strike.min_fall_distance", minFallDistance);
    }

    private static float getMaxBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SmashStrikeMaxBonusDamage")) {
            return tag.getFloat("SmashStrikeMaxBonusDamage");
        }
        return TraitModifierData.getSmashStrike(stack)
                .map(e -> e.max_bonus_damage)
                .orElseGet(() -> TraitsConfig.SMASH_STRIKE_MAX_BONUS_DAMAGE.get().floatValue());
    }

    private static float getDamagePerBlock(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SmashStrikeDamagePerBlock")) {
            return tag.getFloat("SmashStrikeDamagePerBlock");
        }
        return TraitModifierData.getSmashStrike(stack)
                .map(e -> e.damage_per_block)
                .orElseGet(() -> TraitsConfig.SMASH_STRIKE_DAMAGE_PER_BLOCK.get().floatValue());
    }

    private static float getSmashRadius(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SmashStrikeSmashRadius")) {
            return tag.getFloat("SmashStrikeSmashRadius");
        }
        return TraitModifierData.getSmashStrike(stack)
                .map(e -> e.smash_radius)
                .orElseGet(() -> TraitsConfig.SMASH_STRIKE_SMASH_RADIUS.get().floatValue());
    }
}
