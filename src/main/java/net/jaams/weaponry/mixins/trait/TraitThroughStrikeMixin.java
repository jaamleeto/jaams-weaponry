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

@Mixin(Player.class)
public class TraitThroughStrikeMixin {

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.BEFORE))
    private void jaams$onThroughStrikeAttack(net.minecraft.world.entity.Entity target, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide || !(target instanceof LivingEntity living))
            return;
        ItemStack stack = player.getMainHandItem();
        if (!ModTraits.isThroughStrikeItem(stack)) {
            stack = player.getOffhandItem();
        }
        if (!ModTraits.isThroughStrikeItem(stack))
            return;
        CombatEffectHandler.handleThroughStrike(living, player, stack);
    }
}
