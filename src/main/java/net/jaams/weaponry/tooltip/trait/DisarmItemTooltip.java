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

public class DisarmItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.DISARM.get()) {
            return;
        }
        if (!ModTraits.isDisarmItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.disarm", "tooltip.jaams_weaponry.trait.disarm.desc");
        if (!TooltipsConfig.TOOLTIP_DISARM_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.disarm", ChatFormatting.GOLD);
        ModTooltips.addStat(stack, tooltip, "disarm.chance", getChance(stack, tag) * 100.0);
    }

    private static float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisarmChance")) {
            return tag.getFloat("DisarmChance");
        }
        return TraitModifierData.getDisarm(stack)
            .map((entry) -> entry.chance)
            .orElseGet(() -> TraitsConfig.DISARM_CHANCE.get().floatValue());
    }
}
