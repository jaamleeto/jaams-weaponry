
package net.jaams.weaponry.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class FlintHammerItem extends PickaxeItem {
	private static final Tier TIER = new Tier() {
			public int getUses() {
				return 72;
			}

            public float getSpeed() {
                return 4f;
            }

            public float getAttackDamageBonus() {
                return 1f;
            }

			public TagKey<Block> getIncorrectBlocksForDrops() {
			return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
		}

            public int getEnchantmentValue() {
                return 5;
            }

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(Items.FLINT));
			}
		};

	public FlintHammerItem() {
		super(TIER, new Item.Properties().attributes(PickaxeItem.createAttributes(TIER, 3, -3f)));
	}

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
        ItemStack retval = itemstack.copyWithCount(1);
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
