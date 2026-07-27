package net.jaams.weaponry.tooltip.item;

import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.weaponry.util.ModTooltips;

public class AttachmentItemTooltip {

    private static final Map<String, String> ATTACHMENT_LANG_KEYS = Map.ofEntries(
            Map.entry("copper_choke", "tooltip.jaams_weaponry.choke.long_desc"),
            Map.entry("copper_extended_magazine", "tooltip.jaams_weaponry.extended_magazine.long_desc"),
            Map.entry("copper_muzzle", "tooltip.jaams_weaponry.muzzle.long_desc"),
            Map.entry("copper_quick_draw_magazine", "tooltip.jaams_weaponry.quick_draw_magazine.long_desc"));

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null) {
            return;
        }
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) {
            return;
        }
        String path = itemKey.getPath();
        String langKey = ATTACHMENT_LANG_KEYS.get(path);
        if (langKey == null) {
            return;
        }
        ModTooltips.addLongDescription(stack, tooltip, langKey, ChatFormatting.GRAY);
    }
}
