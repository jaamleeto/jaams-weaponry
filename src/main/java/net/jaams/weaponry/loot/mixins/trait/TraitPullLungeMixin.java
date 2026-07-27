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
public class TraitPullLungeMixin {

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onPullLungeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide || !ModTraits.isPullLungeItem(stack)) {
            return;
        }
        if (!ItemStack.isSameItemSameComponents(attacker.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameComponents(attacker.getOffhandItem(), stack))
            return;
        float strength = getStrength(stack, ModComponents.get(stack));
        float maxDistance = getMaxDistance(stack, ModComponents.get(stack));
        float maxVerticalPull = getMaxVerticalPull(stack, ModComponents.get(stack));
        float distanceScaling = getDistanceScaling(stack, ModComponents.get(stack));

        MovementHandler.pullEnemyTowardsPlayer(target, attacker, 1.0F, stack,
                strength, maxDistance, 2.0, 1.0F, 0.1,
                maxVerticalPull, 0.5F, distanceScaling);
        attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.3F);
    }

    @Unique
    private float getStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PullLungeStrength")) {
            return Math.max(0.0F, tag.getFloat("PullLungeStrength"));
        }
        return TraitModifierData.getPullLunge(stack)
                .map(entry -> entry.strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.PULL_LUNGE_STRENGTH.get().floatValue());
    }

    @Unique
    private float getMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PullLungeMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("PullLungeMaxDistance"));
        }
        return TraitsConfig.PULL_LUNGE_MAX_DISTANCE.get().floatValue();
    }

    @Unique
    private float getMaxVerticalPull(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PullLungeMaxVerticalPull")) {
            return Math.max(0.0F, tag.getFloat("PullLungeMaxVerticalPull"));
        }
        return TraitsConfig.PULL_LUNGE_MAX_VERTICAL_PULL.get().floatValue();
    }

    @Unique
    private float getDistanceScaling(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("PullLungeDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("PullLungeDistanceScaling"));
        }
        return TraitsConfig.PULL_LUNGE_DISTANCE_SCALING.get().floatValue();
    }

    @Unique
    private int getDurabilityCost(ItemStack stack, CompoundTag tag) {
        return 0;
    }
}
