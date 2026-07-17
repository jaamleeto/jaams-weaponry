package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.weaponry.item.BroomItem;
import net.jaams.weaponry.JaamsWeaponryMod;

public class FarmersDelightItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> RICE_BALE_BROOM = REGISTRY.register("rice_bale_broom",
            () -> new BroomItem());
    public static final RegistryObject<Item> STRAW_BROOM = REGISTRY.register("straw_broom", () -> new BroomItem());
}
