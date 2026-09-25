package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.util.ModComponents;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.particle.CustomSweepParticleData;
import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class TraitAfterStrikeMixin {

    @Unique
    private static final String NBT_HITS = "AfterStrikeHits";

    @Unique
    private boolean isAfterStrikeEnabled(ItemStack stack) {
        if (!TraitsConfig.AFTER_STRIKE.get())
            return false;
        return ModTraits.isAfterStrikeItem(stack);
    }

    @Inject(method = "hurtEnemy", at = @At("HEAD"))
    private void jaams$onAfterStrikeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || target.level().isClientSide())
            return;
        if (!isAfterStrikeEnabled(stack))
            return;
        float attackScale = attacker.getAttackStrengthScale(0.5F);
        if (requiresFullyCharged(stack) && attackScale < 0.9F) {
            return;
        }
        boolean isCritical = ModUtils.isCritical(attacker, target, attacker.getAttackStrengthScale(0.5F));
        if (isCritical && criticalTriggersFlurry(stack)) {
            ModComponents.update(stack, nbt -> nbt.putInt(NBT_HITS, 0));
            scheduleAfterStrikeAttackChain(stack, target, attacker, getAfterStrikeAttackCount(stack));
        } else {
            updateHitCounterAndCheckChain(stack, target, attacker);
        }
    }

    @Unique
    private void updateHitCounterAndCheckChain(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        CompoundTag nbt = ModComponents.getOrCreate(stack);
        int hits = nbt.getInt(NBT_HITS) + 1;
        int hitsRequired = getRequiredHits(stack);
        if (hits >= hitsRequired) {
            hits = 0;
            scheduleAfterStrikeAttackChain(stack, target, attacker, getAfterStrikeAttackCount(stack));
        }
        nbt.putInt(NBT_HITS, hits);
        ModComponents.set(stack, nbt);
    }

    @Unique
    private void scheduleAfterStrikeAttackChain(ItemStack stack, LivingEntity target, LivingEntity attacker,
            int remainingAttacks) {
        if (remainingAttacks <= 0)
            return;
        Level level = target.level();
        if (!(level instanceof ServerLevel serverLevel))
            return;
        int delay = getAfterStrikeAttackInterval(stack);
        int currentAttackIndex = getAfterStrikeAttackCount(stack) - remainingAttacks + 1;
        JaamsWeaponryMod.queueServerWork(delay, () -> {
            if (!target.isAlive() || !attacker.isAlive()
                    || (attacker instanceof Player player && ModUtils.isAlliedEntity(player, target))) {
                return;
            }
            float damageModifier = calculateDynamicModifier(stack, currentAttackIndex);
            float baseDamage = AmountProvider.get(attacker)
                    .map((amount) -> amount.getDamage())
                    .orElse(4.0F);
            float finalDamage = baseDamage * damageModifier;
            if (finalDamage > 0) {
                target.invulnerableTime = 0;
                DamageSource damageSource = attacker instanceof Player
                        ? attacker.damageSources().playerAttack((Player) attacker)
                        : attacker.damageSources().mobAttack(attacker);
                if (target.hurt(damageSource, finalDamage)) {
                    net.jaams.weaponry.util.ModUtils.applyAttackEnchantEffects(target, attacker);
                    int durabilityCost = getDurabilityCost(stack);
                    if (durabilityCost > 0) {
                        ModUtils.applyTraitDurabilityCost(stack, attacker, durabilityCost, EquipmentSlot.MAINHAND);
                    }
                }
                float particleSize = Math.max(0.4F, 1.0F - (currentAttackIndex * 0.15F));
                spawnSweepParticles(serverLevel, target, particleSize);
                serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP,
                        attacker instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F,
                        0.9F + (currentAttackIndex * 0.05F));
            }
            scheduleAfterStrikeAttackChain(stack, target, attacker, remainingAttacks - 1);
        });
    }

    @Unique
    private float calculateDynamicModifier(ItemStack stack, int attackIndex) {
        float initialModifier = getInitialDamageModifier(stack);
        float decayFactor = getDamageDecayFactor(stack);
        if (decayFactor <= 0.0F) {
            return initialModifier;
        }
        return initialModifier * (float) Math.pow(decayFactor, attackIndex - 1);
    }

    @Unique
    private void spawnSweepParticles(ServerLevel serverLevel, LivingEntity entity, float size) {
        if (entity == null || serverLevel == null)
            return;
        float r = 1.0F,
                g = 1.0F,
                b = 1.0F;
        double centerY = entity.getY() + entity.getBbHeight() / 2.0;
        double yOffset = (serverLevel.random.nextFloat() - 0.5) * 0.6;
        serverLevel.sendParticles(new CustomSweepParticleData(r, g, b, size), entity.getX(), centerY + yOffset,
                entity.getZ(), 1, 0, 0, 0, 0);
    }

    @Unique
    private boolean criticalTriggersFlurry(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeCriticalTriggers")) {
            return tag.getBoolean("AfterStrikeCriticalTriggers");
        }
        return TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.critical_triggers)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_CRITICAL_TRIGGERS.get());
    }

    @Unique
    private boolean requiresFullyCharged(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeRequireFullyCharged"))
            return tag.getBoolean("AfterStrikeRequireFullyCharged");
        return TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.require_fully_charged)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_REQUIRES_CHARGED.get());
    }

    @Unique
    private int getRequiredHits(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeRequiredHits")) {
            return Math.max(1, tag.getInt("AfterStrikeRequiredHits"));
        }
        int value = TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.required_hits)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_REQUIRED_HITS.get());
        return Math.max(1, value);
    }

    @Unique
    private int getAfterStrikeAttackCount(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeAttackCount")) {
            return Math.max(1, tag.getInt("AfterStrikeAttackCount"));
        }
        int value = TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.attack_count)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_STRIKES_COUNT.get());
        return Math.max(1, value);
    }

    @Unique
    private int getAfterStrikeAttackInterval(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeAttackInterval")) {
            return Math.max(1, tag.getInt("AfterStrikeAttackInterval"));
        }
        int value = TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.attack_interval)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_STRIKES_INTERVAL.get());
        return Math.max(1, value);
    }

    @Unique
    private float getInitialDamageModifier(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeInitialModifier")) {
            return Math.max(0.0F, tag.getFloat("AfterStrikeInitialModifier"));
        }
        float value = TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.initial_modifier)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_INITIAL_MODIFIER.get().floatValue());
        return Math.max(0.0F, value);
    }

    @Unique
    private float getDamageDecayFactor(ItemStack stack) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains("AfterStrikeDecayFactor")) {
            return Math.max(0.0F, tag.getFloat("AfterStrikeDecayFactor"));
        }
        float value = TraitModifierData.getAfterStrike(stack)
                .map((entry) -> entry.decay_factor)
                .filter(java.util.Objects::nonNull)
                .orElseGet(() -> TraitsConfig.AFTER_STRIKE_DECAY_FACTOR.get().floatValue());
        return Math.max(0.0F, value);
    }

    @Unique
    private int getDurabilityCost(ItemStack stack) {
        return 0;
    }
}
