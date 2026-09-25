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

public class DexterousLungeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.DEXTEROUS_LUNGE.get()) {
            return;
        }
        if (!ModTraits.isDexterousLungeItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.dexterous_lunge", "tooltip.jaams_weaponry.trait.dexterous_lunge.desc");
        if (!TooltipsConfig.TOOLTIP_DEXTEROUS_LUNGE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.dexterous_lunge", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "dexterous_lunge.pull_strength", getPullStrength(stack, tag));
        ModTooltips.addStat(stack, tooltip, "dexterous_lunge.attract_strength", getAttractStrength(stack, tag));
    }

    private static float getPullStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DexterousLungePullStrength")) {
            return tag.getFloat("DexterousLungePullStrength");
        }
        return TraitModifierData.getDexterousLunge(stack)
            .map((entry) -> entry.pull_strength)
            .orElseGet(() -> TraitsConfig.DEXTEROUS_LUNGE_PULL_STRENGTH.get().floatValue());
    }

    private static float getAttractStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DexterousLungeAttractStrength")) {
            return tag.getFloat("DexterousLungeAttractStrength");
        }
        return TraitModifierData.getDexterousLunge(stack)
            .map((entry) -> entry.attract_strength)
            .orElseGet(() -> TraitsConfig.DEXTEROUS_LUNGE_ATTRACT_STRENGTH.get().floatValue());
    }
}
