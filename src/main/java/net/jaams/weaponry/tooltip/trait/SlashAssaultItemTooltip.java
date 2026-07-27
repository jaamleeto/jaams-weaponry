package net.jaams.weaponry.tooltip.trait;

import net.jaams.weaponry.util.ModComponents;

import java.util.List;
import java.util.Locale;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SlashAssaultItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.SLASH_ASSAULT.get()) {
            return;
        }
        if (!ModTraits.isSlashAssaultItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.slash_assault",
                "tooltip.jaams_weaponry.trait.slash_assault.desc");
        if (!TooltipsConfig.TOOLTIP_SLASH_ASSAULT_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.slash_assault",
                ChatFormatting.GOLD);
        addDashDistanceLine(stack, tag, tooltip);
        addSlashRangeLine(stack, tag, tooltip);
        addCooldownLine(stack, tag, tooltip);
        addActivationModeLine(stack, tooltip);
    }

    private static void addActivationModeLine(ItemStack stack, List<Component> tooltip) {
        ModEnums.SlashAssaultMode mode = getActivationMode(stack);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.slash_assault_mode",
                ChatFormatting.GOLD);
        String modeKey = switch (mode) {
            case SPRINT_CLICK -> "tooltip.jaams_weaponry.properties.slash_assault.mode.sprint_click";
            case INSTANT_ON_RIGHT_CLICK -> "tooltip.jaams_weaponry.properties.slash_assault.mode.instant";
            case CHARGE_AND_RELEASE -> "tooltip.jaams_weaponry.properties.slash_assault.mode.charge_release";
            case CHARGE_AND_FINISH_USING -> "tooltip.jaams_weaponry.properties.slash_assault.mode.charge_finishing";
            case CHARGE_RELEASE_AND_FINISH -> "tooltip.jaams_weaponry.properties.slash_assault.mode.charge_hybrid";
        };
        tooltip.add(Component.translatable(modeKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static ModEnums.SlashAssaultMode getActivationMode(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("SlashAssaultMode")) {
            try {
                return ModEnums.SlashAssaultMode.valueOf(tag.getString("SlashAssaultMode").toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        ModEnums.SlashAssaultMode jsonMode = TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.slash_assault_mode)
                .filter(java.util.Objects::nonNull)
                .filter((m) -> !m.isEmpty())
                .map((m) -> {
                    try {
                        return ModEnums.SlashAssaultMode.valueOf(m.toUpperCase(Locale.ROOT));
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .orElse(null);
        if (jsonMode != null) {
            return jsonMode;
        }
        return TraitsConfig.SLASH_ASSAULT_ACTIVATION_MODE.get();
    }

    private static void addDashDistanceLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float dashDistance = getDashDistance(stack, tag);
        double roundedDistance = ModTooltips.roundToTwoDecimals(dashDistance);
        ModTooltips.addStat(stack, tooltip, "slash_assault.dash_distance", roundedDistance);
    }

    private static void addSlashRangeLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float slashRange = getSlashRange(stack, tag);
        double roundedRange = ModTooltips.roundToTwoDecimals(slashRange);
        ModTooltips.addStat(stack, tooltip, "slash_assault.slash_range", roundedRange);
    }

    private static void addCooldownLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int slashCooldown = getSlashCooldown(stack, tag);
        ModTooltips.addStat(stack, tooltip, "slash_assault.slash_cooldown", slashCooldown / 20.0);
    }

    private static float getDashDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SlashAssaultDashDistance")) {
            return tag.getFloat("SlashAssaultDashDistance");
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.dash_distance)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_DASH_DISTANCE.get().floatValue());
    }

    private static float getSlashRange(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SlashAssaultSlashRange")) {
            return tag.getFloat("SlashAssaultSlashRange");
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.slash_range)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_RANGE.get().floatValue());
    }

    private static int getSlashCooldown(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("SlashAssaultSlashCooldown")) {
            return tag.getInt("SlashAssaultSlashCooldown");
        }
        return TraitModifierData.getSlashAssault(stack)
                .map((e) -> e.slash_cooldown)
                .orElseGet(() -> TraitsConfig.SLASH_ASSAULT_SLASH_COOLDOWN.get());
    }
}
