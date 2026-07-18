package net.jaams.weaponry.mixins.trait;

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

@Mixin(ItemStack.class)
public class TraitDisengageMixin {

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onDisengageHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide || !ModTraits.isDisengageItem(stack)) {
            return;
        }
        if (!ItemStack.isSameItemSameTags(attacker.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameTags(attacker.getOffhandItem(), stack))
            return;
        float strength = getStrength(stack, stack.getTag());
        float maxDistance = getMaxDistance(stack, stack.getTag());
        float maxVerticalPush = getMaxVerticalPush(stack, stack.getTag());
        float distanceScaling = getDistanceScaling(stack, stack.getTag());

        MovementHandler.pushAwayFromTarget(target, attacker, 1.0F, stack,
                strength, maxDistance, 2.0, 1.0F, 0.1,
                maxVerticalPush, 0.5F, distanceScaling);
        attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 0.7F);
    }

    @Unique
    private float getStrength(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisengageStrength")) {
            return Math.max(0.0F, tag.getFloat("DisengageStrength"));
        }
        return TraitModifierData.getDisengage(stack)
                .map(entry -> entry.strength)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DISENGAGE_STRENGTH.get().floatValue());
    }

    @Unique
    private float getMaxDistance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisengageMaxDistance")) {
            return Math.max(1.0F, tag.getFloat("DisengageMaxDistance"));
        }
        return TraitsConfig.DISENGAGE_MAX_DISTANCE.get().floatValue();
    }

    @Unique
    private float getMaxVerticalPush(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisengageMaxVerticalPush")) {
            return Math.max(0.0F, tag.getFloat("DisengageMaxVerticalPush"));
        }
        return TraitsConfig.DISENGAGE_MAX_VERTICAL_PUSH.get().floatValue();
    }

    @Unique
    private float getDistanceScaling(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisengageDistanceScaling")) {
            return Math.max(0.0F, tag.getFloat("DisengageDistanceScaling"));
        }
        return TraitsConfig.DISENGAGE_DISTANCE_SCALING.get().floatValue();
    }

    @Unique
    private int getDurabilityCost(ItemStack stack, CompoundTag tag) {
        return 0;
    }
}
