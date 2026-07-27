package net.jaams.weaponry.data;

import net.jaams.weaponry.util.ModComponents;

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
        CompoundTag tag = ModComponents.get(stack);
        if (tag == null) return false;
        for (ThrowableTypeData data : ALL_TYPES) {
            if (data.name.equals(type)) {
                return tag.getBoolean(data.forceNbtKey);
            }
        }
        return false;
    }

    public boolean matches(ItemStack stack) {
        if (stack == null) return false;
        return (tag != null && stack.is(tag)) || (ModComponents.has(stack) && ModComponents.get(stack).getBoolean(forceNbtKey));
    }

    public static boolean isEnabled(String name) {
        if (name == null) return false;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE.get();
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    public static ModEnums.ThrowMode getThrowMode(String name) {
        if (name == null) return ModEnums.ThrowMode.CHARGE_AND_RELEASE;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_THROW_MODE.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_THROW_MODE.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_THROW_MODE.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_THROW_MODE.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_THROW_MODE.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_THROW_MODE.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_THROW_MODE.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_THROW_MODE.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_THROW_MODE.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_THROW_MODE.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_THROW_MODE.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_THROW_MODE.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_THROW_MODE.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_THROW_MODE.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_THROW_MODE.get();
                default -> ModEnums.ThrowMode.CHARGE_AND_RELEASE;
            };
        } catch (Exception e) {
            return ModEnums.ThrowMode.CHARGE_AND_RELEASE;
        }
    }

    public static double getDefaultBaseDamage(String name) {
        if (name == null) return 1.0;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ProjectileCommonConfig.AXE_PROJECTILE_BASE_DAMAGE.get();
                case "CLEAVER" -> ProjectileCommonConfig.CLEAVER_PROJECTILE_BASE_DAMAGE.get();
                case "ROYAL_AXE" -> ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_BASE_DAMAGE.get();
                case "ROYAL_SPEAR" -> ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_BASE_DAMAGE.get();
                case "GIANT_SHURIKEN" -> ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BASE_DAMAGE.get();
                case "SHURIKEN" -> ProjectileCommonConfig.SHURIKEN_PROJECTILE_BASE_DAMAGE.get();
                case "KUNAI" -> ProjectileCommonConfig.KUNAI_PROJECTILE_BASE_DAMAGE.get();
                case "PRONGED_KUNAI" -> ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BASE_DAMAGE.get();
                case "SHARP_STONE" -> ProjectileCommonConfig.SHARP_STONE_PROJECTILE_BASE_DAMAGE.get();
                case "SPEAR" -> ProjectileCommonConfig.SPEAR_PROJECTILE_BASE_DAMAGE.get();
                case "TRIDENT" -> ProjectileCommonConfig.TRIDENT_PROJECTILE_BASE_DAMAGE.get();
                case "HUNTERS_BOOMERANG" -> ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BASE_DAMAGE.get();
                case "RING" -> ProjectileCommonConfig.RING_PROJECTILE_BASE_DAMAGE.get();
                case "BROOM" -> ProjectileCommonConfig.BROOM_PROJECTILE_BASE_DAMAGE.get();
                case "DYNAMITE" -> ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_DAMAGE.get();
                default -> 1.0;
            };
        } catch (Exception e) {
            return 1.0;
        }
    }

    public static double getDefaultBaseKnockback(String name) {
        if (name == null) return 0.1;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ProjectileCommonConfig.AXE_PROJECTILE_BASE_KNOCKBACK.get();
                case "CLEAVER" -> ProjectileCommonConfig.CLEAVER_PROJECTILE_BASE_KNOCKBACK.get();
                case "ROYAL_AXE" -> ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_BASE_KNOCKBACK.get();
                case "ROYAL_SPEAR" -> ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_BASE_KNOCKBACK.get();
                case "GIANT_SHURIKEN" -> ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_BASE_KNOCKBACK.get();
                case "SHURIKEN" -> ProjectileCommonConfig.SHURIKEN_PROJECTILE_BASE_KNOCKBACK.get();
                case "KUNAI" -> ProjectileCommonConfig.KUNAI_PROJECTILE_BASE_KNOCKBACK.get();
                case "PRONGED_KUNAI" -> ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_BASE_KNOCKBACK.get();
                case "SHARP_STONE" -> ProjectileCommonConfig.SHARP_STONE_PROJECTILE_BASE_KNOCKBACK.get();
                case "SPEAR" -> ProjectileCommonConfig.SPEAR_PROJECTILE_BASE_KNOCKBACK.get();
                case "TRIDENT" -> ProjectileCommonConfig.TRIDENT_PROJECTILE_BASE_KNOCKBACK.get();
                case "HUNTERS_BOOMERANG" -> ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_BASE_KNOCKBACK.get();
                case "RING" -> ProjectileCommonConfig.RING_PROJECTILE_BASE_KNOCKBACK.get();
                case "BROOM" -> ProjectileCommonConfig.BROOM_PROJECTILE_BASE_KNOCKBACK.get();
                case "DYNAMITE" -> ProjectileCommonConfig.DYNAMITE_PROJECTILE_BASE_KNOCKBACK.get();
                default -> 0.1;
            };
        } catch (Exception e) {
            return 0.1;
        }
    }

    public static int getDefaultPiercingLevel(String name) {
        if (name == null) return 0;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ProjectileCommonConfig.AXE_PROJECTILE_PIERCING_LEVEL.get();
                case "CLEAVER" -> ProjectileCommonConfig.CLEAVER_PROJECTILE_PIERCING_LEVEL.get();
                case "ROYAL_AXE" -> ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_PIERCING_LEVEL.get();
                case "ROYAL_SPEAR" -> ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_PIERCING_LEVEL.get();
                case "GIANT_SHURIKEN" -> ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_PIERCING_LEVEL.get();
                case "SHURIKEN" -> ProjectileCommonConfig.SHURIKEN_PROJECTILE_PIERCING_LEVEL.get();
                case "KUNAI" -> ProjectileCommonConfig.KUNAI_PROJECTILE_PIERCING_LEVEL.get();
                case "PRONGED_KUNAI" -> ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_PIERCING_LEVEL.get();
                case "SHARP_STONE" -> ProjectileCommonConfig.SHARP_STONE_PROJECTILE_PIERCING_LEVEL.get();
                case "SPEAR" -> ProjectileCommonConfig.SPEAR_PROJECTILE_PIERCING_LEVEL.get();
                case "TRIDENT" -> ProjectileCommonConfig.TRIDENT_PROJECTILE_PIERCING_LEVEL.get();
                case "HUNTERS_BOOMERANG" -> ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_PIERCING_LEVEL.get();
                case "RING" -> ProjectileCommonConfig.RING_PROJECTILE_PIERCING_LEVEL.get();
                case "BROOM" -> ProjectileCommonConfig.BROOM_PROJECTILE_PIERCING_LEVEL.get();
                case "DYNAMITE" -> ProjectileCommonConfig.DYNAMITE_PROJECTILE_PIERCING_LEVEL.get();
                default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getDefaultWaterInertia(String name) {
        if (name == null) return 0.6D;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ProjectileCommonConfig.AXE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "CLEAVER" -> ProjectileCommonConfig.CLEAVER_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "ROYAL_AXE" -> ProjectileCommonConfig.ROYAL_AXE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "ROYAL_SPEAR" -> ProjectileCommonConfig.ROYAL_SPEAR_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "GIANT_SHURIKEN" -> ProjectileCommonConfig.GIANT_SHURIKEN_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "SHURIKEN" -> ProjectileCommonConfig.SHURIKEN_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "KUNAI" -> ProjectileCommonConfig.KUNAI_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "PRONGED_KUNAI" -> ProjectileCommonConfig.PRONGED_KUNAI_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "SHARP_STONE" -> ProjectileCommonConfig.SHARP_STONE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "SPEAR" -> ProjectileCommonConfig.SPEAR_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "TRIDENT" -> ProjectileCommonConfig.TRIDENT_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "HUNTERS_BOOMERANG" -> ProjectileCommonConfig.HUNTERS_BOOMERANG_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "RING" -> ProjectileCommonConfig.RING_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "BROOM" -> ProjectileCommonConfig.BROOM_PROJECTILE_WATER_INERTIA.get().doubleValue();
                case "DYNAMITE" -> ProjectileCommonConfig.DYNAMITE_PROJECTILE_WATER_INERTIA.get().doubleValue();
                default -> 0.6D;
            };
        } catch (Exception e) {
            return 0.6D;
        }
    }

    public static int getMinChargeTicks(String name) {
        if (name == null) return 0;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_MIN_CHARGE.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_MIN_CHARGE.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_CHARGE.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_MIN_CHARGE.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MIN_CHARGE.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_MIN_CHARGE.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_MIN_CHARGE.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_MIN_CHARGE.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_MIN_CHARGE.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_MIN_CHARGE.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_MIN_CHARGE.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MIN_CHARGE.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_MIN_CHARGE.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_MIN_CHARGE.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_MIN_CHARGE.get();
                default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getMaxChargeTicks(String name) {
        if (name == null) return 20;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_MAX_CHARGE.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_MAX_CHARGE.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_MAX_CHARGE.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_MAX_CHARGE.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MAX_CHARGE.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_MAX_CHARGE.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_MAX_CHARGE.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_MAX_CHARGE.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_MAX_CHARGE.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_MAX_CHARGE.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_MAX_CHARGE.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MAX_CHARGE.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_MAX_CHARGE.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_MAX_CHARGE.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_MAX_CHARGE.get();
                default -> 20;
            };
        } catch (Exception e) {
            return 20;
        }
    }

    public static float getMinSpeed(String name) {
        if (name == null) return 1.0f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_MIN_SPEED.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_MIN_SPEED.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_SPEED.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_MIN_SPEED.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MIN_SPEED.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_MIN_SPEED.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_MIN_SPEED.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_MIN_SPEED.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_MIN_SPEED.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_MIN_SPEED.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_MIN_SPEED.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MIN_SPEED.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_MIN_SPEED.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_MIN_SPEED.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_MIN_SPEED.get().floatValue();
                default -> 1.0f;
            };
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static float getMaxSpeed(String name) {
        if (name == null) return 2.2f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_MAX_SPEED.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_MAX_SPEED.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_MAX_SPEED.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_MAX_SPEED.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MAX_SPEED.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_MAX_SPEED.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_MAX_SPEED.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_MAX_SPEED.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_MAX_SPEED.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_MAX_SPEED.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_MAX_SPEED.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MAX_SPEED.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_MAX_SPEED.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_MAX_SPEED.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_MAX_SPEED.get().floatValue();
                default -> 2.2f;
            };
        } catch (Exception e) {
            return 2.2f;
        }
    }

    public static float getInaccuracy(String name) {
        if (name == null) return 1.0f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_INACCURACY.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_INACCURACY.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_INACCURACY.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_INACCURACY.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_INACCURACY.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_INACCURACY.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_INACCURACY.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_INACCURACY.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_INACCURACY.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_INACCURACY.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_INACCURACY.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_INACCURACY.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_INACCURACY.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_INACCURACY.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_INACCURACY.get().floatValue();
                default -> 1.0f;
            };
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static float getCriticalPowerThreshold(String name) {
        if (name == null) return 0.8f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_CRITICAL_POWER_THRESHOLD.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_CRITICAL_POWER_THRESHOLD.get().floatValue();
                default -> 0.8f;
            };
        } catch (Exception e) {
            return 0.8f;
        }
    }

    public static int getDamageOnThrow(String name) {
        if (name == null) return 1;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_DAMAGE_AMOUNT_ON_THROW.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_DAMAGE_AMOUNT_ON_THROW.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_DAMAGE_AMOUNT_ON_THROW.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_DAMAGE_AMOUNT_ON_THROW.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_DAMAGE_AMOUNT_ON_THROW.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_DAMAGE_AMOUNT_ON_THROW.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_DAMAGE_AMOUNT_ON_THROW.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_DAMAGE_AMOUNT_ON_THROW.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_DAMAGE_AMOUNT_ON_THROW.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_DAMAGE_AMOUNT_ON_THROW.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_DAMAGE_AMOUNT_ON_THROW.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_DAMAGE_AMOUNT_ON_THROW.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_DAMAGE_AMOUNT_ON_THROW.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_DAMAGE_AMOUNT_ON_THROW.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_DAMAGE_AMOUNT_ON_THROW.get();
                default -> 1;
            };
        } catch (Exception e) {
            return 1;
        }
    }

    public static boolean getAllowMultishot(String name) {
        if (name == null) return false;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_ALLOW_MULTISHOT.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_ALLOW_MULTISHOT.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_ALLOW_MULTISHOT.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_ALLOW_MULTISHOT.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_ALLOW_MULTISHOT.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_ALLOW_MULTISHOT.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_ALLOW_MULTISHOT.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_ALLOW_MULTISHOT.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_ALLOW_MULTISHOT.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_ALLOW_MULTISHOT.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_ALLOW_MULTISHOT.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_ALLOW_MULTISHOT.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_ALLOW_MULTISHOT.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_ALLOW_MULTISHOT.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_ALLOW_MULTISHOT.get();
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    public static float getMultishotSpreadAngle(String name) {
        if (name == null) return 10.0f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_MULTISHOT_SPREAD_ANGLE.get().floatValue();
                default -> 10.0f;
            };
        } catch (Exception e) {
            return 10.0f;
        }
    }

    public static float getRecoil(String name) {
        if (name == null) return 0.0f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_RECOIL.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_RECOIL.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_RECOIL.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_RECOIL.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_RECOIL.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_RECOIL.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_RECOIL.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_RECOIL.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_RECOIL.get().floatValue();
                default -> 0.0f;
            };
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public static boolean getRecoilOnlyFullyCharged(String name) {
        if (name == null) return false;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_RECOIL_ONLY_FULLY_CHARGED.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_RECOIL_ONLY_FULLY_CHARGED.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_ONLY_FULLY_CHARGED.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_ONLY_FULLY_CHARGED.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_ONLY_FULLY_CHARGED.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_ONLY_FULLY_CHARGED.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_RECOIL_ONLY_FULLY_CHARGED.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_ONLY_FULLY_CHARGED.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_ONLY_FULLY_CHARGED.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_RECOIL_ONLY_FULLY_CHARGED.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_RECOIL_ONLY_FULLY_CHARGED.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_ONLY_FULLY_CHARGED.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_RECOIL_ONLY_FULLY_CHARGED.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_RECOIL_ONLY_FULLY_CHARGED.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_ONLY_FULLY_CHARGED.get();
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    public static float getRecoilCrouchReduction(String name) {
        if (name == null) return 0.4f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_RECOIL_CROUCH_REDUCTION.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_CROUCH_REDUCTION.get().floatValue();
                default -> 0.4f;
            };
        } catch (Exception e) {
            return 0.4f;
        }
    }

    public static float getRecoilVerticalMultiplier(String name) {
        if (name == null) return 1.0f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_VERTICAL_MULTIPLIER.get().floatValue();
                default -> 1.0f;
            };
        } catch (Exception e) {
            return 1.0f;
        }
    }

    public static float getRecoilPitchKick(String name) {
        if (name == null) return 8.0f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_RECOIL_PITCH_KICK.get().floatValue();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_RECOIL_PITCH_KICK.get().floatValue();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_RECOIL_PITCH_KICK.get().floatValue();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_RECOIL_PITCH_KICK.get().floatValue();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_RECOIL_PITCH_KICK.get().floatValue();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_RECOIL_PITCH_KICK.get().floatValue();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_RECOIL_PITCH_KICK.get().floatValue();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_RECOIL_PITCH_KICK.get().floatValue();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_RECOIL_PITCH_KICK.get().floatValue();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_RECOIL_PITCH_KICK.get().floatValue();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_RECOIL_PITCH_KICK.get().floatValue();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_RECOIL_PITCH_KICK.get().floatValue();
                case "RING" -> ThrowableConfig.THROWABLE_RING_RECOIL_PITCH_KICK.get().floatValue();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_RECOIL_PITCH_KICK.get().floatValue();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_RECOIL_PITCH_KICK.get().floatValue();
                default -> 8.0f;
            };
        } catch (Exception e) {
            return 8.0f;
        }
    }

    public static int getInstantCooldownTicks(String name) {
        if (name == null) return 0;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_COOLDOWN_TICKS_INSTANT.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_COOLDOWN_TICKS_INSTANT.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_COOLDOWN_TICKS_INSTANT.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_COOLDOWN_TICKS_INSTANT.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_COOLDOWN_TICKS_INSTANT.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_COOLDOWN_TICKS_INSTANT.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_COOLDOWN_TICKS_INSTANT.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_COOLDOWN_TICKS_INSTANT.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_COOLDOWN_TICKS_INSTANT.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_COOLDOWN_TICKS_INSTANT.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_COOLDOWN_TICKS_INSTANT.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_COOLDOWN_TICKS_INSTANT.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_COOLDOWN_TICKS_INSTANT.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_COOLDOWN_TICKS_INSTANT.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_COOLDOWN_TICKS_INSTANT.get();
                default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getUseDurationTicks(String name) {
        if (name == null) return 72000;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_USE_DURATION_TICKS.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_USE_DURATION_TICKS.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_USE_DURATION_TICKS.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_USE_DURATION_TICKS.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_USE_DURATION_TICKS.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_USE_DURATION_TICKS.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_USE_DURATION_TICKS.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_USE_DURATION_TICKS.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_USE_DURATION_TICKS.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_USE_DURATION_TICKS.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_USE_DURATION_TICKS.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_USE_DURATION_TICKS.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_USE_DURATION_TICKS.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_USE_DURATION_TICKS.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_USE_DURATION_TICKS.get();
                default -> 72000;
            };
        } catch (Exception e) {
            return 72000;
        }
    }

    public static int getMinRemainingDurability(String name) {
        if (name == null) return -1;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_MIN_REMAINING_DURABILITY.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_MIN_REMAINING_DURABILITY.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_MIN_REMAINING_DURABILITY.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_MIN_REMAINING_DURABILITY.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_MIN_REMAINING_DURABILITY.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_MIN_REMAINING_DURABILITY.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_MIN_REMAINING_DURABILITY.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_MIN_REMAINING_DURABILITY.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_MIN_REMAINING_DURABILITY.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_MIN_REMAINING_DURABILITY.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_MIN_REMAINING_DURABILITY.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_MIN_REMAINING_DURABILITY.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_MIN_REMAINING_DURABILITY.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_MIN_REMAINING_DURABILITY.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_MIN_REMAINING_DURABILITY.get();
                default -> -1;
            };
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean getRememberSlot(String name) {
        if (name == null) return false;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_REMEMBER_SLOT.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_REMEMBER_SLOT.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_REMEMBER_SLOT.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_REMEMBER_SLOT.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_REMEMBER_SLOT.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_REMEMBER_SLOT.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_REMEMBER_SLOT.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_REMEMBER_SLOT.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_REMEMBER_SLOT.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_REMEMBER_SLOT.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_REMEMBER_SLOT.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_REMEMBER_SLOT.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_REMEMBER_SLOT.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_REMEMBER_SLOT.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_REMEMBER_SLOT.get();
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    public static UseAnim getUseAnimation(String name) {
        if (name == null) return UseAnim.SPEAR;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ThrowableConfig.THROWABLE_AXE_USE_ANIMATION.get();
                case "CLEAVER" -> ThrowableConfig.THROWABLE_CLEAVER_USE_ANIMATION.get();
                case "ROYAL_AXE" -> ThrowableConfig.THROWABLE_ROYAL_AXE_USE_ANIMATION.get();
                case "ROYAL_SPEAR" -> ThrowableConfig.THROWABLE_ROYAL_SPEAR_USE_ANIMATION.get();
                case "GIANT_SHURIKEN" -> ThrowableConfig.THROWABLE_GIANT_SHURIKEN_USE_ANIMATION.get();
                case "SHURIKEN" -> ThrowableConfig.THROWABLE_SHURIKEN_USE_ANIMATION.get();
                case "KUNAI" -> ThrowableConfig.THROWABLE_KUNAI_USE_ANIMATION.get();
                case "PRONGED_KUNAI" -> ThrowableConfig.THROWABLE_PRONGED_KUNAI_USE_ANIMATION.get();
                case "SHARP_STONE" -> ThrowableConfig.THROWABLE_SHARP_STONE_USE_ANIMATION.get();
                case "SPEAR" -> ThrowableConfig.THROWABLE_SPEAR_USE_ANIMATION.get();
                case "TRIDENT" -> ThrowableConfig.THROWABLE_TRIDENT_USE_ANIMATION.get();
                case "HUNTERS_BOOMERANG" -> ThrowableConfig.THROWABLE_HUNTERS_BOOMERANG_USE_ANIMATION.get();
                case "RING" -> ThrowableConfig.THROWABLE_RING_USE_ANIMATION.get();
                case "BROOM" -> ThrowableConfig.THROWABLE_BROOM_USE_ANIMATION.get();
                case "DYNAMITE" -> ThrowableConfig.THROWABLE_DYNAMITE_USE_ANIMATION.get();
                default -> UseAnim.SPEAR;
            };
        } catch (Exception e) {
            return UseAnim.SPEAR;
        }
    }

    public static SoundEvent getShootSound(String name) {
        if (name == null) {
            return ModSounds.PROJECTILE_THROW.get();
        }
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "AXE" -> ModSounds.AXE_THROW.get();
                case "CLEAVER" -> ModSounds.CLEAVER_THROW.get();
                case "ROYAL_AXE" -> ModSounds.ROYAL_AXE_THROW.get();
                case "ROYAL_SPEAR" -> ModSounds.ROYAL_SPEAR_THROW.get();
                case "GIANT_SHURIKEN" -> ModSounds.GIANT_SHURIKEN_THROW.get();
                case "SHURIKEN" -> ModSounds.SHURIKEN_THROW.get();
                case "KUNAI" -> ModSounds.KUNAI_THROW.get();
                case "PRONGED_KUNAI" -> ModSounds.PRONGED_KUNAI_THROW.get();
                case "SHARP_STONE" -> ModSounds.SHARP_STONE_FIRED.get();
                case "SPEAR" -> ModSounds.SPEAR_THROW.get();
                case "TRIDENT" -> SoundEvents.TRIDENT_THROW.value();
                case "HUNTERS_BOOMERANG" -> ModSounds.HUNTERS_BOOMERANG_THROW.get();
                case "RING" -> ModSounds.RING_THROW.get();
                case "BROOM" -> ModSounds.BROOM_THROW.get();
                case "DYNAMITE" -> ModSounds.DYNAMITE_FIRED.get();
                default -> ModSounds.PROJECTILE_THROW.get();
            };
        } catch (Exception e) {
            return ModSounds.PROJECTILE_THROW.get();
        }
    }

    public static BaseWeaponProjectileEntity createProjectileEntity(String type, Level level, Player player, ItemStack projectileStack) {
        if (type == null || type.isEmpty()) return null;
        try {
            type = type.toUpperCase(Locale.ROOT).trim();
            return switch (type) {
                case "AXE" -> new AxeProjectileEntity(level, player, projectileStack);
                case "CLEAVER" -> new CleaverProjectileEntity(level, player, projectileStack);
                case "ROYAL_AXE" -> new RoyalAxeProjectileEntity(level, player, projectileStack);
                case "ROYAL_SPEAR" -> new RoyalSpearProjectileEntity(level, player, projectileStack);
                case "GIANT_SHURIKEN" -> new GiantShurikenProjectileEntity(level, player, projectileStack);
                case "SHURIKEN" -> new ShurikenProjectileEntity(level, player, projectileStack);
                case "KUNAI" -> new KunaiProjectileEntity(level, player, projectileStack);
                case "PRONGED_KUNAI" -> new ProngedKunaiProjectileEntity(level, player, projectileStack);
                case "SHARP_STONE" -> new SharpStoneProjectileEntity(level, player, projectileStack);
                case "SPEAR" -> new SpearProjectileEntity(level, player, projectileStack);
                case "TRIDENT" -> new TridentProjectileEntity(level, player, projectileStack);
                case "HUNTERS_BOOMERANG", "BOOMERANG" -> new HuntersBoomerangProjectileEntity(level, player, projectileStack);
                case "RING" -> new RingProjectileEntity(level, player, projectileStack);
                case "BROOM" -> new BroomProjectileEntity(level, player, projectileStack);
                case "DYNAMITE" -> new DynamiteProjectileEntity(level, player, projectileStack);
                default -> null;
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double getThrowbackMinRangeDefault(String name) {
        if (name == null) return 5.0;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG" -> TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MIN_RANGE.get();
                case "RING" -> TraitsConfig.THROWBACK_RING_PROJECTILE_MIN_RANGE.get();
                default -> 5.0;
            };
        } catch (Exception e) {
            return 5.0;
        }
    }

    public static float getThrowbackMaxRangeDefault(String name) {
        if (name == null) return 30.0f;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG" -> TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_MAX_RANGE.get().floatValue();
                case "RING" -> TraitsConfig.THROWBACK_RING_PROJECTILE_MAX_RANGE.get().floatValue();
                default -> 30.0f;
            };
        } catch (Exception e) {
            return 30.0f;
        }
    }

    public static double getThrowbackReturnSpeedDefault(String name) {
        if (name == null) return 1.5d;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG" -> TraitsConfig.THROWBACK_HUNTERS_BOOMERANG_PROJECTILE_RETURN_SPEED.get();
                case "RING" -> TraitsConfig.THROWBACK_RING_PROJECTILE_RETURN_SPEED.get();
                default -> 1.5d;
            };
        } catch (Exception e) {
            return 1.5d;
        }
    }

    public static double getDefaultCollectorMaxItems(String name) {
        if (name == null) return 0;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG" -> TraitsConfig.COLLECTOR_HUNTERS_BOOMERANG_PROJECTILE_MAX_ITEMS.get();
                default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getDefaultDisarmingShotChance(String name) {
        if (name == null) return 0.0;
        try {
            return switch (name.toUpperCase(Locale.ROOT)) {
                case "HUNTERS_BOOMERANG" -> TraitsConfig.DISARMING_SHOT_HUNTERS_BOOMERANG_PROJECTILE_CHANCE.get();
                default -> 0.0;
            };
        } catch (Exception e) {
            return 0.0;
        }
    }
}
