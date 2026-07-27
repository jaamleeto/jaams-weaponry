package net.jaams.weaponry.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.weaponry.item.RoyalSwordItem;
import net.jaams.weaponry.item.RoyalSpearItem;
import net.jaams.weaponry.item.RoyalRapierItem;
import net.jaams.weaponry.item.RoyalCrossbowItem;
import net.jaams.weaponry.item.RoyalBowItem;
import net.jaams.weaponry.item.RoyalAxeItem;
import net.jaams.weaponry.JaamsWeaponryMod;

public class RoyalItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> ROYAL_SPEAR = REGISTRY.register("royal_spear", () -> new RoyalSpearItem());
    public static final DeferredHolder<Item, Item> ROYAL_SWORD = REGISTRY.register("royal_sword", () -> new RoyalSwordItem());
    public static final DeferredHolder<Item, Item> ROYAL_BOW = REGISTRY.register("royal_bow", () -> new RoyalBowItem());
    public static final DeferredHolder<Item, Item> ROYAL_AXE = REGISTRY.register("royal_axe", () -> new RoyalAxeItem());
    public static final DeferredHolder<Item, Item> ROYAL_RAPIER = REGISTRY.register("royal_rapier",
            () -> new RoyalRapierItem());
    public static final DeferredHolder<Item, Item> ROYAL_CROSSBOW = REGISTRY.register("royal_crossbow",
            () -> new RoyalCrossbowItem());
}
