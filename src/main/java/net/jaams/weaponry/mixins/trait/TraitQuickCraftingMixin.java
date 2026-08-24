package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.init.ModItems;
import net.jaams.weaponry.particle.MiniSweepParticleData;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitQuickCraftingMixin {

    @Unique
    private boolean isQuickCraftingEnabled(ItemStack stack) {
        if (!TraitsConfig.QUICK_CRAFTING.get()) {
            return false;
        }
        return ModTraits.isQuickCraftingItem(stack);
    }

    @Unique
    private boolean shouldApplyQuickCraftingLogic(Player player, InteractionHand hand) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isQuickCraftingEnabled(stack)) {
            return false;
        }
        Item ingredientItem = jaam$getIngredientItem(stack);
        int requiredCount = jaam$getIngredientCount(stack);
        ItemStack otherHandStack = player.getItemInHand(
                hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return otherHandStack.is(ingredientItem) && otherHandStack.getCount() >= requiredCount;
    }

    @Unique
    private boolean shouldApplyQuickCraftingLogic() {
        ItemStack stack = (ItemStack) (Object) this;
        return isQuickCraftingEnabled(stack);
    }

    @Unique
    private Item jaam$getIngredientItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        String itemId;
        if (tag != null && tag.contains("QuickCraftingIngredient")) {
            itemId = tag.getString("QuickCraftingIngredient");
        } else {
            itemId = TraitModifierData.getQuickCrafting(stack)
                    .map((entry) -> entry.ingredient)
                    .filter(java.util.Objects::nonNull)
                    .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_INGREDIENT.get());
        }
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemId));
        return item != null ? item : ModItems.SHORT_STICK.get();
    }

    @Unique
    private int jaam$getIngredientCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingIngredientCount")) {
            return Math.max(1, tag.getInt("QuickCraftingIngredientCount"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
                .map((entry) -> entry.ingredient_count)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_INGREDIENT_COUNT.get());
        return Math.max(1, value);
    }

    @Unique
    private Item jaam$getResultItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        String itemId;
        if (tag != null && tag.contains("QuickCraftingResult")) {
            itemId = tag.getString("QuickCraftingResult");
        } else {
            itemId = TraitModifierData.getQuickCrafting(stack)
                    .map((entry) -> entry.result)
                    .filter(java.util.Objects::nonNull)
                    .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_RESULT.get());
        }
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemId));
        return item != null ? item : ModItems.STAKE.get();
    }

    @Unique
    private int jaam$getResultCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingResultCount")) {
            return Math.max(1, tag.getInt("QuickCraftingResultCount"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
                .map((entry) -> entry.result_count)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_RESULT_COUNT.get());
        return Math.max(1, value);
    }

    @Unique
    private int jaam$getUseDuration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingUseDuration")) {
            return Math.max(0, tag.getInt("QuickCraftingUseDuration"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
                .map((entry) -> entry.use_duration)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_USE_DURATION.get());
        return Math.max(0, value);
    }

    @Unique
    private int jaam$getDurabilityCost(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingDurabilityCost")) {
            return Math.max(0, tag.getInt("QuickCraftingDurabilityCost"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
                .map((entry) -> entry.durability_cost)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_DURABILITY_COST.get());
        return Math.max(0, value);
    }

    @Unique
    private int jaam$getCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("QuickCraftingCooldown")) {
            return Math.max(0, tag.getInt("QuickCraftingCooldown"));
        }
        int value = TraitModifierData.getQuickCrafting(stack)
                .map((entry) -> entry.cooldown)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_COOLDOWN.get());
        return Math.max(0, value);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaam$onQuickCraftingUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!shouldApplyQuickCraftingLogic(player, hand)) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaam$modifyQuickCraftingUseDuration(CallbackInfoReturnable<Integer> cir) {
        if (!shouldApplyQuickCraftingLogic()) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        cir.setReturnValue(jaam$getUseDuration(stack));
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaam$changeQuickCraftingUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        if (!shouldApplyQuickCraftingLogic()) {
            return;
        }
        cir.setReturnValue(UseAnim.BOW);
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void jaam$onFinishUsingQuickCrafting(Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> cir) {
        if (!shouldApplyQuickCraftingLogic()) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (!level.isClientSide()) {
            processQuickCraftingUsage(stack, level, entity, entity.getUsedItemHand());
        }
        cir.setReturnValue(stack);
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void jaam$onReleaseQuickCrafting(Level level, LivingEntity entity, int durationUsed, CallbackInfo ci) {
        if (!shouldApplyQuickCraftingLogic()) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (!level.isClientSide()) {
            int maxDuration = jaam$getUseDuration(stack);
            int remainingDuration = maxDuration - durationUsed;
            if (remainingDuration >= (maxDuration / 2)) {
                processQuickCraftingUsage(stack, level, entity, entity.getUsedItemHand());
            }
        }
        ci.cancel();
    }

    @Unique
    private void processQuickCraftingUsage(ItemStack itemstack, Level level, LivingEntity entity,
            InteractionHand usedHand) {
        ItemStack otherHandStack = entity.getItemInHand(
                usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        Item ingredientItem = jaam$getIngredientItem(itemstack);
        int requiredCount = jaam$getIngredientCount(itemstack);
        if (otherHandStack.is(ingredientItem) && otherHandStack.getCount() >= requiredCount) {
            int durabilityCost = jaam$getDurabilityCost(itemstack);
            if (durabilityCost > 0) {
                itemstack.hurtAndBreak(durabilityCost, entity, (brokenItemStack) -> {
                    brokenItemStack.broadcastBreakEvent(usedHand);
                });
            }
            if (entity instanceof Player player && !player.isCreative()) {
                otherHandStack.shrink(requiredCount);
            }
            Item resultItem = jaam$getResultItem(itemstack);
            int resultCount = jaam$getResultCount(itemstack);
            ItemStack resultStack = new ItemStack(resultItem, resultCount);
            if (entity instanceof Player player) {
                ItemHandlerHelper.giveItemToPlayer(player, resultStack);
                int cooldown = jaam$getCooldown(itemstack);
                if (cooldown > 0) {
                    boolean globalCooldown = TraitModifierData.getQuickCrafting(itemstack)
                            .map(e -> e.global_cooldown)
                            .filter(java.util.Objects::nonNull)
                            .orElseGet(() -> TraitsConfig.QUICK_CRAFTING_GLOBAL_COOLDOWN.get());

                    if (globalCooldown) {
                        for (ItemStack invStack : player.getInventory().items) {
                            if (!invStack.isEmpty() && isQuickCraftingEnabled(invStack)) {
                                player.getCooldowns().addCooldown(invStack.getItem(), cooldown);
                            }
                        }
                        ItemStack offHandStack = player.getOffhandItem();
                        if (!offHandStack.isEmpty() && isQuickCraftingEnabled(offHandStack)) {
                            player.getCooldowns().addCooldown(offHandStack.getItem(), cooldown);
                        }
                        for (ItemStack armorStack : player.getInventory().armor) {
                            if (!armorStack.isEmpty() && isQuickCraftingEnabled(armorStack)) {
                                player.getCooldowns().addCooldown(armorStack.getItem(), cooldown);
                            }
                        }
                    } else {
                        player.getCooldowns().addCooldown(itemstack.getItem(), cooldown);
                    }
                }
            }
            entity.swing(usedHand, true);
            if (level instanceof ServerLevel serverWorld) {
                spawnQuickCraftingParticlesAndSounds(itemstack, entity, serverWorld);
            }
        }
    }

    @Unique
    private void spawnQuickCraftingParticlesAndSounds(ItemStack itemstack, LivingEntity entity,
            ServerLevel serverWorld) {
        if (!(entity instanceof Player player)) {
            return;
        }
        Item ingredientItem = jaam$getIngredientItem(itemstack);
        ItemStack particleStack = new ItemStack(ingredientItem);
        ModUtils.spawnItemParticlesInFront(player, particleStack, 6, 1.0f, false);
        float r = 1.0F,
                g = 1.0F,
                b = 1.0F;
        ModUtils.spawnCustomParticlesInFront(player, itemstack, new MiniSweepParticleData(r, g, b, 0.35f), r, g, b,
                0.35f, 1.0f, 1, false);
        CompoundTag tag = itemstack.getTag();
        String customParticleId;
        if (tag != null && tag.contains("QuickCraftingParticle")) {
            customParticleId = tag.getString("QuickCraftingParticle");
        } else {
            customParticleId = TraitModifierData.getQuickCrafting(itemstack)
                    .map((entry) -> entry.particle)
                    .filter(java.util.Objects::nonNull)
                    .orElse("");
        }
        if (!customParticleId.isEmpty()) {
            ParticleType<?> customType = ForgeRegistries.PARTICLE_TYPES
                    .getValue(ResourceLocation.parse(customParticleId));
            if (customType instanceof SimpleParticleType simpleType) {
                double px = player.getX() + player.getLookAngle().x * 1.2;
                double py = player.getY() + player.getEyeHeight() + player.getLookAngle().y * 0.5;
                double pz = player.getZ() + player.getLookAngle().z * 1.2;
                serverWorld.sendParticles(simpleType, px, py, pz, 5, 0.2, 0.2, 0.2, 0.1);
            }
        }
        Vec3 lookDirection = player.getLookAngle().normalize();
        double sweepX = player.getX() + lookDirection.x * 1.5;
        double sweepY = player.getY() + player.getEyeHeight() + lookDirection.y * 0.5;
        double sweepZ = player.getZ() + lookDirection.z * 1.5;
        if (player.isUnderWater()) {
            serverWorld.sendParticles(ParticleTypes.BUBBLE, sweepX, sweepY, sweepZ, 1, 0.2, 0.2, 0.2, 0.0);
        }
        serverWorld.playSound(null, sweepX, sweepY, sweepZ, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F,
                player.isUnderWater() ? 0.8F : 1.0F);
        BlockPos pos = player.blockPosition();
        String customSoundId;
        if (tag != null && tag.contains("QuickCraftingSound")) {
            customSoundId = tag.getString("QuickCraftingSound");
        } else {
            customSoundId = TraitModifierData.getQuickCrafting(itemstack)
                    .map((entry) -> entry.sound)
                    .filter(java.util.Objects::nonNull)
                    .orElse("jaams_weaponry:dagger");
        }
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse(customSoundId));
        if (soundEvent != null) {
            serverWorld.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 1.0F,
                    player.isUnderWater() ? 0.8F : 1.0F);
        }
    }
}
