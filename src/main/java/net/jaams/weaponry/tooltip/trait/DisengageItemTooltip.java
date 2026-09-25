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

public class DisengageItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.DISENGAGE.get()) {
            return;
        }
        if (!ModTraits.isDisengageItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.disengage", "tooltip.jaams_weaponry.trait.disengage.desc");
        if (!TooltipsConfig.TOOLTIP_DISENGAGE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.disengage", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "disengage.strength", getStrength(stack, tag));
    }

    private static float getStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisengageStrength")) {
            return tag.getFloat("DisengageStrength");
        }
        return TraitModifierData.getDisengage(stack)
            .map((entry) -> entry.strength)
            .orElseGet(() -> TraitsConfig.DISENGAGE_STRENGTH.get().floatValue());
    }
}
