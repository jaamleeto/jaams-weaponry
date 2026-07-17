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

public class BrittleHandleItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.BRITTLE_HANDLE.get()) {
            return;
        }
        if (!ModTraits.isBrittleHandleItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.brittle_handle", "tooltip.jaams_weaponry.trait.brittle_handle.desc");
        if (!TooltipsConfig.TOOLTIP_BRITTLE_HANDLE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.brittle_handle", ChatFormatting.RED);
        ModTooltips.addStat(stack, tooltip, "brittle_handle.extra_durability", getExtraDurability(stack, tag));
    }

    private static int getExtraDurability(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("BrittleHandleExtraDurability")) {
            return tag.getInt("BrittleHandleExtraDurability");
        }
        return TraitModifierData.getBrittleHandle(stack)
            .map((entry) -> entry.extra_durability_cost)
            .orElseGet(() -> TraitsConfig.BRITTLE_HANDLE_EXTRA_DURABILITY.get());
    }
}
