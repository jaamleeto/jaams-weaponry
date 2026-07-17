package net.jaams.weaponry.recipe;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.init.ModRecipes;
import net.jaams.weaponry.registry.BottomItems;
import net.jaams.weaponry.registry.TopItems;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class QuickCraftingRecipe extends CustomRecipe {

    public QuickCraftingRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack toolStack = findQuickCraftingTool(container);
        if (toolStack.isEmpty()) {
            return false;
        }
        if (!TraitsConfig.QUICK_CRAFTING.get() || !ModTraits.isQuickCraftingItem(toolStack)) {
            return false;
        }
        Item requiredIngredient = getIngredientItem(toolStack);
        int requiredCount = getIngredientCount(toolStack);
        boolean hasTool = false;
        boolean hasIngredient = false;
        int slotsOccupied = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                slotsOccupied++;
                if (ModTraits.isQuickCraftingItem(stack) && !hasTool) {
                    hasTool = true;
                } else if (stack.is(requiredIngredient) && !hasIngredient) {
                    if (stack.getCount() >= requiredCount) {
                        hasIngredient = true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return slotsOccupied == 2 && hasTool && hasIngredient;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack toolStack = findQuickCraftingTool(container);
        if (!toolStack.isEmpty()) {
            Item resultItem = getResultItem(toolStack);
            int resultCount = getResultCount(toolStack);
            return new ItemStack(resultItem, resultCount);
        }
        return new ItemStack(TopItems.STAKE.get());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        ItemStack toolStack = findQuickCraftingTool(container);
        int requiredCount = toolStack.isEmpty() ? 1 : getIngredientCount(toolStack);
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (ModTraits.isQuickCraftingItem(stack)) {
                    ItemStack damagedTool = stack.copy();
                    damagedTool.setCount(1);
                    int damageCost = getDurabilityCost(stack);
                    if (damageCost > 0) {
                        damagedTool.setDamageValue(damagedTool.getDamageValue() + damageCost);
                    }
                    if (damagedTool.getDamageValue() >= damagedTool.getMaxDamage()) {
                        remainingItems.set(i, ItemStack.EMPTY);
                    } else {
                        remainingItems.set(i, damagedTool);
                    }
                } else {
                    int shrinkAmount = requiredCount - 1;
                    if (shrinkAmount > 0) {
                        stack.shrink(shrinkAmount);
                    }
                    if (stack.isEmpty() && stack.getItem().hasCraftingRemainingItem(stack)) {
                        remainingItems.set(i, stack.getItem().getCraftingRemainingItem(stack));
                    } else {
                        remainingItems.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
        return remainingItems;
    }

    private ItemStack findQuickCraftingTool(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && ModTraits.isQuickCraftingItem(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private Item getIngredientItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        String itemId;
        if (tag != null && tag.contains("QuickCraftingIngredient")) {
            itemId = tag.getString("QuickCraftingIngredient");
        } else {
            itemId = TraitModifierData.getQuickCrafting(stack)
                .map((entry) -> entry.ingredient)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_INGREDIENT.get());
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        return item != null ? item : BottomItems.SHORT_STICK.get();
    }

    private int getIngredientCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingIngredientCount")) {
            return Math.max(1, tag.getInt("QuickCraftingIngredientCount"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
            .map((entry) -> entry.ingredient_count)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_INGREDIENT_COUNT.get());
        return Math.max(1, value);
    }

    private Item getResultItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        String itemId;
        if (tag != null && tag.contains("QuickCraftingResult")) {
            itemId = tag.getString("QuickCraftingResult");
        } else {
            itemId = TraitModifierData.getQuickCrafting(stack)
                .map((entry) -> entry.result)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_RESULT.get());
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        return item != null ? item : TopItems.STAKE.get();
    }

    private int getResultCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingResultCount")) {
            return Math.max(1, tag.getInt("QuickCraftingResultCount"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
            .map((entry) -> entry.result_count)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_RESULT_COUNT.get());
        return Math.max(1, value);
    }

    private int getDurabilityCost(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingDurabilityCost")) {
            return Math.max(0, tag.getInt("QuickCraftingDurabilityCost"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
            .map((entry) -> entry.durability_cost)
            .filter(java.util.Objects::nonNull)
            .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_DURABILITY_COST.get());
        return Math.max(0, value);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.QUICK_CRAFTING.get();
    }
}
