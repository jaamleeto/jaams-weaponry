
package net.jaams.weaponry.item;

import net.jaams.weaponry.util.ModUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.weaponry.dyeable.IDyeableItem;

import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;

public class GauntletItem extends Item implements IDyeableItem {
	public GauntletItem() {
		super(new Item.Properties().durability(220).rarity(Rarity.COMMON).attributes(net.jaams.weaponry.util.ModAttributeHelper.mainhand(1, -2.4)));
	}

	@Override
	public int getDefaultColor() {
		return 0xFFA06540;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	

@Override
public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
	boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
	if (entity == null || sourceentity == null)
		return false;
	ModUtils.playSound(entity, "jaams_weaponry:gauntlet_hit");
	return retval;
}
}
