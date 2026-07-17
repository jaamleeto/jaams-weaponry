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

public class PiercerStrikeItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.PIERCER_STRIKE.get()) {
            return;
        }
        if (!ModTraits.isPiercerStrikeItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.piercer_strike",
                "tooltip.jaams_weaponry.trait.piercer_strike.desc");
        if (!TooltipsConfig.TOOLTIP_PIERCER_STRIKE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.piercer_strike",
                ChatFormatting.GOLD);
        addDamageBonusLine(stack, tag, tooltip);
        addMinArmorLine(stack, tag, tooltip);
    }

    private static void addDamageBonusLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float bonusDamage = getBonusDamage(stack, tag);
        if (bonusDamage > 0.0F) {
            double roundedDamage = ModTooltips.roundToTwoDecimals(bonusDamage);
            ModTooltips.addStat(stack, tooltip, "piercer_strike.bonus_damage", roundedDamage);
        }
    }

    private static void addMinArmorLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int minArmor = getMinArmor(stack, tag);
        tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.piercer_strike.min_armor", minArmor)
                .withStyle(ChatFormatting.GRAY));
    }

    private static int getMinArmor(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PiercerStrikeMinArmor")) {
            return Math.max(0, tag.getInt("PiercerStrikeMinArmor"));
        }
        return TraitModifierData.getPiercerStrike(stack)
                .map((entry) -> entry.min_armor)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PIERCER_STRIKE_MIN_ARMOR.get());
    }

    private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PiercerStrikeBonusDamage")) {
            return tag.getFloat("PiercerStrikeBonusDamage");
        }
        return TraitModifierData.getPiercerStrike(stack)
                .map((entry) -> entry.bonus_damage)
                .orElseGet(() -> TraitsConfig.PIERCER_STRIKE_BONUS_DAMAGE.get().floatValue());
    }
}
