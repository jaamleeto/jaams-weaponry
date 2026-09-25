package net.jaams.weaponry.mixins.trait;

import net.jaams.weaponry.util.ModComponents;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.jaams.weaponry.util.ModTraits;
import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.data.TraitModifierData;
import net.jaams.weaponry.configuration.common.TraitsConfig;

@Mixin(ItemStack.class)
public class TraitSmashStrikeMixin {

    @Unique
    private static final float MIN_FALL_DISTANCE = 2.0F;
    @Unique
    private static final float MAX_EFFECTIVE_FALL_DISTANCE = 100.0F;
    @Unique
    private static final float MIN_KNOCKBACK_STRENGTH = 0.1F;
    @Unique
    private static final float MAX_KNOCKBACK_STRENGTH = 0.2F;
    @Unique
    private static final float KNOCKBACK_SCALING_FACTOR = 0.01F;

    @Inject(method = "hurtEnemy", at = @At("RETURN"))
    private void jaams$onSmashStrikeHurtEnemy(LivingEntity target, Player attacker, CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (target == null || attacker == null || attacker.level().isClientSide
                || !ModTraits.isSmashStrikeItem(stack)) {
            return;
        }

        float fallDistance = attacker.fallDistance;
        if (fallDistance < MIN_FALL_DISTANCE || attacker.isInWater() || attacker.isInLava()) {
            return;
        }

        float attackStrength = attacker.getAttackStrengthScale(0.5F);
        applySmashLogic(attacker, target, stack, attackStrength, fallDistance);
    }

