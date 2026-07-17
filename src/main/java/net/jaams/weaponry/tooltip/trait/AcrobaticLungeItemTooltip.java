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

public class AcrobaticLungeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.ACROBATIC_LUNGE.get()) {
            return;
        }
        if (!ModTraits.isAcrobaticLungeItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.acrobatic_lunge", "tooltip.jaams_weaponry.trait.acrobatic_lunge.desc");
        if (!TooltipsConfig.TOOLTIP_ACROBATIC_LUNGE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.acrobatic_lunge", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "acrobatic_lunge.strength", getStrength(stack, tag));
    }

    private static float getStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AcrobaticLungeStrength")) {
            return tag.getFloat("AcrobaticLungeStrength");
        }
        return TraitModifierData.getAcrobaticLunge(stack)
            .map((entry) -> entry.strength)
            .orElseGet(() -> TraitsConfig.ACROBATIC_LUNGE_STRENGTH.get().floatValue());
    }
}
