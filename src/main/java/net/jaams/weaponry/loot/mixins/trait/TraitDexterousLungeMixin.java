package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.handler.trait.MovementHandler;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitDexterousLungeMixin {

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onDexterousLungeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide
                || !ModTraits.isDexterousLungeItem(stack)) {
            return;
        }
        if (!ItemStack.isSameItemSameComponents(attacker.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameComponents(attacker.getOffhandItem(), stack))
            return;
        CompoundTag tag = ModComponents.get(stack);
        float pullStrength = getPullStrength(stack, tag);
        float attractStrength = getAttractStrength(stack, tag);
        float maxDistance = getMaxDistance(stack, tag);
        float maxVerticalPull = getMaxVerticalPull(stack, tag);
        float distanceScaling = getDistanceScaling(stack, tag);

        MovementHandler.pullTowardsEnemy(target, attacker, 1.0F, stack,
                pullStrength, attractStrength, maxDistance, 2.0, 1.0F, 0.1,
                maxVerticalPull, 0.5F, distanceScaling);
        attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.3F);
    }

    @Unique
    private float getAttractStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DexterousLungeAttractStrength")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungeAttractStrength"));
        }
        return TraitModifierData.getDexterousLunge(stack)
                .map(entry -> entry.attract_strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DEXTEROUS_LUNGE_ATTRACT_STRENGTH.get().floatValue());
    }

    @Unique
    private float getPullStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DexterousLungePullStrength")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungePullStrength"));
        }
        return TraitModifierData.getDexterousLunge(stack)
                .map(entry -> entry.pull_strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DEXTEROUS_LUNGE_PULL_STRENGTH.get().floatValue());
    }

    @Unique
    private float getMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DexterousLungeMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("DexterousLungeMaxDistance"));
        }
        return TraitsConfig.DEXTEROUS_LUNGE_MAX_DISTANCE.get().floatValue();
    }

    @Unique
    private float getMaxVerticalPull(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DexterousLungeMaxVerticalPull")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungeMaxVerticalPull"));
        }
        return TraitsConfig.DEXTEROUS_LUNGE_MAX_VERTICAL_PULL.get().floatValue();
    }

    @Unique
    private float getDistanceScaling(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DexterousLungeDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("DexterousLungeDistanceScaling"));
        }
        return TraitsConfig.DEXTEROUS_LUNGE_DISTANCE_SCALING.get().floatValue();
    }

    @Unique
    private int getDurabilityCost(ItemStack stack, CompoundTag tag) {
        return 0;
    }
}
