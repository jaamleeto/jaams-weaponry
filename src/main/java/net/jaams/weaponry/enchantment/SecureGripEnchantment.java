
package net.jaams.weaponry.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.weaponry.configuration.common.EnchantmentsConfig;

public class SecureGripEnchantment extends Enchantment {
	public SecureGripEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, slots);
	}

	@Override
	public int getMaxLevel() {
		return EnchantmentsConfig.SECURE_GRIP_MAX_LEVEL.get();
	}

	@Override
	public int getMinCost(int level) {
		return EnchantmentsConfig.SECURE_GRIP_MIN_COST.get() + (level - 1) * EnchantmentsConfig.SECURE_GRIP_COST_PER_LEVEL.get();
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + EnchantmentsConfig.SECURE_GRIP_COST_RANGE.get();
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return EnchantmentsConfig.SECURE_GRIP.get() && super.canApplyAtEnchantingTable(stack);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return EnchantmentsConfig.SECURE_GRIP.get();
	}

	@Override
	public boolean isTreasureOnly() {
		return EnchantmentsConfig.SECURE_GRIP_IS_TREASURE.get();
	}
}
