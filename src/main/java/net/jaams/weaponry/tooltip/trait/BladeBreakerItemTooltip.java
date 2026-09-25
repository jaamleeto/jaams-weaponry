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

public class BladeBreakerItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.BLADE_BREAKER.get()) {
            return;
        }
        if (!ModTraits.isBladeBreakerItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.blade_breaker", "tooltip.jaams_weaponry.trait.blade_breaker.desc");
        if (!TooltipsConfig.TOOLTIP_BLADE_BREAKER_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.blade_breaker", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "blade_breaker.chance", getChance(stack, tag) * 100.0);
        ModTooltips.addStatInt(stack, tooltip, "blade_breaker.durability_damage", getDurabilityDamage(stack, tag));
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BladeBreakerChance")) {
            return tag.getFloat("BladeBreakerChance");
        }
        return TraitModifierData.getBladeBreaker(stack)
            .map((entry) -> entry.chance)
            .orElseGet(() -> TraitsConfig.BLADE_BREAKER_CHANCE.get().floatValue());
    }

    private static int getDurabilityDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BladeBreakerDurabilityDamage")) {
            return tag.getInt("BladeBreakerDurabilityDamage");
        }
        return TraitModifierData.getBladeBreaker(stack)
            .map((entry) -> entry.durability_damage)
            .orElseGet(() -> TraitsConfig.BLADE_BREAKER_DURABILITY_DAMAGE.get());
    }
}
