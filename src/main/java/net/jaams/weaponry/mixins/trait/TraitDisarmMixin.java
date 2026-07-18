package net.jaams.weaponry.mixins.trait;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.handler.trait.DisarmHandler;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;

@Mixin(ItemStack.class)
public class TraitDisarmMixin {
    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onDisarmHurtEnemy(LivingEntity target, Player attacker, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide || !ModTraits.isDisarmItem(stack)) {
            return;
        }
        if (!ItemStack.isSameItemSameTags(attacker.getMainHandItem(), stack) &&
                !ItemStack.isSameItemSameTags(attacker.getOffhandItem(), stack))
            return;
        float chance = getChance(stack, stack.getTag());
        if (attacker.getRandom().nextFloat() >= chance) {
            return;
        }
        DisarmHandler.disarmEnemy(target, attacker);
    }

    @Unique
    private float getChance(ItemStack stack, CompoundTag tag) {
        if (tag != null && tag.contains("DisarmChance")) {
            return Math.max(0.0F, Math.min(1.0F, tag.getFloat("DisarmChance")));
        }
        return TraitModifierData.getDisarm(stack).map(entry -> entry.chance).filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.DISARM_CHANCE.get().floatValue());
    }
}
