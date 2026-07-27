package net.jaams.weaponry.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.item.tiered.TieredWeapons;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.JaamsWeaponryMod;

public class ElectrumItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> HEAVY_ELECTRUM_INGOT = REGISTRY.register("heavy_electrum_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DOUBLE_ELECTRUM_INGOT = REGISTRY.register("double_electrum_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ELECTRUM_DAGGER = REGISTRY.register("electrum_dagger",
            () -> TieredWeapons.sword(1820, 10f, 2f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -1.8f));
    public static final DeferredHolder<Item, Item> ELECTRUM_DAGGER_REVERSE = REGISTRY.register("electrum_dagger_reverse",
            () -> TieredWeapons.sword(1820, 10f, 1.5f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -1.4f));
    public static final DeferredHolder<Item, Item> ELECTRUM_KNUCKLE = REGISTRY.register("electrum_knuckle",
            () -> TieredWeapons.sword(1820, 10f, 2.5f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -1.6f));
    public static final DeferredHolder<Item, Item> ELECTRUM_CLEAVER = REGISTRY.register("electrum_cleaver",
            () -> TieredWeapons.sword(1820, 10f, 5f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -3.2f));
    public static final DeferredHolder<Item, Item> ELECTRUM_RING = REGISTRY.register("electrum_ring",
            () -> TieredWeapons.sword(1820, 10f, 3f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -1.8f));
    public static final DeferredHolder<Item, Item> ELECTRUM_KAMA = REGISTRY.register("electrum_kama",
            () -> TieredWeapons.sword(1820, 10f, 3.5f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -2.2f));
    public static final DeferredHolder<Item, Item> ELECTRUM_CLAW = REGISTRY.register("electrum_claw",
            () -> TieredWeapons.sword(1820, 10f, 3f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -2.0f));
    public static final DeferredHolder<Item, Item> ELECTRUM_MACHETE = REGISTRY.register("electrum_machete",
            () -> TieredWeapons.sword(1920, 10f, 4.5f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -2.5f));
    public static final DeferredHolder<Item, Item> ELECTRUM_KATAR = REGISTRY.register("electrum_katar",
            () -> TieredWeapons.sword(1920, 10f, 5f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -2.6f));
    public static final DeferredHolder<Item, Item> ELECTRUM_SICKLE = REGISTRY.register("electrum_sickle",
            () -> TieredWeapons.sword(1920, 10f, 4f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -2.2f));
    public static final DeferredHolder<Item, Item> ELECTRUM_SPEAR = REGISTRY.register("electrum_spear",
            () -> TieredWeapons.sword(1820, 10f, 4f, 4, 18,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/electrum_ingot"))), 3,
                    -2.6f));
    public static final DeferredHolder<Item, Item> ELECTRUM_BATTLE_AXE = REGISTRY.register("electrum_battle_axe",
            () -> TieredWeapons.axe(1920, 10f, 12f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 1f, -3.3f));
    public static final DeferredHolder<Item, Item> ELECTRUM_BROADSWORD = REGISTRY.register("electrum_broadsword",
            () -> TieredWeapons.sword(2040, 10f, 7f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.8f));
    public static final DeferredHolder<Item, Item> ELECTRUM_BUTTERFLY_SWORD = REGISTRY.register("electrum_butterfly_sword",
            () -> TieredWeapons.sword(1920, 10f, 6f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> ELECTRUM_GREATSWORD = REGISTRY.register("electrum_greatsword",
            () -> TieredWeapons.sword(2020, 10f, 9f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -3.2f));
    public static final DeferredHolder<Item, Item> ELECTRUM_HAMMER = REGISTRY.register("electrum_hammer",
            () -> TieredWeapons.pickaxe(2120, 10f, 9f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -3.4f));
    public static final DeferredHolder<Item, Item> ELECTRUM_HOOK_SWORD = REGISTRY.register("electrum_hook_sword",
            () -> TieredWeapons.sword(1920, 10f, 6.5f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> ELECTRUM_KATANA = REGISTRY.register("electrum_katana",
            () -> TieredWeapons.sword(1820, 10f, 5.5f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.2f));
    public static final DeferredHolder<Item, Item> ELECTRUM_LONGSWORD = REGISTRY.register("electrum_longsword",
            () -> TieredWeapons.sword(2020, 10f, 7f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.9f));
    public static final DeferredHolder<Item, Item> ELECTRUM_SAW_CLEAVER = REGISTRY.register("electrum_saw_cleaver",
            () -> TieredWeapons.sword(1920, 10f, 7.5f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> ELECTRUM_SAW_CLEAVER_UNFOLDED = REGISTRY.register(
            "electrum_saw_cleaver_unfolded",
            () -> TieredWeapons.sword(1920, 10f, 6.5f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.8f));
    public static final DeferredHolder<Item, Item> ELECTRUM_SCYTHE = REGISTRY.register("electrum_scythe",
            () -> TieredWeapons.sword(1920, 10f, 8f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -3.1f));
    public static final DeferredHolder<Item, Item> ELECTRUM_TWINBLADE = REGISTRY.register("electrum_twinblade",
            () -> TieredWeapons.sword(1920, 10f, 5f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -2.2f));
    public static final DeferredHolder<Item, Item> ELECTRUM_ZWEIHANDER = REGISTRY.register("electrum_zweihander",
            () -> TieredWeapons.sword(2040, 10f, 7f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ELECTRUM_INGOT.get())), 3, -3.1f));
    public static final DeferredHolder<Item, Item> ELECTRUM_BUSTER_SWORD = REGISTRY.register("electrum_buster_sword",
            () -> TieredWeapons.sword(2120, 10f, 11f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_ELECTRUM_INGOT.get())), 3, -3.4f));
    public static final DeferredHolder<Item, Item> ELECTRUM_GREATHAMMER = REGISTRY.register("electrum_greathammer",
            () -> TieredWeapons.pickaxe(2220, 10f, 13f, 4, 18,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_ELECTRUM_INGOT.get())), 3, -3.6f));
    public static final DeferredHolder<Item, Item> ELECTRUM_SHOTGUN = REGISTRY.register("electrum_shotgun",
            () -> TieredWeapons.simpleItem(1820, 18));
}
