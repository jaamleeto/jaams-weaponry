package net.jaams.weaponry.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

public class TessenItem extends SwordItem {
    public TessenItem() {
        super(new Tier() {
            public int getUses() {
                return 90;
            }

            public float getSpeed() {
                return 2f;
            }

            public float getAttackDamageBonus() {
                return -1f;
            }

            public int getLevel() {
                return 0;
            }

            public int getEnchantmentValue() {
                return 15;
            }

            public Ingredient getRepairIngredient() {
                return Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks")));
            }
        }, 3, -1.8f, new Item.Properties());
    }
}
