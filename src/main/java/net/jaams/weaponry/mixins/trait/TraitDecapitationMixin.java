package net.jaams.weaponry.mixins.trait;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.handler.trait.DecapitationHandler;
import net.jaams.weaponry.configuration.common.TraitsConfig;

@Mixin(ItemStack.class)
public class TraitDecapitationMixin {

    @Unique
    private boolean isDecapitationEnabled(ItemStack stack) {
        if (!TraitsConfig.DECAPITATION.get()) {
            return false;
        }
        return ModTraits.isDecapitationItem(stack);
    }

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onDecapitationHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide) {
            return;
        }
        if (!isDecapitationEnabled(stack)) {
            return;
        }

        Level world = target.level();
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.5;
        double z = target.getZ();

        DecapitationHandler.handleDecapitation(world, x, y, z, target, attacker, stack);
    }
}
