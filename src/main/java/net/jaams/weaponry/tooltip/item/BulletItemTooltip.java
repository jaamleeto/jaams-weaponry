package net.jaams.weaponry.tooltip.item;

import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import net.jaams.weaponry.util.ModTooltips;

public class BulletItemTooltip {

    private static final Map<String, String> BULLET_LANG_KEYS = Map.ofEntries(
            Map.entry("bullet", "tooltip.jaams_weaponry.ammo.standard.long_desc"),
            Map.entry("fire_bullet", "tooltip.jaams_weaponry.ammo.fire.long_desc"),
            Map.entry("heavy_bullet", "tooltip.jaams_weaponry.ammo.heavy.long_desc"),
            Map.entry("glowing_bullet", "tooltip.jaams_weaponry.ammo.glowing.long_desc"),
            Map.entry("sharp_bullet", "tooltip.jaams_weaponry.ammo.sharp.long_desc"),
            Map.entry("echo_bullet", "tooltip.jaams_weaponry.ammo.echo.long_desc"),
            Map.entry("shotshell", "tooltip.jaams_weaponry.ammo.standard.long_desc"),
            Map.entry("fire_shotshell", "tooltip.jaams_weaponry.ammo.fire.long_desc"),
            Map.entry("heavy_shotshell", "tooltip.jaams_weaponry.ammo.heavy.long_desc"),
            Map.entry("glowing_shotshell", "tooltip.jaams_weaponry.ammo.glowing.long_desc"),
            Map.entry("sharp_shotshell", "tooltip.jaams_weaponry.ammo.sharp.long_desc"),
            Map.entry("echo_shotshell", "tooltip.jaams_weaponry.ammo.echo.long_desc"));

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null) {
            return;
        }
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemKey == null) {
            return;
        }
        String path = itemKey.getPath();
        String langKey = BULLET_LANG_KEYS.get(path);
        if (langKey == null) {
            return;
        }
        ModTooltips.addLongDescription(stack, tooltip, langKey, ChatFormatting.GRAY);
    }
}
