package net.jaams.weaponry.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.item.ArchersBottleItem;
import net.jaams.weaponry.item.BokkenItem;
import net.jaams.weaponry.item.BroomItem;
import net.jaams.weaponry.item.FlintMalletItem;
import net.jaams.weaponry.item.RagersBottleItem;
import net.jaams.weaponry.item.WarPickItem;
import net.jaams.weaponry.item.WarriorsBottleItem;

public class BottomItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> BROOM = REGISTRY.register("broom", () -> new BroomItem());
    public static final DeferredHolder<Item, Item> BOKKEN = REGISTRY.register("bokken", () -> new BokkenItem());
    public static final DeferredHolder<Item, Item> WAR_PICK = REGISTRY.register("war_pick", () -> new WarPickItem());
    public static final DeferredHolder<Item, Item> SMOKE_BOMB = REGISTRY.register("smoke_bomb",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DYNAMITE = REGISTRY.register("dynamite",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> FLINT_MALLET = REGISTRY.register("flint_mallet",
            () -> new FlintMalletItem());
    public static final DeferredHolder<Item, Item> LONG_STICK = REGISTRY.register("long_stick",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SHORT_STICK = REGISTRY.register("short_stick",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COPPER_MUZZLE = REGISTRY.register("copper_muzzle",
            () -> new Item(new Item.Properties().durability(64)));
    public static final DeferredHolder<Item, Item> COPPER_QUICK_DRAW_MAGAZINE = REGISTRY
            .register("copper_quick_draw_magazine", () -> new Item(new Item.Properties().durability(64)));
    public static final DeferredHolder<Item, Item> COPPER_CHOKE = REGISTRY.register("copper_choke",
            () -> new Item(new Item.Properties().durability(84)));
    public static final DeferredHolder<Item, Item> COPPER_EXTENDED_MAGAZINE = REGISTRY.register("copper_extended_magazine",
            () -> new Item(new Item.Properties().durability(84)));
    public static final DeferredHolder<Item, Item> BULLET = REGISTRY.register("bullet",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> FIRE_BULLET = REGISTRY.register("fire_bullet",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> HEAVY_BULLET = REGISTRY.register("heavy_bullet",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GLOWING_BULLET = REGISTRY.register("glowing_bullet",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SHARP_BULLET = REGISTRY.register("sharp_bullet",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ECHO_BULLET = REGISTRY.register("echo_bullet",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SHOTSHELL = REGISTRY.register("shotshell",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> FIRE_SHOTSHELL = REGISTRY.register("fire_shotshell",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> HEAVY_SHOTSHELL = REGISTRY.register("heavy_shotshell",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GLOWING_SHOTSHELL = REGISTRY.register("glowing_shotshell",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SHARP_SHOTSHELL = REGISTRY.register("sharp_shotshell",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ECHO_SHOTSHELL = REGISTRY.register("echo_shotshell",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RAGERS_BOTTLE = REGISTRY.register("ragers_bottle",
            () -> new RagersBottleItem());
    public static final DeferredHolder<Item, Item> WARRIORS_BOTTLE = REGISTRY.register("warriors_bottle",
            () -> new WarriorsBottleItem());
    public static final DeferredHolder<Item, Item> ARCHERS_BOTTLE = REGISTRY.register("archers_bottle",
            () -> new ArchersBottleItem());
    public static final DeferredHolder<Item, Item> BULLET_ICON = REGISTRY.register("bullet_icon",
            () -> new Item(new Item.Properties().stacksTo(1)));
}
