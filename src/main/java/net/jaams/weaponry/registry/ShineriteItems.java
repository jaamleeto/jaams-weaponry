package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.item.tiered.TieredWeapons;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.JaamsWeaponryMod;

public class ShineriteItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> HEAVY_SHINERITE_INGOT = REGISTRY.register("heavy_shinerite_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_SHINERITE_INGOT = REGISTRY.register("double_shinerite_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SHINERITE_DAGGER = REGISTRY.register("shinerite_dagger",
            () -> TieredWeapons.shineriteSword(1461, 8f, 0f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -1.8f));
    public static final RegistryObject<Item> SHINERITE_DAGGER_REVERSE = REGISTRY.register("shinerite_dagger_reverse",
            () -> TieredWeapons.shineriteSword(1461, 8f, -0.5f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -1.4f));
    public static final RegistryObject<Item> SHINERITE_KNUCKLE = REGISTRY.register("shinerite_knuckle",
            () -> TieredWeapons.shineriteSword(1461, 8f, 0.5f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -1.6f));
    public static final RegistryObject<Item> SHINERITE_CLEAVER = REGISTRY.register("shinerite_cleaver",
            () -> TieredWeapons.shineriteSword(1461, 8f, 3f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -3.2f));
    public static final RegistryObject<Item> SHINERITE_RING = REGISTRY.register("shinerite_ring",
            () -> TieredWeapons.shineriteSword(1461, 8f, 1f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -1.8f));
    public static final RegistryObject<Item> SHINERITE_KAMA = REGISTRY.register("shinerite_kama",
            () -> TieredWeapons.shineriteSword(1461, 8f, 1.5f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -2.2f));
    public static final RegistryObject<Item> SHINERITE_CLAW = REGISTRY.register("shinerite_claw",
            () -> TieredWeapons.shineriteSword(1461, 8f, 1f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -2.0f));
    public static final RegistryObject<Item> SHINERITE_MACHETE = REGISTRY.register("shinerite_machete",
            () -> TieredWeapons.shineriteSword(1561, 8f, 2.5f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -2.5f));
    public static final RegistryObject<Item> SHINERITE_KATAR = REGISTRY.register("shinerite_katar",
            () -> TieredWeapons.shineriteSword(1561, 8f, 3f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -2.6f));
    public static final RegistryObject<Item> SHINERITE_SICKLE = REGISTRY.register("shinerite_sickle",
            () -> TieredWeapons.shineriteSword(1561, 8f, 2f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -2.2f));
    public static final RegistryObject<Item> SHINERITE_SPEAR = REGISTRY.register("shinerite_spear",
            () -> TieredWeapons.shineriteSword(1461, 8f, 2f, 3, 14,
                    Ingredient.of(ItemTags.create(new ResourceLocation("jaams_weaponry:ingredient/shinerite_ingot"))),
                    3, -2.6f));
    public static final RegistryObject<Item> SHINERITE_BATTLE_AXE = REGISTRY.register("shinerite_battle_axe",
            () -> TieredWeapons.shineriteAxe(1561, 8f, 10f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 1f, -3.3f));
    public static final RegistryObject<Item> SHINERITE_BROADSWORD = REGISTRY.register("shinerite_broadsword",
            () -> TieredWeapons.shineriteSword(1681, 8f, 5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.8f));
    public static final RegistryObject<Item> SHINERITE_BUTTERFLY_SWORD = REGISTRY.register("shinerite_butterfly_sword",
            () -> TieredWeapons.shineriteSword(1561, 8f, 4f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.6f));
    public static final RegistryObject<Item> SHINERITE_GREATSWORD = REGISTRY.register("shinerite_greatsword",
            () -> TieredWeapons.shineriteSword(1661, 8f, 7f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -3.2f));
    public static final RegistryObject<Item> SHINERITE_HAMMER = REGISTRY.register("shinerite_hammer",
            () -> TieredWeapons.shineritePickaxe(1761, 8f, 7f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -3.4f));
    public static final RegistryObject<Item> SHINERITE_HOOK_SWORD = REGISTRY.register("shinerite_hook_sword",
            () -> TieredWeapons.shineriteSword(1561, 8f, 4.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.6f));
    public static final RegistryObject<Item> SHINERITE_KATANA = REGISTRY.register("shinerite_katana",
            () -> TieredWeapons.shineriteSword(1461, 8f, 3.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.2f));
    public static final RegistryObject<Item> SHINERITE_LONGSWORD = REGISTRY.register("shinerite_longsword",
            () -> TieredWeapons.shineriteSword(1661, 8f, 5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.9f));
    public static final RegistryObject<Item> SHINERITE_SAW_CLEAVER = REGISTRY.register("shinerite_saw_cleaver",
            () -> TieredWeapons.shineriteSword(1561, 6f, 5.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.6f));
    public static final RegistryObject<Item> SHINERITE_SAW_CLEAVER_UNFOLDED = REGISTRY.register(
            "shinerite_saw_cleaver_unfolded",
            () -> TieredWeapons.shineriteSword(1561, 8f, 4.5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.8f));
    public static final RegistryObject<Item> SHINERITE_SCYTHE = REGISTRY.register("shinerite_scythe",
            () -> TieredWeapons.shineriteSword(1561, 8f, 6f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -3.1f));
    public static final RegistryObject<Item> SHINERITE_TWINBLADE = REGISTRY.register("shinerite_twinblade",
            () -> TieredWeapons.shineriteSword(1561, 8f, 2f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -2.2f));
    public static final RegistryObject<Item> SHINERITE_ZWEIHANDER = REGISTRY.register("shinerite_zweihander",
            () -> TieredWeapons.shineriteSword(1681, 8f, 5f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_SHINERITE_INGOT.get())), 3, -3.1f));
    public static final RegistryObject<Item> SHINERITE_BUSTER_SWORD = REGISTRY.register("shinerite_buster_sword",
            () -> TieredWeapons.shineriteSword(1761, 8f, 9f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_SHINERITE_INGOT.get())), 3, -3.4f));
    public static final RegistryObject<Item> SHINERITE_GREATHAMMER = REGISTRY.register("shinerite_greathammer",
            () -> TieredWeapons.shineritePickaxe(1861, 8f, 11f, 3, 14,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_SHINERITE_INGOT.get())), 3, -3.6f));
    public static final RegistryObject<Item> SHINERITE_PISTOL = REGISTRY.register("shinerite_pistol",
            () -> TieredWeapons.shineriteSimpleItem(132, 22));
}
