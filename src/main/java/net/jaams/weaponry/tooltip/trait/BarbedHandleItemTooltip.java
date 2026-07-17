package net.jaams.weaponry.tooltip.trait;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.List;

public class BarbedHandleItemTooltip {
    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.BARBED_HANDLE.get()) {
            return;
        }
        if (!ModTraits.isBarbedHandleItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.barbed_handle", "tooltip.jaams_weaponry.trait.barbed_handle.desc");
        if (!TooltipsConfig.TOOLTIP_BARBED_HANDLE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.barbed_handle", ChatFormatting.RED);
        double percent = ModTooltips.roundToTwoDecimals(getDamageReturnFactor(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "barbed_handle.damage_return", percent);
    }

    private static float getDamageReturnFactor(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BarbedHandleDamageReturnFactor")) {
            return tag.getFloat("BarbedHandleDamageReturnFactor");
        }
        return TraitModifierData.getBarbedHandle(stack).map(entry -> entry.damage_return_factor)
                .orElseGet(() -> TraitsConfig.BARBED_HANDLE_DAMAGE_RETURN_FACTOR.get().floatValue());
    }
}
