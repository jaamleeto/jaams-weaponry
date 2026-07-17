package net.jaams.weaponry.tooltip.item;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.gun.helper.GunAttachmentHelper;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class AttachmentItemTooltip {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.##");
    private static final double GENERIC_MUZZLE_MULTIPLIER = 1.5;
    private static final double GENERIC_MAGAZINE_COOLDOWN = 0.5;

    private static final String HEADER_KEY = "tooltip.jaams_weaponry.properties.attachment";

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null) {
            return;
        }
        if (!TooltipsConfig.TOOLTIP_ATTACHMENT_PROPERTIES.get()) {
            return;
        }
        if (!GunAttachmentHelper.isAttachment(stack)) {
            return;
        }

        ModTooltips.addExtraInfo(stack, tooltip, HEADER_KEY, ChatFormatting.YELLOW);
        addDamageModifier(stack, tooltip);
        addProjectileSpeed(stack, tooltip);
        addKnockbackModifier(stack, tooltip);
        addInaccuracy(stack, tooltip);
        addSpreadAngle(stack, tooltip);
        addCooldown(stack, tooltip);
        addRecoil(stack, tooltip);
        addCrouchRecoilReduction(stack, tooltip);
        addVerticalRecoil(stack, tooltip);
        addCameraKick(stack, tooltip);
        addShakeIntensity(stack, tooltip);
        addPiercingModifier(stack, tooltip);
        addProjectileCount(stack, tooltip);
        addAmmoConsumption(stack, tooltip);
        addAttachmentConsumption(stack, tooltip);
        addShakeResetDelay(stack, tooltip);
        addOffhandCooldown(stack, tooltip);
        addFirePattern(stack, tooltip);
    }

    private static void addDamageModifier(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunProjectileDamageModifier", "attachment.damage_modifier");
    }

    private static void addProjectileSpeed(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunProjectileSpeed", "attachment.projectile_speed");
    }

    private static void addKnockbackModifier(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunProjectileKnockbackModifier", "attachment.knockback_modifier");
    }

    private static void addInaccuracy(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunProjectileInaccuracy", "attachment.inaccuracy");
    }

    private static void addSpreadAngle(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunSpreadAngle", "attachment.spread_angle");
    }

    private static void addCooldown(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunCooldown", "attachment.cooldown");
    }

    private static void addRecoil(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunRecoilDistance", "attachment.recoil");
    }

    private static void addCrouchRecoilReduction(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunCrouchRecoilReduction", "attachment.crouch_recoil");
    }

    private static void addVerticalRecoil(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunVerticalRecoilMultiplier", "attachment.vertical_recoil");
    }

    private static void addCameraKick(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunXRotRecoilIntensity", "attachment.camera_kick");
    }

    private static void addShakeIntensity(ItemStack stack, List<Component> tooltip) {
        addDoubleStat(stack, tooltip, "GunShakeIntensity", "attachment.shake");
    }

    private static void addPiercingModifier(ItemStack stack, List<Component> tooltip) {
        addIntStat(stack, tooltip, "GunProjectilePiercingModifier", "attachment.piercing_modifier");
    }

    private static void addProjectileCount(ItemStack stack, List<Component> tooltip) {
        addIntStat(stack, tooltip, "GunProjectileCount", "attachment.projectile_count");
    }

    private static void addAmmoConsumption(ItemStack stack, List<Component> tooltip) {
        addIntStat(stack, tooltip, "GunAmmoConsumption", "attachment.ammo_use");
    }

    private static void addAttachmentConsumption(ItemStack stack, List<Component> tooltip) {
        addIntStat(stack, tooltip, "GunAttachmentConsumption", "attachment.attachment_wear");
    }

    private static void addShakeResetDelay(ItemStack stack, List<Component> tooltip) {
        addIntStat(stack, tooltip, "GunShakeResetDelay", "attachment.shake_reset");
    }

    private static void addOffhandCooldown(ItemStack stack, List<Component> tooltip) {
        addIntStat(stack, tooltip, "GunOffhandCooldown", "attachment.offhand_cooldown");
    }

    private static void addFirePattern(ItemStack stack, List<Component> tooltip) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        String pattern = null;
        if (entry.isPresent() && !entry.get().fire_pattern.isEmpty()) {
            pattern = entry.get().fire_pattern;
        } else if (stack.hasTag() && stack.getTag().contains("GunFirePattern")) {
            pattern = stack.getTag().getString("GunFirePattern");
        }
        if (pattern == null) {
            return;
        }
        ModEnums.GunFirePattern firePattern = ModEnums.GunFirePattern.fromString(pattern);
        if (firePattern == ModEnums.GunFirePattern.DEFAULT) {
            return;
        }
        Component patternName = Component.translatable("tooltip.jaams_weaponry.fire_pattern." + firePattern.name().toLowerCase());
        ModTooltips.addStatText(stack, tooltip, "attachment.fire_pattern", patternName.getString());
    }

    private static void addDoubleStat(ItemStack stack, List<Component> tooltip, String nbtKey, String translationSuffix) {
        StatValue stat = getDoubleValue(stack, nbtKey);
        if (!shouldShow(stat)) {
            return;
        }
        ModTooltips.addStatText(stack, tooltip, translationSuffix, formatDouble(stat));
    }

    private static void addIntStat(ItemStack stack, List<Component> tooltip, String nbtKey, String translationSuffix) {
        StatValue stat = getIntValue(stack, nbtKey);
        if (!shouldShow(stat)) {
            return;
        }
        ModTooltips.addStatText(stack, tooltip, translationSuffix, formatInt(stat));
    }

    private static StatValue getDoubleValue(ItemStack stack, String nbtKey) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        if (entry.isPresent() && entry.get().modifiers != null) {
            for (GunItemData.AttachmentModifier mod : entry.get().modifiers) {
                if (mod != null && nbtKey.equals(mod.key)) {
                    ModEnums.ModifierOperation op = ModEnums.ModifierOperation.fromString(mod.operation);
                    return new StatValue(mod.value, op);
                }
            }
        }
        ModEnums.ModifierOperation operation = ModUtils.getModifierOperation(stack, nbtKey);
        if (stack.hasTag() && stack.getTag().contains(nbtKey)) {
            return new StatValue(stack.getTag().getDouble(nbtKey), operation);
        }
        double defaultValue = getDefaultMultiplier(stack, nbtKey, operation);
        if (Double.isNaN(defaultValue)) {
            return new StatValue(0.0, operation);
        }
        return new StatValue(defaultValue, ModEnums.ModifierOperation.MULTIPLY);
    }

    private static StatValue getIntValue(ItemStack stack, String nbtKey) {
        Optional<GunItemData.AttachmentEntry> entry = GunItemData.getAttachmentData(stack);
        if (entry.isPresent() && entry.get().modifiers != null) {
            for (GunItemData.AttachmentModifier mod : entry.get().modifiers) {
                if (mod != null && nbtKey.equals(mod.key)) {
                    ModEnums.ModifierOperation op = ModEnums.ModifierOperation.fromString(mod.operation);
                    return new StatValue((int) mod.value, op);
                }
            }
        }
        ModEnums.ModifierOperation operation = ModUtils.getModifierOperation(stack, nbtKey);
        if (stack.hasTag() && stack.getTag().contains(nbtKey)) {
            return new StatValue(stack.getTag().getInt(nbtKey), operation);
        }
        return new StatValue(0, operation);
    }

    private static double getDefaultMultiplier(ItemStack stack, String nbtKey, ModEnums.ModifierOperation operation) {
        if (operation != ModEnums.ModifierOperation.MULTIPLY) {
            return Double.NaN;
        }
        if (GunAttachmentHelper.isMuzzleAttachment(stack)) {
            if (nbtKey.equals("GunProjectileDamageModifier") || nbtKey.equals("GunProjectileSpeed")) {
                return getMuzzleDefaultMultiplier(stack);
            }
        }
        if (GunAttachmentHelper.isMagazineAttachment(stack)) {
            if (nbtKey.equals("GunCooldown")) {
                return getMagazineDefaultCooldown(stack);
            }
        }
        return Double.NaN;
    }

    private static double getMuzzleDefaultMultiplier(ItemStack stack) {
        return GENERIC_MUZZLE_MULTIPLIER;
    }

    private static double getMagazineDefaultCooldown(ItemStack stack) {
        return GENERIC_MAGAZINE_COOLDOWN;
    }

    private static boolean shouldShow(StatValue stat) {
        boolean result;
        switch (stat.operation) {
            case MULTIPLY:
                result = stat.value != 1.0 && stat.value > 0.0;
                break;
            case ADD:
            case SUBTRACT:
                result = stat.value != 0.0;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    private static String formatDouble(StatValue stat) {
        String result;
        switch (stat.operation) {
            case MULTIPLY:
                result = "x" + FORMAT.format(stat.value);
                break;
            case ADD:
                result = (stat.value >= 0 ? "+" : "") + FORMAT.format(stat.value);
                break;
            case SUBTRACT:
                result = (stat.value >= 0 ? "-" : "+") + FORMAT.format(Math.abs(stat.value));
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    private static String formatInt(StatValue stat) {
        int intValue = (int) stat.value;
        String result;
        switch (stat.operation) {
            case MULTIPLY:
                result = "x" + intValue;
                break;
            case ADD:
                result = (intValue >= 0 ? "+" : "") + intValue;
                break;
            case SUBTRACT:
                result = (intValue >= 0 ? "-" : "+") + Math.abs(intValue);
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    private record StatValue(double value, ModEnums.ModifierOperation operation) {
    }
}
