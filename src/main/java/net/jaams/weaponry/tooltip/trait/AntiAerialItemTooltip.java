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

public class AntiAerialItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.ANTI_AERIAL.get()) {
            return;
        }
        if (!ModTraits.isAntiAerialItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.anti_aerial", "tooltip.jaams_weaponry.trait.anti_aerial.desc");
        if (!TooltipsConfig.TOOLTIP_ANTI_AERIAL_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.anti_aerial", ChatFormatting.GOLD);
        float bonusDamage = getBonusDamage(stack, tag);
        if (bonusDamage > 0.0F) {
            ModTooltips.addStat(stack, tooltip, "anti_aerial.bonus_damage", ModTooltips.roundToTwoDecimals(bonusDamage));
        }
    }

    private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AntiAerialBonusDamage")) {
            return tag.getFloat("AntiAerialBonusDamage");
        }
        return TraitModifierData.getAntiAerial(stack)
            .map((entry) -> entry.bonus_damage)
            .orElseGet(() -> TraitsConfig.ANTI_AERIAL_BONUS_DAMAGE.get().floatValue());
    }
}
