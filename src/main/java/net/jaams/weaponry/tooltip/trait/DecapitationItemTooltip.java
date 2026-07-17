package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;

public class DecapitationItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.DECAPITATION.get()) {
            return;
        }
        if (!ModTraits.isDecapitationItem(stack)) {
            return;
        }
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.decapitation",
                "tooltip.jaams_weaponry.trait.decapitation.desc");

        if (!TooltipsConfig.TOOLTIP_DECAPITATION_PROPERTIES.get()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.decapitation", ChatFormatting.GOLD);
        double chance = ModTooltips.roundToTwoDecimals(getGeneralChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "decapitation.chance", chance);
        double multiplier = getCriticalMultiplier(stack, tag);
        ModTooltips.addStat(stack, tooltip, "decapitation.critical_multiplier", multiplier);
    }

    private static float getGeneralChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DecapitationGeneralChance")) {
            return tag.getFloat("DecapitationGeneralChance");
        }
        return TraitModifierData.getDecapitation(stack).map(entry -> entry.general_chance)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DECAPITATION_CHANCE.get().floatValue());
    }

    private static double getCriticalMultiplier(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DecapitationCriticalMultiplier")) {
            return tag.getFloat("DecapitationCriticalMultiplier");
        }
        return TraitModifierData.getDecapitation(stack).map(entry -> entry.critical_multiplier)
                .filter(java.util.Objects::nonNull)
                .map(Float::doubleValue)
                .orElseGet(() -> TraitsConfig.DECAPITATION_CRITICAL_MULTIPLIER.get());
    }
}
