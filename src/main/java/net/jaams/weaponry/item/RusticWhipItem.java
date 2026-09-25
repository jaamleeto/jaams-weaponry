
package net.jaams.weaponry.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.jaams.weaponry.util.ModUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RusticWhipItem extends SwordItem {
	private static final Tier TIER = new Tier() {
			@Override
			public int getUses() {
				return 132;
			}

			@Override
			public float getSpeed() {
				return 4.0f;
			}

			@Override
			public float getAttackDamageBonus() {
				return -0.0f;
			}

			@Override
			public TagKey<Block> getIncorrectBlocksForDrops() {
			return BlockTags.INCORRECT_FOR_STONE_TOOL;
		}

			@Override
			public int getEnchantmentValue() {
				return 2;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(Items.LEATHER);
			}
		};

	public RusticWhipItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 0, -2.2f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		if (entity == null || sourceentity == null)
			return false;
		ModUtils.playSound(entity, "jaams_weaponry:whip_hit");
		return retval;
	}
}
