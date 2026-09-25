package net.jaams.weaponry.tooltip.item;

import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.weaponry.util.ModTooltips;

public class FlintItemTooltip {

    private static final Map<String, String> FLINT_LANG_KEYS = Map.of(
            "flint_hammer", "tooltip.jaams_weaponry.flint_hammer.long_desc",
            "flint_mallet", "tooltip.jaams_weaponry.flint_mallet.long_desc");

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null) {
            return;
        }
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) {
            return;
        }
        String langKey = FLINT_LANG_KEYS.get(itemKey.getPath());
        if (langKey == null) {
            return;
        }
        ModTooltips.addLongDescription(stack, tooltip, langKey, ChatFormatting.GRAY);
    }
}