    @Unique
    private void applySmashLogic(LivingEntity attacker, LivingEntity target, ItemStack stack, float attackStrength,
            float fallDistance) {
        Level level = attacker.level();

        
        float damagePerBlock = getFloatProperty(stack, "SmashStrikeDamagePerBlock",
                () -> TraitsConfig.SMASH_STRIKE_DAMAGE_PER_BLOCK.get().floatValue());
        float maxBonusDamage = getFloatProperty(stack, "SmashStrikeMaxBonusDamage",
                () -> TraitsConfig.SMASH_STRIKE_MAX_BONUS_DAMAGE.get().floatValue());
        float residualDamageBase = getFloatProperty(stack, "SmashStrikeResidualDamageBase",
                () -> TraitsConfig.SMASH_STRIKE_RESIDUAL_DAMAGE_BASE.get().floatValue());
        float residualDamagePerBlock = getFloatProperty(stack, "SmashStrikeResidualDamagePerBlock",
                () -> TraitsConfig.SMASH_STRIKE_RESIDUAL_DAMAGE_PER_BLOCK.get().floatValue());
        float maxResidualDamage = getFloatProperty(stack, "SmashStrikeMaxResidualDamage",
                () -> TraitsConfig.SMASH_STRIKE_MAX_RESIDUAL_DAMAGE.get().floatValue());
        float smashRadius = getFloatProperty(stack, "SmashStrikeSmashRadius",
                () -> TraitsConfig.SMASH_STRIKE_SMASH_RADIUS.get().floatValue());
        float shakeIntensity = getFloatProperty(stack, "SmashStrikeShakeIntensity",
                () -> TraitsConfig.SMASH_STRIKE_SHAKE_INTENSITY.get().floatValue());
        int shakeResetDelay = getIntProperty(stack, "SmashStrikeShakeResetDelay",
                () -> TraitsConfig.SMASH_STRIKE_SHAKE_RESET_DELAY.get());
        float allyDamageMultiplier = getFloatProperty(stack, "SmashStrikeAllyDamageMultiplier",
                () -> TraitsConfig.SMASH_STRIKE_ALLY_DAMAGE_MULTIPLIER.get().floatValue());
        int durabilityDamageBase = getIntProperty(stack, "SmashStrikeDurabilityDamageBase",
                () -> TraitsConfig.SMASH_STRIKE_DURABILITY_DAMAGE_BASE.get());
        float durabilityDamagePerBlock = getFloatProperty(stack, "SmashStrikeDurabilityDamagePerBlock",
                () -> TraitsConfig.SMASH_STRIKE_DURABILITY_DAMAGE_PER_BLOCK.get().floatValue());
        int maxDurabilityDamage = getIntProperty(stack, "SmashStrikeMaxDurabilityDamage",
                () -> TraitsConfig.SMASH_STRIKE_MAX_DURABILITY_DAMAGE.get());

        float effectiveFallDistance = Math.min(fallDistance, MAX_EFFECTIVE_FALL_DISTANCE);
        float damageBonus = Math.min(effectiveFallDistance * damagePerBlock, maxBonusDamage) * attackStrength;

        
        ModUtils.applySmashDamage(attacker, target, stack, damageBonus);

        
        applyShockwave(attacker, target, level, stack, effectiveFallDistance, attackStrength,
                smashRadius, residualDamageBase, residualDamagePerBlock, maxResidualDamage,
                allyDamageMultiplier);

        
        ModUtils.applyShakeEffect(attacker, shakeIntensity, shakeResetDelay);
        ModUtils.applyShakeEffect(target, shakeIntensity, shakeResetDelay);

        
        spawnBlockParticles(level, target.blockPosition());
        spawnCritParticles(level, target);

        
        level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), attacker instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE,
                0.8F, 1.0F + level.random.nextFloat() * 0.2F);

        
        attacker.fallDistance = 0.0F;

        
        if (attacker instanceof Player player) {
            player.causeFoodExhaustion(0.1F);
        }

        
        if (!(attacker instanceof Player player && player.isCreative())) {
            int durabilityDamage = durabilityDamageBase + Math.round(effectiveFallDistance * durabilityDamagePerBlock);
            durabilityDamage = Math.min(durabilityDamage, maxDurabilityDamage);
            if (durabilityDamage > 0) {
                stack.hurtAndBreak(durabilityDamage, attacker, EquipmentSlot.MAINHAND);
            }
        }
    }

    @Unique
    private void applyShockwave(LivingEntity attacker, LivingEntity target, Level level, ItemStack stack,
            float fallDistance, float attackStrength, float smashRadius,
            float residualDamageBase, float residualDamagePerBlock, float maxResidualDamage,
            float allyDamageMultiplier) {
        AABB area = new AABB(target.blockPosition()).inflate(smashRadius, 0.5D, smashRadius);
        float residualDamage = Math.min(residualDamageBase + fallDistance * residualDamagePerBlock, maxResidualDamage)
                * attackStrength;
        float knockbackStrength = calculateKnockback(fallDistance);

        DamageSource damageSource = new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
                                ResourceLocation.parse("jaams_weaponry:smash"))),
                attacker);

        level.getEntities(attacker, area, e -> e != target && e.isAlive() && !(e instanceof ItemEntity))
                .forEach(entity -> {
                    float effectiveDamage = residualDamage;
                    if (attacker instanceof Player player && ModUtils.isAlliedEntity(player, entity)) {
                        effectiveDamage *= allyDamageMultiplier;
                    }
                    if (entity instanceof LivingEntity livingEntity && effectiveDamage > 0) {
                        livingEntity.hurt(damageSource, effectiveDamage);
                    }
                    float knockbackResistance = entity instanceof LivingEntity living
                            ? (float) living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
                            : 0.0F;
                    float effectiveKnockback = knockbackStrength * (1.0F - knockbackResistance);
                    if (effectiveKnockback > 0) {
                        Vec3 knockbackVector = entity.position().subtract(target.position()).normalize()
                                .scale(effectiveKnockback).add(0, 0.3, 0);
                        entity.push(knockbackVector.x, Math.min(knockbackVector.y, 0.5), knockbackVector.z);
                        entity.hurtMarked = true;
                    }
                });
    }

    @Unique
    private float calculateKnockback(float fallDistance) {
        float t = Math.min(fallDistance * KNOCKBACK_SCALING_FACTOR, 1.0F);
        return MIN_KNOCKBACK_STRENGTH + t * (MAX_KNOCKBACK_STRENGTH - MIN_KNOCKBACK_STRENGTH);
    }

    @Unique
    private void spawnBlockParticles(Level level, BlockPos impactPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos basePos = impactPos.below();
        RandomSource random = level.random;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = basePos.offset(x, 0, z);
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && level.getBlockState(pos.above()).isAir()) {
                    for (int i = 0; i < 3; i++) {
                        double offsetX = random.nextGaussian() * 0.3;
                        double offsetY = random.nextDouble() * 0.5 + 0.5;
                        double offsetZ = random.nextGaussian() * 0.3;
                        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                                pos.getX() + 0.5 + offsetX, pos.getY() + 1.0 + offsetY, pos.getZ() + 0.5 + offsetZ,
                                1, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }
        }
    }

    @Unique
    private void spawnCritParticles(Level level, LivingEntity target) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            double offsetX = level.random.nextGaussian() * 0.2;
            double offsetY = level.random.nextGaussian() * 0.2 + 1.0;
            double offsetZ = level.random.nextGaussian() * 0.2;
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    target.getX() + offsetX, target.getY() + offsetY, target.getZ() + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Unique
    private float getFloatProperty(ItemStack stack, String nbtKey, java.util.function.Supplier<Float> defaultValue) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains(nbtKey)) {
            return tag.getFloat(nbtKey);
        }
        return TraitModifierData.getSmashStrike(stack)
                .map(entry -> {
                    switch (nbtKey) {
                        case "SmashStrikeDamagePerBlock":
                            return entry.damage_per_block;
                        case "SmashStrikeMaxBonusDamage":
                            return entry.max_bonus_damage;
                        case "SmashStrikeResidualDamageBase":
                            return entry.residual_damage_base;
                        case "SmashStrikeResidualDamagePerBlock":
                            return entry.residual_damage_per_block;
                        case "SmashStrikeMaxResidualDamage":
                            return entry.max_residual_damage;
                        case "SmashStrikeSmashRadius":
                            return entry.smash_radius;
                        case "SmashStrikeShakeIntensity":
                            return entry.shake_intensity;
                        case "SmashStrikeAllyDamageMultiplier":
                            return entry.ally_damage_multiplier;
                        case "SmashStrikeDurabilityDamagePerBlock":
                            return entry.durability_damage_per_block;
                        default:
                            return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .orElseGet(defaultValue);
    }

    @Unique
    private int getIntProperty(ItemStack stack, String nbtKey, java.util.function.Supplier<Integer> defaultValue) {
        CompoundTag tag = ModComponents.get(stack);
        if (tag != null && tag.contains(nbtKey)) {
            return tag.getInt(nbtKey);
        }
        return TraitModifierData.getSmashStrike(stack)
                .map(entry -> {
                    switch (nbtKey) {
                        case "SmashStrikeShakeResetDelay":
                            return entry.shake_reset_delay;
                        case "SmashStrikeDurabilityDamageBase":
                            return entry.durability_damage_base;
                        case "SmashStrikeMaxDurabilityDamage":
                            return entry.max_durability_damage;
                        default:
                            return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .orElseGet(defaultValue);
    }
}
