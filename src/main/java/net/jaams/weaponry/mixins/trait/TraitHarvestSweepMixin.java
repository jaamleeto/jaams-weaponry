package net.jaams.weaponry.mixins.trait;

import net.minecraft.world.entity.LivingEntity;

import net.jaams.weaponry.util.ModComponents;

import java.util.ArrayList;
import java.util.List;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitHarvestSweepMixin {

    @Unique
    private boolean isHarvestSweepEnabled(ItemStack stack) {
        if (!TraitsConfig.HARVEST_SWEEP.get()) {
            return false;
        }
        return ModTraits.isHarvestSweepItem(stack);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaams$onHarvestSweepUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isHarvestSweepEnabled(stack)) {
            return;
        }
        if (player.isCrouching()) {
            return;
        }
        HitResult hitResult = player.pick(5.0D, 0.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        UseOnContext context = new UseOnContext(player, hand, blockHitResult);
        BlockPos pos = context.getClickedPos();
        BlockState clickedBlockState = level.getBlockState(pos);

        if (isTillable(clickedBlockState)) {
            if (level.isClientSide()) {
                cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, true));
                return;
            }
            player.swing(hand, true);
            tillLandInArea((ServerLevel) level, pos, player, stack);
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, false));
        } else if (canHarvest(stack) && clickedBlockState.getBlock() instanceof CropBlock crop
                && crop.isMaxAge(clickedBlockState)) {
            if (level.isClientSide()) {
                cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, true));
                return;
            }
            player.swing(hand, true);
            harvestCropsInArea((ServerLevel) level, pos, player, stack);
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, false));
        }
    }

    @Unique
    private void tillLandInArea(ServerLevel level, BlockPos centerPos, Player player, ItemStack tool) {
        int range = getTillRange(tool);
        int durabilityCostPerBlock = getTillDurabilityCost(tool);
        int affectedBlocks = 0;
        int minOffset = -(range - 1) / 2;
        int maxOffset = range / 2;
        for (int dx = minOffset; dx <= maxOffset; dx++) {
            for (int dz = minOffset; dz <= maxOffset; dz++) {
                BlockPos targetPos = centerPos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);
                if (isTillable(targetState)) {
                    BlockPos abovePos = targetPos.above();
                    if (level.isEmptyBlock(abovePos)) {
                        level.setBlock(targetPos, Blocks.FARMLAND.defaultBlockState(), 3);
                        affectedBlocks++;
                    }
                }
            }
        }
        if (affectedBlocks > 0) {
            level.playSound(null, centerPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            int durabilityCost = isDurabilityPerBlock(tool) ? affectedBlocks * durabilityCostPerBlock
                    : durabilityCostPerBlock;
            tool.hurtAndBreak(durabilityCost, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
        }
    }

    @Unique
    private void harvestCropsInArea(ServerLevel level, BlockPos centerPos, Player player, ItemStack tool) {
        int range = getHarvestRange(tool);
        int maxBlocks = getMaxBlocks(tool);
        int durabilityCostPerBlock = getHarvestDurabilityCost(tool);
        int affectedBlocks = 0;
        Inventory inventory = player.getInventory();
        List<ItemStack> allDrops = new ArrayList<>();
        int minOffset = -(range - 1) / 2;
        int maxOffset = range / 2;
        for (int dx = minOffset; dx <= maxOffset && affectedBlocks < maxBlocks; dx++) {
            for (int dz = minOffset; dz <= maxOffset && affectedBlocks < maxBlocks; dz++) {
                BlockPos targetPos = centerPos.offset(dx, 0, dz);
                BlockState blockState = level.getBlockState(targetPos);
                if (blockState.getBlock() instanceof CropBlock crop && crop.isMaxAge(blockState)) {
                    LootParams.Builder lootparams = new LootParams.Builder(level)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(targetPos))
                            .withParameter(LootContextParams.TOOL, tool);
                    List<ItemStack> drops = blockState.getDrops(lootparams);
                    for (ItemStack drop : drops) {
                        if (!drop.isEmpty()) {
                            allDrops.add(drop.copy());
                        }
                    }
                    level.destroyBlock(targetPos, false);
                    level.setBlock(targetPos, crop.getStateForAge(0), 3);
                    level.playSound(null, targetPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    affectedBlocks++;
                }
            }
        }
        
        List<ItemStack> consolidatedDrops = new ArrayList<>();
        for (ItemStack drop : allDrops) {
            ItemStack existing = null;
            for (ItemStack consolidated : consolidatedDrops) {
                if (ItemStack.isSameItemSameComponents(consolidated, drop)) {
                    existing = consolidated;
                    break;
                }
            }
            if (existing == null) {
                consolidatedDrops.add(drop.copy());
            } else {
                existing.grow(drop.getCount());
            }
        }
        
        for (ItemStack drop : consolidatedDrops) {
            int remaining = drop.getCount();
            for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
                ItemStack slotStack = inventory.getItem(i);
                if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, drop)) {
                    int spaceLeft = slotStack.getMaxStackSize() - slotStack.getCount();
                    if (spaceLeft > 0) {
                        int amountToAdd = Math.min(remaining, spaceLeft);
                        slotStack.grow(amountToAdd);
                        remaining -= amountToAdd;
                        inventory.setChanged();
                    }
                }
            }
            while (remaining > 0) {
                int amountToAdd = Math.min(remaining, drop.getMaxStackSize());
                ItemStack newStack = drop.copyWithCount(amountToAdd);
                int emptySlot = -1;
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    if (inventory.getItem(i).isEmpty()) {
                        emptySlot = i;
                        break;
                    }
                }
                if (emptySlot != -1) {
                    inventory.setItem(emptySlot, newStack);
                    remaining -= amountToAdd;
                    inventory.setChanged();
                } else {
                    ItemStack remainingStack = drop.copyWithCount(remaining);
                    level.addFreshEntity(new ItemEntity(level, centerPos.getX() + 0.5, centerPos.getY() + 0.5,
                            centerPos.getZ() + 0.5, remainingStack));
                    break;
                }
            }
        }
        if (affectedBlocks > 0) {
            generateSweepEffect(level, player);
            int durabilityCost = isDurabilityPerBlock(tool) ? affectedBlocks * durabilityCostPerBlock
                    : durabilityCostPerBlock;
            tool.hurtAndBreak(durabilityCost, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
        }
    }

    @Unique
    private void generateSweepEffect(ServerLevel serverLevel, Player player) {
        double x = player.getX();
        double y = player.getY() + player.getEyeHeight() * 0.5;
        double z = player.getZ();
        Vec3 lookDirection = player.getLookAngle();
        double offsetX = lookDirection.x * 1.5F;
        double offsetY = serverLevel.random.nextFloat() * 0.5 - 0.25;
        double offsetZ = lookDirection.z * 2.5F;
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK,
                x + offsetX, y + offsetY, z + offsetZ, 1, 0, 0, 0, 0);
        serverLevel.playSound(null, x + offsetX, y + offsetY, z + offsetZ,
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Unique
    private boolean isTillable(BlockState blockState) {
        return blockState.is(Blocks.DIRT) || blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.COARSE_DIRT);
    }

    

    @Unique
    private boolean canHarvest(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("HarvestSweepCanHarvest")) {
            return tag.getBoolean("HarvestSweepCanHarvest");
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.can_harvest)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_CAN_HARVEST.get());
    }

    @Unique
    private int getHarvestRange(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("HarvestSweepRange")) {
            return Math.max(0, tag.getInt("HarvestSweepRange"));
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.range)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_RANGE.get());
    }

    @Unique
    private int getTillRange(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("HarvestSweepTillRange")) {
            return Math.max(0, tag.getInt("HarvestSweepTillRange"));
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.till_range)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_TILL_RANGE.get());
    }

    @Unique
    private int getMaxBlocks(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("HarvestSweepMaxBlocks")) {
            return Math.max(1, tag.getInt("HarvestSweepMaxBlocks"));
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.max_blocks)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_MAX_BLOCKS.get());
    }

    @Unique
    private int getTillDurabilityCost(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("HarvestSweepTillDurabilityCost")) {
            return Math.max(0, tag.getInt("HarvestSweepTillDurabilityCost"));
        }
        Integer jsonValue = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.till_durability_cost)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonValue != null) {
            return jsonValue;
        }
        
        Integer generalJson = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.durability_cost_per_block)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (generalJson != null) {
            return generalJson;
        }
        return TraitsConfig.HARVEST_SWEEP_TILL_DURABILITY_COST.get();
    }

    @Unique
    private int getHarvestDurabilityCost(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("HarvestSweepHarvestDurabilityCost")) {
            return Math.max(0, tag.getInt("HarvestSweepHarvestDurabilityCost"));
        }
        Integer jsonValue = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.harvest_durability_cost)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonValue != null) {
            return jsonValue;
        }
        
        Integer generalJson = TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.durability_cost_per_block)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (generalJson != null) {
            return generalJson;
        }
        return TraitsConfig.HARVEST_SWEEP_HARVEST_DURABILITY_COST.get();
    }

    @Unique
    private boolean isDurabilityPerBlock(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("HarvestSweepDurabilityPerBlock")) {
            return tag.getBoolean("HarvestSweepDurabilityPerBlock");
        }
        return TraitModifierData.getHarvestSweep(stack)
                .map((d) -> d.durability_per_block)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.HARVEST_SWEEP_DURABILITY_PER_BLOCK.get());
    }
}
