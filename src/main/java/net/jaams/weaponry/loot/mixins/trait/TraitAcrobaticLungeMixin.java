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
public class TraitAcrobaticLungeMixin {

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onAcrobaticLungeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide
                || !ModTraits.isAcrobaticLungeItem(stack)) {
            return;
        }
        if (!ItemStack.isSameItemSameComponents(attacker.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameComponents(attacker.getOffhandItem(), stack))
            return;
        float strength = getStrength(stack, ModComponents.get(stack));
        float maxDistance = getMaxDistance(stack, ModComponents.get(stack));
        float maxVerticalPull = getMaxVerticalPull(stack, ModComponents.get(stack));
        float distanceScaling = getDistanceScaling(stack, ModComponents.get(stack));

        MovementHandler.pullTowardsEnemy(target, attacker, 1.0F, stack,
                strength, strength, maxDistance, 2.0, 1.0F, 0.1,
                maxVerticalPull, 0.5F, distanceScaling);
        attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.3F);
    }

    @Unique
    private float getStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AcrobaticLungeStrength")) {
            return Math.max(0.0F, tag.getFloat("AcrobaticLungeStrength"));
        }
        return TraitModifierData.getAcrobaticLunge(stack)
                .map(entry -> entry.strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.ACROBATIC_LUNGE_STRENGTH.get().floatValue());
    }

    @Unique
    private float getMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AcrobaticLungeMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("AcrobaticLungeMaxDistance"));
        }
        return TraitsConfig.ACROBATIC_LUNGE_MAX_DISTANCE.get().floatValue();
    }

    @Unique
    private float getMaxVerticalPull(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AcrobaticLungeMaxVerticalPull")) {
            return Math.max(0.0F, tag.getFloat("AcrobaticLungeMaxVerticalPull"));
        }
        return TraitsConfig.ACROBATIC_LUNGE_MAX_VERTICAL_PULL.get().floatValue();
    }

    @Unique
    private float getDistanceScaling(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("AcrobaticLungeDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("AcrobaticLungeDistanceScaling"));
        }
        return TraitsConfig.ACROBATIC_LUNGE_DISTANCE_SCALING.get().floatValue();
    }

    @Unique
    private int getDurabilityCost(ItemStack stack, CompoundTag tag) {
        return 0;
    }
}
