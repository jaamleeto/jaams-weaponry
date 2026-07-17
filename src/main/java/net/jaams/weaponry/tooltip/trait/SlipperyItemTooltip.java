package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SlipperyItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.SLIPPERY.get()) {
            return;
        }
        if (!ModTraits.isSlipperyItem(stack)) {
            return;
        }
        // Secure Grip enchantment prevents the Slippery trait from working
        if (stack.getEnchantmentLevel(ModEnchantments.SECURE_GRIP.get()) > 0) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addNegativeTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.slippery", "tooltip.jaams_weaponry.trait.slippery.desc");
        if (!TooltipsConfig.TOOLTIP_SLIPPERY_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.slippery", ChatFormatting.RED);
        double chance = ModTooltips.roundToTwoDecimals(getDisarmChance(stack, tag) * 100.0);
        ModTooltips.addStat(stack, tooltip, "slippery.disarm_chance", chance);
    }

    private static float getDisarmChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SlipperyChance")) {
            return tag.getFloat("SlipperyChance");
        }
        return TraitModifierData.getSlippery(stack)
            .map((entry) -> entry.disarm_chance)
            .orElseGet(() -> TraitsConfig.SLIPPERY_CHANCE.get().floatValue());
    }
}
