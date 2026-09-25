package net.jaams.weaponry.mixins.common;

import net.jaams.weaponry.util.ModComponents;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.item.GauntletItem;
import net.jaams.weaponry.util.ModTags;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    private void injectHurtEnemy(LivingEntity entity, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        if (entity == null || attacker == null) {
            return;
        }
        ItemStack itemStack = (ItemStack) (Object) this;
        Level level = attacker.level();
        if (level == null) {
            return;
        }
        int overdriveLevel = ModEnchantments.level(itemStack, ModEnchantments.OVERDRIVE);
        if (overdriveLevel > 0) {
            int extraDamage = ModEnchantments.overdriveDurabilityCost(overdriveLevel);
            itemStack.hurtAndBreak(extraDamage, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
        }
        if (itemStack.is(ModTags.HAMMERS) || itemStack.is(ModTags.GREAT_HAMMERS)) {
            if (itemStack.getDisplayName().getString().toLowerCase().equals("[bonk]")) {
                ModUtils.playSound(entity, "jaams_weaponry:bonk");
            }
        }
        if (itemStack.is(ModTags.BROOMS)) {
            ModUtils.playSound(entity, "jaams_weaponry:broom_hit");
            if (entity.level() instanceof ServerLevel serverLevel) {
                spawnItemParticles(serverLevel, itemStack, entity, entity.level().random, 5);
            }
        }
        if (itemStack.is(ModTags.WAR_PICKS)) {
            ModUtils.playSound(entity, "jaams_weaponry:war_pick_hit");
            if (entity.level() instanceof ServerLevel serverLevel) {
                spawnItemParticles(serverLevel, itemStack, entity, entity.level().random, 5);
            }
        }
    }

    private void spawnItemParticles(ServerLevel serverLevel, ItemStack itemstack, LivingEntity entity, RandomSource random, int particleCount) {
        if (itemstack == null || itemstack.isEmpty()) {
            return;
        }
        for (int i = 0; i < particleCount; i++) {
            double xOffset = entity.getX() + (random.nextDouble() - 0.5) * entity.getBbWidth();
            double yOffset = entity.getY() + entity.getBbHeight() * 0.5 + (random.nextDouble() - 0.5) * entity.getBbHeight() * 0.5;
            double zOffset = entity.getZ() + (random.nextDouble() - 0.5) * entity.getBbWidth();
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemstack), xOffset, yOffset, zOffset, 1, 0.1, 0.1, 0.1, 0.05);
        }
    }
}
