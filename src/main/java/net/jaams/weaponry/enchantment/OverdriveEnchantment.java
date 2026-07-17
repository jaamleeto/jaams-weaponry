
package net.jaams.weaponry.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.weaponry.configuration.common.EnchantmentsConfig;

public class OverdriveEnchantment extends Enchantment {
	public OverdriveEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEAPON, slots);
	}

	@Override
	public int getMaxLevel() {
		return EnchantmentsConfig.OVERDRIVE_MAX_LEVEL.get();
	}

	@Override
	public int getMinCost(int level) {
		return EnchantmentsConfig.OVERDRIVE_MIN_COST.get() + (level - 1) * EnchantmentsConfig.OVERDRIVE_COST_PER_LEVEL.get();
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + EnchantmentsConfig.OVERDRIVE_COST_RANGE.get();
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return EnchantmentsConfig.OVERDRIVE.get() && super.canApplyAtEnchantingTable(stack);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return EnchantmentsConfig.OVERDRIVE.get();
	}

	@Override
	public boolean isTreasureOnly() {
		return EnchantmentsConfig.OVERDRIVE_IS_TREASURE.get();
	}

	public static int getDurabilityCost(int level) {
		if (!EnchantmentsConfig.OVERDRIVE.get()) {
			return 0;
		}
		return EnchantmentsConfig.OVERDRIVE_DURABILITY_COST_PER_LEVEL.get() * level;
	}
}
