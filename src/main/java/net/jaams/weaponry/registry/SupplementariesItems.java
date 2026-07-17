package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.weaponry.item.BroomItem;
import net.jaams.weaponry.JaamsWeaponryMod;

public class SupplementariesItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> FLAX_BALE_BROOM = REGISTRY.register("flax_bale_broom",
            () -> new BroomItem());
}
