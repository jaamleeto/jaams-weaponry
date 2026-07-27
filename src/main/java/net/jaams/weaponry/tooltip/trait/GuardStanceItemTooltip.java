package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.handler.trait.GuardStanceHandler;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GuardStanceItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!ModTraits.isGuardStanceItem(stack)) {
            return;
        }
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.guard_stance",
                "tooltip.jaams_weaponry.trait.guard_stance.desc");
        if (!TooltipsConfig.TOOLTIP_GUARD_STANCE_PROPERTIES.get()) {
            return;
        }
        GuardStanceHandler.BlockingProperties props = GuardStanceHandler.getBlockingProperties(stack);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.guard_stance",
                ChatFormatting.GOLD);
        double reductionPercent = props.blockDamageReduction * 100.0;
        ModTooltips.addStat(stack, tooltip, "guard_stance.block_damage_reduction", reductionPercent);
        ModTooltips.addStatInt(stack, tooltip, "guard_stance.cooldown_ticks", props.cooldownTicks);
        double knockback = ModTooltips.roundToTwoDecimals(props.knockbackForce);
        ModTooltips.addStat(stack, tooltip, "guard_stance.knockback_force", knockback);
        double areaRange = ModTooltips.roundToTwoDecimals(props.areaRange);
        ModTooltips.addStat(stack, tooltip, "guard_stance.area_range", areaRange);
    }
}
