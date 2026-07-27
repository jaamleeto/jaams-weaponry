package net.jaams.weaponry.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

public class TessenItem extends SwordItem {
	private static final Tier TIER = new Tier() {
            public int getUses() {
                return 90;
            }

            public float getSpeed() {
                return 2f;
            }

            public float getAttackDamageBonus() {
                return -1f;
            }

            public TagKey<Block> getIncorrectBlocksForDrops() {
			return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
		}

            public int getEnchantmentValue() {
                return 15;
            }

            public Ingredient getRepairIngredient() {
                return Ingredient.of(ItemTags.create(ResourceLocation.parse("minecraft:planks")));
            }
        };

    public TessenItem() {
        super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -1.8f)));
    }
}
