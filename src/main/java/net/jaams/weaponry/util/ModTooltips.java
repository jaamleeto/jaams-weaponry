package net.jaams.weaponry.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class ModTooltips {
    private static boolean canAddTooltip(ItemStack stack, boolean requireCtrl, boolean requireAlt,
            List<? extends String> extraExclusions) {
        if (!TooltipsConfig.TOOLTIPS.get())
            return false;
        if (isItemExcluded(stack, TooltipsConfig.EXCLUDED_TOOLTIPS_ITEMS.get()))
            return false;
        if (requireCtrl && !TooltipsConfig.CONTROL_TOOLTIPS.get())
            return false;
        if (requireAlt && !TooltipsConfig.ALT_TOOLTIPS.get())
            return false;
        if (extraExclusions != null && !extraExclusions.isEmpty() && isItemExcluded(stack, extraExclusions)) {
            return false;
        }
        return true;
    }

    public static void addTrait(ItemStack stack, List<Component> tooltip, String traitKey, String descKey) {
        if (!canAddTooltip(stack, false, false, null))
            return;
        tooltip.add(Component.translatable(traitKey).withStyle(ChatFormatting.GOLD));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void addNegativeTrait(ItemStack stack, List<Component> tooltip, String traitKey, String descKey) {
        if (!canAddTooltip(stack, false, false, null))
            return;
        tooltip.add(Component.translatable(traitKey).withStyle(ChatFormatting.RED));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void addProjectileTrait(ItemStack stack, List<Component> tooltip, String traitKey, String descKey) {
        if (!canAddTooltip(stack, false, false, null))
            return;
        tooltip.add(Component.translatable(traitKey).withStyle(ChatFormatting.YELLOW));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void addExtraInfo(ItemStack stack, List<Component> tooltip, String key, ChatFormatting... styles) {
        if (!canAddTooltip(stack, true, false, TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get()))
            return;
        tooltip.add(Component.translatable(key).withStyle(styles));
    }

    public static void addStat(ItemStack stack, List<Component> tooltip, String key, double value) {
        if (value <= 0)
            return;
        if (!canAddTooltip(stack, true, false, TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get()))
            return;
        double rounded = roundToTwoDecimals(value);
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
        String formattedValue = df.format(rounded);
        tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties." + key, formattedValue)
                .withStyle(ChatFormatting.GRAY));
    }

    public static void addStatText(ItemStack stack, List<Component> tooltip, String key, String value) {
        if (value == null || value.isEmpty())
            return;
        if (!canAddTooltip(stack, true, false, TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get()))
            return;
        tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties." + key, value)
                .withStyle(ChatFormatting.GRAY));
    }

    public static void addStatInt(ItemStack stack, List<Component> tooltip, String key, int value) {
        if (value <= 0)
            return;
        if (!canAddTooltip(stack, true, false, TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get()))
            return;
        tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties." + key, value)
                .withStyle(ChatFormatting.GRAY));
    }

    public static void addLongDescription(ItemStack stack, List<Component> tooltip, String key,
            ChatFormatting... styles) {
        if (!canAddTooltip(stack, false, true, TooltipsConfig.EXCLUDED_ALT_TOOLTIPS_ITEMS.get()))
            return;
        tooltip.add(Component.translatable(key).withStyle(styles));
    }

    public static void addLongDescriptionComponent(ItemStack stack, List<Component> tooltip, Component component) {
        if (!canAddTooltip(stack, false, true, TooltipsConfig.EXCLUDED_ALT_TOOLTIPS_ITEMS.get()))
            return;
        tooltip.add(component);
    }

    public static boolean isItemExcluded(ItemStack stack, List<? extends String> excludedList) {
        if (stack == null || stack.isEmpty() || excludedList == null || excludedList.isEmpty()) {
            return false;
        }
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemKey == null) {
            return false;
        }
        Set<String> excludedSet = new HashSet<>(excludedList);
        return ModUtils.matchesList(excludedSet, itemKey, stack.getItem(), false);
    }

    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> Integer.toString(n);
        };
    }

    public static String formatNumber(double number) {
        return String.format("%.1f", number);
    }

    public static String getKeyDisplayName(ModEnums.KeyOption option) {
        return switch (option) {
            case ALT -> "Alt";
            case SHIFT -> "Shift";
            case CONTROL -> "Ctrl";
        };
    }
}
