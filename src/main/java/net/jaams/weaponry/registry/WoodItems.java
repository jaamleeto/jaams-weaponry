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

public class WoodItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> HEAVY_COMPRESSED_WOOD = REGISTRY.register("heavy_compressed_wood",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_COMPRESSED_WOOD = REGISTRY.register("double_compressed_wood",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WOODEN_DAGGER = REGISTRY.register("wooden_dagger",
            () -> TieredWeapons.sword(49, 12f, -3f, 0, 22,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -1.8f));
    public static final RegistryObject<Item> WOODEN_DAGGER_REVERSE = REGISTRY.register("wooden_dagger_reverse",
            () -> TieredWeapons.sword(49, 2f, -3.5f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -1.4f));
    public static final RegistryObject<Item> WOODEN_KNUCKLE = REGISTRY.register("wooden_knuckle",
            () -> TieredWeapons.sword(49, 2f, -2.5f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -1.6f));
    public static final RegistryObject<Item> WOODEN_CLEAVER = REGISTRY.register("wooden_cleaver",
            () -> TieredWeapons.sword(49, 2f, 0f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -3.2f));
    public static final RegistryObject<Item> WOODEN_RING = REGISTRY.register("wooden_ring",
            () -> TieredWeapons.sword(49, 2f, -2f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -1.8f));
    public static final RegistryObject<Item> WOODEN_KAMA = REGISTRY.register("wooden_kama",
            () -> TieredWeapons.sword(49, 2f, -1.5f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -2.2f));
    public static final RegistryObject<Item> WOODEN_CLAW = REGISTRY.register("wooden_claw",
            () -> TieredWeapons.sword(49, 2f, -2f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -2.0f));
    public static final RegistryObject<Item> WOODEN_MACHETE = REGISTRY.register("wooden_machete",
            () -> TieredWeapons.sword(32, 2f, -1.5f, 0, 22,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -2.5f));
    public static final RegistryObject<Item> WOODEN_KATAR = REGISTRY.register("wooden_katar",
            () -> TieredWeapons.sword(59, 2f, 0f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -2.6f));
    public static final RegistryObject<Item> WOODEN_SICKLE = REGISTRY.register("wooden_sickle",
            () -> TieredWeapons.sword(59, 2f, -1f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -2.2f));
    public static final RegistryObject<Item> WOODEN_SPEAR = REGISTRY.register("wooden_spear",
            () -> TieredWeapons.sword(49, 2f, -1f, 0, 15,
                    Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks"))), 3, -2.6f));
    public static final RegistryObject<Item> WOODEN_HAMMER = REGISTRY.register("wooden_hammer",
            () -> TieredWeapons.pickaxe(99, 2f, 4f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -3.4f));
    public static final RegistryObject<Item> WOODEN_BATTLE_AXE = REGISTRY.register("wooden_battle_axe",
            () -> TieredWeapons.axe(59, 2f, 7f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 1f, -3.3f));
    public static final RegistryObject<Item> WOODEN_LONGSWORD = REGISTRY.register("wooden_longsword",
            () -> TieredWeapons.sword(69, 2f, 2f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.9f));
    public static final RegistryObject<Item> WOODEN_ZWEIHANDER = REGISTRY.register("wooden_zweihander",
            () -> TieredWeapons.sword(74, 2f, 2f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -3.1f));
    public static final RegistryObject<Item> WOODEN_GREATSWORD = REGISTRY.register("wooden_greatsword",
            () -> TieredWeapons.sword(69, 2f, 4f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -3.2f));
    public static final RegistryObject<Item> WOODEN_BROADSWORD = REGISTRY.register("wooden_broadsword",
            () -> TieredWeapons.sword(79, 2f, 2f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.8f));
    public static final RegistryObject<Item> WOODEN_KATANA = REGISTRY.register("wooden_katana",
            () -> TieredWeapons.sword(49, 2f, 0.5f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.2f));
    public static final RegistryObject<Item> WOODEN_BUTTERFLY_SWORD = REGISTRY.register("wooden_butterfly_sword",
            () -> TieredWeapons.sword(59, 2f, 1f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.6f));
    public static final RegistryObject<Item> WOODEN_HOOK_SWORD = REGISTRY.register("wooden_hook_sword",
            () -> TieredWeapons.sword(59, 2f, 1.5f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.6f));
    public static final RegistryObject<Item> WOODEN_SCYTHE = REGISTRY.register("wooden_scythe",
            () -> TieredWeapons.sword(59, 2f, 3f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -3.1f));
    public static final RegistryObject<Item> WOODEN_TWINBLADE = REGISTRY.register("wooden_twinblade",
            () -> TieredWeapons.sword(59, 2f, -2f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.2f));
    public static final RegistryObject<Item> WOODEN_SAW_CLEAVER = REGISTRY.register("wooden_saw_cleaver",
            () -> TieredWeapons.sword(59, 2f, 2.5f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.6f));
    public static final RegistryObject<Item> WOODEN_SAW_CLEAVER_UNFOLDED = REGISTRY.register(
            "wooden_saw_cleaver_unfolded",
            () -> TieredWeapons.sword(59, 2f, 2.5f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.DOUBLE_COMPRESSED_WOOD.get())), 3, -2.8f));
    public static final RegistryObject<Item> WOODEN_BUSTER_SWORD = REGISTRY.register("wooden_buster_sword",
            () -> TieredWeapons.sword(89, 2f, 6f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_COMPRESSED_WOOD.get())), 3, -3.4f));
    public static final RegistryObject<Item> WOODEN_GREATHAMMER = REGISTRY.register("wooden_greathammer",
            () -> TieredWeapons.pickaxe(119, 2f, 8f, 0, 15,
                    Ingredient.of(new ItemStack(ModItems.HEAVY_COMPRESSED_WOOD.get())), 3, -3.6f));
    public static final RegistryObject<Item> WOODEN_SLINGSHOT = REGISTRY.register("wooden_slingshot",
            () -> TieredWeapons.simpleItem(49, 15));
}
