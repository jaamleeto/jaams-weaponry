package net.jaams.weaponry.item.shinerite;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

import net.jaams.weaponry.dyeable.IDyeableItem;

public class ShineriteAxeItem extends AxeItem implements IDyeableItem {
    public ShineriteAxeItem(Tier tier, float attackModifier, float attackSpeed, Properties properties) {
        super(tier, attackModifier, attackSpeed, properties);
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
