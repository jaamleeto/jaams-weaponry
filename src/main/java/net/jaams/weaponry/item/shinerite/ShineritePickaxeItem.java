package net.jaams.weaponry.item.shinerite;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

import net.jaams.weaponry.dyeable.IDyeableItem;

public class ShineritePickaxeItem extends PickaxeItem implements IDyeableItem {
    public ShineritePickaxeItem(Tier tier, int attackModifier, float attackSpeed, Properties properties) {
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
