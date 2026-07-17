package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.handler.trait.ParryGuardHandler;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ParryGuardItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!ModTraits.isParryGuardItem(stack)) {
            return;
        }
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.parry_guard",
                "tooltip.jaams_weaponry.trait.parry_guard.desc");
        if (!TooltipsConfig.TOOLTIP_PARRY_GUARD_PROPERTIES.get()) {
            return;
        }
        ParryGuardHandler.ParryGuardProperties props = ParryGuardHandler.getParryGuardProperties(stack);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.parry_guard",
                ChatFormatting.GOLD);
        double reductionPercent = props.blockDamageReduction * 100.0;
        ModTooltips.addStat(stack, tooltip, "parry_guard.block_damage_reduction", reductionPercent);
        ModTooltips.addStatInt(stack, tooltip, "parry_guard.cooldown_ticks", props.cooldownTicks);
    }
}
