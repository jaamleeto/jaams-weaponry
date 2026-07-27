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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitSparringStrikeMixin {

    @Inject(method = "hurtEnemy", at = @At("TAIL"))
    private void jaams$onSparringStrikeHurtEnemy(LivingEntity entity, Player sourceentity, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (entity == null || sourceentity == null || sourceentity.level().isClientSide)
            return;
        if (!ModTraits.isSparringStrikeItem(stack))
            return;
        if (!ItemStack.isSameItemSameComponents(sourceentity.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameComponents(sourceentity.getOffhandItem(), stack))
            return;
        CombatEffectHandler.handleSparringStrike(entity, sourceentity, stack);
    }
}
