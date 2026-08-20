package net.jaams.weaponry.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import net.jaams.weaponry.data.RangedItemData;
import net.jaams.weaponry.data.ThrowableTypeData;

public class ModCompats {
	public static boolean isSmokeBomb(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		if (stack.is(ModTags.SMOKE_BOMBS))
			return true;
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean("ForceSmokeBomb");
	}

	public static boolean isSlingshot(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		if (stack.is(ModTags.SLINGSHOTS))
			return true;
		if (RangedItemData.isSlingshot(stack))
			return true;
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean("ForceSlingshot");
	}

	public static boolean isAxeThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "AXE");
	}

	public static boolean isCleaverThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "CLEAVER");
	}

	public static boolean isRoyalAxeThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "ROYAL_AXE");
	}

	public static boolean isRoyalSpearThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "ROYAL_SPEAR");
	}

	public static boolean isGiantShurikenThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "GIANT_SHURIKEN");
	}

	public static boolean isShurikenThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "SHURIKEN");
	}

	public static boolean isKunaiThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "KUNAI");
	}

	public static boolean isProngedKunaiThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "PRONGED_KUNAI");
	}

	public static boolean isSharpStoneThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "SHARP_STONE");
	}

	public static boolean isSpearThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "SPEAR");
	}

	public static boolean isTridentThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "TRIDENT");
	}

	public static boolean isHuntersBoomerangThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "HUNTERS_BOOMERANG");
	}

	public static boolean isRingThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "RING");
	}

	public static boolean isBroomThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "BROOM");
	}

	public static boolean isDynamiteThrowable(ItemStack stack) {
		return ThrowableTypeData.isThrowableType(stack, "DYNAMITE");
	}

	public static boolean isThrowableActive(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		ThrowableTypeData type = ThrowableTypeData.getType(stack);
		if (type == null)
			return false;
		return ThrowableTypeData.isEnabled(type.name);
	}
}
