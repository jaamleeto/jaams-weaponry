package net.jaams.weaponry.tooltip.throwable;

import org.checkerframework.checker.units.qual.cd;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModTooltips;
import net.jaams.weaponry.util.ModProjectiles;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.entity.DynamiteProjectileEntity;
import net.jaams.weaponry.data.ThrowableTypeData;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;

import java.util.Locale;
import java.util.List;

public class ThrowableItemTooltip {
    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || tooltip == null || stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ThrowableTrait") && !tag.getBoolean("ThrowableTrait")) {
            return;
        }
        if (!TraitsConfig.THROWABLE.get()) {
            return;
        }
        ThrowableTypeData effectiveType = getEffectiveThrowableType(stack);
        ThrowableItemData data = ThrowableItemData.getData(stack).orElse(null);
        boolean hasJsonEnabled = isValidJsonThrowable(data);
        if (effectiveType == null && !hasJsonEnabled) {
            return;
        }
        ThrowableItemData.ThrowableEntry json = hasJsonEnabled ? data.throwable
                : new ThrowableItemData.ThrowableEntry();
        ModEnums.ThrowMode throwMode = getThrowMode(stack, json);
        String projectileType = getEffectiveProjectileType(stack, json, effectiveType);
        addMainTrait(stack, tooltip);
        addSpecialTraits(stack, tooltip, projectileType);
        addThrowingProperties(stack, tooltip, json, throwMode, projectileType, effectiveType);
        addThrowingMode(stack, tooltip, throwMode);
    }

    private static ThrowableTypeData getEffectiveThrowableType(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return null;
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            for (ThrowableTypeData type : ThrowableTypeData.ALL_TYPES) {
                if (tag.getBoolean(type.forceNbtKey)) {
                    return type;
                }
            }
        }
        return null;
    }

    private static String getEffectiveProjectileType(ItemStack stack, ThrowableItemData.ThrowableEntry json,
            ThrowableTypeData effectiveType) {
        String nbt = getStringNBT(stack, "ThrowableProjectileType");
        if (nbt != null && !nbt.isEmpty())
            return nbt;
        if (json.projectile != null && !json.projectile.isEmpty())
            return json.projectile;
        if (effectiveType != null)
            return effectiveType.name;
        return "GENERIC";
    }

    private static boolean isValidJsonThrowable(ThrowableItemData data) {
        if (data == null || data.throwable == null)
            return false;
        ThrowableItemData.ThrowableEntry t = data.throwable;
        return Boolean.TRUE.equals(t.throw_enabled) && t.projectile != null && !t.projectile.trim().isEmpty();
    }

    private static ModEnums.ThrowMode getThrowMode(ItemStack stack, ThrowableItemData.ThrowableEntry json) {
        String nbt = getStringNBT(stack, "ThrowableThrowMode");
        if (nbt != null && !nbt.isEmpty()) {
            try {
                return ModEnums.ThrowMode.valueOf(nbt.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        if (json.throw_mode != null && !json.throw_mode.isEmpty()) {
            try {
                return ModEnums.ThrowMode.valueOf(json.throw_mode.toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
        }
        ThrowableTypeData type = getEffectiveThrowableType(stack);
        return type != null ? ThrowableTypeData.getThrowMode(type.name) : ModEnums.ThrowMode.CHARGE_AND_RELEASE;
    }

    private static void addThrowingProperties(ItemStack stack, List<Component> tooltip,
            ThrowableItemData.ThrowableEntry json, ModEnums.ThrowMode throwMode, String projectileType,
            ThrowableTypeData type) {
        if (!TooltipsConfig.TOOLTIP_THROWING_PROPERTIES.get())
            return;
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.throwing", ChatFormatting.YELLOW);
        addRealBaseDamage(stack, tooltip, json, type);
        addRealBaseKnockback(stack, tooltip, json, type);
        addChargeTimes(stack, tooltip, throwMode, json, type);
        addSpeedStats(stack, tooltip, throwMode, json, type);
        addInaccuracy(stack, tooltip, json, type);
        addPiercingLevel(stack, tooltip, type);
        addCooldownIfInstant(stack, tooltip, throwMode, json);
        addRecoil(stack, tooltip, json, type);
        addWaterInertia(stack, tooltip, type);
        if ("DYNAMITE".equals(projectileType)) {
            addDynamiteProperties(stack, tooltip);
        }
        addThrowbackPropertiesIfEnabled(stack, tooltip, throwMode, projectileType);
        switch (projectileType) {
            case "HUNTERS_BOOMERANG" -> {
                addCollectorPropertiesIfEnabled(stack, tooltip, projectileType);
                addDisarmingShotPropertiesIfEnabled(stack, tooltip, projectileType);
            }
            case "KUNAI" -> addPiercingShotPropertiesIfEnabled(stack, tooltip);
            case "PRONGED_KUNAI" -> addBackstabPropertiesIfEnabled(stack, tooltip);
            case "SHURIKEN" -> addDisablingShotPropertiesIfEnabled(stack, tooltip);
            case "GIANT_SHURIKEN" -> addSweepingShotPropertiesIfEnabled(stack, tooltip);
        }
    }

    private static void addChargeTimes(ItemStack stack, List<Component> tooltip, ModEnums.ThrowMode throwMode,
            ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        if (throwMode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK)
            return;
        int minCharge = getMinChargeTicks(stack, json, type);
        if (minCharge > 0) {
            ModTooltips.addStat(stack, tooltip, "min_charge", ModTooltips.roundToTwoDecimals(minCharge / 20.0));
        }
        int maxCharge = getMaxChargeTicks(stack, json, type);
        if (maxCharge > 0 && maxCharge != 72000) {
            ModTooltips.addStat(stack, tooltip, "max_charge", ModTooltips.roundToTwoDecimals(maxCharge / 20.0));
        }
    }

    private static void addSpeedStats(ItemStack stack, List<Component> tooltip, ModEnums.ThrowMode throwMode,
            ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        if (throwMode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK) {
            float speed = getMaxSpeed(stack, json, type);
            if (speed > 0.0f) {
                ModTooltips.addStat(stack, tooltip, "throw_speed", ModTooltips.roundToTwoDecimals(speed));
            }
        } else {
            float minSpeed = getMinSpeed(stack, json, type);
            float maxSpeed = getMaxSpeed(stack, json, type);
            if (minSpeed > 0.0f)
                ModTooltips.addStat(stack, tooltip, "min_speed", ModTooltips.roundToTwoDecimals(minSpeed));
            if (maxSpeed > 0.0f)
                ModTooltips.addStat(stack, tooltip, "max_speed", ModTooltips.roundToTwoDecimals(maxSpeed));
        }
    }

    private static void addInaccuracy(ItemStack stack, List<Component> tooltip, ThrowableItemData.ThrowableEntry json,
            ThrowableTypeData type) {
        float inaccuracy = getInaccuracy(stack, json, type);
        if (inaccuracy > 0.1) {
            ModTooltips.addStat(stack, tooltip, "inaccuracy", ModTooltips.roundToTwoDecimals(inaccuracy));
        }
    }

    private static void addRecoil(ItemStack stack, List<Component> tooltip, ThrowableItemData.ThrowableEntry json,
            ThrowableTypeData type) {
        float recoil = getRecoil(stack, json, type);
        if (recoil > 0.0) {
            ModTooltips.addStat(stack, tooltip, "recoil", ModTooltips.roundToTwoDecimals(recoil));
        }
    }

    private static double getRealBaseDamage(ItemStack stack, ThrowableItemData.ThrowableEntry json,
            ThrowableTypeData type) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ProjectileBaseDamage", 99))
            return tag.getDouble("ProjectileBaseDamage");
        if (json.base_damage != null && json.base_damage >= 0.0)
            return json.base_damage;
        double itemDamage = ModProjectiles.getItemAttackDamage(stack);
        double defaultDmg = (type != null) ? ThrowableTypeData.getDefaultBaseDamage(type.name) : 1.0;
        return itemDamage + defaultDmg + 1.0;
    }

    private static double getRealBaseKnockback(ItemStack stack, ThrowableItemData.ThrowableEntry json,
            ThrowableTypeData type) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ProjectileBaseKnockback", 99))
            return tag.getDouble("ProjectileBaseKnockback");
        if (json.base_knockback != null && json.base_knockback >= 0.0)
            return json.base_knockback;
        return (type != null) ? ThrowableTypeData.getDefaultBaseKnockback(type.name) : 0.1;
    }

    private static void addRealBaseDamage(ItemStack stack, List<Component> tooltip,
            ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        double damage = getRealBaseDamage(stack, json, type);
        if (damage > 0.0) {
            ModTooltips.addStat(stack, tooltip, "base_damage", ModTooltips.roundToTwoDecimals(damage));
        }
    }

    private static void addRealBaseKnockback(ItemStack stack, List<Component> tooltip,
            ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        double kb = getRealBaseKnockback(stack, json, type);
        if (kb > 0.0) {
            ModTooltips.addStat(stack, tooltip, "base_knockback", ModTooltips.roundToTwoDecimals(kb));
        }
    }

    private static int getMinChargeTicks(ItemStack stack, ThrowableItemData.ThrowableEntry json,
            ThrowableTypeData type) {
        Integer nbt = getIntNBT(stack, "ThrowableMinChargeTicks");
        if (nbt != null && nbt >= 0)
            return nbt;
        if (json.min_charge_ticks != null && json.min_charge_ticks >= 0)
            return json.min_charge_ticks;
        return type != null ? ThrowableTypeData.getMinChargeTicks(type.name) : 0;
    }

    private static int getMaxChargeTicks(ItemStack stack, ThrowableItemData.ThrowableEntry json,
            ThrowableTypeData type) {
        Integer nbt = getIntNBT(stack, "ThrowableMaxChargeTicks");
        if (nbt != null && nbt >= 0)
            return nbt;
        if (json.max_charge_ticks != null && json.max_charge_ticks >= 0)
            return json.max_charge_ticks;
        return type != null ? ThrowableTypeData.getMaxChargeTicks(type.name) : 20;
    }

    private static float getMinSpeed(ItemStack stack, ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        Float nbt = getFloatNBT(stack, "ThrowableMinSpeed");
        if (nbt != null && nbt >= 0)
            return nbt;
        if (json.min_speed != null && json.min_speed >= 0)
            return json.min_speed;
        return type != null ? ThrowableTypeData.getMinSpeed(type.name) : 1.0f;
    }

    private static float getMaxSpeed(ItemStack stack, ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        Float nbt = getFloatNBT(stack, "ThrowableMaxSpeed");
        if (nbt != null && nbt >= 0)
            return nbt;
        if (json.max_speed != null && json.max_speed >= 0)
            return json.max_speed;
        return type != null ? ThrowableTypeData.getMaxSpeed(type.name) : 2.2f;
    }

    private static float getInaccuracy(ItemStack stack, ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        Float nbt = getFloatNBT(stack, "ThrowableInaccuracy");
        if (nbt != null && nbt >= 0)
            return nbt;
        if (json.inaccuracy != null && json.inaccuracy >= 0)
            return json.inaccuracy;
        return type != null ? ThrowableTypeData.getInaccuracy(type.name) : 1.0f;
    }

    private static float getRecoil(ItemStack stack, ThrowableItemData.ThrowableEntry json, ThrowableTypeData type) {
        Float nbt = getFloatNBT(stack, "ThrowableRecoil");
        if (nbt != null && nbt >= 0)
            return nbt;
        if (json.recoil != null && json.recoil >= 0)
            return json.recoil;
        return type != null ? ThrowableTypeData.getRecoil(type.name) : 0.0f;
    }

    private static void addDynamiteProperties(ItemStack stack, List<Component> tooltip) {
        float power = DynamiteProjectileEntity.getExplosionPower(stack);
        int fuseTicks = DynamiteProjectileEntity.getFuseTicks(stack);
        double timeSeconds = fuseTicks / 20.0;
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.dynamite", ChatFormatting.YELLOW);
        ModTooltips.addStat(stack, tooltip, "explosion_power", ModTooltips.roundToTwoDecimals(power));
        ModTooltips.addStat(stack, tooltip, "explosion_time", ModTooltips.roundToTwoDecimals(timeSeconds));
    }

    private static void addPiercingLevel(ItemStack stack, List<Component> tooltip, ThrowableTypeData type) {
        int piercing = 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ProjectilePiercingLevel", 99)) {
            piercing = Math.max(0, tag.getInt("ProjectilePiercingLevel"));
        } else if (type != null) {
            piercing = ThrowableTypeData.getDefaultPiercingLevel(type.name);
        }
        piercing += EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, stack);
        if (piercing > 0) {
            ModTooltips.addStat(stack, tooltip, "piercing_level", piercing);
        }
    }

    private static void addWaterInertia(ItemStack stack, List<Component> tooltip, ThrowableTypeData type) {
        double def = (type != null) ? ThrowableTypeData.getDefaultWaterInertia(type.name) : 0.6D;
        double inertia = ModProjectiles.getWaterInertia(stack, def);
        ModTooltips.addStat(stack, tooltip, "water_inertia", ModTooltips.roundToTwoDecimals(inertia));
    }

    private static void addThrowbackPropertiesIfEnabled(ItemStack stack, List<Component> tooltip,
            ModEnums.ThrowMode throwMode, String projectileType) {
        if (!isThrowbackEnabled(stack) || !TooltipsConfig.TOOLTIP_THROWBACK_PROPERTIES.get())
            return;
        if (!"HUNTERS_BOOMERANG".equals(projectileType) && !"RING".equals(projectileType))
            return;
        double minRange = ModProjectiles.getThrowbackMinRange(stack,
                ThrowableTypeData.getThrowbackMinRangeDefault(projectileType));
        float maxRange = ModProjectiles.getThrowbackMaxRange(stack,
                (float) ThrowableTypeData.getThrowbackMaxRangeDefault(projectileType));
        double returnSpeed = ModProjectiles.getThrowbackReturnSpeed(stack,
                ThrowableTypeData.getThrowbackReturnSpeedDefault(projectileType));
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.throwback", ChatFormatting.YELLOW);
        if (throwMode == ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK) {
            ModTooltips.addStat(stack, tooltip, "throw_range", ModTooltips.roundToTwoDecimals(maxRange));
        } else {
            ModTooltips.addStat(stack, tooltip, "min_range", ModTooltips.roundToTwoDecimals(minRange));
            ModTooltips.addStat(stack, tooltip, "max_range", ModTooltips.roundToTwoDecimals(maxRange));
        }
        ModTooltips.addStat(stack, tooltip, "return_speed", ModTooltips.roundToTwoDecimals(returnSpeed));
    }

    private static void addCollectorPropertiesIfEnabled(ItemStack stack, List<Component> tooltip,
            String projectileType) {
        if (!isCollectorEnabled(stack) || !TooltipsConfig.TOOLTIP_COLLECTOR_PROPERTIES.get())
            return;
        int maxItems = ModProjectiles.getMaxMountedEntities(stack, true,
                (int) ThrowableTypeData.getDefaultCollectorMaxItems(projectileType));
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.collector", ChatFormatting.YELLOW);
        if (maxItems > 0)
            ModTooltips.addStat(stack, tooltip, "collector_max_items", maxItems);
    }

    private static void addDisarmingShotPropertiesIfEnabled(ItemStack stack, List<Component> tooltip,
            String projectileType) {
        if (!isDisarmingShotEnabled(stack) || !TooltipsConfig.TOOLTIP_DISARMING_SHOT_PROPERTIES.get())
            return;
        double chance = ModProjectiles.getDisarmingShotChance(stack,
                (float) ThrowableTypeData.getDefaultDisarmingShotChance(projectileType));
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.disarming_shot",
                ChatFormatting.YELLOW);
        if (chance > 0.0) {
            ModTooltips.addStat(stack, tooltip, "disarming_shot_chance",
                    ModTooltips.roundToTwoDecimals(chance * 100.0));
        }
    }

    private static void addPiercingShotPropertiesIfEnabled(ItemStack stack, List<Component> tooltip) {
        if (!isPiercingShotEnabled(stack) || !TooltipsConfig.TOOLTIP_PIERCING_SHOT_PROPERTIES.get())
            return;
        double chance = ModProjectiles.getPiercingShotChance(stack,
                TraitsConfig.PIERCING_SHOT_KUNAI_PROJECTILE_CHANCE.get().doubleValue());
        double bonus = ModProjectiles.getPiercingShotBonusDamage(stack,
                TraitsConfig.PIERCING_SHOT_KUNAI_PROJECTILE_BONUS_DAMAGE.get().doubleValue());
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.piercing_shot",
                ChatFormatting.YELLOW);
        if (chance > 0.0)
            ModTooltips.addStat(stack, tooltip, "piercing_shot_chance", ModTooltips.roundToTwoDecimals(chance * 100.0));
        if (bonus > 0.0)
            ModTooltips.addStat(stack, tooltip, "piercing_shot_bonus_damage", ModTooltips.roundToTwoDecimals(bonus));
    }

    private static void addBackstabPropertiesIfEnabled(ItemStack stack, List<Component> tooltip) {
        if (!isBackstabShotEnabled(stack) || !TooltipsConfig.TOOLTIP_BACKSTAB_SHOT_PROPERTIES.get())
            return;
        double mult = ModProjectiles.getBackstabShotDamageMultiplier(stack,
                TraitsConfig.BACKSTAB_SHOT_PRONGED_KUNAI_PROJECTILE_MULTIPLIER.get().doubleValue());
        if (mult > 0.0) {
            ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.backstab_shot",
                    ChatFormatting.YELLOW);
            ModTooltips.addStat(stack, tooltip, "backstab_shot_multiplier", ModTooltips.roundToTwoDecimals(mult));
        }
    }

    private static void addDisablingShotPropertiesIfEnabled(ItemStack stack, List<Component> tooltip) {
        if (!isDisablingShotEnabled(stack) || !TooltipsConfig.TOOLTIP_DISABLING_SHOT_PROPERTIES.get())
            return;
        double chance = ModProjectiles.getDisablingShotChance(stack,
                TraitsConfig.DISABLING_SHOT_SHURIKEN_PROJECTILE_CHANCE.get().floatValue());
        int cd = ModProjectiles.getDisablingShotCooldown(stack,
                TraitsConfig.DISABLING_SHOT_SHURIKEN_PROJECTILE_COOLDOWN.get());
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.disabling_shot",
                ChatFormatting.YELLOW);
        if (chance > 0.0)
            ModTooltips.addStat(stack, tooltip, "disabling_shot_chance",
                    ModTooltips.roundToTwoDecimals(chance * 100.0));
        if (cd > 0)
            ModTooltips.addStat(stack, tooltip, "disabling_shot_cooldown", ModTooltips.roundToTwoDecimals(cd / 20.0));
    }

    private static void addSweepingShotPropertiesIfEnabled(ItemStack stack, List<Component> tooltip) {
        if (!isSweepingShotEnabled(stack) || !TooltipsConfig.TOOLTIP_SWEEPING_SHOT_PROPERTIES.get())
            return;
        double radius = ModProjectiles.getSweepingShotRadius(stack,
                TraitsConfig.SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_RADIUS.get().doubleValue());
        double factor = ModProjectiles.getSweepingShotDamageFactor(stack,
                TraitsConfig.SWEEPING_SHOT_GIANT_SHURIKEN_PROJECTILE_DAMAGE_FACTOR.get().doubleValue());
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.sweeping_shot",
                ChatFormatting.YELLOW);
        if (factor > 0.0)
            ModTooltips.addStat(stack, tooltip, "sweeping_shot_damage_factor", ModTooltips.roundToTwoDecimals(factor));
        if (radius > 0.0)
            ModTooltips.addStat(stack, tooltip, "sweeping_shot_radius", ModTooltips.roundToTwoDecimals(radius));
    }

    private static void addCooldownIfInstant(ItemStack stack, List<Component> tooltip, ModEnums.ThrowMode throwMode,
            ThrowableItemData.ThrowableEntry json) {
        if (throwMode != ModEnums.ThrowMode.INSTANT_ON_RIGHT_CLICK)
            return;
        int cd = 10;
        Integer nbt = getIntNBT(stack, "ThrowableInstantCooldownTicks");
        if (nbt != null)
            cd = nbt;
        ModTooltips.addStat(stack, tooltip, "cooldown", ModTooltips.roundToTwoDecimals(cd / 20.0));
    }

    private static void addThrowingMode(ItemStack stack, List<Component> tooltip, ModEnums.ThrowMode throwMode) {
        if (!TooltipsConfig.TOOLTIP_THROWING_MODE.get())
            return;
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.throwing_mode",
                ChatFormatting.YELLOW);
        String key = switch (throwMode) {
            case INSTANT_ON_RIGHT_CLICK -> "tooltip.jaams_weaponry.properties.throw_mode.instant";
            case CHARGE_AND_RELEASE -> "tooltip.jaams_weaponry.properties.throw_mode.charge_release";
            case CHARGE_AND_FINISH_USING -> "tooltip.jaams_weaponry.properties.throw_mode.charge_finishing";
            case CHARGE_RELEASE_AND_FINISH -> "tooltip.jaams_weaponry.properties.throw_mode.charge_hybrid";
            default -> "tooltip.jaams_weaponry.properties.throw_mode.charge_release";
        };
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static void addMainTrait(ItemStack stack, List<Component> tooltip) {
        ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.throwable",
                "tooltip.jaams_weaponry.trait.throwable.desc");
    }

    private static void addSpecialTraits(ItemStack stack, List<Component> tooltip, String projectileType) {
        boolean hb = "HUNTERS_BOOMERANG".equals(projectileType);
        boolean ring = "RING".equals(projectileType);
        if ((hb || ring) && isThrowbackEnabled(stack)) {
            ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.throwback",
                    "tooltip.jaams_weaponry.trait.throwback.desc");
        }
        if (hb && isCollectorEnabled(stack))
            ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.collector",
                    "tooltip.jaams_weaponry.trait.collector.desc");
        if (hb && isDisarmingShotEnabled(stack))
            ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.disarming_shot",
                    "tooltip.jaams_weaponry.trait.disarming_shot.desc");
        if ("KUNAI".equals(projectileType) && isPiercingShotEnabled(stack))
            ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.piercing_shot",
                    "tooltip.jaams_weaponry.trait.piercing_shot.desc");
        if ("PRONGED_KUNAI".equals(projectileType) && isBackstabShotEnabled(stack))
            ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.backstab_shot",
                    "tooltip.jaams_weaponry.trait.backstab_shot.desc");
        if ("SHURIKEN".equals(projectileType) && isDisablingShotEnabled(stack))
            ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.disabling_shot",
                    "tooltip.jaams_weaponry.trait.disabling_shot.desc");
        if ("GIANT_SHURIKEN".equals(projectileType) && isSweepingShotEnabled(stack))
            ModTooltips.addProjectileTrait(stack, tooltip, "tooltip.jaams_weaponry.trait.sweeping_shot",
                    "tooltip.jaams_weaponry.trait.sweeping_shot.desc");
    }

    private static Integer getIntNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_INT)) ? tag.getInt(key) : null;
    }

    private static Float getFloatNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_FLOAT)) ? tag.getFloat(key) : null;
    }

    private static String getStringNBT(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return (tag != null && tag.contains(key, Tag.TAG_STRING)) ? tag.getString(key) : null;
    }

    private static boolean isThrowbackEnabled(ItemStack stack) {
        return ModProjectiles.getThrowbackEnabled(stack, TraitsConfig.THROWBACK.get());
    }

    private static boolean isCollectorEnabled(ItemStack stack) {
        return ModProjectiles.getCollectorEnabled(stack, TraitsConfig.COLLECTOR.get());
    }

    private static boolean isDisarmingShotEnabled(ItemStack stack) {
        return ModProjectiles.getDisarmingShotEnabled(stack, TraitsConfig.DISARMING_SHOT.get());
    }

    private static boolean isPiercingShotEnabled(ItemStack stack) {
        return ModProjectiles.getPiercingShotEnabled(stack, TraitsConfig.PIERCING_SHOT.get());
    }

    private static boolean isBackstabShotEnabled(ItemStack stack) {
        return ModProjectiles.getBackstabShotEnabled(stack, TraitsConfig.BACKSTAB_SHOT.get());
    }

    private static boolean isDisablingShotEnabled(ItemStack stack) {
        return ModProjectiles.getDisablingShotEnabled(stack, TraitsConfig.DISABLING_SHOT.get());
    }

    private static boolean isSweepingShotEnabled(ItemStack stack) {
        return ModProjectiles.getSweepingShotEnabled(stack, TraitsConfig.SWEEPING_SHOT.get());
    }
}
