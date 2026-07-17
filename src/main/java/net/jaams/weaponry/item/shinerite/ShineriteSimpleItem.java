package net.jaams.weaponry.item.shinerite;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.jaams.weaponry.dyeable.IDyeableItem;

public class ShineriteSimpleItem extends Item implements IDyeableItem {
    public ShineriteSimpleItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultColor() {
        return -1;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
