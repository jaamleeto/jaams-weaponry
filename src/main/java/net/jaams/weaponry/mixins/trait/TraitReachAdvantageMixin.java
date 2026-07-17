package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.handler.trait.BonusDamageHandler;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class TraitReachAdvantageMixin {

    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    private void jaams$onReachAdvantageHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide)
            return;
        if (!ModTraits.isReachAdvantageItem(stack))
            return;
        CompoundTag tag = stack.getTag();
        boolean requireFullyCharged = getRequireFullyCharged(stack, tag);
        float attackStrength = attacker.getAttackStrengthScale(0.5F);
        BonusDamageHandler.handleReachAdvantage(target, attacker, stack, requireFullyCharged, attackStrength);
    }

    @Unique
    private boolean getRequireFullyCharged(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("ReachAdvantageFullyCharged")) {
            return tag.getBoolean("ReachAdvantageFullyCharged");
        }
        return TraitModifierData.getReachAdvantage(stack)
                .map((entry) -> entry.require_fully_charged)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.REACH_ADVANTAGE_REQUIRE_FULLY_CHARGED.get());
    }
}
