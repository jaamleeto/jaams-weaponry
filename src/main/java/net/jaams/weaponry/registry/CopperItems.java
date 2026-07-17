package net.jaams.weaponry.registry;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.jaams.weaponry.item.tiered.TieredWeapons;
import net.jaams.weaponry.JaamsWeaponryMod;

public class CopperItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS,
            JaamsWeaponryMod.MODID);

    public static final RegistryObject<Item> HEAVY_COPPER_INGOT = REGISTRY.register("heavy_copper_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DOUBLE_COPPER_INGOT = REGISTRY.register("double_copper_ingot",
            () -> new Item(new Item.Properties()));

    

    public static final RegistryObject<Item> COPPER_DAGGER = REGISTRY.register("copper_dagger",
            () -> TieredWeapons.sword(180, 12f, -2f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -1.8f));
    public static final RegistryObject<Item> COPPER_DAGGER_REVERSE = REGISTRY.register("copper_dagger_reverse",
            () -> TieredWeapons.sword(180, 4f, -2.5f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -1.4f));
    public static final RegistryObject<Item> COPPER_KNUCKLE = REGISTRY.register("copper_knuckle",
            () -> TieredWeapons.sword(190, 4f, -1.5f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -1.6f));
    public static final RegistryObject<Item> COPPER_CLEAVER = REGISTRY.register("copper_cleaver",
            () -> TieredWeapons.sword(190, 4f, 1f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -3.2f));
    public static final RegistryObject<Item> COPPER_RING = REGISTRY.register("copper_ring",
            () -> TieredWeapons.sword(190, 4f, -1f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -1.8f));
    public static final RegistryObject<Item> COPPER_KAMA = REGISTRY.register("copper_kama",
            () -> TieredWeapons.sword(190, 4f, -0.5f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -2.2f));
    public static final RegistryObject<Item> COPPER_CLAW = REGISTRY.register("copper_claw",
            () -> TieredWeapons.sword(190, 4f, -1f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -2.0f));
    public static final RegistryObject<Item> COPPER_SPEAR = REGISTRY.register("copper_spear",
            () -> TieredWeapons.sword(190, 4f, 0f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -2.6f));

    

    public static final RegistryObject<Item> COPPER_MACHETE = REGISTRY.register("copper_machete",
            () -> TieredWeapons.sword(200, 4f, 1.5f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -2.5f));
    public static final RegistryObject<Item> COPPER_KATAR = REGISTRY.register("copper_katar",
            () -> TieredWeapons.sword(200, 3f, 1f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -2.6f));
    public static final RegistryObject<Item> COPPER_SICKLE = REGISTRY.register("copper_sickle",
            () -> TieredWeapons.sword(200, 4f, 0f, 1, 14, Ingredient.of(Items.COPPER_INGOT), 3, -2.2f));

    

    public static final RegistryObject<Item> COPPER_KATANA = REGISTRY.register("copper_katana",
            () -> TieredWeapons.sword(190, 4f, 1.5f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.2f));
    public static final RegistryObject<Item> COPPER_BUTTERFLY_SWORD = REGISTRY.register("copper_butterfly_sword",
            () -> TieredWeapons.sword(200, 4f, 2f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.6f));
    public static final RegistryObject<Item> COPPER_HOOK_SWORD = REGISTRY.register("copper_hook_sword",
            () -> TieredWeapons.sword(200, 4f, 2.5f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.6f));
    public static final RegistryObject<Item> COPPER_SCYTHE = REGISTRY.register("copper_scythe",
            () -> TieredWeapons.sword(200, 4f, 4f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -3.1f));
    public static final RegistryObject<Item> COPPER_TWINBLADE = REGISTRY.register("copper_twinblade",
            () -> TieredWeapons.sword(200, 4f, -2f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.2f));
    public static final RegistryObject<Item> COPPER_SAW_CLEAVER = REGISTRY.register("copper_saw_cleaver",
            () -> TieredWeapons.sword(200, 4f, 3.5f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.6f));
    public static final RegistryObject<Item> COPPER_SAW_CLEAVER_UNFOLDED = REGISTRY.register(
            "copper_saw_cleaver_unfolded",
            () -> TieredWeapons.sword(200, 4f, 2.5f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.8f));
    public static final RegistryObject<Item> COPPER_BATTLE_AXE = REGISTRY.register("copper_battle_axe",
            () -> TieredWeapons.axe(200, 4f, 8f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 1f, -3.3f));

    

    public static final RegistryObject<Item> COPPER_LONGSWORD = REGISTRY.register("copper_longsword",
            () -> TieredWeapons.sword(220, 4f, 3f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.9f));
    public static final RegistryObject<Item> COPPER_ZWEIHANDER = REGISTRY.register("copper_zweihander",
            () -> TieredWeapons.sword(230, 4f, 3f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -3.1f));
    public static final RegistryObject<Item> COPPER_GREATSWORD = REGISTRY.register("copper_greatsword",
            () -> TieredWeapons.sword(240, 4f, 5f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -3.2f));
    public static final RegistryObject<Item> COPPER_BROADSWORD = REGISTRY.register("copper_broadsword",
            () -> TieredWeapons.sword(250, 4f, 3f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -2.8f));

    

    public static final RegistryObject<Item> COPPER_HAMMER = REGISTRY.register("copper_hammer",
            () -> TieredWeapons.pickaxe(360, 4f, 5f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.DOUBLE_COPPER_INGOT.get())), 3, -3.4f));
    public static final RegistryObject<Item> COPPER_BUSTER_SWORD = REGISTRY.register("copper_buster_sword",
            () -> TieredWeapons.sword(370, 4f, 7f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.HEAVY_COPPER_INGOT.get())), 3, -3.4f));
    public static final RegistryObject<Item> COPPER_GREATHAMMER = REGISTRY.register("copper_greathammer",
            () -> TieredWeapons.pickaxe(480, 4f, 9f, 1, 14,
                    Ingredient.of(new ItemStack(CopperItems.HEAVY_COPPER_INGOT.get())), 3, -3.6f));

    

    public static final RegistryObject<Item> COPPER_PISTOL = REGISTRY.register("copper_pistol",
            () -> TieredWeapons.simpleItem(290, 14));
}
