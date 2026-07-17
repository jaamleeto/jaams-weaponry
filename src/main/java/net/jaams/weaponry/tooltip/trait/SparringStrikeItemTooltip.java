package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SparringStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.SPARRING_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isSparringStrikeItem(stack)) {
            return;
        }
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.sparring_strike",
                "tooltip.jaams_weaponry.trait.sparring_strike.desc");
    }
}
