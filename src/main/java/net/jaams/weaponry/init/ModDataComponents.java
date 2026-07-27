package net.jaams.weaponry.init;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 1.21.1 data-component registrations for mod-specific per-stack data.
 * <p>
 * Every piece of data that the mod previously stuffed into
 * {@code DataComponents.CUSTOM_DATA} (the vanilla {@code CustomData}
 * CompoundTag blob) should live in one of these typed components instead.
 * The legacy {@link net.jaams.weaponry.util.ModComponents} bridge still supports
 * reading from the old {@code CUSTOM_DATA} for backward compatibility with
 * existing items.
 */
public final class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> REGISTRY =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JaamsWeaponryMod.MODID);

    // ── General-purpose mod data (CompoundTag) ──────────────────────────
    // Catch-all for the hundreds of dynamic keys (traits, projectile
    // properties, dynamite settings, gun state, skins, …) that previously
    // lived inside the vanilla CustomData blob.
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> WEAPONRY_DATA =
            register("weaponry_data", () -> DataComponentType.<CompoundTag>builder()
                    .persistent(CompoundTag.CODEC)
                    .build());

    // ── Strongly-typed components for the most common data ──────────────
    // Dye colour for IDyeableItem implementations (replaces the "color"
    // key that was previously inside the CompoundTag).
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DYE_COLOR =
            register("dye_color", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .build());

    // ── Helper ──────────────────────────────────────────────────────────
    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name, Supplier<DataComponentType<T>> sup) {
        return REGISTRY.register(name, sup);
    }

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
