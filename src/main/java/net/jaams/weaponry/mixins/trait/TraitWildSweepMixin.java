package net.jaams.weaponry.mixins.trait;

import java.util.List;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitWildSweepMixin {

    @Unique
    private boolean isWildSweepEnabled(ItemStack stack) {
        if (!TraitsConfig.WILD_SWEEP.get()) {
            return false;
        }
        return ModTraits.isWildSweepItem(stack);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaams$onWildSweepUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isWildSweepEnabled(stack)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
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
        if (!isBreakablePlant(clickedBlockState, stack)) {
            return;
        }
        if (level.isClientSide()) {
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, true));
            return;
        }
        boolean blocksBroken = breakBlocksInArea(level, pos, player, stack);
        if (blocksBroken) {
            player.swing(hand, true);
            int durabilityCost = getDurabilityCost(stack);
            if (durabilityCost > 0) {
                ModUtils.applyTraitDurabilityCost(stack, player, durabilityCost, (e) -> e.broadcastBreakEvent(hand));
            }
            if (level instanceof ServerLevel serverLevel) {
                generateSweepEffect(serverLevel, player);
            }
            applyCooldown(player, stack);
        }
        cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, false));
    }

    @Unique
    private boolean breakBlocksInArea(Level level, BlockPos centerPos, Player player, ItemStack itemStack) {
        boolean blocksBroken = false;
        int radius = getBreakRadius(itemStack);
        int minOffset = -(radius - 1) / 2;
        int maxOffset = radius / 2;
        for (int dx = minOffset; dx <= maxOffset; dx++) {
            for (int dz = minOffset; dz <= maxOffset; dz++) {
                BlockPos targetPos = centerPos.offset(dx, 0, dz);
                BlockState blockState = level.getBlockState(targetPos);
                if (isBreakablePlant(blockState, itemStack)) {
                    level.destroyBlock(targetPos, true, player);
                    blocksBroken = true;
                }
            }
        }
        return blocksBroken;
    }

    @Unique
    private void generateSweepEffect(ServerLevel serverLevel, Player player) {
        double x = player.getX();
        double y = player.getY() + player.getEyeHeight() * 0.5;
        double z = player.getZ();
        net.minecraft.world.phys.Vec3 lookDirection = player.getLookAngle();
        double offsetX = lookDirection.x * 1.5F;
        double offsetY = 0.0;
        double offsetZ = lookDirection.z * 1.5F;
        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, x + offsetX, y + offsetY, z + offsetZ, 1, 0.1, 0.1, 0.1,
                0.0);
        serverLevel.playSound(null, x + offsetX, y + offsetY, z + offsetZ, SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Unique
    private boolean isBreakablePlant(BlockState blockState, ItemStack stack) {
        String blockId = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString();

        List<String> patterns = getBreakableBlockPatterns(stack);
        for (String pattern : patterns) {
            if (blockId.contains(pattern)) {
                return true;
            }
        }

        return blockState.getBlock() instanceof SugarCaneBlock || blockState.getBlock() instanceof TallGrassBlock
                || blockState.getBlock() instanceof DoublePlantBlock;
    }

    @Unique
    private void applyCooldown(Player player, ItemStack stack) {
        int cooldownTicks = getCooldown(stack);
        if (cooldownTicks > 0) {
            boolean globalCooldown = TraitModifierData.getWildSweep(stack)
                    .map(e -> e.global_cooldown)
                    .filter(java.util.Objects::nonNull)
                    .orElseGet(() -> TraitsConfig.WILD_SWEEP_GLOBAL_COOLDOWN.get());

            if (globalCooldown) {
                for (ItemStack invStack : player.getInventory().items) {
                    if (!invStack.isEmpty() && isWildSweepEnabled(invStack)) {
                        player.getCooldowns().addCooldown(invStack.getItem(), cooldownTicks);
                    }
                }
                ItemStack offHandStack = player.getOffhandItem();
                if (!offHandStack.isEmpty() && isWildSweepEnabled(offHandStack)) {
                    player.getCooldowns().addCooldown(offHandStack.getItem(), cooldownTicks);
                }
                for (ItemStack armorStack : player.getInventory().armor) {
                    if (!armorStack.isEmpty() && isWildSweepEnabled(armorStack)) {
                        player.getCooldowns().addCooldown(armorStack.getItem(), cooldownTicks);
                    }
                }
            } else {
                player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
            }
        }
    }



    @Unique
    private int getBreakRadius(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("WildSweepBreakRadius")) {
            return Math.max(0, tag.getInt("WildSweepBreakRadius"));
        }
        return TraitModifierData.getWildSweep(stack)
                .map((e) -> e.break_radius)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.WILD_SWEEP_RADIUS.get());
    }

    @Unique
    private int getDurabilityCost(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("WildSweepDurabilityCost")) {
            return Math.max(0, tag.getInt("WildSweepDurabilityCost"));
        }
        return TraitModifierData.getWildSweep(stack)
                .map((e) -> e.durability_cost)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.WILD_SWEEP_DURABILITY_COST.get());
    }

    @Unique
    private int getCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("WildSweepCooldown")) {
            return Math.max(0, tag.getInt("WildSweepCooldown"));
        }
        return TraitModifierData.getWildSweep(stack)
                .map((e) -> e.cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.WILD_SWEEP_COOLDOWN.get());
    }

    @Unique
    private List<String> getBreakableBlockPatterns(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("WildSweepBreakableBlocks")) {
            return List.of(tag.getString("WildSweepBreakableBlocks").split(","));
        }
        List<String> jsonPatterns = TraitModifierData.getWildSweep(stack)
                .map((e) -> e.breakable_blocks)
                .filter(java.util.Objects::nonNull)
                .orElse(null);
        if (jsonPatterns != null && !jsonPatterns.isEmpty()) {
            return jsonPatterns;
        }
        return List.copyOf(TraitsConfig.WILD_SWEEP_BREAKABLE_BLOCKS.get());
    }
}
