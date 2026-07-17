package net.jaams.weaponry.tooltip.trait;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class WildSweepItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.WILD_SWEEP.get()) {
            return;
        }
        if (!ModTraits.isWildSweepItem(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.wild_sweep",
                "tooltip.jaams_weaponry.trait.wild_sweep.desc");
        if (!TooltipsConfig.TOOLTIP_WILD_SWEEP_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.wild_sweep", ChatFormatting.GOLD);
        addBreakRadiusLine(stack, tag, tooltip);
        addCooldownLine(stack, tag, tooltip);
        addBreakableBlocksLine(stack, tag, tooltip);
    }

    private static void addBreakRadiusLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int radius = getBreakRadius(stack, tag);
        ModTooltips.addStatInt(stack, tooltip, "wild_sweep.break_radius", radius);
    }

    private static void addCooldownLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int cooldown = getCooldown(stack, tag);
        ModTooltips.addStat(stack, tooltip, "wild_sweep.cooldown", cooldown / 20.0);
    }

    private static void addBreakableBlocksLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        List<String> blocks = getBreakableBlockPatterns(stack, tag);
        if (!blocks.isEmpty()) {
            String blocksList = blocks.stream()
                    .map(WildSweepItemTooltip::formatBlockPattern)
                    .collect(Collectors.joining(", "));
            tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.wild_sweep.blocks", blocksList)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static String formatBlockPattern(String pattern) {
        String cleaned = pattern.trim();
        if (cleaned.startsWith("#")) {
            cleaned = cleaned.substring(1);
        }
        int colonIndex = cleaned.indexOf(':');
        if (colonIndex != -1) {
            cleaned = cleaned.substring(colonIndex + 1);
        }
        if (cleaned.isEmpty()) {
            return pattern.trim();
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c == '_') {
                nextUpper = true;
                result.append(' ');
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static int getBreakRadius(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WildSweepBreakRadius")) {
            return tag.getInt("WildSweepBreakRadius");
        }
        return TraitModifierData.getWildSweep(stack)
                .map((e) -> e.break_radius)
                .orElseGet(() -> TraitsConfig.WILD_SWEEP_RADIUS.get());
    }

    private static int getCooldown(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WildSweepCooldown")) {
            return tag.getInt("WildSweepCooldown");
        }
        return TraitModifierData.getWildSweep(stack)
                .map((e) -> e.cooldown)
                .orElseGet(() -> TraitsConfig.WILD_SWEEP_COOLDOWN.get());
    }

    private static List<String> getBreakableBlockPatterns(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("WildSweepBreakableBlocks")) {
            return List.of(tag.getString("WildSweepBreakableBlocks").split(","));
        }
        List<String> jsonPatterns = TraitModifierData.getWildSweep(stack)
                .map((e) -> e.breakable_blocks)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonPatterns != null && !jsonPatterns.isEmpty()) {
            return jsonPatterns;
        }
        return List.copyOf(TraitsConfig.WILD_SWEEP_BREAKABLE_BLOCKS.get());
    }
}
