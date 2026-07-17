
package net.jaams.weaponry.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;

public class AftermathEnchantment extends Enchantment {
	public AftermathEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.COMMON, EnchantmentCategory.BOW, slots);
	}

	@Override
	public int getMaxLevel() {
		return EnchantmentsConfig.AFTERMATH_MAX_LEVEL.get();
	}

	@Override
	public int getMinCost(int level) {
		return EnchantmentsConfig.AFTERMATH_MIN_COST.get() + (level - 1) * EnchantmentsConfig.AFTERMATH_COST_PER_LEVEL.get();
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + EnchantmentsConfig.AFTERMATH_COST_RANGE.get();
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return EnchantmentsConfig.AFTERMATH.get() && stack.is(ModTags.ENCHANTABLE_AFTERMATH);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return EnchantmentsConfig.AFTERMATH.get() && stack.is(ModTags.ENCHANTABLE_AFTERMATH);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return EnchantmentsConfig.AFTERMATH.get();
	}

	@Override
	public boolean isTreasureOnly() {
		return EnchantmentsConfig.AFTERMATH_IS_TREASURE.get();
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	public static float getExtraDamage(int level) {
		if (!EnchantmentsConfig.AFTERMATH.get()) {
			return 0.0F;
		}
		return EnchantmentsConfig.AFTERMATH_EXTRA_DAMAGE_PER_LEVEL.get().floatValue() * level;
	}
}
