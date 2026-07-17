package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.recipe.DyeItemRecipe;
import net.jaams.weaponry.recipe.QuickCraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, JaamsWeaponryMod.MODID);
    public static final RegistryObject<SimpleCraftingRecipeSerializer<DyeItemRecipe>> DYEABLE_ITEM = REGISTRY.register("weaponry_dyeable", () -> new SimpleCraftingRecipeSerializer<>(DyeItemRecipe::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<QuickCraftingRecipe>> QUICK_CRAFTING = REGISTRY.register("quick_crafting", () -> new SimpleCraftingRecipeSerializer<>(QuickCraftingRecipe::new));
}
