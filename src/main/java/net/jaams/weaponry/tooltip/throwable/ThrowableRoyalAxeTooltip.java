package net.jaams.weaponry.tooltip.throwable;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.loader.ThrowableModifierLoader;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.common.ThrowableConfig;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.Locale;
import java.util.List;

public class ThrowableRoyalAxeTooltip {
    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || tooltip == null || stack.isEmpty()) {
            return;
        }
        if (stack.hasTag()) {
            for (String key : stack.getTag().getAllKeys()) {
                if (key.startsWith("Force") && key.endsWith("Throwable")) {
                    if (stack.getTag().getBoolean(key)) {
                        return;
                    }
                }
            }
        }
        if (hasJsonThrowable(stack)) {
            return;
        }
        if (!isRoyalAxe(stack)) {
            return;
        }
        if (!isThrowableEnabled(stack)) {
            return;
        }
        ModEnums.ThrowMode throwMode = getThrowMode(stack);
        boolean isInstant = throwMode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK;
        addMainTraits(stack, tooltip);
        addThrowingPropertiesIfEnabled(stack, tooltip, throwMode, isInstant);
        addThrowingModeIfEnabled(stack, tooltip, throwMode);
    }

    private static boolean hasJsonThrowable(ItemStack stack) {
        return !ThrowableModifierLoader.INSTANCE.getForItem(stack.getItem()).isEmpty();
    }

    private static boolean isRoyalAxe(ItemStack stack) {
        return stack.is(ModTags.ROYAL_AXES) || ModUtils.getBooleanNBT(stack, "ForceRoyalAxeThrowable") == Boolean.TRUE;
    }

    private static boolean isThrowableEnabled(ItemStack stack) {
        if (!TraitsConfig.THROWABLE.get())
            return false;
        if (!isRoyalAxe(stack))
            return false;
        if (!ThrowableConfig.THROWABLE_ROYAL_AXE.get())
            return false;
        Boolean trait = ModUtils.getBooleanNBT(stack, "ThrowableTrait");
        if (trait != null) {
            return trait;
        }
        return true;
    }

    private static ModEnums.ThrowMode getThrowMode(ItemStack stack) {
        String nbt = ModUtils.getStringNBT(stack, "ThrowableThrowMode");
        if (nbt != null && !nbt.isEmpty()) {
            try {
                return ModEnums.ThrowMode.valueOf(nbt.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        return ThrowableConfig.THROWABLE_ROYAL_AXE_THROW_MODE.get();
    }

    private static void addMainTraits(ItemStack stack, List<Component> tooltip) {
        ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.throwable",
                "tooltip.jaams_weaponry.trait.throwable.desc");
    }

    private static void addThrowingPropertiesIfEnabled(ItemStack stack, List<Component> tooltip,
            ModEnums.ThrowMode throwMode, boolean isInstant) {
        if (!TooltipsConfig.TOOLTIP_THROWING_PROPERTIES.get()) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.throwing", ChatFormatting.YELLOW);
        addBaseDamage(stack, tooltip);
        addBaseKnockback(stack, tooltip);
        addChargeTimesIfApplicable(stack, tooltip, isInstant);
        addSpeedStats(stack, tooltip, isInstant);
        addInaccuracy(stack, tooltip);
        addPiercingLevel(stack, tooltip);
        addCooldownIfInstant(stack, tooltip, isInstant);
        addRecoil(stack, tooltip);
        addWaterInertia(stack, tooltip);
    }

    private static void addBaseDamage(ItemStack stack, List<Component> tooltip) {
        double baseDamage = ModUtils.calculateDamage(stack);
        Double nbtDamage = ModUtils.getDoubleNBT(stack, "ProjectileBaseDamage");
        if (nbtDamage != null) {
            baseDamage += nbtDamage;
        } else {
            baseDamage += ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_BASE_DAMAGE.get();
        }
        baseDamage += 1.0;
        ModTooltips.addStat(stack, tooltip, "base_damage", ModTooltips.roundToTwoDecimals(baseDamage));
    }

    private static void addBaseKnockback(ItemStack stack, List<Component> tooltip) {
        double knockback = 0.0;
        Double nbtKb = ModUtils.getDoubleNBT(stack, "ProjectileBaseKnockback");
        if (nbtKb != null) {
            knockback = nbtKb;
        } else {
            knockback = ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_BASE_KNOCKBACK.get();
        }
        if (knockback > 0.0) {
            ModTooltips.addStat(stack, tooltip, "base_knockback", ModTooltips.roundToTwoDecimals(knockback));
        }
    }

    private static void addChargeTimesIfApplicable(ItemStack stack, List<Component> tooltip, boolean isInstant) {
        if (isInstant)
            return;
        Integer minCharge = ModUtils.getIntNBT(stack, "ThrowableMinChargeTicks");
        if (minCharge == null || minCharge <= 0) {
            minCharge = ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_CHARGE.get();
        }
        Integer maxCharge = ModUtils.getIntNBT(stack, "ThrowableMaxChargeTicks");
        if (maxCharge == null || maxCharge <= 0) {
            maxCharge = ThrowableConfig.THROWABLE_ROYAL_AXE_MAX_CHARGE.get();
        }
        if (minCharge > 0) {
            ModTooltips.addStat(stack, tooltip, "min_charge", ModTooltips.roundToTwoDecimals(minCharge / 20.0));
        }
        if (maxCharge > 0 && maxCharge != 72000) {
            ModTooltips.addStat(stack, tooltip, "max_charge", ModTooltips.roundToTwoDecimals(maxCharge / 20.0));
        }
    }

    private static void addSpeedStats(ItemStack stack, List<Component> tooltip, boolean isInstant) {
        Double minSpeedNBT = ModUtils.getDoubleNBT(stack, "ThrowableMinSpeed");
        double minSpeed = (minSpeedNBT != null && minSpeedNBT > 0) ? minSpeedNBT
                : ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_SPEED.get().doubleValue();
        Double maxSpeedNBT = ModUtils.getDoubleNBT(stack, "ThrowableMaxSpeed");
        double maxSpeed = (maxSpeedNBT != null && maxSpeedNBT > 0) ? maxSpeedNBT
                : ThrowableConfig.THROWABLE_ROYAL_AXE_MAX_SPEED.get().doubleValue();
        if (isInstant) {
            if (maxSpeed > 0.0) {
                ModTooltips.addStat(stack, tooltip, "throw_speed", ModTooltips.roundToTwoDecimals(maxSpeed));
            }
        } else {
            if (minSpeed > 0.0)
                ModTooltips.addStat(stack, tooltip, "min_speed", ModTooltips.roundToTwoDecimals(minSpeed));
            if (maxSpeed > 0.0)
                ModTooltips.addStat(stack, tooltip, "max_speed", ModTooltips.roundToTwoDecimals(maxSpeed));
        }
    }

    private static void addInaccuracy(ItemStack stack, List<Component> tooltip) {
        Double inaccuracyNBT = ModUtils.getDoubleNBT(stack, "ThrowableInaccuracy");
        double inaccuracy = (inaccuracyNBT != null && inaccuracyNBT > 0) ? inaccuracyNBT
                : ThrowableConfig.THROWABLE_ROYAL_AXE_INACCURACY.get().doubleValue();
        if (inaccuracy > 0.1) {
            ModTooltips.addStat(stack, tooltip, "inaccuracy", ModTooltips.roundToTwoDecimals(inaccuracy));
        }
    }

    private static void addPiercingLevel(ItemStack stack, List<Component> tooltip) {
        int piercingLevel = ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_PIERCING_LEVEL.get();
        Integer nbtPiercing = ModUtils.getIntNBT(stack, "ProjectilePiercingLevel");
        if (nbtPiercing != null) {
            piercingLevel = Math.max(0, nbtPiercing);
        }
        piercingLevel += EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, stack);
        if (piercingLevel > 0) {
            ModTooltips.addStat(stack, tooltip, "piercing_level", piercingLevel);
        }
    }

    private static void addCooldownIfInstant(ItemStack stack, List<Component> tooltip, boolean isInstant) {
        if (!isInstant)
            return;
        Integer cooldown = ModUtils.getIntNBT(stack, "ThrowableInstantCooldownTicks");
        int cooldownTicks = (cooldown != null && cooldown >= 0) ? cooldown
                : ThrowableConfig.THROWABLE_ROYAL_AXE_COOLDOWN_TICKS_INSTANT.get();
        ModTooltips.addStat(stack, tooltip, "cooldown", ModTooltips.roundToTwoDecimals(cooldownTicks / 20.0));
    }

    private static void addRecoil(ItemStack stack, List<Component> tooltip) {
        Double recoilNBT = ModUtils.getDoubleNBT(stack, "ThrowableRecoil");
        double recoil = (recoilNBT != null && recoilNBT >= 0) ? recoilNBT
                : ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL.get().doubleValue();
        if (recoil > 0.0) {
            ModTooltips.addStat(stack, tooltip, "recoil", ModTooltips.roundToTwoDecimals(recoil));
        }
    }

    private static void addWaterInertia(ItemStack stack, List<Component> tooltip) {
        Double inertiaNBT = ModUtils.getDoubleNBT(stack, "ProjectileWaterInertia");
        double waterInertia = (inertiaNBT != null && inertiaNBT > 0) ? inertiaNBT
                : ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_WATER_INERTIA.get().doubleValue();
        ModTooltips.addStat(stack, tooltip, "water_inertia", ModTooltips.roundToTwoDecimals(waterInertia));
    }

    private static void addThrowingModeIfEnabled(ItemStack stack, List<Component> tooltip,
            ModEnums.ThrowMode throwMode) {
        if (!TooltipsConfig.TOOLTIP_THROWING_MODE.get())
            return;
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.throwing_mode",
                ChatFormatting.YELLOW);
        String modeKey = switch (throwMode) {
            case INSTANT_ON_RIGHT_CLICK -> "tooltip.jaams_weaponry.properties.throw_mode.instant";
            case CHARGE_AND_RELEASE -> "tooltip.jaams_weaponry.properties.throw_mode.charge_release";
            case CHARGE_AND_FINISH_USING -> "tooltip.jaams_weaponry.properties.throw_mode.charge_finishing";
            case CHARGE_RELEASE_AND_FINISH -> "tooltip.jaams_weaponry.properties.throw_mode.charge_hybrid";
        };
        tooltip.add(Component.translatable(modeKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
