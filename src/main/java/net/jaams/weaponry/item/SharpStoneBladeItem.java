
package net.jaams.weaponry.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.jaams.weaponry.init.ModItems;

public class SharpStoneBladeItem extends SwordItem {
	public SharpStoneBladeItem() {
		super(new Tier() {
			public int getUses() {
				return 80;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return -2f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 5;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(ModItems.SHARP_STONE.get()));
			}
		}, 3, -1.6f, new Item.Properties());
	}
}
