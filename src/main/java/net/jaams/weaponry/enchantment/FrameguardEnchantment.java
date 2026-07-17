
package net.jaams.weaponry.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;

public class FrameguardEnchantment extends Enchantment {
	public FrameguardEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, slots);
	}

	@Override
	public int getMaxLevel() {
		return EnchantmentsConfig.FRAMEGUARD_MAX_LEVEL.get();
	}

	@Override
	public int getMinCost(int level) {
		return EnchantmentsConfig.FRAMEGUARD_MIN_COST.get() + (level - 1) * EnchantmentsConfig.FRAMEGUARD_COST_PER_LEVEL.get();
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + EnchantmentsConfig.FRAMEGUARD_COST_RANGE.get();
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return EnchantmentsConfig.FRAMEGUARD.get() && stack.is(ModTags.ENCHANTABLE_FRAMEGUARD);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return EnchantmentsConfig.FRAMEGUARD.get() && stack.is(ModTags.ENCHANTABLE_FRAMEGUARD);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return EnchantmentsConfig.FRAMEGUARD.get();
	}

	@Override
	public boolean isTreasureOnly() {
		return EnchantmentsConfig.FRAMEGUARD_IS_TREASURE.get();
	}
}
