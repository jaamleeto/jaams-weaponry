package net.jaams.weaponry.mixins.common;

import java.util.List;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.enchantment.OverdriveEnchantment;
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
    private void injectHurtEnemy(LivingEntity entity, Player attacker, CallbackInfo ci) {
        if (entity == null || attacker == null) {
            return;
        }
        ItemStack itemStack = (ItemStack) (Object) this;
        Level level = attacker.level();
        if (level == null) {
            return;
        }
        int overdriveLevel = itemStack.getEnchantmentLevel(ModEnchantments.OVERDRIVE.get());
        if (overdriveLevel > 0) {
            int extraDamage = OverdriveEnchantment.getDurabilityCost(overdriveLevel);
            itemStack.hurtAndBreak(extraDamage, attacker,
                    (player) -> player.broadcastBreakEvent(player.getUsedItemHand()));
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

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void addGauntletDyedTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir,
            List<Component> list, MutableComponent nombre, int hideFlags) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!(stack.getItem() instanceof GauntletItem gauntletItem)) {
            return;
        }
        if (ModUtils.isImbued(stack)) {
            return;
        }
        int color = ModUtils.getCurrentColor(stack);
        int defaultColor = gauntletItem.getDefaultColor();
        if (color == defaultColor) {
            return;
        }
        if (flag.isAdvanced()) {
            String hex = String.format("#%06X", (0xFFFFFF & color));
            list.add(
                    Component.translatable("tooltip.jaams_weaponry.color")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(" " + hex).withStyle(ChatFormatting.GRAY)));
        } else {
            list.add(Component.translatable("tooltip.jaams_weaponry.dyed").withStyle(ChatFormatting.GRAY,
                    ChatFormatting.ITALIC));
        }
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void jaams$addAfterStrikeHitsTooltip(@javax.annotation.Nullable Player player, TooltipFlag flag,
            CallbackInfoReturnable<List<Component>> cir, List<Component> list, MutableComponent nombre, int hideFlags) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!TooltipsConfig.TOOLTIP_AFTER_STRIKE_HITS.get()) {
            return;
        }
        if (!TraitsConfig.AFTER_STRIKE.get() || !ModTraits.isAfterStrikeItem(stack)) {
            return;
        }
        if (!flag.isAdvanced() || stack.getItem().isEdible()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        int currentHits = (tag != null) ? tag.getInt("AfterStrikeHits") : 0;
        if (currentHits <= 0) {
            return;
        }
        int requiredHits = 1;
        if (tag != null && tag.contains("AfterStrikeRequiredHits")) {
            requiredHits = Math.max(1, tag.getInt("AfterStrikeRequiredHits"));
        } else {
            int value = TraitModifierData.getAfterStrike(stack)
                    .map((entry) -> entry.required_hits)
                    .filter(java.util.Objects::nonNull)
                    .orElseGet(() -> TraitsConfig.AFTER_STRIKE_REQUIRED_HITS.get());
            requiredHits = Math.max(1, value);
        }
        if (requiredHits <= 1) {
            return;
        }
        list.add(Component.translatable("tooltip.jaams_weaponry.after_strike.hits", currentHits, requiredHits)
                .withStyle(ChatFormatting.WHITE));
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void jaams$addRapidBoostHitsTooltip(@javax.annotation.Nullable Player player, TooltipFlag flag,
            CallbackInfoReturnable<List<Component>> cir, List<Component> list, MutableComponent nombre, int hideFlags) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!TooltipsConfig.TOOLTIP_RAPID_BOOST_HITS.get()) {
            return;
        }
        if (!TraitsConfig.RAPID_BOOST.get() || !ModTraits.isRapidBoostItem(stack)) {
            return;
        }
        if (!flag.isAdvanced() || stack.getItem().isEdible()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        int currentHits = (tag != null) ? tag.getInt("RapidBoostHits") : 0;
        if (currentHits <= 0) {
            return;
        }
        int requiredHits = 1;
        if (tag != null && tag.contains("RapidBoostMaxHits")) {
            requiredHits = Math.max(1, tag.getInt("RapidBoostMaxHits"));
        } else {
            int value = TraitModifierData.getRapidBoost(stack)
                    .map((entry) -> entry.max_hits)
                    .filter(java.util.Objects::nonNull)
                    .orElseGet(() -> TraitsConfig.RAPID_BOOST_MAX_HITS.get());
            requiredHits = Math.max(1, value);
        }
        if (requiredHits <= 1) {
            return;
        }
        list.add(Component.translatable("tooltip.jaams_weaponry.rapid_boost.hits", currentHits, requiredHits)
                .withStyle(ChatFormatting.WHITE));
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void jaams$addPowerBoostHitsTooltip(@javax.annotation.Nullable Player player, TooltipFlag flag,
            CallbackInfoReturnable<List<Component>> cir, List<Component> list, MutableComponent nombre, int hideFlags) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!TooltipsConfig.TOOLTIP_POWER_BOOST_HITS.get()) {
            return;
        }
        if (!TraitsConfig.POWER_BOOST.get() || !ModTraits.isPowerBoostItem(stack)) {
            return;
        }
        if (!flag.isAdvanced() || stack.getItem().isEdible()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        int currentHits = (tag != null) ? tag.getInt("PowerBoostHits") : 0;
        if (currentHits <= 0) {
            return;
        }
        int requiredHits = 1;
        if (tag != null && tag.contains("PowerBoostMaxHits")) {
            requiredHits = Math.max(1, tag.getInt("PowerBoostMaxHits"));
        } else {
            int value = TraitModifierData.getPowerBoost(stack)
                    .map((entry) -> entry.max_hits)
                    .filter(java.util.Objects::nonNull)
                    .orElseGet(() -> TraitsConfig.POWER_BOOST_MAX_HITS.get());
            requiredHits = Math.max(1, value);
        }
        if (requiredHits <= 1) {
            return;
        }
        list.add(Component.translatable("tooltip.jaams_weaponry.power_boost.hits", currentHits, requiredHits)
                .withStyle(ChatFormatting.WHITE));
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void jaams$addBusterStrikeHitsTooltip(@javax.annotation.Nullable Player player, TooltipFlag flag,
            CallbackInfoReturnable<List<Component>> cir, List<Component> list, MutableComponent nombre, int hideFlags) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!TooltipsConfig.TOOLTIP_BUSTER_STRIKE_HITS.get()) {
            return;
        }
        if (!TraitsConfig.BUSTER_STRIKE.get() || !ModTraits.isBusterStrikeItem(stack)) {
            return;
        }
        if (!flag.isAdvanced() || stack.getItem().isEdible()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        int currentHits = (tag != null) ? tag.getInt("BusterStrikeHits") : 0;
        if (currentHits <= 0) {
            return;
        }
        int requiredHits = 1;
        if (tag != null && tag.contains("BusterStrikeRequiredHits")) {
            requiredHits = Math.max(1, tag.getInt("BusterStrikeRequiredHits"));
        } else {
            int value = TraitModifierData.getBusterStrike(stack)
                    .map((entry) -> entry.required_hits)
                    .filter(java.util.Objects::nonNull)
                    .orElseGet(() -> TraitsConfig.BUSTER_STRIKE_REQUIRED_HITS.get());
            requiredHits = Math.max(1, value);
        }
        if (requiredHits <= 1) {
            return;
        }
        list.add(Component.translatable("tooltip.jaams_weaponry.buster_strike.hits", currentHits, requiredHits)
                .withStyle(ChatFormatting.WHITE));
    }
}
