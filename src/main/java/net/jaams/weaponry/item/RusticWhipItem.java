
package net.jaams.weaponry.item;

import net.jaams.weaponry.util.ModUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RusticWhipItem extends SwordItem {
	public RusticWhipItem() {
		super(new Tier() {
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
			public int getLevel() {
				return 1;
			}

			@Override
			public int getEnchantmentValue() {
				return 2;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(Items.LEATHER);
			}
		}, 0, -2.2f, new Item.Properties());
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
