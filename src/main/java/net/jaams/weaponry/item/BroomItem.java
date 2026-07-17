package net.jaams.weaponry.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class BroomItem extends SwordItem {
    public BroomItem() {
        super(new Tier() {
            public int getUses() {
                return 80;
            }

            public float getSpeed() {
                return 2f;
            }

            public float getAttackDamageBonus() {
                return -3f;
            }

            public int getLevel() {
                return 0;
            }

            public int getEnchantmentValue() {
                return 2;
            }

            public Ingredient getRepairIngredient() {
                return Ingredient.of(new ItemStack(Blocks.HAY_BLOCK));
            }
        }, 3, -2.2f, new Item.Properties());
    }
}
