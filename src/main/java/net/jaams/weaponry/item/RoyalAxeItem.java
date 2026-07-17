
package net.jaams.weaponry.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.AxeItem;

public class RoyalAxeItem extends AxeItem {
	public RoyalAxeItem() {
		super(new Tier() {
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
			public int getLevel() {
				return 2;
			}

			@Override
			public int getEnchantmentValue() {
				return 16;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 1, -3.2f, new Item.Properties());
	}
}
