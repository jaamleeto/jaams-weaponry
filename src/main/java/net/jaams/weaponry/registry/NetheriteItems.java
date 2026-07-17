package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.jaams.weaponry.item.NetheriteShotgunItem;
import net.jaams.weaponry.item.tiered.TieredWeapons;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.JaamsWeaponryMod;

public class NetheriteItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, JaamsWeaponryMod.MODID);

	public static final RegistryObject<Item> HEAVY_NETHERITE_INGOT = REGISTRY.register("heavy_netherite_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> DOUBLE_NETHERITE_INGOT = REGISTRY.register("double_netherite_ingot", () -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> NETHERITE_DAGGER = REGISTRY.register("netherite_dagger",
			() -> TieredWeapons.sword(1931, 9f, 1f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -1.8f, true));
	public static final RegistryObject<Item> NETHERITE_DAGGER_REVERSE = REGISTRY.register("netherite_dagger_reverse",
			() -> TieredWeapons.sword(1931, 9f, 0.5f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -1.4f));
	public static final RegistryObject<Item> NETHERITE_KNUCKLE = REGISTRY.register("netherite_knuckle",
			() -> TieredWeapons.sword(1931, 9f, 1.5f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -1.6f, true));
	public static final RegistryObject<Item> NETHERITE_CLEAVER = REGISTRY.register("netherite_cleaver",
			() -> TieredWeapons.sword(1931, 9f, 4f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -3.2f, true));
	public static final RegistryObject<Item> NETHERITE_RING = REGISTRY.register("netherite_ring",
			() -> TieredWeapons.sword(1931, 9f, 2f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -1.8f, true));
	public static final RegistryObject<Item> NETHERITE_KAMA = REGISTRY.register("netherite_kama",
			() -> TieredWeapons.sword(1931, 9f, 2.5f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -2.2f, true));
	public static final RegistryObject<Item> NETHERITE_CLAW = REGISTRY.register("netherite_claw",
			() -> TieredWeapons.sword(1931, 9f, 2f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -2.0f, true));
	public static final RegistryObject<Item> NETHERITE_MACHETE = REGISTRY.register("netherite_machete",
			() -> TieredWeapons.sword(2031, 9f, 3.5f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -2.5f, true));
	public static final RegistryObject<Item> NETHERITE_KATAR = REGISTRY.register("netherite_katar",
			() -> TieredWeapons.sword(2031, 9f, 4f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -2.6f, true));
	public static final RegistryObject<Item> NETHERITE_SICKLE = REGISTRY.register("netherite_sickle",
			() -> TieredWeapons.sword(2031, 9f, 3f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -2.2f, true));
	public static final RegistryObject<Item> NETHERITE_SPEAR = REGISTRY.register("netherite_spear",
			() -> TieredWeapons.sword(1931, 9f, 3f, 4, 15, Ingredient.of(new ItemStack(Items.NETHERITE_INGOT)), 3, -2.6f, true));
	public static final RegistryObject<Item> NETHERITE_BATTLE_AXE = REGISTRY.register("netherite_battle_axe",
			() -> TieredWeapons.axe(2031, 9f, 11f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 1f, -3.3f, true));
	public static final RegistryObject<Item> NETHERITE_BROADSWORD = REGISTRY.register("netherite_broadsword",
			() -> TieredWeapons.sword(2151, 9f, 6f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.8f, true));
	public static final RegistryObject<Item> NETHERITE_BUTTERFLY_SWORD = REGISTRY.register("netherite_butterfly_sword",
			() -> TieredWeapons.sword(2031, 9f, 5f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.6f, true));
	public static final RegistryObject<Item> NETHERITE_GREATSWORD = REGISTRY.register("netherite_greatsword",
			() -> TieredWeapons.sword(2131, 9f, 8f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -3.2f, true));
	public static final RegistryObject<Item> NETHERITE_HAMMER = REGISTRY.register("netherite_hammer",
			() -> TieredWeapons.pickaxe(2231, 9f, 8f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -3.4f, true));
	public static final RegistryObject<Item> NETHERITE_HOOK_SWORD = REGISTRY.register("netherite_hook_sword",
			() -> TieredWeapons.sword(2031, 9f, 5.5f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.6f, true));
	public static final RegistryObject<Item> NETHERITE_KATANA = REGISTRY.register("netherite_katana",
			() -> TieredWeapons.sword(1931, 9f, 4.5f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.2f, true));
	public static final RegistryObject<Item> NETHERITE_LONGSWORD = REGISTRY.register("netherite_longsword",
			() -> TieredWeapons.sword(2131, 9f, 6f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.9f, true));
	public static final RegistryObject<Item> NETHERITE_SAW_CLEAVER = REGISTRY.register("netherite_saw_cleaver",
			() -> TieredWeapons.sword(2031, 9f, 6.5f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.6f, true));
	public static final RegistryObject<Item> NETHERITE_SAW_CLEAVER_UNFOLDED = REGISTRY.register("netherite_saw_cleaver_unfolded",
			() -> TieredWeapons.sword(2031, 9f, 5.5f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.8f, true));
	public static final RegistryObject<Item> NETHERITE_SCYTHE = REGISTRY.register("netherite_scythe",
			() -> TieredWeapons.sword(2031, 9f, 7f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -3.1f, true));
	public static final RegistryObject<Item> NETHERITE_TWINBLADE = REGISTRY.register("netherite_twinblade",
			() -> TieredWeapons.sword(2031, 9f, 4f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -2.2f, true));
	public static final RegistryObject<Item> NETHERITE_ZWEIHANDER = REGISTRY.register("netherite_zweihander",
			() -> TieredWeapons.sword(2131, 9f, 6f, 4, 15, Ingredient.of(new ItemStack(ModItems.DOUBLE_NETHERITE_INGOT.get())), 3, -3.1f, true));
	public static final RegistryObject<Item> NETHERITE_BUSTER_SWORD = REGISTRY.register("netherite_buster_sword",
			() -> TieredWeapons.sword(2231, 9f, 10f, 4, 15, Ingredient.of(new ItemStack(ModItems.HEAVY_NETHERITE_INGOT.get())), 3, -3.4f, true));
	public static final RegistryObject<Item> NETHERITE_GREATHAMMER = REGISTRY.register("netherite_greathammer",
			() -> TieredWeapons.pickaxe(2331, 9f, 12f, 4, 15, Ingredient.of(new ItemStack(ModItems.HEAVY_NETHERITE_INGOT.get())), 3, -3.6f, true));
	public static final RegistryObject<Item> NETHERITE_SHOTGUN = REGISTRY.register("netherite_shotgun",
			() -> new NetheriteShotgunItem(new Item.Properties().durability(2131).fireResistant()));
}
