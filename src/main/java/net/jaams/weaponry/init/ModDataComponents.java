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

public final class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> REGISTRY =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JaamsWeaponryMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> MOD_DATA =
            register("mod_data", () -> DataComponentType.<CompoundTag>builder()
                    .persistent(CompoundTag.CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DYE_COLOR =
            register("dye_color", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .build());

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name, Supplier<DataComponentType<T>> sup) {
        return REGISTRY.register(name, sup);
    }

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
