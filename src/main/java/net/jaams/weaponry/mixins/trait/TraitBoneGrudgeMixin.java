package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.handler.trait.BonusDamageHandler;
import net.jaams.weaponry.util.ModTraits;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitBoneGrudgeMixin {

    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    private void jaams$onBoneGrudgeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide)
            return;
        if (!ModTraits.isBoneGrudgeItem(stack))
            return;
        BonusDamageHandler.handleBoneGrudge(target, attacker, stack);
    }
}
