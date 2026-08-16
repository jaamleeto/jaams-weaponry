package net.jaams.weaponry.mixins.common;

import org.spongepowered.asm.mixin.Mixin;

import net.jaams.weaponry.inject.ItemInjection;
import net.jaams.weaponry.util.ModCompats;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.extensions.IItemExtension;

@Mixin(value = Item.class, remap = true)
public abstract class ForgeItemMixin implements IItemExtension, ItemInjection {

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
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (ModCompats.isSlingshot(stack)) {
            if (enchantment.is(Enchantments.POWER) || enchantment.is(Enchantments.PUNCH)
                    || enchantment.is(Enchantments.FLAME) || enchantment.is(Enchantments.PIERCING)
                    || enchantment.is(Enchantments.MULTISHOT)) {
                return true;
            }
        }

        if (ModCompats.isThrowableActive(stack)) {
            if (enchantment.is(Enchantments.PIERCING) || enchantment.is(Enchantments.LOYALTY)
                    || enchantment.is(Enchantments.MULTISHOT)) {
                return true;
            }
        }

        if (stack.is(ModTags.CLAWS) || stack.is(ModTags.DAGGERS) || stack.is(ModTags.REVERSE_DAGGERS)
                || stack.is(ModTags.HOOK_SWORDS) || stack.is(ModTags.KATARS) || stack.is(ModTags.KNUCKLES)) {
            if (enchantment.is(Enchantments.SWEEPING_EDGE)) {
                return false;
            }
        }

        if (stack.is(ModTags.BATTLE_AXES)) {
            if (enchantment.is(Enchantments.SWEEPING_EDGE) || enchantment.is(Enchantments.FIRE_ASPECT)
                    || enchantment.is(Enchantments.BANE_OF_ARTHROPODS) || enchantment.is(Enchantments.SMITE)
                    || enchantment.is(Enchantments.LOOTING)) {
                return true;
            }
        }

        if (stack.is(ModTags.HAMMERS) || stack.is(ModTags.GREAT_HAMMERS)) {
            if (enchantment.is(Enchantments.SWEEPING_EDGE) || enchantment.is(Enchantments.SMITE)
                    || enchantment.is(Enchantments.BANE_OF_ARTHROPODS) || enchantment.is(Enchantments.LOOTING)
                    || enchantment.is(Enchantments.KNOCKBACK) || enchantment.is(Enchantments.FIRE_ASPECT)) {
                return true;
            }
        }

        return IItemExtension.super.supportsEnchantment(stack, enchantment);
    }
}
