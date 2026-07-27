package net.jaams.weaponry.recipe;

import java.util.ArrayList;
import java.util.List;
import net.jaams.weaponry.dyeable.IDyeableItem;
import net.jaams.weaponry.init.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DyeItemRecipe extends CustomRecipe {

    public DyeItemRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        ItemStack item = ItemStack.EMPTY;
        List<ItemStack> dyes = new ArrayList<>();
        for (int i = 0; i < container.size(); i++) {
            ItemStack item$container = container.getItem(i);
            if (!item$container.isEmpty()) {
                if (item$container.getItem() instanceof IDyeableItem) {
                    if (!item.isEmpty()) return false;
                    item = item$container;
                } else {
                    if (!(item$container.getItem() instanceof DyeItem)) return false;
                    dyes.add(item$container);
                }
            }
        }
        return !item.isEmpty() && !dyes.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider access) {
        ItemStack item = ItemStack.EMPTY;
        List<DyeItem> dyes = new ArrayList<>();
        for (int i = 0; i < container.size(); i++) {
            ItemStack item$container = container.getItem(i);
            if (!item$container.isEmpty()) {
                if (item$container.getItem() instanceof IDyeableItem) {
                    if (!item.isEmpty()) return ItemStack.EMPTY;
                    item = item$container.copy();
                } else {
                    if (!(item$container.getItem() instanceof DyeItem)) return ItemStack.EMPTY;
                    dyes.add((DyeItem) item$container.getItem());
                }
            }
        }
        return !item.isEmpty() && !dyes.isEmpty() ? IDyeableItem.dye(item, dyes) : ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DYEABLE_ITEM.get();
    }
}
