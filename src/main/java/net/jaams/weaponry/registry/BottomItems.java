package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

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
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> BROOM = REGISTRY.register("broom", () -> new BroomItem());
    public static final RegistryObject<Item> BOKKEN = REGISTRY.register("bokken", () -> new BokkenItem());
    public static final RegistryObject<Item> WAR_PICK = REGISTRY.register("war_pick", () -> new WarPickItem());
    public static final RegistryObject<Item> SMOKE_BOMB = REGISTRY.register("smoke_bomb",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DYNAMITE = REGISTRY.register("dynamite",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> FLINT_MALLET = REGISTRY.register("flint_mallet",
            () -> new FlintMalletItem());
    public static final RegistryObject<Item> LONG_STICK = REGISTRY.register("long_stick",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHORT_STICK = REGISTRY.register("short_stick",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COPPER_MUZZLE = REGISTRY.register("copper_muzzle",
            () -> new Item(new Item.Properties().durability(64)));
    public static final RegistryObject<Item> COPPER_QUICK_DRAW_MAGAZINE = REGISTRY
            .register("copper_quick_draw_magazine", () -> new Item(new Item.Properties().durability(64)));
    public static final RegistryObject<Item> COPPER_CHOKE = REGISTRY.register("copper_choke",
            () -> new Item(new Item.Properties().durability(84)));
    public static final RegistryObject<Item> COPPER_EXTENDED_MAGAZINE = REGISTRY.register("copper_extended_magazine",
            () -> new Item(new Item.Properties().durability(84)));
    public static final RegistryObject<Item> BULLET = REGISTRY.register("bullet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FIRE_BULLET = REGISTRY.register("fire_bullet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEAVY_BULLET = REGISTRY.register("heavy_bullet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLOWING_BULLET = REGISTRY.register("glowing_bullet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHARP_BULLET = REGISTRY.register("sharp_bullet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ECHO_BULLET = REGISTRY.register("echo_bullet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHOTSHELL = REGISTRY.register("shotshell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FIRE_SHOTSHELL = REGISTRY.register("fire_shotshell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEAVY_SHOTSHELL = REGISTRY.register("heavy_shotshell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLOWING_SHOTSHELL = REGISTRY.register("glowing_shotshell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHARP_SHOTSHELL = REGISTRY.register("sharp_shotshell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ECHO_SHOTSHELL = REGISTRY.register("echo_shotshell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAGERS_BOTTLE = REGISTRY.register("ragers_bottle",
            () -> new RagersBottleItem());
    public static final RegistryObject<Item> WARRIORS_BOTTLE = REGISTRY.register("warriors_bottle",
            () -> new WarriorsBottleItem());
    public static final RegistryObject<Item> ARCHERS_BOTTLE = REGISTRY.register("archers_bottle",
            () -> new ArchersBottleItem());
    public static final RegistryObject<Item> BULLET_ICON = REGISTRY.register("bullet_icon",
            () -> new Item(new Item.Properties().stacksTo(1)));
}
