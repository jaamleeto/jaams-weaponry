
package net.jaams.weaponry.item;

import net.jaams.weaponry.util.ModUtils;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.EquipmentSlot;

import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;

public class StakeItem extends Item {
	public StakeItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON).attributes(ModUtils.mainhand(0.5, -2.4)));
	}

	
}
