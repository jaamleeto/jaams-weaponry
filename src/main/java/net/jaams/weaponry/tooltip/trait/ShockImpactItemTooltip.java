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

public class ShockImpactItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (!TraitsConfig.SHOCK_IMPACT.get()) {
            return;
        }
        if (!ModTraits.isShockImpactItem(stack)) {
            return;
        }
        CompoundTag tag = ModComponents.get(stack);
        ModTooltips.addTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.shock_impact",
                "tooltip.jaams_weaponry.trait.shock_impact.desc");
        if (!TooltipsConfig.TOOLTIP_SHOCK_IMPACT_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shock_impact",
                ChatFormatting.GOLD);
        addSmashRadiusLine(stack, tag, tooltip);
        addCooldownLine(stack, tag, tooltip);
        addActivationModeLine(stack, tooltip);
    }

    private static void addActivationModeLine(ItemStack stack, List<Component> tooltip) {
        ModEnums.ShockImpactMode mode = getActivationMode(stack);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shock_impact_mode",
                ChatFormatting.GOLD);
        String modeKey = switch (mode) {
            case INSTANT_ON_RIGHT_CLICK -> "tooltip.jaams_weaponry.properties.shock_impact.mode.instant";
            case CHARGE_AND_RELEASE -> "tooltip.jaams_weaponry.properties.shock_impact.mode.charge_release";
            case CHARGE_AND_FINISH_USING -> "tooltip.jaams_weaponry.properties.shock_impact.mode.charge_finishing";
            case CHARGE_RELEASE_AND_FINISH -> "tooltip.jaams_weaponry.properties.shock_impact.mode.charge_hybrid";
        };
        tooltip.add(Component.translatable(modeKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static ModEnums.ShockImpactMode getActivationMode(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("ShockImpactMode")) {
            try {
                return ModEnums.ShockImpactMode.valueOf(tag.getString("ShockImpactMode").toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        String jsonMode = TraitModifierData.getShockImpact(stack)
                .map(e -> e.shock_impact_mode)
                .filter(java.util.Objects::nonNull)
                .filter(m -> !m.isEmpty())
                .orElse(null);
        if (jsonMode != null) {
            try {
                return ModEnums.ShockImpactMode.valueOf(jsonMode.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        return TraitsConfig.SHOCK_IMPACT_ACTIVATION_MODE.get();
    }

    private static void addSmashRadiusLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        float smashRadius = getSmashRadius(stack, tag);
        double roundedRadius = ModTooltips.roundToTwoDecimals(smashRadius);
        ModTooltips.addStat(stack, tooltip, "shock_impact.smash_radius", roundedRadius);
    }

    private static void addCooldownLine(ItemStack stack, CompoundTag tag, List<Component> tooltip) {
        int cooldownTicks = getCooldownTicks(stack, tag);
        ModTooltips.addStat(stack, tooltip, "shock_impact.cooldown", cooldownTicks / 20.0);
    }

    private static float getSmashRadius(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ShockImpactSmashRadius")) {
            return tag.getFloat("ShockImpactSmashRadius");
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.smash_radius)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_SMASH_RADIUS.get().floatValue());
    }

    private static int getCooldownTicks(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ShockImpactCooldownTicks")) {
            return tag.getInt("ShockImpactCooldownTicks");
        }
        return TraitModifierData.getShockImpact(stack)
                .map(e -> e.cooldown_ticks)
                .orElseGet(() -> TraitsConfig.SHOCK_IMPACT_COOLDOWN_TICKS.get());
    }
}
