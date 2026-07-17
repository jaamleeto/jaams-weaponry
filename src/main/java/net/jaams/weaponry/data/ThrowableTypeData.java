package net.jaams.weaponry.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.jaams.weaponry.component.projectile.BaseWeaponProjectileEntity;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.configuration.common.ThrowableConfig;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.entity.AxeProjectileEntity;
import net.jaams.weaponry.entity.BroomProjectileEntity;
import net.jaams.weaponry.entity.CleaverProjectileEntity;
import net.jaams.weaponry.entity.DynamiteProjectileEntity;
import net.jaams.weaponry.entity.GiantShurikenProjectileEntity;
import net.jaams.weaponry.entity.HuntersBoomerangProjectileEntity;
import net.jaams.weaponry.entity.KunaiProjectileEntity;
import net.jaams.weaponry.entity.ProngedKunaiProjectileEntity;
import net.jaams.weaponry.entity.RingProjectileEntity;
import net.jaams.weaponry.entity.RoyalAxeProjectileEntity;
import net.jaams.weaponry.entity.RoyalSpearProjectileEntity;
import net.jaams.weaponry.entity.SharpStoneProjectileEntity;
import net.jaams.weaponry.entity.ShurikenProjectileEntity;
import net.jaams.weaponry.entity.SpearProjectileEntity;
import net.jaams.weaponry.entity.TridentProjectileEntity;
import net.jaams.weaponry.init.ModSounds;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class ThrowableTypeData {

    public final String name;
    public final TagKey<Item> tag;
    public final String forceNbtKey;

    private ThrowableTypeData(String name, TagKey<Item> tag, String forceNbtKey) {
        this.name = name;
        this.tag = tag;
        this.forceNbtKey = forceNbtKey;
    }

    public static final List<ThrowableTypeData> ALL_TYPES = new ArrayList<>();

    static {
        ALL_TYPES.add(new ThrowableTypeData("AXE", ModTags.AXES, "ForceAxeThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("CLEAVER", ModTags.CLEAVERS, "ForceCleaverThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("ROYAL_AXE", ModTags.ROYAL_AXES, "ForceRoyalAxeThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("ROYAL_SPEAR", ModTags.ROYAL_SPEARS, "ForceRoyalSpearThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("GIANT_SHURIKEN", ModTags.GIANT_SHURIKENS, "ForceGiantShurikenThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("SHURIKEN", ModTags.SHURIKENS, "ForceShurikenThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("KUNAI", ModTags.KUNAIS, "ForceKunaiThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("PRONGED_KUNAI", ModTags.PRONGED_KUNAIS, "ForceProngedKunaiThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("SHARP_STONE", ModTags.SHARP_STONES, "ForceSharpStoneThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("SPEAR", ModTags.SPEARS, "ForceSpearThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("TRIDENT", ModTags.TRIDENTS, "ForceTridentThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("HUNTERS_BOOMERANG", ModTags.HUNTERS_BOOMERANGS, "ForceHuntersBoomerangThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("RING", ModTags.RINGS, "ForceRingThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("BROOM", ModTags.BROOMS, "ForceBroomThrowable"));
        ALL_TYPES.add(new ThrowableTypeData("DYNAMITE", ModTags.DYNAMITES, "ForceDynamiteThrowable"));
    }

    public static ThrowableTypeData getType(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        for (ThrowableTypeData type : ALL_TYPES) {
            if (type.matches(stack)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isThrowableType(ItemStack stack, String projectileType) {
        if (stack == null || stack.isEmpty()) return false;
        boolean matchFound = ALL_TYPES.stream()
            .filter((type) -> type.name.equals(projectileType))
            .anyMatch((type) -> type.matches(stack));
        if (matchFound) return true;
        return ThrowableItemData.getData(stack)
            .map((data) -> projectileType.equals(data.throwable.projectile))
            .orElse(false);
    }

    public static boolean hasTagForType(ItemStack stack, String type) {
        for (ThrowableTypeData data : ALL_TYPES) {
            if (data.name.equals(type)) {
                return stack.is(data.tag);
            }
        }
        return false;
    }

    public static boolean hasForceNBT(ItemStack stack, String type) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        for (ThrowableTypeData data : ALL_TYPES) {
            if (data.name.equals(type)) {
                return tag.getBoolean(data.forceNbtKey);
            }
        }
        return false;
    }

    public static boolean hasAnyForceNBT(ItemStack stack) {
        if (stack == null || !stack.hasTag()) return false;
        CompoundTag tag = stack.getTag();
        for (ThrowableTypeData data : ALL_TYPES) {
            if (tag.getBoolean(data.forceNbtKey)) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(ItemStack stack) {
        if (stack == null) return false;
        return (tag != null && stack.is(tag)) || (stack.hasTag() && stack.getTag().getBoolean(forceNbtKey));
    }

    public static boolean isEnabled(String name) {
        if (name == null) return false;
        try {
            boolean result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE.get();
                    break;
                default:
                    result = false;
                    break;
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    public static ModEnums.ThrowMode getThrowMode(String name) {
        if (name == null) return ModEnums.ThrowMode.CHARGE_AND_RELEASE;
        try {
            ModEnums.ThrowMode result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_THROW_MODE.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_THROW_MODE.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_THROW_MODE.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_THROW_MODE.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_THROW_MODE.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_THROW_MODE.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_THROW_MODE.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_THROW_MODE.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_THROW_MODE.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_THROW_MODE.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_THROW_MODE.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_THROW_MODE.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_THROW_MODE.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_THROW_MODE.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_THROW_MODE.get();
                    break;
                default:
                    result = ModEnums.ThrowMode.CHARGE_AND_RELEASE;
                    break;
            }
            return result;
        } catch (Exception e) {
            return ModEnums.ThrowMode.CHARGE_AND_RELEASE;
        }
    }

    public static double getDefaultBaseDamage(String name) {
        if (name == null) return 1.0;
        try {
            double result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ProjectileCommonConfig.AXE_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "CLEAVER":
                    result = ProjectileCommonConfig.CLEAVER_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "ROYAL_AXE":
                    result = ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "SHURIKEN":
                    result = ProjectileCommonConfig.SHURIKEN_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "KUNAI":
                    result = ProjectileCommonConfig.KUNAI_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "SHARP_STONE":
                    result = ProjectileCommonConfig.SHARP_STONE_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "SPEAR":
                    result = ProjectileCommonConfig.SPEAR_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "TRIDENT":
                    result = ProjectileCommonConfig.TRIDENT_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "RING":
                    result = ProjectileCommonConfig.RING_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "BROOM":
                    result = ProjectileCommonConfig.BROOM_PROJECTILE_BASE_DAMAGE.get();
                    break;
                case "DYNAMITE":
                    result = ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_DAMAGE.get();
                    break;
                default:
                    result = 1.0;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 1.0;
        }
    }

    public static double getDefaultBaseKnockback(String name) {
        if (name == null) return 0.1;
        try {
            double result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ProjectileCommonConfig.AXE_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "CLEAVER":
                    result = ProjectileCommonConfig.CLEAVER_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "ROYAL_AXE":
                    result = ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "SHURIKEN":
                    result = ProjectileCommonConfig.SHURIKEN_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "KUNAI":
                    result = ProjectileCommonConfig.KUNAI_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "SHARP_STONE":
                    result = ProjectileCommonConfig.SHARP_STONE_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "SPEAR":
                    result = ProjectileCommonConfig.SPEAR_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "TRIDENT":
                    result = ProjectileCommonConfig.TRIDENT_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "RING":
                    result = ProjectileCommonConfig.RING_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "BROOM":
                    result = ProjectileCommonConfig.BROOM_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                case "DYNAMITE":
                    result = ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_KNOCKBACK.get();
                    break;
                default:
                    result = 0.1;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0.1;
        }
    }

    public static int getDefaultPiercingLevel(String name) {
        if (name == null) return 0;
        try {
            int result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ProjectileCommonConfig.AXE_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "CLEAVER":
                    result = ProjectileCommonConfig.CLEAVER_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "ROYAL_AXE":
                    result = ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "SHURIKEN":
                    result = ProjectileCommonConfig.SHURIKEN_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "KUNAI":
                    result = ProjectileCommonConfig.KUNAI_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "SHARP_STONE":
                    result = ProjectileCommonConfig.SHARP_STONE_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "SPEAR":
                    result = ProjectileCommonConfig.SPEAR_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "TRIDENT":
                    result = ProjectileCommonConfig.TRIDENT_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "RING":
                    result = ProjectileCommonConfig.RING_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "BROOM":
                    result = ProjectileCommonConfig.BROOM_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                case "DYNAMITE":
                    result = ProjectileCommonConfig.DYNAMITE_PROJECTILE_PIERCING_LEVEL.get();
                    break;
                default:
                    result = 0;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getDefaultWaterInertia(String name) {
        if (name == null) return 0.6D;
        try {
            double result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ProjectileCommonConfig.AXE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "CLEAVER":
                    result = ProjectileCommonConfig.CLEAVER_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "ROYAL_AXE":
                    result = ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "SHURIKEN":
                    result = ProjectileCommonConfig.SHURIKEN_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "KUNAI":
                    result = ProjectileCommonConfig.KUNAI_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "SHARP_STONE":
                    result = ProjectileCommonConfig.SHARP_STONE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "SPEAR":
                    result = ProjectileCommonConfig.SPEAR_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "TRIDENT":
                    result = ProjectileCommonConfig.TRIDENT_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "RING":
                    result = ProjectileCommonConfig.RING_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "BROOM":
                    result = ProjectileCommonConfig.BROOM_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                case "DYNAMITE":
                    result = ProjectileCommonConfig.DYNAMITE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                    break;
                default:
                    result = 0.6D;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0.6D;
        }
    }

    public static int getMinChargeTicks(String name) {
        if (name == null) return 0;
        try {
            int result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_MIN_CHARGE.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_MIN_CHARGE.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_CHARGE.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_MIN_CHARGE.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MIN_CHARGE.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_MIN_CHARGE.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_MIN_CHARGE.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_MIN_CHARGE.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_MIN_CHARGE.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_MIN_CHARGE.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_MIN_CHARGE.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MIN_CHARGE.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_MIN_CHARGE.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_MIN_CHARGE.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_MIN_CHARGE.get();
                    break;
                default:
                    result = 0;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getMaxChargeTicks(String name) {
        if (name == null) return 20;
        try {
            int result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_MAX_CHARGE.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_MAX_CHARGE.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_MAX_CHARGE.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_MAX_CHARGE.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MAX_CHARGE.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_MAX_CHARGE.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_MAX_CHARGE.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_MAX_CHARGE.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_MAX_CHARGE.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_MAX_CHARGE.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_MAX_CHARGE.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MAX_CHARGE.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_MAX_CHARGE.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_MAX_CHARGE.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_MAX_CHARGE.get();
                    break;
                default:
                    result = 20;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 20;
        }
    }

    public static float getMinSpeed(String name) {
        if (name == null) return 1.0f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_MIN_SPEED.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_MIN_SPEED.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_SPEED.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_MIN_SPEED.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MIN_SPEED.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_MIN_SPEED.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_MIN_SPEED.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_MIN_SPEED.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_MIN_SPEED.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_MIN_SPEED.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_MIN_SPEED.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MIN_SPEED.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_MIN_SPEED.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_MIN_SPEED.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_MIN_SPEED.get().floatValue();
                    break;
                default:
                    result = 1.0f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static float getMaxSpeed(String name) {
        if (name == null) return 2.2f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_MAX_SPEED.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_MAX_SPEED.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_MAX_SPEED.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_MAX_SPEED.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MAX_SPEED.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_MAX_SPEED.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_MAX_SPEED.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_MAX_SPEED.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_MAX_SPEED.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_MAX_SPEED.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_MAX_SPEED.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MAX_SPEED.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_MAX_SPEED.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_MAX_SPEED.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_MAX_SPEED.get().floatValue();
                    break;
                default:
                    result = 2.2f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 2.2f;
        }
    }

    public static float getInaccuracy(String name) {
        if (name == null) return 1.0f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_INACCURACY.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_INACCURACY.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_INACCURACY.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_INACCURACY.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_INACCURACY.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_INACCURACY.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_INACCURACY.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_INACCURACY.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_INACCURACY.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_INACCURACY.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_INACCURACY.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_INACCURACY.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_INACCURACY.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_INACCURACY.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_INACCURACY.get().floatValue();
                    break;
                default:
                    result = 1.0f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static float getCriticalPowerThreshold(String name) {
        if (name == null) return 0.8f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                    break;
                default:
                    result = 0.8f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0.8f;
        }
    }

    public static int getDamageOnThrow(String name) {
        if (name == null) return 1;
        try {
            int result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_DAMAGE_AMOUNT_ON_THROW.get();
                    break;
                default:
                    result = 1;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 1;
        }
    }

    public static boolean getAllowMultishot(String name) {
        if (name == null) return false;
        try {
            boolean result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_ALLOW_MULTISHOT.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_ALLOW_MULTISHOT.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_ALLOW_MULTISHOT.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_ALLOW_MULTISHOT.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_ALLOW_MULTISHOT.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_ALLOW_MULTISHOT.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_ALLOW_MULTISHOT.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_ALLOW_MULTISHOT.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_ALLOW_MULTISHOT.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_ALLOW_MULTISHOT.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_ALLOW_MULTISHOT.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_ALLOW_MULTISHOT.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_ALLOW_MULTISHOT.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_ALLOW_MULTISHOT.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_ALLOW_MULTISHOT.get();
                    break;
                default:
                    result = false;
                    break;
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    public static float getMultishotSpreadAngle(String name) {
        if (name == null) return 10.0f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                    break;
                default:
                    result = 10.0f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 10.0f;
        }
    }

    public static float getRecoil(String name) {
        if (name == null) return 0.0f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_RECOIL.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_RECOIL.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_RECOIL.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_RECOIL.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_RECOIL.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_RECOIL.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_RECOIL.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_RECOIL.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_RECOIL.get().floatValue();
                    break;
                default:
                    result = 0.0f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public static boolean getRecoilOnlyFullyCharged(String name) {
        if (name == null) return false;
        try {
            boolean result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_ONLY_FULLY_CHARGED.get();
                    break;
                default:
                    result = false;
                    break;
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    public static float getRecoilCrouchReduction(String name) {
        if (name == null) return 0.4f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                    break;
                default:
                    result = 0.4f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0.4f;
        }
    }

    public static float getRecoilVerticalMultiplier(String name) {
        if (name == null) return 1.0f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                    break;
                default:
                    result = 1.0f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static float getRecoilPitchKick(String name) {
        if (name == null) return 8.0f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_PITCH_KICK.get().floatValue();
                    break;
                default:
                    result = 8.0f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 8.0f;
        }
    }

    public static int getInstantCooldownTicks(String name) {
        if (name == null) return 0;
        try {
            int result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_COOLDOWN_TICKS_INSTANT.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_COOLDOWN_TICKS_INSTANT.get();
                    break;
                default:
                    result = 0;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getUseDurationTicks(String name) {
        if (name == null) return 72000;
        try {
            int result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_USE_DURATION_TICKS.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_USE_DURATION_TICKS.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_USE_DURATION_TICKS.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_USE_DURATION_TICKS.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_USE_DURATION_TICKS.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_USE_DURATION_TICKS.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_USE_DURATION_TICKS.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_USE_DURATION_TICKS.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_USE_DURATION_TICKS.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_USE_DURATION_TICKS.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_USE_DURATION_TICKS.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_USE_DURATION_TICKS.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_USE_DURATION_TICKS.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_USE_DURATION_TICKS.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_USE_DURATION_TICKS.get();
                    break;
                default:
                    result = 72000;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 72000;
        }
    }

    public static int getMinRemainingDurability(String name) {
        if (name == null) return -1;
        try {
            int result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_MIN_REMAINING_DURABILITY.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_MIN_REMAINING_DURABILITY.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_REMAINING_DURABILITY.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_MIN_REMAINING_DURABILITY.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MIN_REMAINING_DURABILITY.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_MIN_REMAINING_DURABILITY.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_MIN_REMAINING_DURABILITY.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_MIN_REMAINING_DURABILITY.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_MIN_REMAINING_DURABILITY.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_MIN_REMAINING_DURABILITY.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_MIN_REMAINING_DURABILITY.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MIN_REMAINING_DURABILITY.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_MIN_REMAINING_DURABILITY.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_MIN_REMAINING_DURABILITY.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_MIN_REMAINING_DURABILITY.get();
                    break;
                default:
                    result = -1;
                    break;
            }
            return result;
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean getRememberSlot(String name) {
        if (name == null) return false;
        try {
            boolean result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_REMEMBER_SLOT.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_REMEMBER_SLOT.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_REMEMBER_SLOT.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_REMEMBER_SLOT.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_REMEMBER_SLOT.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_REMEMBER_SLOT.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_REMEMBER_SLOT.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_REMEMBER_SLOT.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_REMEMBER_SLOT.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_REMEMBER_SLOT.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_REMEMBER_SLOT.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_REMEMBER_SLOT.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_REMEMBER_SLOT.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_REMEMBER_SLOT.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_REMEMBER_SLOT.get();
                    break;
                default:
                    result = false;
                    break;
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    public static UseAnim getUseAnimation(String name) {
        if (name == null) return UseAnim.SPEAR;
        try {
            UseAnim result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ThrowableConfig.THROWABLE_AXE_USE_ANIMATION.get();
                    break;
                case "CLEAVER":
                    result = ThrowableConfig.THROWABLE_CLEAVER_USE_ANIMATION.get();
                    break;
                case "ROYAL_AXE":
                    result = ThrowableConfig.THROWABLE_ROYAL_AXE_USE_ANIMATION.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ThrowableConfig.THROWABLE_ROYAL_SPEAR_USE_ANIMATION.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ThrowableConfig.THROWABLE_GIANT_SHURIKEN_USE_ANIMATION.get();
                    break;
                case "SHURIKEN":
                    result = ThrowableConfig.THROWABLE_SHURIKEN_USE_ANIMATION.get();
                    break;
                case "KUNAI":
                    result = ThrowableConfig.THROWABLE_KUNAI_USE_ANIMATION.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ThrowableConfig.THROWABLE_PRONGED_KUNAI_USE_ANIMATION.get();
                    break;
                case "SHARP_STONE":
                    result = ThrowableConfig.THROWABLE_SHARP_STONE_USE_ANIMATION.get();
                    break;
                case "SPEAR":
                    result = ThrowableConfig.THROWABLE_SPEAR_USE_ANIMATION.get();
                    break;
                case "TRIDENT":
                    result = ThrowableConfig.THROWABLE_TRIDENT_USE_ANIMATION.get();
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_USE_ANIMATION.get();
                    break;
                case "RING":
                    result = ThrowableConfig.THROWABLE_RING_USE_ANIMATION.get();
                    break;
                case "BROOM":
                    result = ThrowableConfig.THROWABLE_BROOM_USE_ANIMATION.get();
                    break;
                case "DYNAMITE":
                    result = ThrowableConfig.THROWABLE_DYNAMITE_USE_ANIMATION.get();
                    break;
                default:
                    result = UseAnim.SPEAR;
                    break;
            }
            return result;
        } catch (Exception e) {
            return UseAnim.SPEAR;
        }
    }

    public static SoundEvent getShootSound(String name) {
        if (name == null) {
            return ModSounds.PROJECTILE_THROW.get();
        }
        try {
            SoundEvent result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE":
                    result = ModSounds.AXE_THROW.get();
                    break;
                case "CLEAVER":
                    result = ModSounds.CLEAVER_THROW.get();
                    break;
                case "ROYAL_AXE":
                    result = ModSounds.ROYAL_AXE_THROW.get();
                    break;
                case "ROYAL_SPEAR":
                    result = ModSounds.ROYAL_SPEAR_THROW.get();
                    break;
                case "GIANT_SHURIKEN":
                    result = ModSounds.GIANT_SHURIKEN_THROW.get();
                    break;
                case "SHURIKEN":
                    result = ModSounds.SHURIKEN_THROW.get();
                    break;
                case "KUNAI":
                    result = ModSounds.KUNAI_THROW.get();
                    break;
                case "PRONGED_KUNAI":
                    result = ModSounds.PRONGED_KUNAI_THROW.get();
                    break;
                case "SHARP_STONE":
                    result = ModSounds.SHARP_STONE_FIRED.get();
                    break;
                case "SPEAR":
                    result = ModSounds.SPEAR_THROW.get();
                    break;
                case "TRIDENT":
                    result = SoundEvents.TRIDENT_THROW;
                    break;
                case "HUNTERS_BOOMERANG":
                    result = ModSounds.HUNTERS_BOOMERANG_THROW.get();
                    break;
                case "RING":
                    result = ModSounds.RING_THROW.get();
                    break;
                case "BROOM":
                    result = ModSounds.BROOM_THROW.get();
                    break;
                case "DYNAMITE":
                    result = ModSounds.DYNAMITE_FIRED.get();
                    break;
                default:
                    result = ModSounds.PROJECTILE_THROW.get();
                    break;
            }
            return result;
        } catch (Exception e) {
            return ModSounds.PROJECTILE_THROW.get();
        }
    }

    public static BaseWeaponProjectileEntity createProjectileEntity(String type, Level level, Player player, ItemStack projectileStack) {
        if (type == null || type.isEmpty()) return null;
        try {
            type = type.toUpperCase(Locale.ROOT).trim();
            BaseWeaponProjectileEntity result;
            switch (type) {
                case "AXE":
                    result = new AxeProjectileEntity(level, player, projectileStack);
                    break;
                case "CLEAVER":
                    result = new CleaverProjectileEntity(level, player, projectileStack);
                    break;
                case "ROYAL_AXE":
                    result = new RoyalAxeProjectileEntity(level, player, projectileStack);
                    break;
                case "ROYAL_SPEAR":
                    result = new RoyalSpearProjectileEntity(level, player, projectileStack);
                    break;
                case "GIANT_SHURIKEN":
                    result = new GiantShurikenProjectileEntity(level, player, projectileStack);
                    break;
                case "SHURIKEN":
                    result = new ShurikenProjectileEntity(level, player, projectileStack);
                    break;
                case "KUNAI":
                    result = new KunaiProjectileEntity(level, player, projectileStack);
                    break;
                case "PRONGED_KUNAI":
                    result = new ProngedKunaiProjectileEntity(level, player, projectileStack);
                    break;
                case "SHARP_STONE":
                    result = new SharpStoneProjectileEntity(level, player, projectileStack);
                    break;
                case "SPEAR":
                    result = new SpearProjectileEntity(level, player, projectileStack);
                    break;
                case "TRIDENT":
                    result = new TridentProjectileEntity(level, player, projectileStack);
                    break;
                case "HUNTERS_BOOMERANG":
                case "BOOMERANG":
                    result = new HuntersBoomerangProjectileEntity(level, player, projectileStack);
                    break;
                case "RING":
                    result = new RingProjectileEntity(level, player, projectileStack);
                    break;
                case "BROOM":
                    result = new BroomProjectileEntity(level, player, projectileStack);
                    break;
                case "DYNAMITE":
                    result = new DynamiteProjectileEntity(level, player, projectileStack);
                    break;
                default:
                    result = null;
                    break;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double getThrowbackMinRangeDefault(String name) {
        if (name == null) return 5.0;
        try {
            double result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG":
                    result = TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MIN_RANGE.get();
                    break;
                case "RING":
                    result = TraitsConfig.THROWBACK_RING_PROJECTILE_MIN_RANGE.get();
                    break;
                default:
                    result = 5.0;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 5.0;
        }
    }

    public static float getThrowbackMaxRangeDefault(String name) {
        if (name == null) return 30.0f;
        try {
            float result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG":
                    result = TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MAX_RANGE.get().floatValue();
                    break;
                case "RING":
                    result = TraitsConfig.THROWBACK_RING_PROJECTILE_MAX_RANGE.get().floatValue();
                    break;
                default:
                    result = 30.0f;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 30.0f;
        }
    }

    public static double getThrowbackReturnSpeedDefault(String name) {
        if (name == null) return 1.5d;
        try {
            double result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG":
                    result = TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_SPEED.get();
                    break;
                case "RING":
                    result = TraitsConfig.THROWBACK_RING_PROJECTILE_RETURN_SPEED.get();
                    break;
                default:
                    result = 1.5d;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 1.5d;
        }
    }

    public static double getDefaultCollectorMaxItems(String name) {
        if (name == null) return 0;
        try {
            double result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG":
                    result = TraitsConfig.COLLECTOR_HUNTERS_BOOMERANG_PROJECTILE_MAX_ITEMS.get();
                    break;
                default:
                    result = 0;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getDefaultDisarmingShotChance(String name) {
        if (name == null) return 0.0;
        try {
            double result;
            switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG":
                    result = TraitsConfig.DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_CHANCE.get();
                    break;
                default:
                    result = 0.0;
                    break;
            }
            return result;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
