
package net.jaams.weaponry.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;

public class GhostClipEnchantment extends Enchantment {
	public GhostClipEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, slots);
	}

	@Override
	public int getMaxLevel() {
		return EnchantmentsConfig.GHOST_CLIP_MAX_LEVEL.get();
	}

	@Override
	public int getMinCost(int level) {
		return EnchantmentsConfig.GHOST_CLIP_MIN_COST.get() + (level - 1) * EnchantmentsConfig.GHOST_CLIP_COST_PER_LEVEL.get();
	}

	@Override
	public int getMaxCost(int level) {
		return getMinCost(level) + EnchantmentsConfig.GHOST_CLIP_COST_RANGE.get();
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return EnchantmentsConfig.GHOST_CLIP.get() && stack.is(ModTags.ENCHANTABLE_GHOST_CLIP);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return EnchantmentsConfig.GHOST_CLIP.get() && stack.is(ModTags.ENCHANTABLE_GHOST_CLIP);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return EnchantmentsConfig.GHOST_CLIP.get();
	}

	@Override
	public boolean isTreasureOnly() {
		return EnchantmentsConfig.GHOST_CLIP_IS_TREASURE.get();
	}
}
