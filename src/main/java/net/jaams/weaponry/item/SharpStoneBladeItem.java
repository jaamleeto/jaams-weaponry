
package net.jaams.weaponry.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.jaams.weaponry.init.ModItems;

public class SharpStoneBladeItem extends SwordItem {
	private static final Tier TIER = new Tier() {
			public int getUses() {
				return 80;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return -2f;
			}

			public TagKey<Block> getIncorrectBlocksForDrops() {
			return BlockTags.INCORRECT_FOR_STONE_TOOL;
		}

			public int getEnchantmentValue() {
				return 5;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(ModItems.SHARP_STONE.get()));
			}
		};

	public SharpStoneBladeItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -1.6f)));
	}
}
