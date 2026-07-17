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

public class BackstabItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.BACKSTAB.get()) {
            return;
        }
        if (!ModTraits.isBackstabItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.backstab",
                "tooltip.jaams_weaponry.trait.backstab.desc");
        if (!TooltipsConfig.TOOLTIP_BACKSTAB_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.backstab", ChatFormatting.GOLD);
        float multiplierNormal = getMultiplierNormal(stack, tag);
        if (multiplierNormal > 0) {
            ModTooltips.addStat(stack, tooltip, "backstab.multiplier_normal",
                    ModTooltips.roundToTwoDecimals(multiplierNormal));
        }
    }

    private static float getMultiplierNormal(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BackstabMultiplierNormal")) {
            return Math.max(1.0F, tag.getFloat("BackstabMultiplierNormal"));
        }
        return TraitModifierData.getBackstab(stack)
                .map(e -> e.multiplier_normal)
                .orElseGet(() -> TraitsConfig.BACKSTAB_MULTIPLIER_NORMAL.get().floatValue());
    }
}
