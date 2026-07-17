package net.jaams.weaponry.mixins.trait;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.util.ModTraits;

@Mixin(ItemStack.class)
public class TraitParryGuardMixin {

    @Unique
    private boolean isParryGuardActive(ItemStack stack) {
        return ModTraits.isParryGuardItem(stack);
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void jaams$parry_guardGetUseAnimation(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isParryGuardActive(stack)) {
            cir.setReturnValue(UseAnim.BLOCK);
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void jaams$parry_guardGetUseDuration(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (isParryGuardActive(stack)) {
            
            cir.setReturnValue(72000);
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void jaams$parry_guardUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!isParryGuardActive(stack)) {
            return;
        }
        if (!TraitsConfig.PARRY_GUARD.get()) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            cir.setReturnValue(InteractionResultHolder.fail(stack));
            return;
        }
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }
}
