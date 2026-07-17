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

public class PullLungeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.PULL_LUNGE.get()) {
            return;
        }
        if (!ModTraits.isPullLungeItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.pull_lunge", "tooltip.jaams_weaponry.trait.pull_lunge.desc");
        if (!TooltipsConfig.TOOLTIP_PULL_LUNGE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.pull_lunge", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "pull_lunge.strength", getStrength(stack, tag));
    }

    private static float getStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PullLungeStrength")) {
            return tag.getFloat("PullLungeStrength");
        }
        return TraitModifierData.getPullLunge(stack)
            .map((entry) -> entry.strength)
            .orElseGet(() -> TraitsConfig.PULL_LUNGE_STRENGTH.get().floatValue());
    }
}
