
package net.jaams.weaponry.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class FlintHammerItem extends PickaxeItem {
    public FlintHammerItem() {
        super(new Tier() {
            public int getUses() {
                return 72;
            }

            public float getSpeed() {
                return 4f;
            }

            public float getAttackDamageBonus() {
                return 1f;
            }

            public int getLevel() {
                return 0;
            }

            public int getEnchantmentValue() {
                return 5;
            }

            public Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(Items.FLINT));
            }
        }, 3, -3f, new Item.Properties());
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
        ItemStack retval = new ItemStack(this);
        retval.setDamageValue(itemstack.getDamageValue() + 1);
        if (retval.getDamageValue() >= retval.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        return retval;
    }

    @Override
    public boolean isRepairable(ItemStack itemstack) {
        return false;
    }
}
