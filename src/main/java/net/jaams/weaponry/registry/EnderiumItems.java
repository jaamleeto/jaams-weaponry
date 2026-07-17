package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.item.EnderiumShotgunItem;
import net.jaams.weaponry.item.tiered.TieredWeapons;
import net.minecraft.world.item.Rarity;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.JaamsWeaponryMod;

public class EnderiumItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> HEAVY_ENDERIUM_INGOT = REGISTRY.register("heavy_enderium_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DOUBLE_ENDERIUM_INGOT = REGISTRY.register("double_enderium_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> ENDERIUM_DAGGER = REGISTRY.register("enderium_dagger",
            () -> TieredWeapons.sword(2037, 9f, 3f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -1.8f, true));
    public static final RegistryObject<Item> ENDERIUM_DAGGER_REVERSE = REGISTRY.register("enderium_dagger_reverse",
            () -> TieredWeapons.sword(2037, 9f, 2.5f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -1.4f));
    public static final RegistryObject<Item> ENDERIUM_KNUCKLE = REGISTRY.register("enderium_knuckle",
            () -> TieredWeapons.sword(2037, 9f, 3.5f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -1.6f, true));
    public static final RegistryObject<Item> ENDERIUM_CLEAVER = REGISTRY.register("enderium_cleaver",
            () -> TieredWeapons.sword(2037, 9f, 6f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -3.2f, true));
    public static final RegistryObject<Item> ENDERIUM_RING = REGISTRY.register("enderium_ring",
            () -> TieredWeapons.sword(2037, 9f, 4f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -1.8f, true));
    public static final RegistryObject<Item> ENDERIUM_KAMA = REGISTRY.register("enderium_kama",
            () -> TieredWeapons.sword(2037, 9f, 4.5f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -2.2f, true));
    public static final RegistryObject<Item> ENDERIUM_CLAW = REGISTRY.register("enderium_claw",
            () -> TieredWeapons.sword(1931, 9f, 4f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -2.0f, true));
    public static final RegistryObject<Item> ENDERIUM_MACHETE = REGISTRY.register("enderium_machete",
            () -> TieredWeapons.sword(2137, 9f, 5.5f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -2.5f, true));
    public static final RegistryObject<Item> ENDERIUM_KATAR = REGISTRY.register("enderium_katar",
            () -> TieredWeapons.sword(2137, 9f, 6f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -2.6f, true));
    public static final RegistryObject<Item> ENDERIUM_SICKLE = REGISTRY.register("enderium_sickle",
            () -> TieredWeapons.sword(2037, 9f, 4.5f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -2.2f, true));
    public static final RegistryObject<Item> ENDERIUM_SPEAR = REGISTRY.register("enderium_spear",
            () -> TieredWeapons.sword(1931, 9f, 4.5f, 4, 15,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/enderium_ingot"))), 3,
                    -2.6f, true));
    public static final RegistryObject<Item> ENDERIUM_BATTLE_AXE = REGISTRY.register("enderium_battle_axe",
            () -> TieredWeapons.axe(2137, 9f, 13f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 1f, -3.3f, true));
    public static final RegistryObject<Item> ENDERIUM_BROADSWORD = REGISTRY.register("enderium_broadsword",
            () -> TieredWeapons.sword(2257, 9f, 8f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -2.8f, true));
    public static final RegistryObject<Item> ENDERIUM_BUTTERFLY_SWORD = REGISTRY.register("enderium_butterfly_sword",
            () -> TieredWeapons.sword(2137, 9f, 7f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -2.6f, true));
    public static final RegistryObject<Item> ENDERIUM_GREATSWORD = REGISTRY.register("enderium_greatsword",
            () -> TieredWeapons.sword(2237, 9f, 10f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -3.2f, true));
    public static final RegistryObject<Item> ENDERIUM_HAMMER = REGISTRY.register("enderium_hammer",
            () -> TieredWeapons.pickaxe(2337, 9f, 10f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -3.4f, true));
    public static final RegistryObject<Item> ENDERIUM_HOOK_SWORD = REGISTRY.register("enderium_hook_sword",
            () -> TieredWeapons.sword(2137, 9f, 7.5f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -2.6f, true));
    public static final RegistryObject<Item> ENDERIUM_KATANA = REGISTRY.register("enderium_katana",
            () -> TieredWeapons.sword(2037, 9f, 6.5f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -2.2f, true));
    public static final RegistryObject<Item> ENDERIUM_LONGSWORD = REGISTRY.register("enderium_longsword",
            () -> TieredWeapons.sword(2237, 9f, 8f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -2.9f, true));
    public static final RegistryObject<Item> ENDERIUM_SAW_CLEAVER = REGISTRY.register("enderium_saw_cleaver",
            () -> TieredWeapons.sword(2137, 9f, 8.5f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -2.6f, true));
    public static final RegistryObject<Item> ENDERIUM_SCYTHE = REGISTRY.register("enderium_scythe",
            () -> TieredWeapons.sword(2137, 9f, 9f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -3.1f, true));
    public static final RegistryObject<Item> ENDERIUM_TWINBLADE = REGISTRY.register("enderium_twinblade",
            () -> TieredWeapons.sword(2137, 9f, 6f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -2.2f, true));
    public static final RegistryObject<Item> ENDERIUM_ZWEIHANDER = REGISTRY.register("enderium_zweihander",
            () -> TieredWeapons.sword(2237, 9f, 8f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_ENDERIUM_INGOT.get())), 3, -3.1f, true));
    public static final RegistryObject<Item> ENDERIUM_BUSTER_SWORD = REGISTRY.register("enderium_buster_sword",
            () -> TieredWeapons.sword(2337, 9f, 12f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_ENDERIUM_INGOT.get())), 3, -3.4f, true));
    public static final RegistryObject<Item> ENDERIUM_GREATHAMMER = REGISTRY.register("enderium_greathammer",
            () -> TieredWeapons.pickaxe(2437, 9f, 14f, 4, 15,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_ENDERIUM_INGOT.get())), 3, -3.6f, true));
    public static final RegistryObject<Item> ENDERIUM_SHOTGUN = REGISTRY.register("enderium_shotgun",
            () -> new EnderiumShotgunItem(new Item.Properties().durability(1661)));
}
