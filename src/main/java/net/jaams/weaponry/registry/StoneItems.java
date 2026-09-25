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

public class StoneItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> HEAVY_COMPRESSED_STONE = REGISTRY.register("heavy_compressed_stone",
            () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DOUBLE_COMPRESSED_STONE = REGISTRY.register("double_compressed_stone",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> STONE_DAGGER = REGISTRY.register("stone_dagger",
            () -> TieredWeapons.sword(121, 12f, -2f, 1, 22,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -1.8f));
    public static final DeferredHolder<Item, Item> STONE_DAGGER_REVERSE = REGISTRY.register("stone_dagger_reverse",
            () -> TieredWeapons.sword(121, 4f, -2.5f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -1.4f));
    public static final DeferredHolder<Item, Item> STONE_KNUCKLE = REGISTRY.register("stone_knuckle",
            () -> TieredWeapons.sword(121, 4f, -1.5f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -1.6f));
    public static final DeferredHolder<Item, Item> STONE_CLEAVER = REGISTRY.register("stone_cleaver",
            () -> TieredWeapons.sword(121, 4f, 1f, 1, 22,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -3.2f));
    public static final DeferredHolder<Item, Item> STONE_RING = REGISTRY.register("stone_ring",
            () -> TieredWeapons.sword(121, 4f, -1f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -1.8f));
    public static final DeferredHolder<Item, Item> STONE_KAMA = REGISTRY.register("stone_kama",
            () -> TieredWeapons.sword(121, 4f, -0.5f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -2.2f));
    public static final DeferredHolder<Item, Item> STONE_CLAW = REGISTRY.register("stone_claw",
            () -> TieredWeapons.sword(121, 4f, -1f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -2.0f));
    public static final DeferredHolder<Item, Item> STONE_MACHETE = REGISTRY.register("stone_machete",
            () -> TieredWeapons.sword(32, 4f, 1.5f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -2.5f));
    public static final DeferredHolder<Item, Item> STONE_KATAR = REGISTRY.register("stone_katar",
            () -> TieredWeapons.sword(131, 3f, 1f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -2.6f));
    public static final DeferredHolder<Item, Item> STONE_SICKLE = REGISTRY.register("stone_sickle",
            () -> TieredWeapons.sword(32, 4f, 0f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -2.2f));
    public static final DeferredHolder<Item, Item> STONE_SPEAR = REGISTRY.register("stone_spear",
            () -> TieredWeapons.sword(121, 4f, 0f, 1, 5,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:stone_tool_materials"))), 3, -2.6f));
    public static final DeferredHolder<Item, Item> STONE_HAMMER = REGISTRY.register("stone_hammer",
            () -> TieredWeapons.pickaxe(181, 4f, 5f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -3.4f));
    public static final DeferredHolder<Item, Item> STONE_BATTLE_AXE = REGISTRY.register("stone_battle_axe",
            () -> TieredWeapons.axe(131, 4f, 8f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 1f, -3.3f));
    public static final DeferredHolder<Item, Item> STONE_LONGSWORD = REGISTRY.register("stone_longsword",
            () -> TieredWeapons.sword(151, 4f, 3f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.9f));
    public static final DeferredHolder<Item, Item> STONE_ZWEIHANDER = REGISTRY.register("stone_zweihander",
            () -> TieredWeapons.sword(143, 4f, 3f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -3.1f));
    public static final DeferredHolder<Item, Item> STONE_GREATSWORD = REGISTRY.register("stone_greatsword",
            () -> TieredWeapons.sword(151, 4f, 5f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -3.2f));
    public static final DeferredHolder<Item, Item> STONE_BROADSWORD = REGISTRY.register("stone_broadsword",
            () -> TieredWeapons.sword(161, 4f, 3f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.8f));
    public static final DeferredHolder<Item, Item> STONE_KATANA = REGISTRY.register("stone_katana",
            () -> TieredWeapons.sword(121, 4f, 1.5f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.2f));
    public static final DeferredHolder<Item, Item> STONE_BUTTERFLY_SWORD = REGISTRY.register("stone_butterfly_sword",
            () -> TieredWeapons.sword(131, 4f, 2f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> STONE_HOOK_SWORD = REGISTRY.register("stone_hook_sword",
            () -> TieredWeapons.sword(131, 4f, 2.5f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> STONE_SCYTHE = REGISTRY.register("stone_scythe",
            () -> TieredWeapons.sword(131, 4f, 4f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -3.1f));
    public static final DeferredHolder<Item, Item> STONE_TWINBLADE = REGISTRY.register("stone_twinblade",
            () -> TieredWeapons.sword(131, 4f, -2f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.2f));
    public static final DeferredHolder<Item, Item> STONE_SAW_CLEAVER = REGISTRY.register("stone_saw_cleaver",
            () -> TieredWeapons.sword(131, 4f, 3.5f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.6f));
    public static final DeferredHolder<Item, Item> STONE_SAW_CLEAVER_UNFOLDED = REGISTRY.register(
            "stone_saw_cleaver_unfolded",
            () -> TieredWeapons.sword(131, 4f, 2.5f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_STONE.get())), 3, -2.8f));
    public static final DeferredHolder<Item, Item> STONE_BUSTER_SWORD = REGISTRY.register("stone_buster_sword",
            () -> TieredWeapons.sword(171, 4f, 7f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_COMPRESSED_STONE.get())), 3, -3.4f));
    public static final DeferredHolder<Item, Item> STONE_GREATHAMMER = REGISTRY.register("stone_greathammer",
            () -> TieredWeapons.pickaxe(201, 4f, 9f, 1, 5,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_COMPRESSED_STONE.get())), 3, -3.6f));
    public static final DeferredHolder<Item, Item> STONE_SLINGSHOT = REGISTRY.register("stone_slingshot",
            () -> TieredWeapons.simpleItem(121, 5));
}
