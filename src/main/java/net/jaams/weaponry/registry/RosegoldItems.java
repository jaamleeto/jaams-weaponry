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

public class RosegoldItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> HEAVY_ROSEGOLD_INGOT = REGISTRY.register("heavy_rosegold_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DOUBLE_ROSEGOLD_INGOT = REGISTRY.register("double_rosegold_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ROSEGOLD_DAGGER = REGISTRY.register("rosegold_dagger",
            () -> TieredWeapons.sword(1461, 8, 0f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -1.8f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_DAGGER_REVERSE = REGISTRY.register("rosegold_dagger_reverse",
            () -> TieredWeapons.sword(1461, 8, -0.5f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -1.4f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_KNUCKLE = REGISTRY.register("rosegold_knuckle",
            () -> TieredWeapons.sword(1461, 8, 0.5f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -1.6f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_CLEAVER = REGISTRY.register("rosegold_cleaver",
            () -> TieredWeapons.sword(1461, 8, 3f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -3.2f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_RING = REGISTRY.register("rosegold_ring",
            () -> TieredWeapons.sword(1461, 8, 1f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -1.8f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_KAMA = REGISTRY.register("rosegold_kama",
            () -> TieredWeapons.sword(1461, 8, 1.5f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -2.2f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_CLAW = REGISTRY.register("rosegold_claw",
            () -> TieredWeapons.sword(1461, 8, 1f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -2.0f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_MACHETE = REGISTRY.register("rosegold_machete",
            () -> TieredWeapons.sword(1561, 8, 2.5f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -2.5f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_KATAR = REGISTRY.register("rosegold_katar",
            () -> TieredWeapons.sword(1561, 8, 3f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -2.6f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_SICKLE = REGISTRY.register("rosegold_sickle",
            () -> TieredWeapons.sword(1561, 8, 2f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -2.2f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_SPEAR = REGISTRY.register("rosegold_spear",
            () -> TieredWeapons.sword(1461, 8, 2f, 3, 14,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("jaams_weaponry:ingredient/rosegold_ingot"))), 3,
                    -2.6f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_BATTLE_AXE = REGISTRY.register("rosegold_battle_axe",
            () -> TieredWeapons.axe(1561, 8, 10f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 1f, -3.3f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_BROADSWORD = REGISTRY.register("rosegold_broadsword",
            () -> TieredWeapons.sword(1681, 8, 5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.8f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_BUTTERFLY_SWORD = REGISTRY.register("rosegold_butterfly_sword",
            () -> TieredWeapons.sword(1561, 8, 4f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_GREATSWORD = REGISTRY.register("rosegold_greatsword",
            () -> TieredWeapons.sword(1661, 8, 7f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -3.2f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_HAMMER = REGISTRY.register("rosegold_hammer",
            () -> TieredWeapons.pickaxe(1761, 8, 7f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -3.4f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_HOOK_SWORD = REGISTRY.register("rosegold_hook_sword",
            () -> TieredWeapons.sword(1561, 8, 4.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_KATANA = REGISTRY.register("rosegold_katana",
            () -> TieredWeapons.sword(1461, 8, 3.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.2f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_LONGSWORD = REGISTRY.register("rosegold_longsword",
            () -> TieredWeapons.sword(1661, 8, 5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.9f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_SAW_CLEAVER = REGISTRY.register("rosegold_saw_cleaver",
            () -> TieredWeapons.sword(1561, 6, 5.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_SAW_CLEAVER_UNFOLDED = REGISTRY.register(
            "rosegold_saw_cleaver_unfolded",
            () -> TieredWeapons.sword(1561, 8, 4.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.8f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_SCYTHE = REGISTRY.register("rosegold_scythe",
            () -> TieredWeapons.sword(1561, 8, 6f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -3.1f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_TWINBLADE = REGISTRY.register("rosegold_twinblade",
            () -> TieredWeapons.sword(1561, 8, 2f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -2.2f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_ZWEIHANDER = REGISTRY.register("rosegold_zweihander",
            () -> TieredWeapons.sword(1681, 8, 5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ROSEGOLD_INGOT.get())), 3, -3.1f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_BUSTER_SWORD = REGISTRY.register("rosegold_buster_sword",
            () -> TieredWeapons.sword(1761, 8, 9f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_ROSEGOLD_INGOT.get())), 3, -3.4f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_GREATHAMMER = REGISTRY.register("rosegold_greathammer",
            () -> TieredWeapons.pickaxe(1861, 8, 11f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_ROSEGOLD_INGOT.get())), 3, -3.6f));
    public static final DeferredHolder<Item, Item> ROSEGOLD_PISTOL = REGISTRY.register("rosegold_pistol",
            () -> TieredWeapons.simpleItem(132, 22));
}
