package net.jaams.weaponry.mixins.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraftforge.common.extensions.IForgeItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.jaams.weaponry.inject.ItemInjection;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@Mixin(value = Item.class, remap = true)
public abstract class ForgeItemMixin implements IForgeItem, ItemInjection {

    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack,
            final boolean slotChanged) {
        if (ModCompats.isSlingshot(oldStack) || ModCompats.isSlingshot(newStack)) {
            return false;
        }
        if (ModTraits.isGuardStanceItem(oldStack) || ModTraits.isGuardStanceItem(newStack)) {
            return false;
        }
        if (ModTraits.isParryGuardItem(oldStack) || ModTraits.isParryGuardItem(newStack)) {
            return false;
        }
        return !oldStack.equals(newStack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (ModCompats.isSlingshot(stack)) {
            if (enchantment == Enchantments.POWER_ARROWS
                    || enchantment == Enchantments.PUNCH_ARROWS
                    || enchantment == Enchantments.FLAMING_ARROWS
                    || enchantment == Enchantments.PIERCING
                    || enchantment == Enchantments.MULTISHOT) {
                return true;
            }
        }

        if (ModCompats.isThrowableActive(stack)) {
            if (enchantment == Enchantments.PIERCING
                    || enchantment == Enchantments.LOYALTY
                    || enchantment == Enchantments.MULTISHOT) {
                return true;
            }
        }

        if (stack.is(ModTags.BATTLE_AXES)) {
            if (enchantment == Enchantments.SWEEPING_EDGE
                    || enchantment == Enchantments.FIRE_ASPECT
                    || enchantment == Enchantments.BANE_OF_ARTHROPODS
                    || enchantment == Enchantments.SMITE
                    || enchantment == Enchantments.MOB_LOOTING) {
                return true;
            }
        }

        if (stack.is(ModTags.HAMMERS) || stack.is(ModTags.GREAT_HAMMERS)) {
            if (enchantment == Enchantments.SWEEPING_EDGE
                    || enchantment == Enchantments.SMITE
                    || enchantment == Enchantments.BANE_OF_ARTHROPODS
                    || enchantment == Enchantments.MOB_LOOTING
                    || enchantment == Enchantments.KNOCKBACK
                    || enchantment == Enchantments.FIRE_ASPECT) {
                return true;
            }
        }

        return IForgeItem.super.canApplyAtEnchantingTable(stack, enchantment);
    }

}
