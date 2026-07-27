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

public class GoldenItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, JaamsWeaponryMod.MODID);

	public static final DeferredHolder<Item, Item> HEAVY_GOLD_INGOT = REGISTRY.register("heavy_gold_ingot", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> DOUBLE_GOLD_INGOT = REGISTRY.register("double_gold_ingot", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> GOLDEN_DAGGER = REGISTRY.register("golden_dagger",
			() -> TieredWeapons.sword(22, 12, -3, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -1.8f));
	public static final DeferredHolder<Item, Item> GOLDEN_DAGGER_REVERSE = REGISTRY.register("golden_dagger_reverse",
			() -> TieredWeapons.sword(22, 12, -3.5f, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -1.4f));
	public static final DeferredHolder<Item, Item> GOLDEN_KNUCKLE = REGISTRY.register("golden_knuckle",
			() -> TieredWeapons.sword(22, 12, -2.5f, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -1.6f));
	public static final DeferredHolder<Item, Item> GOLDEN_CLEAVER = REGISTRY.register("golden_cleaver",
			() -> TieredWeapons.sword(22, 12, 0, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -3.2f));
	public static final DeferredHolder<Item, Item> GOLDEN_RING = REGISTRY.register("golden_ring",
			() -> TieredWeapons.sword(22, 12, -2, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -1.8f));
	public static final DeferredHolder<Item, Item> GOLDEN_KAMA = REGISTRY.register("golden_kama",
			() -> TieredWeapons.sword(22, 12, -1.5f, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -2.2f));
	public static final DeferredHolder<Item, Item> GOLDEN_CLAW = REGISTRY.register("golden_claw",
			() -> TieredWeapons.sword(22, 12, -2, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -2.0f));
	public static final DeferredHolder<Item, Item> GOLDEN_MACHETE = REGISTRY.register("golden_machete",
			() -> TieredWeapons.sword(32, 12, -0.5f, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -2.5f));
	public static final DeferredHolder<Item, Item> GOLDEN_KATAR = REGISTRY.register("golden_katar",
			() -> TieredWeapons.sword(32, 12, 0, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -2.6f));
	public static final DeferredHolder<Item, Item> GOLDEN_SICKLE = REGISTRY.register("golden_sickle",
			() -> TieredWeapons.sword(32, 12, -1, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -2.2f));
	public static final DeferredHolder<Item, Item> GOLDEN_SPEAR = REGISTRY.register("golden_spear",
			() -> TieredWeapons.sword(22, 12, -1, 0, 22, Ingredient.of(new ItemStack(Items.GOLD_INGOT)), 3, -2.6f));
	public static final DeferredHolder<Item, Item> GOLDEN_BROADSWORD = REGISTRY.register("golden_broadsword",
			() -> TieredWeapons.sword(52, 12, 2, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.8f));
	public static final DeferredHolder<Item, Item> GOLDEN_BUTTERFLY_SWORD = REGISTRY.register("golden_butterfly_sword",
			() -> TieredWeapons.sword(32, 12, 1, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.6f));
	public static final DeferredHolder<Item, Item> GOLDEN_GREATSWORD = REGISTRY.register("golden_greatsword",
			() -> TieredWeapons.sword(42, 12, 4, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -3.2f));
	public static final DeferredHolder<Item, Item> GOLDEN_HOOK_SWORD = REGISTRY.register("golden_hook_sword",
			() -> TieredWeapons.sword(32, 12, 1.5f, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.6f));
	public static final DeferredHolder<Item, Item> GOLDEN_KATANA = REGISTRY.register("golden_katana",
			() -> TieredWeapons.sword(22, 12, 0.5f, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.2f));
	public static final DeferredHolder<Item, Item> GOLDEN_LONGSWORD = REGISTRY.register("golden_longsword",
			() -> TieredWeapons.sword(42, 12, 2, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.9f));
	public static final DeferredHolder<Item, Item> GOLDEN_SAW_CLEAVER = REGISTRY.register("golden_saw_cleaver",
			() -> TieredWeapons.sword(32, 12, 2.5f, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.6f));
	public static final DeferredHolder<Item, Item> GOLDEN_SAW_CLEAVER_UNFOLDED = REGISTRY.register("golden_saw_cleaver_unfolded",
			() -> TieredWeapons.sword(32, 12, 1.5f, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.8f));
	public static final DeferredHolder<Item, Item> GOLDEN_SCYTHE = REGISTRY.register("golden_scythe",
			() -> TieredWeapons.sword(32, 12, 3, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -3.1f));
	public static final DeferredHolder<Item, Item> GOLDEN_TWINBLADE = REGISTRY.register("golden_twinblade",
			() -> TieredWeapons.sword(32, 12, -2, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -2.2f));
	public static final DeferredHolder<Item, Item> GOLDEN_ZWEIHANDER = REGISTRY.register("golden_zweihander",
			() -> TieredWeapons.sword(48, 12, 2, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -3.1f));
	public static final DeferredHolder<Item, Item> GOLDEN_BUSTER_SWORD = REGISTRY.register("golden_buster_sword",
			() -> TieredWeapons.sword(62, 12, 6, 0, 22, Ingredient.of(new ItemStack(ModItems.HEAVY_GOLD_INGOT.get())), 3, -3.4f));
	public static final DeferredHolder<Item, Item> GOLDEN_HAMMER = REGISTRY.register("golden_hammer",
			() -> TieredWeapons.pickaxe(62, 12, 4, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 3, -3.4f));
	public static final DeferredHolder<Item, Item> GOLDEN_GREATHAMMER = REGISTRY.register("golden_greathammer",
			() -> TieredWeapons.pickaxe(82, 12, 8, 0, 22, Ingredient.of(new ItemStack(ModItems.HEAVY_GOLD_INGOT.get())), 3, -3.6f));
	public static final DeferredHolder<Item, Item> GOLDEN_BATTLE_AXE = REGISTRY.register("golden_battle_axe",
			() -> TieredWeapons.axe(32, 12, 7, 0, 22, Ingredient.of(new ItemStack(ModItems.DOUBLE_GOLD_INGOT.get())), 1f, -3.3f));
	public static final DeferredHolder<Item, Item> GOLDEN_PISTOL = REGISTRY.register("golden_pistol",
			() -> TieredWeapons.simpleItem(132, 22));
}
