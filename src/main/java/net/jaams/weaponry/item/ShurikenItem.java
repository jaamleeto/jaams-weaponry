
package net.jaams.weaponry.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.EquipmentSlot;

import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;

public class ShurikenItem extends Item {
	public ShurikenItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON).attributes(net.jaams.weaponry.util.ModAttributeHelper.mainhand(2, -2.4)));
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	
}
