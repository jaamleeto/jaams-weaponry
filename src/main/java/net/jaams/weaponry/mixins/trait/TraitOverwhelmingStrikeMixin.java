package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.handler.trait.CombatEffectHandler;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class TraitOverwhelmingStrikeMixin {

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onOverwhelmingStrikeHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide)
            return;
        if (!ModTraits.isOverwhelmingStrikeItem(stack))
            return;
        CombatEffectHandler.handleOverwhelmingStrike(target, attacker, stack);
    }
}
