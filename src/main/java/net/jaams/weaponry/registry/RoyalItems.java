package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.weaponry.item.RoyalSwordItem;
import net.jaams.weaponry.item.RoyalSpearItem;
import net.jaams.weaponry.item.RoyalRapierItem;
import net.jaams.weaponry.item.RoyalCrossbowItem;
import net.jaams.weaponry.item.RoyalBowItem;
import net.jaams.weaponry.item.RoyalAxeItem;
import net.jaams.weaponry.JaamsWeaponryMod;

public class RoyalItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> ROYAL_SPEAR = REGISTRY.register("royal_spear", () -> new RoyalSpearItem());
    public static final RegistryObject<Item> ROYAL_SWORD = REGISTRY.register("royal_sword", () -> new RoyalSwordItem());
    public static final RegistryObject<Item> ROYAL_BOW = REGISTRY.register("royal_bow", () -> new RoyalBowItem());
    public static final RegistryObject<Item> ROYAL_AXE = REGISTRY.register("royal_axe", () -> new RoyalAxeItem());
    public static final RegistryObject<Item> ROYAL_RAPIER = REGISTRY.register("royal_rapier",
            () -> new RoyalRapierItem());
    public static final RegistryObject<Item> ROYAL_CROSSBOW = REGISTRY.register("royal_crossbow",
            () -> new RoyalCrossbowItem());
}
