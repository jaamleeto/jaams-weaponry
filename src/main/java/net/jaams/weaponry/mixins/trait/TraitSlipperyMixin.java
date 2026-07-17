package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.handler.trait.SelfEffectHandler;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class TraitSlipperyMixin {

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onSlipperyHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide)
            return;
        if (!ModTraits.isSlipperyItem(stack))
            return;
        SelfEffectHandler.handleSlippery(target, attacker, stack);
    }
}
