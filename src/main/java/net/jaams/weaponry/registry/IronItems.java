package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.jaams.weaponry.item.tiered.TieredWeapons;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.JaamsWeaponryMod;

public class IronItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, JaamsWeaponryMod.MODID);

	public static final RegistryObject<Item> HEAVY_IRON_INGOT = REGISTRY.register("heavy_iron_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> DOUBLE_IRON_INGOT = REGISTRY.register("double_iron_ingot", () -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> IRON_DAGGER = REGISTRY.register("iron_dagger",
			() -> TieredWeapons.sword(200, 6, -1, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -1.8f));
	public static final RegistryObject<Item> IRON_DAGGER_REVERSE = REGISTRY.register("iron_dagger_reverse",
			() -> TieredWeapons.sword(200, 6, -1.5f, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -1.4f));
	public static final RegistryObject<Item> IRON_KNUCKLE = REGISTRY.register("iron_knuckle",
			() -> TieredWeapons.sword(200, 6, -0.5f, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -1.6f));
	public static final RegistryObject<Item> IRON_CLEAVER = REGISTRY.register("iron_cleaver",
			() -> TieredWeapons.sword(200, 6, 2, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -3.2f));
	public static final RegistryObject<Item> IRON_RING = REGISTRY.register("iron_ring",
			() -> TieredWeapons.sword(200, 6, 0, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -1.8f));
	public static final RegistryObject<Item> IRON_KAMA = REGISTRY.register("iron_kama",
			() -> TieredWeapons.sword(200, 6, 0.5f, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -2.2f));
	public static final RegistryObject<Item> IRON_CLAW = REGISTRY.register("iron_claw",
			() -> TieredWeapons.sword(200, 6, 0, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -2.0f));
	public static final RegistryObject<Item> IRON_MACHETE = REGISTRY.register("iron_machete",
			() -> TieredWeapons.sword(250, 6, 1.5f, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -2.5f));
	public static final RegistryObject<Item> IRON_KATAR = REGISTRY.register("iron_katar",
			() -> TieredWeapons.sword(250, 6, 2, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -2.6f));
	public static final RegistryObject<Item> IRON_SICKLE = REGISTRY.register("iron_sickle",
			() -> TieredWeapons.sword(250, 6, 1, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -2.2f));
	public static final RegistryObject<Item> IRON_SPEAR = REGISTRY.register("iron_spear",
			() -> TieredWeapons.sword(200, 6, 1, 2, 14, Ingredient.of(new ItemStack(Items.IRON_INGOT)), 3, -2.6f));
	public static final RegistryObject<Item> IRON_LONGSWORD = REGISTRY.register("iron_longsword",
			() -> TieredWeapons.sword(300, 6, 4, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.9f));
	public static final RegistryObject<Item> IRON_ZWEIHANDER = REGISTRY.register("iron_zweihander",
			() -> TieredWeapons.sword(320, 6, 4, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -3.1f));
	public static final RegistryObject<Item> IRON_GREATSWORD = REGISTRY.register("iron_greatsword",
			() -> TieredWeapons.sword(350, 6, 6, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -3.2f));
	public static final RegistryObject<Item> IRON_BROADSWORD = REGISTRY.register("iron_broadsword",
			() -> TieredWeapons.sword(375, 6, 4, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.8f));
	public static final RegistryObject<Item> IRON_KATANA = REGISTRY.register("iron_katana",
			() -> TieredWeapons.sword(200, 6, 2.5f, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.2f));
	public static final RegistryObject<Item> IRON_BUTTERFLY_SWORD = REGISTRY.register("iron_butterfly_sword",
			() -> TieredWeapons.sword(250, 6, 3, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.6f));
	public static final RegistryObject<Item> IRON_HOOK_SWORD = REGISTRY.register("iron_hook_sword",
			() -> TieredWeapons.sword(250, 6, 3.5f, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.6f));
	public static final RegistryObject<Item> IRON_SCYTHE = REGISTRY.register("iron_scythe",
			() -> TieredWeapons.sword(250, 6, 5, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -3.1f));
	public static final RegistryObject<Item> IRON_TWINBLADE = REGISTRY.register("iron_twinblade",
			() -> TieredWeapons.sword(250, 6, 0, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.2f));
	public static final RegistryObject<Item> IRON_SAW_CLEAVER = REGISTRY.register("iron_saw_cleaver",
			() -> TieredWeapons.sword(250, 6, 4.5f, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.6f));
	public static final RegistryObject<Item> IRON_SAW_CLEAVER_UNFOLDED = REGISTRY.register("iron_saw_cleaver_unfolded",
			() -> TieredWeapons.sword(250, 6, 3.5f, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -2.8f));
	public static final RegistryObject<Item> IRON_BUSTER_SWORD = REGISTRY.register("iron_buster_sword",
			() -> TieredWeapons.sword(450, 6, 8, 2, 14, Ingredient.of(new ItemStack(ModItems.HEAVY_IRON_INGOT.get())), 3, -3.4f));
	public static final RegistryObject<Item> IRON_BATTLE_AXE = REGISTRY.register("iron_battle_axe",
			() -> TieredWeapons.axe(250, 6, 9, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 1f, -3.3f));
	public static final RegistryObject<Item> IRON_HAMMER = REGISTRY.register("iron_hammer",
			() -> TieredWeapons.pickaxe(450, 6, 6, 2, 14, Ingredient.of(new ItemStack(ModItems.DOUBLE_IRON_INGOT.get())), 3, -3.4f));
	public static final RegistryObject<Item> IRON_GREATHAMMER = REGISTRY.register("iron_greathammer",
			() -> TieredWeapons.pickaxe(550, 6, 12, 2, 14, Ingredient.of(new ItemStack(ModItems.HEAVY_IRON_INGOT.get())), 3, -3.6f));
	public static final RegistryObject<Item> IRON_SCATTERGUN = REGISTRY.register("iron_scattergun",
			() -> TieredWeapons.simpleItem(350, 14));
}
