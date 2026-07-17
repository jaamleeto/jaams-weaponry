
package net.jaams.weaponry.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;

public class BackblastEnchantment extends Enchantment {
	public BackblastEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE, slots);
	}

	@Override
	public int getMaxLevel() {
		return EnchantmentsConfig.BACKBLAST_MAX_LEVEL.get();
	}

	@Override
	public int getMinCost(int level) {
		return EnchantmentsConfig.BACKBLAST_MIN_COST.get() + (level - 1) * EnchantmentsConfig.BACKBLAST_COST_PER_LEVEL.get();
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + EnchantmentsConfig.BACKBLAST_COST_RANGE.get();
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return EnchantmentsConfig.BACKBLAST.get() && stack.is(ModTags.GUNS);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return EnchantmentsConfig.BACKBLAST.get();
	}

	@Override
	public boolean isTreasureOnly() {
		return EnchantmentsConfig.BACKBLAST_IS_TREASURE.get();
	}
}
