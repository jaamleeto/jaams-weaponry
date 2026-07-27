package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.recipe.DyeItemRecipe;
import net.jaams.weaponry.recipe.QuickCraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, JaamsWeaponryMod.MODID);
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<DyeItemRecipe>> DYEABLE_ITEM = REGISTRY.register("weaponry_dyeable", () -> new SimpleCraftingRecipeSerializer<>(DyeItemRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<QuickCraftingRecipe>> QUICK_CRAFTING = REGISTRY.register("quick_crafting", () -> new SimpleCraftingRecipeSerializer<>(QuickCraftingRecipe::new));
}
