package net.jaams.weaponry.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.weaponry.item.BroomItem;
import net.jaams.weaponry.JaamsWeaponryMod;

public class FarmersDelightItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> RICE_BALE_BROOM = REGISTRY.register("rice_bale_broom",
            () -> new BroomItem());
    public static final DeferredHolder<Item, Item> STRAW_BROOM = REGISTRY.register("straw_broom", () -> new BroomItem());
}
