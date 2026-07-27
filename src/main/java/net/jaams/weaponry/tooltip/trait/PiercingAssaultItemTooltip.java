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

public class PiercingAssaultItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.PIERCING_ASSAULT.get()) {
            return;
        }
        if (!ModTraits.isPiercingAssaultItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.piercing_assault",
                "tooltip.jaams_weaponry.trait.piercing_assault.desc");
        if (!TooltipsConfig.TOOLTIP_PIERCING_ASSAULT_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.piercing_assault",
                ChatFormatting.GOLD);
        addDashDistanceLine(stack, tag, tooltip);
        addPierceRangeLine(stack, tag, tooltip);
        addCooldownLine(stack, tag, tooltip);
        addActivationModeLine(stack, tooltip);
    }

    private static void addActivationModeLine(ItemStack stack, List<Component> tooltip) {
        ModEnums.PiercingAssaultMode mode = getActivationMode(stack);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.piercing_assault_mode",
                ChatFormatting.GOLD);
        String modeKey = switch (mode) {
            case SPRINT_CLICK -> "tooltip.jaams_weaponry.properties.piercing_assault.mode.sprint_click";
            case INSTANT_ON_RIGHT_CLICK -> "tooltip.jaams_weaponry.properties.piercing_assault.mode.instant";
            case CHARGE_AND_RELEASE -> "tooltip.jaams_weaponry.properties.piercing_assault.mode.charge_release";
            case CHARGE_AND_FINISH_USING -> "tooltip.jaams_weaponry.properties.piercing_assault.mode.charge_finishing";
            case CHARGE_RELEASE_AND_FINISH -> "tooltip.jaams_weaponry.properties.piercing_assault.mode.charge_hybrid";
        };
        tooltip.add(Component.translatable(modeKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static ModEnums.PiercingAssaultMode getActivationMode(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("PiercingAssaultMode")) {
            try {
                return ModEnums.PiercingAssaultMode
                        .valueOf(tag.getString("PiercingAssaultMode").toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        ModEnums.PiercingAssaultMode jsonMode = TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.piercing_assault_mode)
                .filter(java.util.Objects::nonNull)
                .filter((m) -> !m.isEmpty())
                .map((m) -> {
                    try {
                        return ModEnums.PiercingAssaultMode.valueOf(m.toUpperCase(Locale.ROOT));
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .orElse(null);
        if (jsonMode != null) {
            return jsonMode;
        }
        return TraitsConfig.PIERCING_ASSAULT_ACTIVATION_MODE.get();
    }

    private static void addDashDistanceLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float dashDistance = getDashDistance(stack, tag);
        double roundedDistance = ModTooltips.roundToTwoDecimals(dashDistance);
        ModTooltips.addStat(stack, tooltip, "piercing_assault.dash_distance", roundedDistance);
    }

    private static void addPierceRangeLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float pierceRange = getPierceRange(stack, tag);
        double roundedRange = ModTooltips.roundToTwoDecimals(pierceRange);
        ModTooltips.addStat(stack, tooltip, "piercing_assault.pierce_range", roundedRange);
    }

    private static void addCooldownLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int pierceCooldown = getPierceCooldown(stack, tag);
        ModTooltips.addStat(stack, tooltip, "piercing_assault.pierce_cooldown", pierceCooldown / 20.0);
    }

    private static float getDashDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PiercingAssaultDashDistance")) {
            return tag.getFloat("PiercingAssaultDashDistance");
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.dash_distance)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_DASH_DISTANCE.get().floatValue());
    }

    private static float getPierceRange(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PiercingAssaultPierceRange")) {
            return tag.getFloat("PiercingAssaultPierceRange");
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.pierce_range)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_RANGE.get().floatValue());
    }

    private static int getPierceCooldown(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PiercingAssaultPierceCooldown")) {
            return tag.getInt("PiercingAssaultPierceCooldown");
        }
        return TraitModifierData.getPiercingAssault(stack)
                .map((e) -> e.pierce_cooldown)
                .orElseGet(() -> TraitsConfig.PIERCING_ASSAULT_PIERCE_COOLDOWN.get());
    }
}
