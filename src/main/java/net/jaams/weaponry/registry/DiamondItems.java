package net.jaams.weaponry.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.jaams.weaponry.item.tiered.TieredWeapons;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.JaamsWeaponryMod;

public class DiamondItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> HEAVY_DIAMOND = REGISTRY.register("heavy_diamond",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DOUBLE_DIAMOND = REGISTRY.register("double_diamond",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> DIAMOND_DAGGER = REGISTRY.register("diamond_dagger",
            () -> TieredWeapons.sword(1461, 8f, 0f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -1.8f));
    public static final DeferredHolder<Item, Item> DIAMOND_DAGGER_REVERSE = REGISTRY.register("diamond_dagger_reverse",
            () -> TieredWeapons.sword(1461, 8f, -0.5f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -1.4f));
    public static final DeferredHolder<Item, Item> DIAMOND_KNUCKLE = REGISTRY.register("diamond_knuckle",
            () -> TieredWeapons.sword(1461, 8f, 0.5f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -1.6f));
    public static final DeferredHolder<Item, Item> DIAMOND_CLEAVER = REGISTRY.register("diamond_cleaver",
            () -> TieredWeapons.sword(1461, 8f, 3f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -3.2f));
    public static final DeferredHolder<Item, Item> DIAMOND_RING = REGISTRY.register("diamond_ring",
            () -> TieredWeapons.sword(1461, 8f, 1f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -1.8f));
    public static final DeferredHolder<Item, Item> DIAMOND_KAMA = REGISTRY.register("diamond_kama",
            () -> TieredWeapons.sword(1461, 8f, 1.5f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -2.2f));
    public static final DeferredHolder<Item, Item> DIAMOND_CLAW = REGISTRY.register("diamond_claw",
            () -> TieredWeapons.sword(1461, 8f, 1f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -2.0f));
    public static final DeferredHolder<Item, Item> DIAMOND_MACHETE = REGISTRY.register("diamond_machete",
            () -> TieredWeapons.sword(1561, 8f, 2.5f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -2.5f));
    public static final DeferredHolder<Item, Item> DIAMOND_KATAR = REGISTRY.register("diamond_katar",
            () -> TieredWeapons.sword(1561, 8f, 3f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -2.6f));
    public static final DeferredHolder<Item, Item> DIAMOND_SICKLE = REGISTRY.register("diamond_sickle",
            () -> TieredWeapons.sword(1561, 8f, 2f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -2.2f));
    public static final DeferredHolder<Item, Item> DIAMOND_SPEAR = REGISTRY.register("diamond_spear",
            () -> TieredWeapons.sword(1461, 8f, 2f, 3, 10, Ingredient.of(new ItemStack(Items.DIAMOND)), 3, -2.6f));
    public static final DeferredHolder<Item, Item> DIAMOND_BATTLE_AXE = REGISTRY.register("diamond_battle_axe",
            () -> TieredWeapons.axe(1561, 8f, 10f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    1f, -3.3f));
    public static final DeferredHolder<Item, Item> DIAMOND_BROADSWORD = REGISTRY.register("diamond_broadsword",
            () -> TieredWeapons.sword(1681, 8f, 5f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    3, -2.8f));
    public static final DeferredHolder<Item, Item> DIAMOND_BUTTERFLY_SWORD = REGISTRY.register("diamond_butterfly_sword",
            () -> TieredWeapons.sword(1561, 8f, 4f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    3, -2.6f));
    public static final DeferredHolder<Item, Item> DIAMOND_GREATSWORD = REGISTRY.register("diamond_greatsword",
            () -> TieredWeapons.sword(1661, 8f, 7f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    3, -3.2f));
    public static final DeferredHolder<Item, Item> DIAMOND_HAMMER = REGISTRY.register("diamond_hammer",
            () -> TieredWeapons.pickaxe(1761, 8f, 7f, 3, 10,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())), 3, -3.4f));
    public static final DeferredHolder<Item, Item> DIAMOND_HOOK_SWORD = REGISTRY.register("diamond_hook_sword",
            () -> TieredWeapons.sword(1561, 8f, 4.5f, 3, 10,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> DIAMOND_KATANA = REGISTRY.register("diamond_katana",
            () -> TieredWeapons.sword(1461, 8f, 3.5f, 3, 10,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())), 3, -2.2f));
    public static final DeferredHolder<Item, Item> DIAMOND_LONGSWORD = REGISTRY.register("diamond_longsword",
            () -> TieredWeapons.sword(1661, 8f, 5f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    3, -2.9f));
    public static final DeferredHolder<Item, Item> DIAMOND_SAW_CLEAVER = REGISTRY.register("diamond_saw_cleaver",
            () -> TieredWeapons.sword(1561, 6f, 5.5f, 3, 10,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> DIAMOND_SAW_CLEAVER_UNFOLDED = REGISTRY.register(
            "diamond_saw_cleaver_unfolded",
            () -> TieredWeapons.sword(1561, 8f, 4.5f, 3, 10,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())), 3, -2.8f));
    public static final DeferredHolder<Item, Item> DIAMOND_SCYTHE = REGISTRY.register("diamond_scythe",
            () -> TieredWeapons.sword(1561, 8f, 6f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    3, -3.1f));
    public static final DeferredHolder<Item, Item> DIAMOND_TWINBLADE = REGISTRY.register("diamond_twinblade",
            () -> TieredWeapons.sword(1561, 8f, 2f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    3, -2.2f));
    public static final DeferredHolder<Item, Item> DIAMOND_ZWEIHANDER = REGISTRY.register("diamond_zweihander",
            () -> TieredWeapons.sword(1681, 8f, 5f, 3, 10, Ingredient.of(new ItemStack(ModItems.DOUBLE_DIAMOND.get())),
                    3, -3.1f));
    public static final DeferredHolder<Item, Item> DIAMOND_BUSTER_SWORD = REGISTRY.register("diamond_buster_sword",
            () -> TieredWeapons.sword(1761, 8f, 9f, 3, 10, Ingredient.of(new ItemStack(ModItems.HEAVY_DIAMOND.get())),
                    3, -3.4f));
    public static final DeferredHolder<Item, Item> DIAMOND_GREATHAMMER = REGISTRY.register("diamond_greathammer",
            () -> TieredWeapons.pickaxe(1861, 8f, 11f, 3, 10,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_DIAMOND.get())), 3, -3.6f));
    public static final DeferredHolder<Item, Item> DIAMOND_SHOTGUN = REGISTRY.register("diamond_shotgun",
            () -> TieredWeapons.simpleItem(1661, 10));
}
