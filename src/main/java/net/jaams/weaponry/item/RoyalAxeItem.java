
package net.jaams.weaponry.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.AxeItem;

public class RoyalAxeItem extends AxeItem {
	private static final Tier TIER = new Tier() {
			@Override
			public int getUses() {
				return 1600;
			}

			@Override
			public float getSpeed() {
				return 8f;
			}

			@Override
			public float getAttackDamageBonus() {
				return 10f;
			}

			@Override
			public TagKey<Block> getIncorrectBlocksForDrops() {
			return BlockTags.INCORRECT_FOR_IRON_TOOL;
		}

			@Override
			public int getEnchantmentValue() {
				return 16;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		};

	public RoyalAxeItem() {
		super(TIER, new Item.Properties().attributes(AxeItem.createAttributes(TIER, 1, -3.2f)));
	}
}
