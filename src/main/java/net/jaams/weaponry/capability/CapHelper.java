package net.jaams.weaponry.capability;


import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

/** Optional-style item-handler lookups replacing the Forge LazyOptional capability API. */
public final class CapHelper {

    private CapHelper() {
    }

    public static Optional<IItemHandler> itemHandler(ItemStack stack) {
        return stack == null || stack.isEmpty() ? Optional.empty() : Optional.ofNullable(stack.getCapability(Capabilities.ItemHandler.ITEM));
    }

    public static Optional<IItemHandler> itemHandler(Entity entity) {
        return entity == null ? Optional.empty() : Optional.ofNullable(entity.getCapability(Capabilities.ItemHandler.ENTITY, null));
    }

    public static Optional<IItemHandler> itemHandler(BlockEntity blockEntity) {
        if (blockEntity == null || blockEntity.getLevel() == null)
            return Optional.empty();
        return Optional.ofNullable(blockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null));
    }
}
