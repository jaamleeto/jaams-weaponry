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

public class ThreatResponseItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.THREAT_RESPONSE.get()) {
            return;
        }
        if (!ModTraits.isThreatResponseItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.threat_response",
                "tooltip.jaams_weaponry.trait.threat_response.desc");
        if (!TooltipsConfig.TOOLTIP_THREAT_RESPONSE_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.threat_response",
                ChatFormatting.GOLD);
        addDamageBonusLine(stack, tag, tooltip);
    }

    private static void addDamageBonusLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        ModTooltips.addStat(stack, tooltip, "threat_response.bonus_damage", getBonusDamage(stack, tag));
    }

    private static float getBonusDamage(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ThreatResponseBonusDamage")) {
            return tag.getFloat("ThreatResponseBonusDamage");
        }
        return TraitModifierData.getThreatResponse(stack)
                .map((entry) -> entry.bonus_damage)
                .orElseGet(() -> TraitsConfig.THREAT_RESPONSE_BONUS_DAMAGE.get().floatValue());
    }
}
