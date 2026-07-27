
package net.jaams.weaponry.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;

public class EnderiumShotgunItem extends Item {
	public EnderiumShotgunItem(Properties properties) {
		super(properties.attributes(net.jaams.weaponry.util.ModAttributeHelper.mainhand(6, -2.4)));
	}

	@Override
	public int getEnchantmentValue() {
		return 10;
	}

	

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		itemstack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
		return true;
	}
}
