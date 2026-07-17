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

public class DuelistItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.DUELIST.get()) {
            return;
        }
        if (!ModTraits.isDuelistItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.duelist", "tooltip.jaams_weaponry.trait.duelist.desc");
        if (!TooltipsConfig.TOOLTIP_DUELIST_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.duelist", ChatFormatting.GOLD);
        addDamageBonusLine(stack, tag, tooltip);
    }

    private static void addDamageBonusLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float bonusDamage = getBonusDamage(stack, tag);
        if (bonusDamage > 0.0F) {
            double roundedDamage = ModTooltips.roundToTwoDecimals(bonusDamage);
            ModTooltips.addStat(stack, tooltip, "duelist.bonus_damage", roundedDamage);
        }
    }

    private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DuelistBonusDamage")) {
            return tag.getFloat("DuelistBonusDamage");
        }
        return TraitModifierData.getDuelist(stack)
            .map((entry) -> entry.bonus_damage)
            .orElseGet(() -> TraitsConfig.DUELIST_BONUS_DAMAGE.get().floatValue());
    }
}
