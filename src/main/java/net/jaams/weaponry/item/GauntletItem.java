
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
		super(new Item.Properties().durability(220).rarity(Rarity.COMMON));
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
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 1d, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -2.4, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
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
